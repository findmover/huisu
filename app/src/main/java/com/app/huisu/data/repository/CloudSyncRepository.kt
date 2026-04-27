package com.app.huisu.data.repository

import com.app.huisu.data.cloud.CloudSnapshotCipher
import com.app.huisu.data.cloud.CloudSyncConfig
import com.app.huisu.data.cloud.CloudSyncPreferences
import com.app.huisu.data.cloud.CloudSyncTimestamps
import com.app.huisu.data.cloud.DatabaseSnapshotStore
import com.app.huisu.data.cloud.SyncApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

data class CloudSyncResult(
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Singleton
class CloudSyncRepository @Inject constructor(
    private val preferences: CloudSyncPreferences,
    private val snapshotStore: DatabaseSnapshotStore,
    private val syncApiClient: SyncApiClient,
    private val snapshotCipher: CloudSnapshotCipher
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val uploadMutex = Mutex()
    private var pendingUploadJob: Job? = null

    fun loadConfig(): CloudSyncConfig = preferences.loadConfig()

    fun loadTimestamps(): CloudSyncTimestamps = preferences.loadTimestamps()

    fun saveConfig(config: CloudSyncConfig) {
        preferences.saveConfig(config)
    }

    fun requestAutoUpload() {
        if (!preferences.loadConfig().isComplete()) return

        pendingUploadJob?.cancel()
        pendingUploadJob = scope.launch {
            delay(AUTO_UPLOAD_DEBOUNCE_MS)
            runCatching { uploadSnapshot(force = false) }
        }
    }

    suspend fun testConnection(): CloudSyncResult = uploadMutex.withLock {
        val config = preferences.loadConfig()
        require(config.isComplete()) { "请先填写完整云同步配置" }

        syncApiClient.probe(config)
        CloudSyncResult(message = "同步服务连接正常")
    }

    suspend fun uploadSnapshot(force: Boolean = false): CloudSyncResult = uploadMutex.withLock {
        val config = preferences.loadConfig()
        require(config.isComplete()) { "请先填写完整云同步配置" }

        val rawSnapshot = snapshotStore.exportSnapshot().toString()
        val snapshot = if (config.encryptionPassword.isNotBlank()) {
            JSONObject(snapshotCipher.encrypt(rawSnapshot, config.encryptionPassword))
        } else {
            JSONObject(rawSnapshot)
        }
        val currentRevision = preferences.loadTimestamps().lastRevision
        val response = syncApiClient.putSnapshot(
            config = config,
            baseRevision = currentRevision,
            snapshot = snapshot,
            force = force
        )
        val now = System.currentTimeMillis()
        preferences.setLastUploadAt(now)
        preferences.setLastRevision(response.revision)
        CloudSyncResult(message = "已上传本地快照，云端版本 ${response.revision}", timestamp = now)
    }

    suspend fun downloadSnapshot(): CloudSyncResult = uploadMutex.withLock {
        val config = preferences.loadConfig()
        require(config.isComplete()) { "请先填写完整云同步配置" }

        val response = syncApiClient.getSnapshot(config)
        require(response.exists && response.snapshot != null) { "云端还没有同步快照" }

        val rawSnapshot = snapshotCipher.decrypt(response.snapshot.toString(), config.encryptionPassword)
        snapshotStore.importSnapshot(JSONObject(rawSnapshot))
        val now = System.currentTimeMillis()
        preferences.setLastDownloadAt(now)
        preferences.setLastRevision(response.revision)
        CloudSyncResult(message = "已合并云端版本 ${response.revision} 到本地", timestamp = now)
    }

    companion object {
        private const val AUTO_UPLOAD_DEBOUNCE_MS = 1_500L
    }
}
