package com.app.huisu.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.huisu.data.dao.*
import com.app.huisu.data.database.AppDatabase
import com.app.huisu.data.preferences.AppPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `quick_note_images` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `noteId` INTEGER NOT NULL,
                    `fileName` TEXT NOT NULL,
                    `mimeType` TEXT NOT NULL,
                    `dataBase64` TEXT NOT NULL,
                    `sizeBytes` INTEGER NOT NULL,
                    `status` TEXT NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "huisu_database"
        )
            .addMigrations(MIGRATION_8_9)
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()
    }

    @Provides
    @Singleton
    fun provideMeditationDao(database: AppDatabase): MeditationDao {
        return database.meditationDao()
    }

    @Provides
    @Singleton
    fun provideAffirmationRecordDao(database: AppDatabase): AffirmationRecordDao {
        return database.affirmationRecordDao()
    }

    @Provides
    @Singleton
    fun provideAffirmationDao(database: AppDatabase): AffirmationDao {
        return database.affirmationDao()
    }

    @Provides
    @Singleton
    fun provideVideoLinkDao(database: AppDatabase): VideoLinkDao {
        return database.videoLinkDao()
    }

    @Provides
    @Singleton
    fun provideAchievementDao(database: AppDatabase): AchievementDao {
        return database.achievementDao()
    }

    @Provides
    @Singleton
    fun provideTodoCategoryDao(database: AppDatabase): TodoCategoryDao {
        return database.todoCategoryDao()
    }

    @Provides
    @Singleton
    fun provideTodoItemDao(database: AppDatabase): TodoItemDao {
        return database.todoItemDao()
    }

    @Provides
    @Singleton
    fun provideQuickNoteDao(database: AppDatabase): QuickNoteDao {
        return database.quickNoteDao()
    }

    @Provides
    @Singleton
    fun provideQuickNoteImageDao(database: AppDatabase): QuickNoteImageDao {
        return database.quickNoteImageDao()
    }

    @Provides
    @Singleton
    fun provideAppPreferences(@ApplicationContext context: Context): AppPreferences {
        return AppPreferences(context)
    }
}
