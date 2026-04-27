package com.app.huisu

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.app.huisu.data.preferences.AppPreferences
import com.app.huisu.data.repository.AffirmationRepository
import com.app.huisu.data.repository.CloudSyncRepository
import com.app.huisu.data.repository.VideoLinkRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class HuiSuApplication : Application() {

    @Inject
    lateinit var videoLinkRepository: VideoLinkRepository

    @Inject
    lateinit var affirmationRepository: AffirmationRepository

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var cloudSyncRepository: CloudSyncRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        applicationScope.launch {
            runCatching { cloudSyncRepository.syncOnAppStart() }
            initializeDefaultData()
        }
    }

    private suspend fun initializeDefaultData() {
        val existingLinks = videoLinkRepository.getAllVideoLinks().first()
        if (existingLinks.isEmpty()) {
            videoLinkRepository.initializeDefaultLinks()
        }

        if (!appPreferences.affirmationsInitialized.first()) {
            affirmationRepository.initializeDefaultAffirmations()
            appPreferences.setAffirmationsInitialized(true)
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val meditationChannel = NotificationChannel(
                CHANNEL_MEDITATION,
                "冥想计时",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "显示冥想计时进度"
            }

            val affirmationChannel = NotificationChannel(
                CHANNEL_AFFIRMATION,
                "默念提醒",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "定时提醒进行默念"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(meditationChannel)
            notificationManager.createNotificationChannel(affirmationChannel)
        }
    }

    companion object {
        const val CHANNEL_MEDITATION = "meditation_channel"
        const val CHANNEL_AFFIRMATION = "affirmation_channel"
    }
}
