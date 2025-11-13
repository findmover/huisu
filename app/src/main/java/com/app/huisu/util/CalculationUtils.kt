package com.app.huisu.util

import com.app.huisu.data.entity.AffirmationRecord
import com.app.huisu.data.entity.MeditationRecord
import com.app.huisu.data.entity.TodoItem
import java.util.*

/**
 * 统一的计算函数工具类
 * 用于动态计算应用中的各种统计数据
 */
object CalculationUtils {

    /**
     * 计算默念统计数据
     */
    data class AffirmationStats(
        val todayCompleted: Int,
        val weekCompleted: Int,
        val monthCompleted: Int,
        val totalCompleted: Int,
        val weekCompletionRate: Float,
        val monthCompletionRate: Float,
        val totalDuration: Long, // 总计时长（秒）
        val averageDuration: Float, // 平均时长（秒）
        val currentStreak: Int, // 当前连续天数
        val longestStreak: Int // 最长连续天数
    )

    /**
     * 计算冥想统计数据
     */
    data class MeditationStats(
        val todayCount: Int,
        val weekCount: Int,
        val monthCount: Int,
        val totalCount: Int,
        val weekCompletionRate: Float,
        val monthCompletionRate: Float,
        val totalDuration: Long, // 总计时长（秒）
        val averageDuration: Float, // 平均时长（秒）
        val currentStreak: Int, // 当前连续天数
        val longestStreak: Int // 最长连续天数
    )

    /**
     * 计算TODO统计数据
     */
    data class TodoStats(
        val totalCount: Int,
        val completedCount: Int,
        val pendingCount: Int,
        val overdueCount: Int,
        val overallCompletionRate: Float,
        val categoryCompletionRates: Map<Long, Float>,
        val todayCompleted: Int,
        val todayCreated: Int,
        val weeklyTrend: Float // 本周完成率趋势
    )

    /**
     * 计算默念相关统计数据
     * @param allRecords 所有默念记录
     * @param targetWeekDays 目标每周天数（默认7天）
     * @param targetMonthDays 目标每月天数（默认30天）
     * @param minValidDuration 最小有效时长（秒），默认120秒（2分钟）
     * @param targetWeekMinutes 每周目标时长（分钟），默认42分钟
     */
    fun calculateAffirmationStats(
        allRecords: List<AffirmationRecord>,
        targetWeekDays: Int = 7,
        targetMonthDays: Int = 30,
        minValidDuration: Int = 120,
        targetWeekMinutes: Int = 42
    ): AffirmationStats {
        val now = Calendar.getInstance()
        val todayStart = getTodayStart(now)
        val weekStart = getWeekStart(now)
        val monthStart = getMonthStart(now)

        // 过滤有效记录（时长>=2分钟）
        val validRecords = allRecords.filter { it.duration >= minValidDuration }

        val todayRecords = validRecords.filter { it.createDate >= todayStart }
        val weekRecords = validRecords.filter { it.createDate >= weekStart }
        val monthRecords = validRecords.filter { it.createDate >= monthStart }

        val todayCompleted = todayRecords.size
        val weekCompleted = weekRecords.size
        val monthCompleted = monthRecords.size
        val totalCompleted = validRecords.size

        // 计算本周累计时长（秒）
        val weekTotalDuration = weekRecords.sumOf { it.duration.toLong() }
        // 转换为分钟
        val weekTotalMinutes = weekTotalDuration / 60
        // 基于时长的完成率：实际时长 / 目标时长 * 100
        val weekCompletionRate = calculateCompletionRate(weekTotalMinutes.toInt(), targetWeekMinutes)

        // 月完成率仍然基于天数
        val monthCompletionRate = calculateCompletionRate(monthCompleted, targetMonthDays)

        val totalDuration = validRecords.sumOf { it.duration.toLong() }
        val averageDuration = if (totalCompleted > 0) totalDuration.toFloat() / totalCompleted else 0f

        val (currentStreak, longestStreak) = calculateStreaks(validRecords)

        return AffirmationStats(
            todayCompleted = todayCompleted,
            weekCompleted = weekCompleted,
            monthCompleted = monthCompleted,
            totalCompleted = totalCompleted,
            weekCompletionRate = weekCompletionRate,
            monthCompletionRate = monthCompletionRate,
            totalDuration = totalDuration,
            averageDuration = averageDuration,
            currentStreak = currentStreak,
            longestStreak = longestStreak
        )
    }

    /**
     * 计算冥想相关统计数据
     */
    fun calculateMeditationStats(
        allRecords: List<MeditationRecord>,
        targetWeekDays: Int = 7,
        targetMonthDays: Int = 30
    ): MeditationStats {
        val now = Calendar.getInstance()
        val todayStart = getTodayStart(now)
        val weekStart = getWeekStart(now)
        val monthStart = getMonthStart(now)

        val todayRecords = allRecords.filter { it.createDate >= todayStart }
        val weekRecords = allRecords.filter { it.createDate >= weekStart }
        val monthRecords = allRecords.filter { it.createDate >= monthStart }

        val todayCount = todayRecords.size
        val weekCount = weekRecords.size
        val monthCount = monthRecords.size
        val totalCount = allRecords.size

        val weekCompletionRate = calculateCompletionRate(weekCount, targetWeekDays)
        val monthCompletionRate = calculateCompletionRate(monthCount, targetMonthDays)

        val totalDuration = allRecords.sumOf { it.duration.toLong() }
        val averageDuration = if (totalCount > 0) totalDuration.toFloat() / totalCount else 0f

        val (currentStreak, longestStreak) = calculateMeditationStreaks(allRecords)

        return MeditationStats(
            todayCount = todayCount,
            weekCount = weekCount,
            monthCount = monthCount,
            totalCount = totalCount,
            weekCompletionRate = weekCompletionRate,
            monthCompletionRate = monthCompletionRate,
            totalDuration = totalDuration,
            averageDuration = averageDuration,
            currentStreak = currentStreak,
            longestStreak = longestStreak
        )
    }

    /**
     * 计算TODO相关统计数据
     */
    fun calculateTodoStats(
        allTodos: List<TodoItem>,
        categoryId: Long? = null
    ): TodoStats {
        val filteredTodos = if (categoryId != null) {
            allTodos.filter { it.categoryId == categoryId }
        } else {
            allTodos
        }

        val now = System.currentTimeMillis()
        val todayStart = getTodayStart(Calendar.getInstance())

        val completedTodos = filteredTodos.filter { it.isCompleted }
        val pendingTodos = filteredTodos.filter { !it.isCompleted }
        val overdueTodos = pendingTodos.filter {
            it.dueDate != null && it.dueDate!! < now
        }

        val totalCount = filteredTodos.size
        val completedCount = completedTodos.size
        val pendingCount = pendingTodos.size
        val overdueCount = overdueTodos.size

        val overallCompletionRate = calculateCompletionRate(completedCount, totalCount)

        // 计算各分类完成率
        val categoryCompletionRates = allTodos
            .groupBy { it.categoryId }
            .mapValues { (_, todos) ->
                val completed = todos.count { it.isCompleted }
                calculateCompletionRate(completed, todos.size)
            }

        val todayCompleted = completedTodos.count { it.completedAt != null && it.completedAt!! >= todayStart }
        val todayCreated = filteredTodos.count { it.createdAt >= todayStart }

        // 计算本周趋势
        val weeklyTrend = calculateWeeklyTrend(completedTodos)

        return TodoStats(
            totalCount = totalCount,
            completedCount = completedCount,
            pendingCount = pendingCount,
            overdueCount = overdueCount,
            overallCompletionRate = overallCompletionRate,
            categoryCompletionRates = categoryCompletionRates,
            todayCompleted = todayCompleted,
            todayCreated = todayCreated,
            weeklyTrend = weeklyTrend
        )
    }

    /**
     * 计算成就进度
     */
    data class AchievementProgress(
        val currentValue: Int,
        val targetValue: Int,
        val progressPercentage: Float,
        val isCompleted: Boolean,
        val justCompleted: Boolean = false // 是否刚刚达成
    )

    /**
     * 计算成就进度
     * @param currentValue 当前值
     * @param targetValue 目标值
     * @param previousValue 之前的值，用于判断是否刚刚达成
     */
    fun calculateAchievementProgress(
        currentValue: Int,
        targetValue: Int,
        previousValue: Int = currentValue
    ): AchievementProgress {
        val progressPercentage = calculateCompletionRate(currentValue, targetValue)
        val isCompleted = currentValue >= targetValue
        val justCompleted = isCompleted && previousValue < targetValue

        return AchievementProgress(
            currentValue = currentValue,
            targetValue = targetValue,
            progressPercentage = progressPercentage,
            isCompleted = isCompleted,
            justCompleted = justCompleted
        )
    }

    // 私有辅助函数

    private fun calculateCompletionRate(completed: Int, total: Int): Float {
        return if (total > 0) (completed.toFloat() / total) * 100f else 0f
    }

    private fun getTodayStart(calendar: Calendar): Long {
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun getWeekStart(calendar: Calendar): Long {
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return getTodayStart(calendar)
    }

    private fun getMonthStart(calendar: Calendar): Long {
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return getTodayStart(calendar)
    }

    private fun calculateStreaks(records: List<AffirmationRecord>): Pair<Int, Int> {
        if (records.isEmpty()) return 0 to 0

        val sortedRecords = records.sortedBy { it.createDate }
        val dateSet = sortedRecords.map {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.createDate
            "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.DAY_OF_YEAR)}"
        }.toSet()

        var currentStreak = 0
        var longestStreak = 0
        var tempStreak = 0

        val today = Calendar.getInstance()
        val todayString = "${today.get(Calendar.YEAR)}-${today.get(Calendar.DAY_OF_YEAR)}"

        // 检查今天是否有记录
        if (dateSet.contains(todayString)) {
            currentStreak = 1
            tempStreak = 1
        }

        // 检查连续天数
        var checkDate = Calendar.getInstance() as Calendar
        checkDate.add(Calendar.DAY_OF_YEAR, -1)

        while (true) {
            val checkString = "${checkDate.get(Calendar.YEAR)}-${checkDate.get(Calendar.DAY_OF_YEAR)}"
            if (dateSet.contains(checkString)) {
                tempStreak++
                if (dateSet.contains(todayString) || checkDate.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR)) {
                    currentStreak = tempStreak
                }
            } else {
                break
            }
            checkDate.add(Calendar.DAY_OF_YEAR, -1)
        }

        longestStreak = tempStreak

        return currentStreak to longestStreak
    }

    private fun calculateMeditationStreaks(records: List<MeditationRecord>): Pair<Int, Int> {
        if (records.isEmpty()) return 0 to 0

        val sortedRecords = records.sortedBy { it.createDate }
        val dateSet = sortedRecords.map {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.createDate
            "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.DAY_OF_YEAR)}"
        }.toSet()

        var currentStreak = 0
        var longestStreak = 0
        var tempStreak = 0

        val today = Calendar.getInstance()
        val todayString = "${today.get(Calendar.YEAR)}-${today.get(Calendar.DAY_OF_YEAR)}"

        if (dateSet.contains(todayString)) {
            currentStreak = 1
            tempStreak = 1
        }

        var checkDate = Calendar.getInstance() as Calendar
        checkDate.add(Calendar.DAY_OF_YEAR, -1)

        while (true) {
            val checkString = "${checkDate.get(Calendar.YEAR)}-${checkDate.get(Calendar.DAY_OF_YEAR)}"
            if (dateSet.contains(checkString)) {
                tempStreak++
                if (dateSet.contains(todayString) || checkDate.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR)) {
                    currentStreak = tempStreak
                }
            } else {
                break
            }
            checkDate.add(Calendar.DAY_OF_YEAR, -1)
        }

        longestStreak = tempStreak

        return currentStreak to longestStreak
    }

    private fun calculateWeeklyTrend(completedTodos: List<TodoItem>): Float {
        val now = Calendar.getInstance()
        val weekStart = getWeekStart(now)

        val thisWeekCompleted = completedTodos.filter {
            it.completedAt != null && it.completedAt!! >= weekStart
        }.size

        // 简化的趋势计算，实际应用中可能需要更复杂的逻辑
        return thisWeekCompleted.toFloat() / 7f
    }

    /**
     * 格式化时长
     */
    fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return when {
            hours > 0 -> "${hours}小时${minutes}分钟"
            minutes > 0 -> "${minutes}分钟${remainingSeconds}秒"
            else -> "${remainingSeconds}秒"
        }
    }

    /**
     * 格式化完成率
     */
    fun formatCompletionRate(rate: Float): String {
        return "${rate.toInt()}%"
    }
}