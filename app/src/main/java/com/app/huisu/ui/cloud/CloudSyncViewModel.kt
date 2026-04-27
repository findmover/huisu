package com.app.huisu.ui.cloud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.huisu.data.cloud.CloudSyncConfig
import com.app.huisu.data.repository.CloudSyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CloudSyncUiState(
    val serverUrl: String = CloudSyncConfig.DEFAULT_SERVER_URL,
    val apiToken: String = "",
    val deviceId: String = "",
    val encryptionPassword: String = "",
    val hasSavedToken: Boolean = false,
    val hasSavedEncryptionPassword: Boolean = false,
    val lastUploadAt: Long = 0L,
    val lastDownloadAt: Long = 0L,
    val lastRevision: Long = 0L,
    val isBusy: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class CloudSyncViewModel @Inject constructor(
    private val cloudSyncRepository: CloudSyncRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CloudSyncUiState())
    val uiState: StateFlow<CloudSyncUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun updateServerUrl(value: String) = update { copy(serverUrl = value) }
    fun updateApiToken(value: String) = update { copy(apiToken = value) }
    fun updateDeviceId(value: String) = update { copy(deviceId = value) }
    fun updateEncryptionPassword(value: String) = update { copy(encryptionPassword = value) }

    fun saveConfig() {
        saveCurrentConfig()
        load(message = "云同步配置已保存")
    }

    fun testConnection() {
        saveCurrentConfig()
        runSyncAction {
            cloudSyncRepository.testConnection().message
        }
    }

    fun uploadNow() {
        saveCurrentConfig()
        runSyncAction {
            cloudSyncRepository.uploadSnapshot(force = false).message
        }
    }

    fun forceUploadNow() {
        saveCurrentConfig()
        runSyncAction {
            cloudSyncRepository.uploadSnapshot(force = true).message
        }
    }

    fun downloadNow() {
        saveCurrentConfig()
        runSyncAction {
            cloudSyncRepository.downloadSnapshot().message
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun saveCurrentConfig() {
        val current = _uiState.value
        val existing = cloudSyncRepository.loadConfig()
        val config = CloudSyncConfig(
            serverUrl = current.serverUrl,
            apiToken = current.apiToken.ifBlank { existing.apiToken },
            deviceId = current.deviceId.ifBlank { existing.deviceId },
            encryptionPassword = current.encryptionPassword.ifBlank { existing.encryptionPassword }
        )

        cloudSyncRepository.saveConfig(config)
    }

    private fun load(message: String? = null) {
        val config = cloudSyncRepository.loadConfig()
        val timestamps = cloudSyncRepository.loadTimestamps()
        _uiState.value = CloudSyncUiState(
            serverUrl = config.serverUrl,
            apiToken = "",
            deviceId = config.deviceId,
            encryptionPassword = "",
            hasSavedToken = config.apiToken.isNotBlank(),
            hasSavedEncryptionPassword = config.encryptionPassword.isNotBlank(),
            lastUploadAt = timestamps.lastUploadAt,
            lastDownloadAt = timestamps.lastDownloadAt,
            lastRevision = timestamps.lastRevision,
            message = message
        )
    }

    private fun update(block: CloudSyncUiState.() -> CloudSyncUiState) {
        _uiState.update { it.block() }
    }

    private fun runSyncAction(action: suspend () -> String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true, message = null) }
            val message = runCatching { action() }
                .getOrElse { "同步失败: ${it.message ?: "未知错误"}" }
            val timestamps = cloudSyncRepository.loadTimestamps()
            val config = cloudSyncRepository.loadConfig()
            _uiState.update {
                it.copy(
                    isBusy = false,
                    lastUploadAt = timestamps.lastUploadAt,
                    lastDownloadAt = timestamps.lastDownloadAt,
                    lastRevision = timestamps.lastRevision,
                    message = message,
                    apiToken = "",
                    encryptionPassword = "",
                    hasSavedToken = config.apiToken.isNotBlank(),
                    hasSavedEncryptionPassword = config.encryptionPassword.isNotBlank()
                )
            }
        }
    }
}
