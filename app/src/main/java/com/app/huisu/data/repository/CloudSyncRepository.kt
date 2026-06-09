package com.app.huisu.data.repository

import com.app.huisu.data.cloud.CloudSnapshotCipher
import com.app.huisu.data.cloud.CloudSyncConfig
import com.app.huisu.data.cloud.CloudSyncPreferences
import com.app.huisu.data.cloud.CloudSyncTimestamps
import com.app.huisu.data.cloud.DatabaseSnapshotStore
import com.app.huisu.data.cloud.SyncApiClient
import com.app.huisu.data.cloud.SyncConflictException
import com.app.huisu.data.cloud.SyncMergeHistoryResponse
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
    private val syncMutex = Mutex()
    private var pendingUploadJob: Job? = null
    private var startupSyncJob: Job? = null

    fun loadConfig(): CloudSyncConfig = preferences.loadConfig()

    fun loadTimestamps(): CloudSyncTimestamps = preferences.loadTimestamps()

    fun saveConfig(config: CloudSyncConfig) {
        preferences.saveConfig(config)
        if (config.isComplete()) {
            requestStartupSync()
        }
    }

    fun requestStartupSync() {
        if (!preferences.loadConfig().isComplete()) return
        if (startupSyncJob?.isActive == true) return

        startupSyncJob = scope.launch {
            runCatching { syncOnAppStart() }
        }
    }

    fun requestAutoUpload() {
        preferences.setPendingChanges(true)
        if (!preferences.loadConfig().isComplete()) return

        pendingUploadJob?.cancel()
        pendingUploadJob = scope.launch {
            delay(AUTO_UPLOAD_DEBOUNCE_MS)
            runCatching { uploadSnapshot(force = false) }
        }
    }

    suspend fun testConnection(): CloudSyncResult = syncMutex.withLock {
        val config = preferences.loadConfig()
        require(config.isComplete()) { "请先填写完整云同步配置" }

        syncApiClient.probe(config)
        CloudSyncResult(message = "同步服务连接正常")
    }

    suspend fun syncOnAppStart(): CloudSyncResult = syncMutex.withLock {
        val config = preferences.loadConfig()
        require(config.isComplete()) { "请先填写完整云同步配置" }

        val cloud = syncApiClient.getSnapshot(config)
        val localRevision = preferences.loadTimestamps().lastRevision
        val hasPendingChanges = preferences.hasPendingChanges()
        var downloaded = false

        if (cloud.exists && cloud.snapshot != null && cloud.revision > localRevision) {
            importCloudSnapshot(config, cloud.snapshot)
            preferences.setLastRevision(cloud.revision)
            preferences.setLastDownloadAt(System.currentTimeMillis())
            downloaded = true
        }

        val shouldUpload = !cloud.exists || hasPendingChanges
        if (shouldUpload) {
            val uploadedRevision = uploadCurrentSnapshotLocked(config, force = false)
            val message = if (downloaded) {
                "已拉取云端并上传合并结果，云端版本 $uploadedRevision"
            } else {
                "已自动上传本地快照，云端版本 $uploadedRevision"
            }
            return CloudSyncResult(message = message)
        }

        return CloudSyncResult(
            message = if (downloaded) {
                "已自动合并云端版本 ${cloud.revision} 到本地"
            } else {
                "云同步已是最新版本 $localRevision"
            }
        )
    }

    suspend fun uploadSnapshot(force: Boolean = false): CloudSyncResult = syncMutex.withLock {
        val config = preferences.loadConfig()
        require(config.isComplete()) { "请先填写完整云同步配置" }

        val revision = if (force) {
            uploadCurrentSnapshotLocked(config, force = true)
        } else {
            uploadWithConflictMergeLocked(config)
        }
        CloudSyncResult(message = "已上传本地快照，云端版本 $revision")
    }

    suspend fun downloadSnapshot(): CloudSyncResult = syncMutex.withLock {
        val config = preferences.loadConfig()
        require(config.isComplete()) { "请先填写完整云同步配置" }

        val response = syncApiClient.getSnapshot(config)
        require(response.exists && response.snapshot != null) { "云端还没有同步快照" }

        importCloudSnapshot(config, response.snapshot)
        val now = System.currentTimeMillis()
        preferences.setLastDownloadAt(now)
        preferences.setLastRevision(response.revision)
        CloudSyncResult(message = "已合并云端版本 ${response.revision} 到本地", timestamp = now)
    }

    suspend fun previewMergeHistory(limit: Int = DEFAULT_MERGE_HISTORY_LIMIT): CloudSyncResult = syncMutex.withLock {
        val config = preferences.loadConfig()
        require(config.isComplete()) { "请先填写完整云同步配置" }

        val response = syncApiClient.mergeHistory(
            config = config,
            limit = limit,
            dryRun = true
        )
        CloudSyncResult(message = "预览合并历史：${formatMergeCounts(response)}，确认后可执行合并去重")
    }

    suspend fun mergeHistory(limit: Int = DEFAULT_MERGE_HISTORY_LIMIT): CloudSyncResult = syncMutex.withLock {
        val config = preferences.loadConfig()
        require(config.isComplete()) { "请先填写完整云同步配置" }

        val response = syncApiClient.mergeHistory(
            config = config,
            limit = limit,
            dryRun = false
        )
        val cloud = syncApiClient.getSnapshot(config)
        if (cloud.exists && cloud.snapshot != null) {
            importCloudSnapshot(config, cloud.snapshot)
            val now = System.currentTimeMillis()
            preferences.setLastDownloadAt(now)
            preferences.setLastRevision(cloud.revision)
            return CloudSyncResult(
                message = "已合并历史并拉取到本地，云端版本 ${cloud.revision}：${formatMergeCounts(response)}",
                timestamp = now
            )
        }

        preferences.setLastRevision(response.revision)
        CloudSyncResult(message = "已合并历史，云端版本 ${response.revision}：${formatMergeCounts(response)}")
    }

    private suspend fun uploadWithConflictMergeLocked(config: CloudSyncConfig): Long {
        return try {
            uploadCurrentSnapshotLocked(config, force = false)
        } catch (conflict: SyncConflictException) {
            val cloud = syncApiClient.getSnapshot(config)
            if (cloud.exists && cloud.snapshot != null) {
                importCloudSnapshot(config, cloud.snapshot)
                preferences.setLastRevision(cloud.revision)
                preferences.setLastDownloadAt(System.currentTimeMillis())
            }
            uploadCurrentSnapshotLocked(config, force = false)
        }
    }

    private suspend fun uploadCurrentSnapshotLocked(
        config: CloudSyncConfig,
        force: Boolean
    ): Long {
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
        preferences.setPendingChanges(false)
        return response.revision
    }

    private fun importCloudSnapshot(config: CloudSyncConfig, snapshot: JSONObject) {
        val rawSnapshot = snapshotCipher.decrypt(snapshot.toString(), config.encryptionPassword)
        snapshotStore.importSnapshot(JSONObject(rawSnapshot))
    }

    private fun formatMergeCounts(response: SyncMergeHistoryResponse): String {
        val counts = response.mergedTableCounts
        val noteCount = counts["quick_notes"] ?: 0
        val imageCount = counts["quick_note_images"] ?: 0
        val todoCount = counts["todo_items"] ?: 0
        return "速记 $noteCount 条，图片 $imageCount 张，待办 $todoCount 条"
    }

    companion object {
        private const val AUTO_UPLOAD_DEBOUNCE_MS = 1_500L
        private const val DEFAULT_MERGE_HISTORY_LIMIT = 30
    }
}
