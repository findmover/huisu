package com.app.huisu.ui.affirmation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.huisu.ui.components.GlassCard
import com.app.huisu.ui.components.InfoPill
import com.app.huisu.ui.components.PrimaryButton
import com.app.huisu.ui.components.SecondaryButton
import com.app.huisu.ui.components.StatBox
import com.app.huisu.ui.components.ZenBackground
import com.app.huisu.ui.theme.Purple667
import com.app.huisu.ui.theme.TextPrimary
import com.app.huisu.ui.theme.TextSecondary

@Composable
fun AffirmationScreen(
    viewModel: AffirmationViewModel = hiltViewModel(),
    onNavigateToTimer: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToRecords: () -> Unit,
    onNavigateToManagement: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedDuration by remember(uiState.duration) {
        mutableStateOf((uiState.duration / 60f).coerceIn(1f, 10f))
    }
    val selectedAffirmation = uiState.affirmations.firstOrNull { it.isSelected }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    ZenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(700, delayMillis = 140))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatBox(
                            title = "今日完成",
                            value = uiState.stats.todayCompleted.toString(),
                            unit = "次",
                            modifier = Modifier.weight(1f)
                        )
                        StatBox(
                            title = "本周达成",
                            value = uiState.stats.weekCompletionRate.toInt().toString(),
                            unit = "%",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatBox(
                            title = "累计时长",
                            value = (uiState.stats.totalDuration / 60).toString(),
                            unit = "分钟",
                            modifier = Modifier.weight(1f)
                        )
                        StatBox(
                            title = "本月达成",
                            value = uiState.stats.monthCompletionRate.toInt().toString(),
                            unit = "%",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(720, delayMillis = 180)) + slideInVertically(
                    animationSpec = tween(720, delayMillis = 180),
                    initialOffsetY = { 28 }
                )
            ) {
                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "当前主文案",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextSecondary
                        )
                        InfoPill(label = if (selectedAffirmation != null) "已选择" else "待设置")
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 6.dp)
                    ) {
                        Text(
                            text = selectedAffirmation?.content ?: "前往“文案管理”选择一条最适合今天的默念文案。",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary,
                            textAlign = TextAlign.Start
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SecondaryButton(
                            text = "文案管理",
                            onClick = onNavigateToManagement,
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryButton(
                            text = "提醒设置",
                            onClick = onNavigateToSettings,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(760, delayMillis = 220))
            ) {
                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "练习时长",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "${selectedDuration.toInt()} 分钟",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Purple667,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Slider(
                        value = selectedDuration,
                        onValueChange = { selectedDuration = it },
                        onValueChangeFinished = {
                            viewModel.setDuration((selectedDuration.toInt() * 60))
                        },
                        valueRange = 1f..10f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = Purple667,
                            activeTrackColor = Purple667,
                            inactiveTrackColor = Purple667.copy(alpha = 0.18f)
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "1 分钟", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text(text = "10 分钟", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }

                    PrimaryButton(
                        text = "开始练习",
                        enabled = selectedAffirmation != null,
                        onClick = {
                            selectedAffirmation?.let { affirmation ->
                                val durationInSeconds = selectedDuration.toInt() * 60
                                viewModel.startAffirmation(affirmation, durationInSeconds)
                                onNavigateToTimer()
                            }
                        }
                    )

                    SecondaryButton(
                        text = "查看记录",
                        onClick = onNavigateToRecords
                    )
                }
            }

            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}
