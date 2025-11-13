package com.app.huisu.ui.affirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.huisu.data.entity.Affirmation
import com.app.huisu.data.entity.AffirmationRecord
import com.app.huisu.data.preferences.AppPreferences
import com.app.huisu.data.repository.AffirmationRepository
import com.app.huisu.util.CalculationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AffirmationViewModel @Inject constructor(
    private val affirmationRepository: AffirmationRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AffirmationUiState())
    val uiState: StateFlow<AffirmationUiState> = _uiState.asStateFlow()

    private val _timerState = MutableStateFlow(AffirmationTimerState())
    val timerState: StateFlow<AffirmationTimerState> = _timerState.asStateFlow()

    init {
        loadStatistics()
        loadAffirmations()
        loadSettings()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            affirmationRepository.getAllRecords().collect { records ->
                val stats = CalculationUtils.calculateAffirmationStats(records)
                _uiState.update {
                    it.copy(
                        stats = AffirmationStats(
                            todayCompleted = stats.todayCompleted,
                            weekCompletionRate = stats.weekCompletionRate,
                            monthCompletionRate = stats.monthCompletionRate,
                            totalDuration = stats.totalDuration.toInt()
                        )
                    )
                }
            }
        }
    }

    private fun loadAffirmations() {
        viewModelScope.launch {
            affirmationRepository.getActiveAffirmations().collect { affirmations ->
                _uiState.update { it.copy(affirmations = affirmations) }
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            appPreferences.affirmationDuration.collect { duration ->
                _uiState.update { it.copy(duration = duration) }
            }
        }
    }

    fun startAffirmation(affirmation: Affirmation, duration: Int) {
        _timerState.update {
            AffirmationTimerState(
                isRunning = true,
                affirmationText = affirmation.content,
                totalSeconds = duration,
                remainingSeconds = duration,
                startTime = System.currentTimeMillis()
            )
        }
    }

    fun pauseAffirmation() {
        _timerState.update { it.copy(isRunning = false) }
    }

    fun resumeAffirmation() {
        _timerState.update { it.copy(isRunning = true) }
    }

    fun updateTimer(remainingSeconds: Int) {
        _timerState.update { it.copy(remainingSeconds = remainingSeconds) }
    }

    fun completeAffirmation() {
        val state = _timerState.value
        if (state.startTime > 0) {
            viewModelScope.launch {
                val record = AffirmationRecord(
                    content = state.affirmationText,
                    startTime = state.startTime,
                    duration = state.totalSeconds - state.remainingSeconds,
                    isCompleted = true,
                    createDate = getTodayStartMillis()
                )
                affirmationRepository.insertRecord(record)

                // 重置计时器
                _timerState.update { AffirmationTimerState() }
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

    fun setDuration(duration: Int) {
        viewModelScope.launch {
            appPreferences.setAffirmationDuration(duration)
        }
    }

    private fun getTodayStartMillis(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}

data class AffirmationUiState(
    val stats: AffirmationStats = AffirmationStats(),
    val affirmations: List<Affirmation> = emptyList(),
    val duration: Int = 180 // seconds
)

data class AffirmationStats(
    val todayCompleted: Int = 0,
    val weekCompletionRate: Float = 0f,
    val monthCompletionRate: Float = 0f,
    val totalDuration: Int = 0
)

data class AffirmationTimerState(
    val isRunning: Boolean = false,
    val affirmationText: String = "",
    val totalSeconds: Int = 0,
    val remainingSeconds: Int = 0,
    val startTime: Long = 0
)
