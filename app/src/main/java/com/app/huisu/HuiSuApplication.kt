package com.app.huisu

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
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

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        initializeDefaultData()
    }

    private fun initializeDefaultData() {
        applicationScope.launch {
            // 检查是否已有视频链接，如果没有则初始化默认链接
            val existingLinks = videoLinkRepository.getAllVideoLinks().first()
            if (existingLinks.isEmpty()) {
                videoLinkRepository.initializeDefaultLinks()
            }
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
                "积极暗示提醒",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "定时提醒进行积极暗示"
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
