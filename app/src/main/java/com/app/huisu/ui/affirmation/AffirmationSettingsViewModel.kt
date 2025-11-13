package com.app.huisu.ui.affirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.huisu.data.entity.Affirmation
import com.app.huisu.data.preferences.AppPreferences
import com.app.huisu.data.repository.AffirmationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AffirmationSettingsViewModel @Inject constructor(
    private val affirmationRepository: AffirmationRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AffirmationSettingsUiState())
    val uiState: StateFlow<AffirmationSettingsUiState> = _uiState.asStateFlow()

    init {
        loadAffirmations()
        loadSettings()
    }

    private fun loadAffirmations() {
        viewModelScope.launch {
            affirmationRepository.getAllAffirmations().collect { affirmations ->
                _uiState.update { it.copy(affirmations = affirmations) }
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // 合并多个 Flow
            combine(
                appPreferences.morningReminderTime,
                appPreferences.noonReminderTime,
                appPreferences.eveningReminderTime,
                appPreferences.affirmationDuration
            ) { morningTime, noonTime, eveningTime, duration ->
                TimeSettings(
                    morningTime = morningTime,
                    noonTime = noonTime,
                    eveningTime = eveningTime,
                    duration = duration
                )
            }.collect { timeSettings ->
                _uiState.update { it.copy(timeSettings = timeSettings) }
            }
        }
    }

    fun addAffirmation(content: String) {
        viewModelScope.launch {
            val maxOrder = _uiState.value.affirmations.maxOfOrNull { it.order } ?: -1
            val affirmation = Affirmation(
                content = content,
                order = maxOrder + 1
            )
            affirmationRepository.insertAffirmation(affirmation)
        }
    }

    fun updateAffirmation(affirmation: Affirmation) {
        viewModelScope.launch {
            affirmationRepository.updateAffirmation(affirmation)
        }
    }

    fun deleteAffirmation(affirmation: Affirmation) {
        viewModelScope.launch {
            affirmationRepository.deleteAffirmation(affirmation)
        }
    }

    fun updateMorningTime(time: String) {
        viewModelScope.launch {
            appPreferences.setMorningReminderTime(time)
        }
    }

    fun updateNoonTime(time: String) {
        viewModelScope.launch {
            appPreferences.setNoonReminderTime(time)
        }
    }

    fun updateEveningTime(time: String) {
        viewModelScope.launch {
            appPreferences.setEveningReminderTime(time)
        }
    }

    fun updateDuration(duration: Int) {
        viewModelScope.launch {
            appPreferences.setAffirmationDuration(duration)
        }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true) }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun showEditDialog(affirmation: Affirmation) {
        _uiState.update {
            it.copy(
                showEditDialog = true,
                editingAffirmation = affirmation
            )
        }
    }

    fun hideEditDialog() {
        _uiState.update {
            it.copy(
                showEditDialog = false,
                editingAffirmation = null
            )
        }
    }

    fun showTimePickerDialog(timeType: TimeType) {
        _uiState.update {
            it.copy(
                showTimePickerDialog = true,
                currentTimeType = timeType
            )
        }
    }

    fun hideTimePickerDialog() {
        _uiState.update {
            it.copy(
                showTimePickerDialog = false,
                currentTimeType = null
            )
        }
    }

    fun showDurationPickerDialog() {
        _uiState.update { it.copy(showDurationPickerDialog = true) }
    }

    fun hideDurationPickerDialog() {
        _uiState.update { it.copy(showDurationPickerDialog = false) }
    }

    fun selectAffirmation(affirmationId: Long) {
        viewModelScope.launch {
            val affirmations = _uiState.value.affirmations
            affirmations.forEach { affirmation ->
                val updated = affirmation.copy(isSelected = affirmation.id == affirmationId)
                affirmationRepository.updateAffirmation(updated)
            }
        }
    }
}

data class AffirmationSettingsUiState(
    val affirmations: List<Affirmation> = emptyList(),
    val timeSettings: TimeSettings = TimeSettings(),
    val showAddDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val editingAffirmation: Affirmation? = null,
    val showTimePickerDialog: Boolean = false,
    val currentTimeType: TimeType? = null,
    val showDurationPickerDialog: Boolean = false
)

data class TimeSettings(
    val morningTime: String = "08:00",
    val noonTime: String = "12:30",
    val eveningTime: String = "20:00",
    val duration: Int = 180 // 秒
)

enum class TimeType {
    MORNING, NOON, EVENING
}
