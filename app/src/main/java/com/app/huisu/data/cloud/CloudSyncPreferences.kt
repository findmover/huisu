package com.app.huisu.data.cloud

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudSyncPreferences @Inject constructor(
    @ApplicationContext context: Context,
    private val secureTextCipher: SecureTextCipher
) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun loadConfig(): CloudSyncConfig {
        val deviceId = preferences.getString(KEY_DEVICE_ID, "").orEmpty()
            .ifBlank { createAndSaveDeviceId() }

        return CloudSyncConfig(
            serverUrl = preferences.getString(KEY_SERVER_URL, "").orEmpty()
                .ifBlank { CloudSyncConfig.DEFAULT_SERVER_URL },
            apiToken = loadApiToken(),
            deviceId = deviceId,
            encryptionPassword = secureTextCipher.decrypt(preferences.getString(KEY_ENCRYPTION_PASSWORD, "").orEmpty())
        )
    }

    fun saveConfig(config: CloudSyncConfig) {
        val deviceId = config.deviceId.trim().ifBlank { createAndSaveDeviceId() }
        preferences.edit()
            .putString(KEY_SERVER_URL, config.serverUrl.trim().trimEnd('/'))
            .putString(KEY_DEVICE_ID, deviceId)
            .putString(KEY_API_TOKEN, secureTextCipher.encrypt(config.apiToken.trim()))
            .putString(KEY_ENCRYPTION_PASSWORD, secureTextCipher.encrypt(config.encryptionPassword))
            .apply()
    }

    fun loadTimestamps(): CloudSyncTimestamps {
        return CloudSyncTimestamps(
            lastUploadAt = preferences.getLong(KEY_LAST_UPLOAD_AT, 0L),
            lastDownloadAt = preferences.getLong(KEY_LAST_DOWNLOAD_AT, 0L),
            lastRevision = preferences.getLong(KEY_LAST_REVISION, 0L)
        )
    }

    fun setLastUploadAt(timestamp: Long) {
        preferences.edit().putLong(KEY_LAST_UPLOAD_AT, timestamp).apply()
    }

    fun setLastDownloadAt(timestamp: Long) {
        preferences.edit().putLong(KEY_LAST_DOWNLOAD_AT, timestamp).apply()
    }

    fun setLastRevision(revision: Long) {
        preferences.edit().putLong(KEY_LAST_REVISION, revision).apply()
    }

    fun hasPendingChanges(): Boolean {
        return preferences.getBoolean(KEY_HAS_PENDING_CHANGES, false)
    }

    fun setPendingChanges(value: Boolean) {
        preferences.edit().putBoolean(KEY_HAS_PENDING_CHANGES, value).apply()
    }

    private fun createAndSaveDeviceId(): String {
        val deviceId = "android-${UUID.randomUUID()}"
        preferences.edit().putString(KEY_DEVICE_ID, deviceId).apply()
        return deviceId
    }

    private fun loadApiToken(): String {
        val storedToken = secureTextCipher.decrypt(preferences.getString(KEY_API_TOKEN, "").orEmpty())
        if (storedToken.isNotBlank()) return storedToken

        val defaultToken = CloudSyncConfig.DEFAULT_API_TOKEN
        preferences.edit()
            .putString(KEY_API_TOKEN, secureTextCipher.encrypt(defaultToken))
            .apply()
        return defaultToken
    }

    companion object {
        private const val PREFERENCES_NAME = "cloud_sync_preferences"
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_API_TOKEN = "sync_api_token"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_ENCRYPTION_PASSWORD = "encryption_password"
        private const val KEY_LAST_UPLOAD_AT = "last_upload_at"
        private const val KEY_LAST_DOWNLOAD_AT = "last_download_at"
        private const val KEY_LAST_REVISION = "last_revision"
        private const val KEY_HAS_PENDING_CHANGES = "has_pending_changes"
    }
}
