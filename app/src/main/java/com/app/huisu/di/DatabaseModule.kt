package com.app.huisu.di

import android.content.Context
import androidx.room.Room
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

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "huisu_database"
        )
            .fallbackToDestructiveMigration() // 简单的迁移策略：删除并重建
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
    fun provideAppPreferences(@ApplicationContext context: Context): AppPreferences {
        return AppPreferences(context)
    }
}
