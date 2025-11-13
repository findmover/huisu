package com.app.huisu.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class AppPreferences(private val context: Context) {

    companion object {
        val AFFIRMATION_DURATION = intPreferencesKey("affirmation_duration")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val MORNING_REMINDER_TIME = stringPreferencesKey("morning_reminder_time")
        val NOON_REMINDER_TIME = stringPreferencesKey("noon_reminder_time")
        val EVENING_REMINDER_TIME = stringPreferencesKey("evening_reminder_time")
    }

    val affirmationDuration: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[AFFIRMATION_DURATION] ?: 180 } // 默认3分钟

    val themeMode: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[THEME_MODE] ?: "system" }

    val morningReminderTime: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[MORNING_REMINDER_TIME] ?: "08:00" }

    val noonReminderTime: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[NOON_REMINDER_TIME] ?: "12:30" }

    val eveningReminderTime: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[EVENING_REMINDER_TIME] ?: "20:00" }

    suspend fun setAffirmationDuration(duration: Int) {
        context.dataStore.edit { preferences ->
            preferences[AFFIRMATION_DURATION] = duration
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }

    suspend fun setMorningReminderTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[MORNING_REMINDER_TIME] = time
        }
    }

    suspend fun setNoonReminderTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[NOON_REMINDER_TIME] = time
        }
    }

    suspend fun setEveningReminderTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[EVENING_REMINDER_TIME] = time
        }
    }
}
