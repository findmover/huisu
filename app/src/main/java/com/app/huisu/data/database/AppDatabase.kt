package com.app.huisu.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.huisu.data.dao.AchievementDao
import com.app.huisu.data.dao.AffirmationDao
import com.app.huisu.data.dao.AffirmationRecordDao
import com.app.huisu.data.dao.MeditationDao
import com.app.huisu.data.dao.QuickNoteDao
import com.app.huisu.data.dao.TodoCategoryDao
import com.app.huisu.data.dao.TodoItemDao
import com.app.huisu.data.dao.VideoLinkDao
import com.app.huisu.data.entity.Achievement
import com.app.huisu.data.entity.Affirmation
import com.app.huisu.data.entity.AffirmationRecord
import com.app.huisu.data.entity.MeditationRecord
import com.app.huisu.data.entity.QuickNote
import com.app.huisu.data.entity.TodoCategory
import com.app.huisu.data.entity.TodoItem
import com.app.huisu.data.entity.VideoLink

@Database(
    entities = [
        MeditationRecord::class,
        AffirmationRecord::class,
        Affirmation::class,
        VideoLink::class,
        Achievement::class,
        TodoCategory::class,
        TodoItem::class,
        QuickNote::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun meditationDao(): MeditationDao
    abstract fun affirmationRecordDao(): AffirmationRecordDao
    abstract fun affirmationDao(): AffirmationDao
    abstract fun videoLinkDao(): VideoLinkDao
    abstract fun achievementDao(): AchievementDao
    abstract fun todoCategoryDao(): TodoCategoryDao
    abstract fun todoItemDao(): TodoItemDao
    abstract fun quickNoteDao(): QuickNoteDao
}
