package com.app.huisu.data.repository

import com.app.huisu.data.dao.AchievementDao
import com.app.huisu.data.entity.Achievement
import com.app.huisu.data.entity.AchievementCategory
import com.app.huisu.data.entity.AchievementLevel
import com.app.huisu.util.CalculationUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementRepository @Inject constructor(
    private val achievementDao: AchievementDao
) {

    fun getAllAchievements(): Flow<List<Achievement>> {
        return achievementDao.getAllAchievements()
    }

    fun getUnlockedAchievements(): Flow<List<Achievement>> {
        return achievementDao.getUnlockedAchievements()
    }

    fun getAchievementByKey(key: String): Flow<Achievement?> {
        return achievementDao.getAchievementByKey(key)
    }

    suspend fun insertAchievement(achievement: Achievement): Long {
        return achievementDao.insert(achievement)
    }

    suspend fun updateAchievement(achievement: Achievement) {
        achievementDao.update(achievement)
    }

    suspend fun updateProgress(key: String, currentValue: Int) {
        val achievement = achievementDao.getAchievementByKeySync(key)
        if (achievement != null) {
            val unlocked = currentValue >= achievement.targetValue
            val updated = achievement.copy(
                currentValue = currentValue,
                unlocked = unlocked,
                unlockedDate = if (unlocked && !achievement.unlocked) System.currentTimeMillis() else achievement.unlockedDate
            )
            achievementDao.update(updated)
        }
    }

    suspend fun initializeDefaultAchievements() {
        val defaults = listOf(
            // 连续打卡 - 按连续天数递增
            Achievement(key = "streak", icon = "🔥", name = "连续打卡", description = "连续完成：7/30/100/365/1000天", category = AchievementCategory.STREAK, level = AchievementLevel.BRONZE, targetValue = 7, order = 1),

            // 冥想大师 - 按次数递增
            Achievement(key = "meditation_count", icon = "🧘", name = "冥想大师", description = "完成冥想：7/30/100/365/1000次", category = AchievementCategory.MEDITATION_COUNT, level = AchievementLevel.BRONZE, targetValue = 7, order = 2),

            // 冥想时长 - 按时长递增
            Achievement(key = "meditation_duration", icon = "⏱️", name = "冥想时长", description = "累计时长：5/20/50/100/500小时", category = AchievementCategory.MEDITATION_DURATION, level = AchievementLevel.BRONZE, targetValue = 18000, order = 3),

            // 默念达人 - 按暗示次数递增
            Achievement(key = "affirmation_count", icon = "💭", name = "默念达人", description = "完成默念：20/50/200/500/1000次", category = AchievementCategory.AFFIRMATION_COUNT, level = AchievementLevel.BRONZE, targetValue = 20, order = 4)
        )

        defaults.forEach { achievement ->
            val existing = achievementDao.getAchievementByKeySync(achievement.key)
            if (existing == null) {
                achievementDao.insert(achievement)
            }
        }
    }

    // 更新成就进度，自动升级到下一等级
    suspend fun updateProgressWithLevelUp(key: String, currentValue: Int) {
        val achievement = achievementDao.getAchievementByKeySync(key)
        if (achievement != null) {
            // 使用计算工具类获取进度
            val progress = CalculationUtils.calculateAchievementProgress(
                currentValue = currentValue,
                targetValue = achievement.targetValue,
                previousValue = achievement.currentValue
            )

            // 定义每个成就的等级目标值
            val levelTargets = when (key) {
                "streak" -> listOf(7, 30, 100, 365, 1000) // 连续打卡：7/30/100/365/1000天
                "meditation_count" -> listOf(7, 30, 100, 365, 1000) // 冥想大师：7/30/100/365/1000次
                "meditation_duration" -> listOf(18000, 72000, 180000, 360000, 1800000) // 冥想时长：5/20/50/100/500小时
                "affirmation_count" -> listOf(20, 50, 200, 500, 1000) // 默念达人：20/50/200/500/1000次
                else -> return
            }

            val levels = listOf(
                AchievementLevel.BRONZE,
                AchievementLevel.SILVER,
                AchievementLevel.GOLD,
                AchievementLevel.DIAMOND,
                AchievementLevel.LEGEND
            )

            // 找到当前应该达到的等级
            var newLevel = AchievementLevel.BRONZE // 默认青铜
            var newTarget = levelTargets[0]
            var unlocked = false
            var unlockedDate: Long? = null

            // 从最高等级往下找，找到第一个已达成的等级
            var isNewUnlock = false
            val previousLevelIndex = levels.indexOf(achievement.level)
            val previousTargetValue = achievement.targetValue

            for (i in levelTargets.indices.reversed()) {
                if (currentValue >= levelTargets[i]) {
                    // 已达成当前等级
                    newLevel = levels[i]
                    newTarget = levelTargets[i]
                    unlocked = true

                    // 检查是否是新解锁（基于目标值变化来判断）
                    val currentLevelIndex = levels.indexOf(newLevel)

                    // 新解锁的条件:
                    // 1. 之前未解锁，现在解锁了
                    // 2. 目标值发生了变化（说明达到了新的里程碑）
                    // 3. 之前已解锁，但等级提升了
                    isNewUnlock = !achievement.unlocked || // 首次解锁
                                   (unlocked && newTarget > previousTargetValue) || // 达到了更高的里程碑
                                   (achievement.unlocked && currentLevelIndex > previousLevelIndex) // 等级提升

                    unlockedDate = if (isNewUnlock) {
                        System.currentTimeMillis() // 新解锁或等级提升，更新解锁时间
                    } else {
                        achievement.unlockedDate // 保持原解锁时间
                    }
                    break
                }
            }

            // 如果连第一级都没达到
            if (!unlocked && currentValue < levelTargets[0]) {
                newLevel = AchievementLevel.BRONZE
                newTarget = levelTargets[0]
                unlocked = false
                unlockedDate = null
            }

            // 只有真正新解锁时才重置通知状态
            val updated = achievement.copy(
                currentValue = currentValue,
                level = newLevel,
                targetValue = newTarget,
                unlocked = unlocked,
                unlockedDate = unlockedDate,
                notificationShown = if (isNewUnlock) {
                    false // 新解锁的成就，标记为未显示，会触发动画
                } else {
                    achievement.notificationShown // 保持原状态，不重复显示动画
                }
            )
            achievementDao.update(updated)
        }
    }

    /**
     * 检查并返回新解锁的成就
     */
    suspend fun getNewlyUnlockedAchievements(): List<Achievement> {
        return achievementDao.getNewlyUnlockedAchievements()
    }

    /**
     * 标记成就通知为已显示
     */
    suspend fun markNotificationShown(achievementId: Long) {
        achievementDao.updateNotificationShown(achievementId, true)
    }
}
