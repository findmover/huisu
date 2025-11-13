package com.app.huisu.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.app.huisu.HuiSuApplication
import com.app.huisu.MainActivity
import com.app.huisu.R
import com.app.huisu.data.entity.MeditationRecord
import com.app.huisu.data.repository.MeditationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class MeditationTimerService : Service() {

    @Inject
    lateinit var meditationRepository: MeditationRepository

    private val binder = TimerBinder()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var startTime: Long = 0
    private var pausedTime: Long = 0
    private var totalPausedDuration: Long = 0
    private var currentVideoLink: String = ""

    private val _timerState = MutableStateFlow(ServiceTimerState())
    val timerState: StateFlow<ServiceTimerState> = _timerState.asStateFlow()

    private var timerJob: Job? = null
    private var notificationManager: NotificationManager? = null

    inner class TimerBinder : Binder() {
        fun getService(): MeditationTimerService = this@MeditationTimerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                currentVideoLink = intent.getStringExtra(EXTRA_VIDEO_LINK) ?: ""
                startTimer()
            }
            ACTION_STOP -> stopTimer(save = true)
            ACTION_CANCEL -> stopTimer(save = false)
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
        }
        return START_NOT_STICKY
    }

    private fun startTimer() {
        startTime = System.currentTimeMillis()
        totalPausedDuration = 0
        pausedTime = 0

        _timerState.value = ServiceTimerState(
            isRunning = true,
            isPaused = false,
            elapsedSeconds = 0,
            startTime = startTime
        )

        startForeground(NOTIFICATION_ID, createNotification(0))
        startTimerLoop()
    }

    private fun pauseTimer() {
        if (_timerState.value.isRunning && !_timerState.value.isPaused) {
            pausedTime = System.currentTimeMillis()
            _timerState.value = _timerState.value.copy(isPaused = true)
            timerJob?.cancel()
        }
    }

    private fun resumeTimer() {
        if (_timerState.value.isRunning && _timerState.value.isPaused) {
            if (pausedTime > 0) {
                totalPausedDuration += (System.currentTimeMillis() - pausedTime)
            }
            _timerState.value = _timerState.value.copy(isPaused = false)
            startTimerLoop()
        }
    }

    private fun stopTimer(save: Boolean) {
        timerJob?.cancel()

        val finalState = _timerState.value.copy(
            isRunning = false,
            shouldSave = save
        )
        _timerState.value = finalState

        // 如果需要保存记录，直接在Service中保存
        if (save && finalState.elapsedSeconds > 0) {
            serviceScope.launch {
                try {
                    val record = MeditationRecord(
                        startTime = startTime,
                        endTime = System.currentTimeMillis(),
                        duration = finalState.elapsedSeconds,
                        videoLink = currentVideoLink,
                        createDate = getTodayStartMillis()
                    )
                    meditationRepository.insertRecord(record)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // 广播结束事件（用于通知UI更新状态）
        val intent = Intent(ACTION_TIMER_FINISHED).apply {
            putExtra(EXTRA_ELAPSED_SECONDS, finalState.elapsedSeconds)
            putExtra(EXTRA_START_TIME, startTime)
            putExtra(EXTRA_SHOULD_SAVE, save)
        }
        sendBroadcast(intent)

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun getTodayStartMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun startTimerLoop() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (isActive && _timerState.value.isRunning && !_timerState.value.isPaused) {
                delay(1000)

                val elapsed = ((System.currentTimeMillis() - startTime - totalPausedDuration) / 1000).toInt()
                _timerState.value = _timerState.value.copy(elapsedSeconds = elapsed)

                // 更新通知
                notificationManager?.notify(NOTIFICATION_ID, createNotification(elapsed))
            }
        }
    }

    private fun createNotification(elapsedSeconds: Int): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val hours = elapsedSeconds / 3600
        val minutes = (elapsedSeconds % 3600) / 60
        val seconds = elapsedSeconds % 60
        val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)

        return NotificationCompat.Builder(this, HuiSuApplication.CHANNEL_MEDITATION)
            .setContentTitle(getString(R.string.notification_meditation_title))
            .setContentText(getString(R.string.notification_meditation_text, timeString))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        serviceScope.cancel()
    }

    companion object {
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.app.huisu.MEDITATION_START"
        const val ACTION_STOP = "com.app.huisu.MEDITATION_STOP"
        const val ACTION_CANCEL = "com.app.huisu.MEDITATION_CANCEL"
        const val ACTION_PAUSE = "com.app.huisu.MEDITATION_PAUSE"
        const val ACTION_RESUME = "com.app.huisu.MEDITATION_RESUME"
        const val ACTION_TIMER_FINISHED = "com.app.huisu.MEDITATION_TIMER_FINISHED"
        const val EXTRA_ELAPSED_SECONDS = "elapsed_seconds"
        const val EXTRA_START_TIME = "start_time"
        const val EXTRA_SHOULD_SAVE = "should_save"
        const val EXTRA_VIDEO_LINK = "video_link"
    }
}

data class ServiceTimerState(
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val elapsedSeconds: Int = 0,
    val startTime: Long = 0,
    val shouldSave: Boolean = true
)
