package com.app.huisu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val key: String, // 唯一标识符，例如 "streak_7", "meditation_100"
    val icon: String,
    val name: String,
    val description: String,
    val category: AchievementCategory,
    val level: AchievementLevel = AchievementLevel.BRONZE, // 成就等级
    val targetValue: Int, // 目标值
    val currentValue: Int = 0, // 当前进度
    val unlocked: Boolean = false,
    val unlockedDate: Long? = null,
    val notificationShown: Boolean = false, // 解锁通知是否已显示
    val order: Int = 0 // 排序顺序，同一系列的成就按等级排序
)

enum class AchievementCategory {
    MEDITATION_COUNT, // 冥想次数
    MEDITATION_DURATION, // 冥想时长
    AFFIRMATION_COUNT, // 暗示次数
    STREAK, // 连续天数
    SPECIAL // 特殊成就
}

enum class AchievementLevel(
    val displayName: String,
    val color: Long, // ARGB color
    val icon: String
) {
    BRONZE("青铜", 0xFFCD7F32, "🥉"),
    SILVER("白银", 0xFFC0C0C0, "🥈"),
    GOLD("黄金", 0xFFFFD700, "🥇"),
    DIAMOND("钻石", 0xFFB9F2FF, "💎"),
    LEGEND("传奇", 0xFFFF6B6B, "👑")
}
