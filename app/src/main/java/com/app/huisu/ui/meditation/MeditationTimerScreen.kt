package com.app.huisu.ui.meditation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.app.huisu.ui.components.GlassCard
import com.app.huisu.ui.components.InfoPill
import com.app.huisu.ui.components.PrimaryButton
import com.app.huisu.ui.components.SecondaryButton
import com.app.huisu.ui.components.ZenBackground
import com.app.huisu.ui.theme.Mist400
import com.app.huisu.ui.theme.Purple667
import com.app.huisu.ui.theme.Sage400
import com.app.huisu.ui.theme.TextPrimary
import com.app.huisu.ui.theme.TextSecondary
import com.app.huisu.ui.theme.TextTertiary

@Composable
fun MeditationTimerScreen(
    viewModel: MeditationViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val timerState by viewModel.timerState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.ensureServiceBound()
    }

    DisposableEffect(lifecycleOwner, timerState.isRunning, timerState.isPaused) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && timerState.isRunning && !timerState.isPaused) {
                viewModel.pauseMeditation()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ZenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (timerState.isPaused) "冥想已暂停" else "冥想进行中",
                            style = MaterialTheme.typography.displaySmall,
                            color = TextPrimary
                        )
                        Text(
                            text = if (timerState.isPaused) {
                                "先把呼吸重新找回来，再继续这段安静的时间。"
                            } else {
                                "计时会继续留在这里，专注只需要慢慢沉下去。"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }

                    InfoPill(
                        label = if (timerState.isPaused) "已暂停" else "进行中",
                        backgroundColor = if (timerState.isPaused) {
                            Mist400.copy(alpha = 0.18f)
                        } else {
                            Sage400.copy(alpha = 0.18f)
                        },
                        contentColor = TextPrimary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoPill(
                        label = shortLabel(uiState.currentVideoLink?.title ?: "未设置冥想视频"),
                        backgroundColor = Color.White.copy(alpha = 0.72f),
                        contentColor = TextSecondary
                    )
                    InfoPill(
                        label = "已进行 ${formatElapsedTime(timerState.elapsedSeconds)}",
                        backgroundColor = Mist400.copy(alpha = 0.16f),
                        contentColor = TextSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                MeditationTimerOrb(
                    elapsedSeconds = timerState.elapsedSeconds,
                    isPaused = timerState.isPaused
                )
            }

            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TimerMetaCard(
                        title = "状态",
                        value = if (timerState.isPaused) "暂停中" else "沉浸中",
                        modifier = Modifier.weight(1f)
                    )
                    TimerMetaCard(
                        title = "时长",
                        value = formatElapsedTime(timerState.elapsedSeconds),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SecondaryButton(
                        text = if (timerState.isPaused) "继续" else "暂停",
                        onClick = {
                            if (timerState.isPaused) {
                                viewModel.resumeMeditation()
                            } else {
                                viewModel.pauseMeditation()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    PrimaryButton(
                        text = "完成",
                        onClick = {
                            viewModel.endMeditation()
                            onBack()
                        },
                        modifier = Modifier.weight(1f)
                    )
                    SecondaryButton(
                        text = "取消",
                        onClick = { showCancelDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "取消这次冥想？",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "取消后这次记录不会保存，你可以稍后重新开始。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.cancelMeditation()
                        showCancelDialog = false
                        onBack()
                    }
                ) {
                    Text("确认取消", color = Purple667)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("继续冥想", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun MeditationTimerOrb(
    elapsedSeconds: Int,
    isPaused: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "meditation_timer_orb")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isPaused) 3400 else 2600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "meditation_timer_pulse"
    )

    Box(contentAlignment = Alignment.Center) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size((168 + index * 18).dp * pulse)
                    .background(
                        color = Purple667.copy(alpha = 0.07f - index * 0.015f),
                        shape = CircleShape
                    )
            )
        }

        Box(
            modifier = Modifier
                .size(192.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.98f),
                            Sage400.copy(alpha = 0.82f),
                            Mist400.copy(alpha = 0.68f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = formatElapsedTime(elapsedSeconds),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    color = TextPrimary
                )
                Text(
                    text = "已静坐",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = if (isPaused) "暂停后也可以慢慢回来" else "让呼吸轻一点，节奏慢一点",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun TimerMetaCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.64f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = TextTertiary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun formatElapsedTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}

private fun shortLabel(text: String, maxLength: Int = 12): String {
    return if (text.length <= maxLength) text else text.take(maxLength) + "…"
}
