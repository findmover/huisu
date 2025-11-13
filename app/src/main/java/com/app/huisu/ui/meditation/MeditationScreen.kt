package com.app.huisu.ui.meditation

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.huisu.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.app.huisu.ui.components.PrimaryButton
import com.app.huisu.ui.components.StatBox
import com.app.huisu.ui.components.StatsCard

@OptIn(ExperimentalMaterial3Api::class)
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

    // 进入动画状态
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    // 按钮缩放动画
    val buttonScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "buttonScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // 今日冥想统计卡片 - 带渐入动画
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                animationSpec = tween(600),
                initialOffsetY = { -40 }
            )
        ) {
            StatsCard(
                title = stringResource(R.string.meditation_today),
                value = "${uiState.stats.todayDuration / 60}${stringResource(R.string.minutes)}",
                subtitle = stringResource(R.string.meditation_completed, uiState.stats.todayCount) +
                        " · " + stringResource(R.string.meditation_continuous_days, 7)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 数据网格 - 带延迟渐入动画
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 100)) +
                    slideInVertically(animationSpec = tween(600, delayMillis = 100))
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    StatBox(
                        title = stringResource(R.string.meditation_this_week),
                        value = "${uiState.stats.weekDuration / 60}",
                        unit = stringResource(R.string.minutes),
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        title = stringResource(R.string.meditation_this_month),
                        value = "${uiState.stats.monthDuration / 60}",
                        unit = stringResource(R.string.minutes),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    StatBox(
                        title = stringResource(R.string.meditation_total_times),
                        value = "${uiState.stats.totalCount}",
                        unit = stringResource(R.string.times),
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        title = stringResource(R.string.meditation_total_duration),
                        value = String.format("%.1f", uiState.stats.totalDuration / 3600f),
                        unit = stringResource(R.string.hours),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // 开始冥想按钮 - 带缩放弹性动画
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) +
                    scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        initialScale = 0.8f
                    )
        ) {
            PrimaryButton(
                text = "🧘 " + stringResource(R.string.meditation_start),
                onClick = {
                    // 1. 先启动后台计时服务
                    viewModel.startMeditation()

                    // 2. 跳转到B站
                    val videoLink = uiState.currentVideoLink?.link
                    android.util.Log.d("MeditationScreen", "点击开始冥想, videoLink = $videoLink")
                    android.util.Log.d("MeditationScreen", "currentVideoLink = ${uiState.currentVideoLink}")

                    if (!videoLink.isNullOrEmpty()) {
                        try {
                            android.util.Log.d("MeditationScreen", "尝试打开B站: $videoLink")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoLink))
                            intent.setPackage("tv.danmaku.bili") // B站包名
                            context.startActivity(intent)
                            android.util.Log.d("MeditationScreen", "B站打开成功")
                        } catch (e: Exception) {
                            android.util.Log.e("MeditationScreen", "B站打开失败，使用浏览器", e)
                            // 如果没有安装B站,使用浏览器打开
                            try {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(videoLink))
                                context.startActivity(browserIntent)
                                android.util.Log.d("MeditationScreen", "浏览器打开成功")
                            } catch (e2: Exception) {
                                android.util.Log.e("MeditationScreen", "浏览器打开也失败", e2)
                            }
                        }
                    } else {
                        android.util.Log.w("MeditationScreen", "videoLink为空，无法跳转")
                    }

                    // 3. 延迟后跳转到计时页面
                    scope.launch {
                        delay(300)
                        onNavigateToTimer()
                    }
                },
                modifier = Modifier.scale(buttonScale)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        // 底部卡片 - 带渐入动画
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 300)) +
                    slideInVertically(
                        animationSpec = tween(600, delayMillis = 300),
                        initialOffsetY = { 40 }
                    )
        ) {
            Column {
                // 视频设置卡片
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToVideoSettings,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.meditation_current_video),
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = stringResource(R.string.meditation_manage_links),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF999999)
                            )
                        }
                        Text(
                            text = "⚙️",
                            style = MaterialTheme.typography.displayMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 冥想记录卡片
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToRecords,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "冥想记录",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = "查看历史冥想记录",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF999999)
                            )
                        }
                        Text(
                            text = "📝",
                            style = MaterialTheme.typography.displayMedium
                        )
                    }
                }
            }
        }
    }
}
