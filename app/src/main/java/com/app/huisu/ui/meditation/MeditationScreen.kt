package com.app.huisu.ui.meditation

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.huisu.ui.components.GlassCard
import com.app.huisu.ui.components.InfoPill
import com.app.huisu.ui.components.PrimaryButton
import com.app.huisu.ui.components.SecondaryButton
import com.app.huisu.ui.components.StatBox
import com.app.huisu.ui.components.ZenBackground
import com.app.huisu.ui.theme.Mist400
import com.app.huisu.ui.theme.Purple667
import com.app.huisu.ui.theme.Sage400
import com.app.huisu.ui.theme.TextPrimary
import com.app.huisu.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MeditationScreen(
    viewModel: MeditationViewModel = hiltViewModel(),
    onNavigateToTimer: () -> Unit,
    onNavigateToVideoSettings: () -> Unit,
    onNavigateToRecords: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    ZenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 18.dp)
                .padding(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(650, delayMillis = 80)) + slideInVertically(
                    animationSpec = tween(650, delayMillis = 80),
                    initialOffsetY = { 36 }
                )
            ) {
                GlassCard {
                    Text(
                        text = "今日冥想",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary
                    )

                    MeditationFocusOrb(minutes = uiState.stats.todayDuration / 60)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        InfoPill(
                            label = uiState.currentVideoLink?.title ?: "还没有默认视频"
                        )
                    }

                    PrimaryButton(
                        text = "开始冥想",
                        onClick = {
                            viewModel.startMeditation()

                            val videoLink = uiState.currentVideoLink?.link
                            if (!videoLink.isNullOrEmpty()) {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoLink))
                                    intent.setPackage("tv.danmaku.bili")
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    try {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(videoLink)))
                                    } catch (_: Exception) {
                                    }
                                }
                            }

                            scope.launch {
                                delay(300)
                                onNavigateToTimer()
                            }
                        }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SecondaryButton(
                            text = "视频设置",
                            onClick = onNavigateToVideoSettings,
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryButton(
                            text = "练习记录",
                            onClick = onNavigateToRecords,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(700, delayMillis = 160))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatBox(
                            title = "本周静心",
                            value = (uiState.stats.weekDuration / 60).toString(),
                            unit = "分钟",
                            modifier = Modifier.weight(1f)
                        )
                        StatBox(
                            title = "本月累计",
                            value = (uiState.stats.monthDuration / 60).toString(),
                            unit = "分钟",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatBox(
                            title = "练习次数",
                            value = uiState.stats.totalCount.toString(),
                            unit = "次",
                            modifier = Modifier.weight(1f)
                        )
                        StatBox(
                            title = "总时长",
                            value = String.format("%.1f", uiState.stats.totalDuration / 3600f),
                            unit = "小时",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MeditationFocusOrb(minutes: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "meditation_orb")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size((170 + index * 22).dp * pulse)
                    .background(
                        color = Purple667.copy(alpha = 0.07f - index * 0.015f),
                        shape = CircleShape
                    )
            )
        }

        Box(
            modifier = Modifier
                .size(188.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.96f),
                            Sage400.copy(alpha = 0.85f),
                            Mist400.copy(alpha = 0.72f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = minutes.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "分钟",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "让呼吸和节奏慢下来",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
