package com.app.huisu.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.huisu.data.dao.*
import com.app.huisu.data.entity.*

@Database(
    entities = [
        MeditationRecord::class,
        AffirmationRecord::class,
        Affirmation::class,
        VideoLink::class,
        Achievement::class,
        TodoCategory::class,
        TodoItem::class
    ],
    version = 7,  // 升级到版本6，添���TODO功能
    exportSchema = false
)
// @TypeConverters(Converters::class)  // 移除 - 不再需要类型转换器
abstract class AppDatabase : RoomDatabase() {
    abstract fun meditationDao(): MeditationDao
    abstract fun affirmationRecordDao(): AffirmationRecordDao
    abstract fun affirmationDao(): AffirmationDao
    abstract fun videoLinkDao(): VideoLinkDao
    abstract fun achievementDao(): AchievementDao
    abstract fun todoCategoryDao(): TodoCategoryDao
    abstract fun todoItemDao(): TodoItemDao
}
