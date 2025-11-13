package com.app.huisu.ui.meditation

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.huisu.data.entity.MeditationRecord
import com.app.huisu.data.entity.VideoLink
import com.app.huisu.data.repository.MeditationRepository
import com.app.huisu.data.repository.VideoLinkRepository
import com.app.huisu.service.MeditationTimerService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeditationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val meditationRepository: MeditationRepository,
    private val videoLinkRepository: VideoLinkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MeditationUiState())
    val uiState: StateFlow<MeditationUiState> = _uiState.asStateFlow()

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private var timerService: MeditationTimerService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MeditationTimerService.TimerBinder
            timerService = binder.getService()
            isBound = true

            // 订阅Service的状态
            viewModelScope.launch {
                timerService?.timerState?.collect { serviceState ->
                    _timerState.update {
                        it.copy(
                            isRunning = serviceState.isRunning,
                            isPaused = serviceState.isPaused,
                            elapsedSeconds = serviceState.elapsedSeconds,
                            startTime = serviceState.startTime
                        )
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            timerService = null
            isBound = false
        }
    }

    private val timerFinishedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // 只用于重置UI状态，记录保存由Service处理
            _timerState.update { TimerState() }
            unbindService()
        }
    }

    init {
        loadStatistics()
        loadDefaultVideoLink()
        registerTimerFinishedReceiver()
    }

    private fun registerTimerFinishedReceiver() {
        val filter = IntentFilter(MeditationTimerService.ACTION_TIMER_FINISHED)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(timerFinishedReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(timerFinishedReceiver, filter)
        }
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            combine(
                meditationRepository.getTodayDuration(),
                meditationRepository.getTodayCount(),
                meditationRepository.getThisWeekDuration(),
                meditationRepository.getThisWeekCount(),
                meditationRepository.getThisMonthDuration(),
                meditationRepository.getThisMonthCount(),
                meditationRepository.getTotalCount(),
                meditationRepository.getTotalDuration()
            ) { flows ->
                val todayDuration = flows[0] as? Int
                val todayCount = flows[1] as Int
                val weekDuration = flows[2] as? Int
                val weekCount = flows[3] as Int
                val monthDuration = flows[4] as? Int
                val monthCount = flows[5] as Int
                val totalCount = flows[6] as Int
                val totalDuration = flows[7] as? Int

                MeditationStats(
                    todayDuration = todayDuration ?: 0,
                    todayCount = todayCount,
                    weekDuration = weekDuration ?: 0,
                    weekCount = weekCount,
                    monthDuration = monthDuration ?: 0,
                    monthCount = monthCount,
                    totalCount = totalCount,
                    totalDuration = totalDuration ?: 0
                )
            }.collect { stats ->
                _uiState.update { it.copy(stats = stats) }
            }
        }
    }

    private fun loadDefaultVideoLink() {
        viewModelScope.launch {
            videoLinkRepository.getDefaultVideoLink().collect { link ->
                _uiState.update { it.copy(currentVideoLink = link) }
            }
        }
    }

    fun startMeditation() {
        val intent = Intent(context, MeditationTimerService::class.java).apply {
            action = MeditationTimerService.ACTION_START
            putExtra(MeditationTimerService.EXTRA_VIDEO_LINK, _uiState.value.currentVideoLink?.link ?: "")
        }
        context.startForegroundService(intent)

        // 绑定Service以获取实时状态
        bindToService()
    }

    fun ensureServiceBound() {
        // 如果还没绑定，则绑定到服务
        if (!isBound) {
            bindToService()
        }
    }

    private fun bindToService() {
        if (!isBound) {
            val bindIntent = Intent(context, MeditationTimerService::class.java)
            context.bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun pauseMeditation() {
        val intent = Intent(context, MeditationTimerService::class.java).apply {
            action = MeditationTimerService.ACTION_PAUSE
        }
        context.startService(intent)
    }

    fun resumeMeditation() {
        val intent = Intent(context, MeditationTimerService::class.java).apply {
            action = MeditationTimerService.ACTION_RESUME
        }
        context.startService(intent)
    }

    fun endMeditation() {
        val intent = Intent(context, MeditationTimerService::class.java).apply {
            action = MeditationTimerService.ACTION_STOP
        }
        context.startService(intent)
    }

    fun cancelMeditation() {
        val intent = Intent(context, MeditationTimerService::class.java).apply {
            action = MeditationTimerService.ACTION_CANCEL
        }
        context.startService(intent)
    }

    private fun unbindService() {
        if (isBound) {
            try {
                context.unbindService(serviceConnection)
                isBound = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            context.unregisterReceiver(timerFinishedReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        unbindService()
    }
}

data class MeditationUiState(
    val stats: MeditationStats = MeditationStats(),
    val currentVideoLink: VideoLink? = null
)

data class MeditationStats(
    val todayDuration: Int = 0, // in seconds
    val todayCount: Int = 0,
    val weekDuration: Int = 0,
    val weekCount: Int = 0,
    val monthDuration: Int = 0,
    val monthCount: Int = 0,
    val totalCount: Int = 0,
    val totalDuration: Int = 0
)

data class TimerState(
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val startTime: Long = 0,
    val elapsedSeconds: Int = 0
)
