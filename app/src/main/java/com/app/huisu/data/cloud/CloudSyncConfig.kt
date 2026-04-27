package com.app.huisu.data.cloud

data class CloudSyncConfig(
    val serverUrl: String = DEFAULT_SERVER_URL,
    val apiToken: String = "",
    val deviceId: String = "",
    val encryptionPassword: String = ""
) {
    fun isComplete(): Boolean {
        return serverUrl.isNotBlank() &&
            apiToken.isNotBlank() &&
            deviceId.isNotBlank()
    }

    companion object {
        const val DEFAULT_SERVER_URL = "http://106.53.73.104:18080"
    }
}

data class CloudSyncTimestamps(
    val lastUploadAt: Long = 0L,
    val lastDownloadAt: Long = 0L,
    val lastRevision: Long = 0L
)
