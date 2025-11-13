package com.app.huisu.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.huisu.data.entity.Achievement
import com.app.huisu.data.repository.AchievementRepository
import com.app.huisu.data.repository.AffirmationRepository
import com.app.huisu.data.repository.MeditationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val meditationRepository: MeditationRepository,
    private val affirmationRepository: AffirmationRepository,
    private val achievementRepository: AchievementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
        loadAchievements()
        loadCalendarData()
        updateAchievementProgress()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            combine(
                meditationRepository.getTotalCount(),
                meditationRepository.getTotalDuration(),
                affirmationRepository.getTotalDuration(),
                affirmationRepository.getTotalCompletedCount()
            ) { flows ->
                val meditationCount = flows[0] as Int
                val meditationDuration = flows[1] as? Int ?: 0
                val affirmationDuration = flows[2] as? Int ?: 0
                val affirmationCount = flows[3] as Int

                StatisticsData(
                    totalMeditationCount = meditationCount,
                    totalMeditationDuration = meditationDuration,
                    totalAffirmationDuration = affirmationDuration,
                    totalAffirmationCount = affirmationCount
                )
            }.collect { stats ->
                _uiState.update { it.copy(statistics = stats) }
            }
        }
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            achievementRepository.initializeDefaultAchievements()
            achievementRepository.getAllAchievements().collect { allAchievements ->
                // 只显示每个类别的最高等级成就
                val displayAchievements = allAchievements
                    .groupBy { it.category }
                    .mapValues { (_, achievements) ->
                        achievements.maxByOrNull { it.currentValue } ?: achievements.first()
                    }
                    .values
                    .sortedBy { it.order }

                _uiState.update { it.copy(achievements = displayAchievements) }

                // 检查是否有新解锁且未显示通知的成就
                val newlyUnlocked = achievementRepository.getNewlyUnlockedAchievements()
                    .firstOrNull()

                if (newlyUnlocked != null) {
                    _uiState.update { it.copy(newlyUnlockedAchievement = newlyUnlocked) }
                }
            }
        }
    }

    private fun loadCalendarData() {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)

            // 获取本月第一天
            cal.set(year, month, 1, 0, 0, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val startOfMonth = cal.timeInMillis

            // 获取本月最后一天
            cal.set(year, month, cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
            cal.set(Calendar.MILLISECOND, 999)
            val endOfMonth = cal.timeInMillis

            combine(
                meditationRepository.getRecordsByDateRange(startOfMonth, endOfMonth),
                affirmationRepository.getRecordsByDateRange(startOfMonth, endOfMonth)
            ) { meditationRecords, affirmationRecords ->
                // 按日期分组
                val meditationDates = meditationRecords.map { getDayOfMonth(it.createDate) }.toSet()
                val affirmationDates = affirmationRecords.filter { it.isCompleted }.map { getDayOfMonth(it.createDate) }.toSet()

                CalendarData(
                    completedDays = (meditationDates + affirmationDates).toList(),
                    meditationOnlyDays = (meditationDates - affirmationDates).toList(),
                    affirmationOnlyDays = (affirmationDates - meditationDates).toList(),
                    bothCompletedDays = (meditationDates intersect affirmationDates).toList()
                )
            }.collect { calendarData ->
                _uiState.update { it.copy(calendarData = calendarData) }
            }
        }
    }

    private fun updateAchievementProgress() {
        viewModelScope.launch {
            // 更新冥想次数成就 - 自动升级
            meditationRepository.getTotalCount().collect { count ->
                achievementRepository.updateProgressWithLevelUp("meditation_count", count)
            }
        }

        viewModelScope.launch {
            // 更新冥想时长成就 - 自动升级
            meditationRepository.getTotalDuration().collect { duration ->
                val durationValue = duration ?: 0
                achievementRepository.updateProgressWithLevelUp("meditation_duration", durationValue)
            }
        }

        viewModelScope.launch {
            // 更新积极暗示次数成就 - 自动升级
            affirmationRepository.getTotalCompletedCount().collect { count ->
                achievementRepository.updateProgressWithLevelUp("affirmation_count", count)
            }
        }

        // TODO: 连续天数成就需要更复杂的逻辑计算，后续实现
    }

    private fun getDayOfMonth(timestamp: Long): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        return cal.get(Calendar.DAY_OF_MONTH)
    }

    fun onAchievementClick(achievement: Achievement) {
        _uiState.update { it.copy(selectedAchievement = achievement) }
    }

    fun dismissAchievementDialog() {
        _uiState.update { it.copy(selectedAchievement = null) }
    }

    fun dismissUnlockAnimation() {
        val achievement = _uiState.value.newlyUnlockedAchievement
        if (achievement != null) {
            // 标记通知已显示
            viewModelScope.launch {
                achievementRepository.markNotificationShown(achievement.id)
            }
        }
        _uiState.update { it.copy(newlyUnlockedAchievement = null) }
    }
}

data class StatisticsUiState(
    val statistics: StatisticsData = StatisticsData(),
    val achievements: List<Achievement> = emptyList(),
    val calendarData: CalendarData = CalendarData(),
    val selectedAchievement: Achievement? = null,
    val newlyUnlockedAchievement: Achievement? = null
)

data class StatisticsData(
    val totalMeditationCount: Int = 0,
    val totalMeditationDuration: Int = 0, // in seconds
    val totalAffirmationDuration: Int = 0, // in seconds
    val totalAffirmationCount: Int = 0 // total affirmation count
)

data class CalendarData(
    val completedDays: List<Int> = emptyList(),
    val meditationOnlyDays: List<Int> = emptyList(),
    val affirmationOnlyDays: List<Int> = emptyList(),
    val bothCompletedDays: List<Int> = emptyList()
)
