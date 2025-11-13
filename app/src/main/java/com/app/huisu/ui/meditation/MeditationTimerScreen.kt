package com.app.huisu.ui.meditation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.app.huisu.R
import com.app.huisu.ui.components.SecondaryButton
import com.app.huisu.ui.theme.Purple667
import kotlin.math.sin
import kotlin.math.PI

/**
 * 颜色线性插值辅助函数
 */
private fun lerp(start: Color, end: Color, fraction: Float): Color {
    return Color(
        red = start.red + (end.red - start.red) * fraction,
        green = start.green + (end.green - start.green) * fraction,
        blue = start.blue + (end.blue - start.blue) * fraction,
        alpha = start.alpha + (end.alpha - start.alpha) * fraction
    )
}

@Composable
fun MeditationTimerScreen(
    viewModel: MeditationViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val timerState by viewModel.timerState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    var showCancelDialog by remember { mutableStateOf(false) }

    // 页面进入时确保已绑定到Service
    LaunchedEffect(Unit) {
        // 确保绑定到Service获取实时状态
        viewModel.ensureServiceBound()
    }

    // 监听生命周期 - 当用户返回App查看时间后,暂停让他点击"继续"再回B站
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    // 如果计时正在运行(后台计时中),返回App时暂停
                    // 让用户主动点击"继续冥想"后再返回B站
                    if (timerState.isRunning && !timerState.isPaused) {
                        viewModel.pauseMeditation()
                    }
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 动态梦幻渐变背景动画
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")

    // 使用三个不同的动画来控制不同的颜色相位
    val animatedProgress1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "color1"
    )

    val animatedProgress2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "color2"
    )

    val animatedProgress3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "color3"
    )

    // 定义多个梦幻颜色
    val colorPalette = listOf(
        Color(0xFF667EEA), // 紫蓝
        Color(0xFF764BA2), // 深紫
        Color(0xFFFF6B9D), // 粉红
        Color(0xFFC471ED), // 淡紫
        Color(0xFF12C2E9), // 青蓝
        Color(0xFFF093FB), // 粉紫
        Color(0xFF4FACFE)  // 天蓝
    )

    // 使用正弦波计算颜色索引，创建流动效果
    fun getInterpolatedColor(progress: Float, offset: Float): Color {
        val index = (progress * 2 * PI.toFloat() + offset)
        val sinValue = (sin(index.toDouble()) + 1) / 2 // 0到1之间
        val colorIndex = (sinValue * (colorPalette.size - 1)).toInt()
        val nextColorIndex = (colorIndex + 1) % colorPalette.size
        val fraction = (sinValue * (colorPalette.size - 1)) - colorIndex

        return lerp(colorPalette[colorIndex], colorPalette[nextColorIndex], fraction.toFloat())
    }

    val color1 = getInterpolatedColor(animatedProgress1, 0f)
    val color2 = getInterpolatedColor(animatedProgress2, PI.toFloat() * 0.5f)
    val color3 = getInterpolatedColor(animatedProgress3, PI.toFloat())

    val animatedGradient = Brush.verticalGradient(
        colors = listOf(color1, color2, color3),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = animatedGradient)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = if (timerState.isPaused) stringResource(R.string.meditation_paused)
                   else stringResource(R.string.meditation_in_progress),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 计时器显示
        val hours = timerState.elapsedSeconds / 3600
        val minutes = (timerState.elapsedSeconds % 3600) / 60
        val seconds = timerState.elapsedSeconds % 60

        Text(
            text = String.format("%02d:%02d:%02d", hours, minutes, seconds),
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(30.dp))

        // 状态标签
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White.copy(alpha = 0.3f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (timerState.isPaused)
                           "⏸️ ${stringResource(R.string.meditation_paused_hint)}"
                           else "🎬 ${stringResource(R.string.meditation_video_playing_hint)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 提示文字
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 40.dp)
        ) {
            Text(
                text = if (timerState.isPaused)
                       stringResource(R.string.meditation_paused_tip)
                       else stringResource(R.string.meditation_stay_focused),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (timerState.isPaused)
                       stringResource(R.string.meditation_click_continue_tip)
                       else stringResource(R.string.meditation_video_playing),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        // 控制按钮 - 三个按钮横排
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 继续/在看按钮
            SecondaryButton(
                text = if (timerState.isPaused) "▶️ ${stringResource(R.string.meditation_continue)}"
                       else "👁️ ${stringResource(R.string.meditation_watching)}",
                onClick = {
                    if (timerState.isPaused) {
                        viewModel.resumeMeditation()
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = timerState.isPaused
            )

            // 完成按钮
            SecondaryButton(
                text = "✅ ${stringResource(R.string.meditation_finish)}",
                onClick = {
                    viewModel.endMeditation()
                    onBack()
                },
                modifier = Modifier.weight(1f)
            )

            // 取消按钮
            SecondaryButton(
                text = "❌ ${stringResource(R.string.meditation_cancel)}",
                onClick = {
                    showCancelDialog = true
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // 取消确认对话框
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Text(stringResource(R.string.meditation_cancel_title))
            },
            text = {
                Text(stringResource(R.string.meditation_cancel_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.cancelMeditation()
                        showCancelDialog = false
                        onBack()
                    }
                ) {
                    Text(
                        stringResource(R.string.confirm),
                        color = Color.Red
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCancelDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
