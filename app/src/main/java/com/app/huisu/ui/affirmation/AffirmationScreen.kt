package com.app.huisu.ui.affirmation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.huisu.R
import com.app.huisu.data.entity.Affirmation
import com.app.huisu.ui.components.PrimaryButton
import com.app.huisu.ui.components.SecondaryButton
import com.app.huisu.ui.components.StatBox
import com.app.huisu.ui.components.StatsCard
import com.app.huisu.ui.theme.PinkF09
import com.app.huisu.ui.theme.PinkF55

@Composable
fun AffirmationScreen(
    viewModel: AffirmationViewModel = hiltViewModel(),
    onNavigateToTimer: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToRecords: () -> Unit,
    onNavigateToManagement: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedDuration by remember { mutableStateOf(3f) } // 改为分钟，默认3分钟

    // 获取当前选中的暗示语
    val selectedAffirmation = uiState.affirmations.firstOrNull { it.isSelected }

    // 进入动画状态
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // 统计数据 - 简化版
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                animationSpec = tween(600),
                initialOffsetY = { -40 }
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                StatBox(
                    title = "今日完成",
                    value = "${uiState.stats.todayCompleted}",
                    unit = "次",
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    title = "本周完成率",
                    value = "${uiState.stats.weekCompletionRate.toInt()}",
                    unit = "%",
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    title = "累计时长",
                    value = "${uiState.stats.totalDuration / 60}",
                    unit = "分钟",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 选择暗示语
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 100)) +
                    slideInVertically(animationSpec = tween(600, delayMillis = 100))
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💭 当前暗示语",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        TextButton(onClick = onNavigateToManagement) {
                            Text(
                                text = "管理暗示语 >",
                                fontSize = 14.sp,
                                color = Color(0xFF667EEA)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 时长滑动条
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) +
                    slideInVertically(animationSpec = tween(600, delayMillis = 200))
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⏱️ 默念时长",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        Text(
                            text = "${selectedDuration.toInt()} 分钟",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF667EEA)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 滑动条
                    Slider(
                        value = selectedDuration,
                        onValueChange = { selectedDuration = it },
                        valueRange = 1f..10f,
                        steps = 8, // 1,2,3,4,5,6,7,8,9,10 共10个值，steps=8
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF667EEA),
                            activeTrackColor = Color(0xFF667EEA),
                            inactiveTrackColor = Color(0xFFE0E0E0)
                        )
                    )

                    // 刻度标记
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "1分钟",
                            fontSize = 11.sp,
                            color = Color(0xFF999999)
                        )
                        Text(
                            text = "10分钟",
                            fontSize = 11.sp,
                            color = Color(0xFF999999)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 开始按钮
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 300)) +
                    scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        initialScale = 0.8f
                    )
        ) {
            PrimaryButton(
                text = "🚀 开始默念",
                onClick = {
                    selectedAffirmation?.let { affirmation ->
                        val durationInSeconds = (selectedDuration * 60).toInt()
                        println("AffirmationScreen - Starting with: ${affirmation.content}")
                        println("AffirmationScreen - Duration: $durationInSeconds seconds")
                        viewModel.startAffirmation(affirmation, durationInSeconds)
                        onNavigateToTimer()
                    }
                },
                enabled = selectedAffirmation != null,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        // 记录历史按钮
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 400))
        ) {
            SecondaryButton(
                text = "📜 查看记录",
                onClick = onNavigateToRecords,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(100.dp)) // 为底部导航留出空间
    }
}

