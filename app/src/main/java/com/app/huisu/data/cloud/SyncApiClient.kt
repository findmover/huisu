package com.app.huisu.data.cloud

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

data class SyncSnapshotResponse(
    val exists: Boolean,
    val revision: Long,
    val snapshot: JSONObject?
)

data class SyncUploadResponse(
    val revision: Long,
    val updatedAt: String
)

class SyncConflictException(message: String) : IllegalStateException(message)

@Singleton
class SyncApiClient @Inject constructor() {

    suspend fun probe(config: CloudSyncConfig) = withContext(Dispatchers.IO) {
        execute(
            method = "POST",
            url = buildUrl(config, "/v1/sync/probe"),
            config = config,
            body = "",
            requiresAuth = true
        )
    }

    suspend fun getSnapshot(config: CloudSyncConfig): SyncSnapshotResponse = withContext(Dispatchers.IO) {
        val response = JSONObject(
            execute(
                method = "GET",
                url = buildUrl(config, "/v1/sync/snapshot"),
                config = config,
                body = "",
                requiresAuth = true
            )
        )
        SyncSnapshotResponse(
            exists = response.optBoolean("exists", false),
            revision = response.optLong("revision", 0L),
            snapshot = response.optJSONObject("snapshot")
        )
    }

    suspend fun putSnapshot(
        config: CloudSyncConfig,
        baseRevision: Long,
        snapshot: JSONObject,
        force: Boolean = false
    ): SyncUploadResponse = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("device_id", config.deviceId)
            .put("base_revision", baseRevision)
            .put("client_updated_at", System.currentTimeMillis().toString())
            .put("snapshot", snapshot)

        val path = if (force) "/v1/sync/snapshot?force=true" else "/v1/sync/snapshot"
        val response = JSONObject(
            execute(
                method = "PUT",
                url = buildUrl(config, path),
                config = config,
                body = payload.toString(),
                requiresAuth = true
            )
        )
        SyncUploadResponse(
            revision = response.optLong("revision", baseRevision),
            updatedAt = response.optString("updated_at")
        )
    }

    private fun execute(
        method: String,
        url: URL,
        config: CloudSyncConfig,
        body: String,
        requiresAuth: Boolean
    ): String {
        require(config.isComplete()) { "请先填写完整云同步配置" }

        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 15_000
            readTimeout = 25_000
            doInput = true
            setRequestProperty("Accept", "application/json")
            if (requiresAuth) {
                setRequestProperty("Authorization", "Bearer ${config.apiToken}")
            }
            if (body.isNotBlank()) {
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
            }
        }

        if (body.isNotBlank()) {
            connection.doOutput = true
            OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { it.write(body) }
        }

        val responseCode = connection.responseCode
        val responseBody = if (responseCode in 200..299) {
            runCatching {
                connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            }.getOrDefault("")
        } else {
            connection.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
        }
        connection.disconnect()

        if (responseCode == HttpURLConnection.HTTP_CONFLICT) {
            throw SyncConflictException("云端版本已变化")
        }
        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED || responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
            error("Token 无效或没有访问权限")
        }
        if (responseCode !in 200..299) {
            error("同步服务请求失败($responseCode): ${responseBody.ifBlank { "无错误详情" }}")
        }
        return responseBody
    }

    private fun buildUrl(config: CloudSyncConfig, path: String): URL {
        return URL("${config.serverUrl.trim().trimEnd('/')}$path")
    }
}
