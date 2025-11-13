package com.app.huisu.ui.affirmation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.huisu.R
import com.app.huisu.ui.components.SecondaryButton
import com.app.huisu.ui.theme.PinkF09
import com.app.huisu.ui.theme.PinkF55
import com.app.huisu.ui.theme.Purple667
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AffirmationTimerScreen(
    viewModel: AffirmationViewModel,
    onBack: () -> Unit
) {
    val timerState by viewModel.timerState.collectAsState()

    // 毫秒计数器，用于显示动画效果
    var milliseconds by remember { mutableStateOf(0) }

    // 倒计时逻辑 - 100ms更新一次以显示毫秒
    LaunchedEffect(timerState.isRunning, timerState.remainingSeconds, milliseconds) {
        if (timerState.isRunning && timerState.remainingSeconds > 0) {
            delay(100) // 100ms更新一次
            milliseconds = (milliseconds + 1) % 10
            if (milliseconds == 0) {
                // 每秒更新一次remainingSeconds
                viewModel.updateTimer(timerState.remainingSeconds - 1)
            }
        } else if (timerState.remainingSeconds == 0 && timerState.totalSeconds > 0) {
            // 倒计时结束
            viewModel.completeAffirmation()
            onBack()
        }
    }

    // iOS风格的动态梦幻渐变背景动画
    val infiniteTransition = rememberInfiniteTransition(label = "backgroundGradient")

    // 三个独立的动画控制器，速度不同，创造流动感（超慢速度）
    val animatedProgress1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(80000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "color1"
    )

    val animatedProgress2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(70000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "color2"
    )

    val animatedProgress3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(90000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "color3"
    )

    // iOS风格的梦幻色彩调色板
    val colorPalette = listOf(
        Color(0xFF667EEA), // 紫蓝
        Color(0xFF764BA2), // 深紫
        Color(0xFFFF6B9D), // 粉红
        Color(0xFFC471ED), // 淡紫
        Color(0xFF12C2E9), // 青蓝
        Color(0xFFF093FB)  // 粉紫
    )

    // 使用正弦波平滑地在颜色之间循环过渡
    fun getFlowingColor(progress: Float, offset: Float): Color {
        val angle = (progress * 2 * PI.toFloat() + offset)
        val sinValue = (sin(angle.toDouble()) + 1) / 2 // 归一化到0-1
        val position = sinValue * (colorPalette.size - 1)
        val index = position.toInt()
        val nextIndex = (index + 1) % colorPalette.size
        val fraction = (position - index).toFloat()

        return lerpColor(colorPalette[index], colorPalette[nextIndex], fraction)
    }

    // 计算三个渐变层的颜色
    val color1 = getFlowingColor(animatedProgress1, 0f)
    val color2 = getFlowingColor(animatedProgress2, PI.toFloat() * 0.66f)
    val color3 = getFlowingColor(animatedProgress3, PI.toFloat() * 1.33f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(color1, color2, color3)
                )
            )
    ) {
        // 沉浸式内容区域
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(top = 60.dp, bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 顶部暗示语显示 - 自适应大小，根据内容自动缩减字体
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .padding(bottom = 30.dp),
                contentAlignment = Alignment.Center
            ) {
                AutoSizeText(
                    text = timerState.affirmationText,
                    maxFontSize = 28.sp,
                    minFontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 10.dp)
                )
            }

            // 中部圆形进度条和倒计时
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(vertical = 30.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // 背景圆环 - 半透明白色
                    CircularProgressIndicator(
                        progress = 1f,
                        modifier = Modifier.size(200.dp),
                        color = Color.White.copy(alpha = 0.3f),
                        strokeWidth = 14.dp,
                        trackColor = Color.Transparent
                    )

                    // 进度圆环 - 纯白色
                    val progressValue = if (timerState.totalSeconds > 0)
                        timerState.remainingSeconds.toFloat() / timerState.totalSeconds
                    else 0f

                    CircularProgressIndicator(
                        progress = progressValue,
                        modifier = Modifier.size(200.dp),
                        color = Color.White,
                        strokeWidth = 14.dp,
                        trackColor = Color.Transparent
                    )

                    // 倒计时数字 - 纯白色大字体，带毫秒
                    val minutes = timerState.remainingSeconds / 60
                    val seconds = timerState.remainingSeconds % 60

                    // 使用Row来实现基线对齐，让数字更好地居中
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 25.dp) // 向下偏移，让视觉居中
                    ) {
                        Text(
                            text = String.format("%d:%02d", minutes, seconds),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 48.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        // 毫秒显示（无小数点）
                        Text(
                            text = "${milliseconds}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "在心中轻轻重复",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )
            }

            // 控制按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f, fill = false)
                    .padding(top = 30.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (timerState.isRunning) {
                            viewModel.pauseAffirmation()
                        } else {
                            viewModel.resumeAffirmation()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.25f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text(
                        text = if (timerState.isRunning) "⏸" else "▶",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        viewModel.completeAffirmation()
                        onBack()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF667EEA)
                    ),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text(
                        text = "完成",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * 颜色线性插值函数 - 平滑过渡两种颜色
 */
private fun lerpColor(start: Color, end: Color, fraction: Float): Color {
    return Color(
        red = start.red + (end.red - start.red) * fraction,
        green = start.green + (end.green - start.green) * fraction,
        blue = start.blue + (end.blue - start.blue) * fraction,
        alpha = start.alpha + (end.alpha - start.alpha) * fraction
    )
}

@Composable
fun AutoSizeText(
    text: String,
    maxFontSize: androidx.compose.ui.unit.TextUnit,
    minFontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight,
    color: Color,
    textAlign: TextAlign,
    modifier: Modifier = Modifier
) {
    var fontSize by remember(text) { mutableStateOf(maxFontSize) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    BoxWithConstraints(modifier = modifier) {
        val maxHeightPx = with(androidx.compose.ui.platform.LocalDensity.current) {
            maxHeight.toPx()
        }
        val maxWidthPx = with(androidx.compose.ui.platform.LocalDensity.current) {
            maxWidth.toPx()
        }

        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = fontWeight,
            color = color,
            textAlign = textAlign,
            softWrap = true,
            maxLines = Int.MAX_VALUE,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (readyToDraw) Modifier else Modifier.alpha(0f)
                ),
            onTextLayout = { textLayoutResult ->
                if (!readyToDraw) {
                    val hasOverflow = textLayoutResult.hasVisualOverflow ||
                            textLayoutResult.didOverflowHeight ||
                            textLayoutResult.didOverflowWidth ||
                            (maxHeightPx > 0 && textLayoutResult.size.height > maxHeightPx * 0.95f) || // 留5%余量
                            (maxWidthPx > 0 && textLayoutResult.size.width > maxWidthPx * 0.95f)

                    // 如果文本溢出，快速减小字体
                    if (hasOverflow) {
                        if (fontSize.value > minFontSize.value) {
                            // 根据溢出程度调整减少幅度
                            val heightOverflow = if (maxHeightPx > 0) {
                                textLayoutResult.size.height.toFloat() / maxHeightPx
                            } else {
                                1.5f
                            }
                            val reduction = when {
                                heightOverflow > 1.5f -> 3f
                                heightOverflow > 1.3f -> 2f
                                heightOverflow > 1.1f -> 1f
                                else -> 0.5f
                            }
                            val newSize = (fontSize.value - reduction).coerceAtLeast(minFontSize.value)
                            fontSize = newSize.sp
                        } else {
                            // 已达到最小字体，显示出来
                            readyToDraw = true
                        }
                    } else {
                        // 没有溢出，可以显示
                        readyToDraw = true
                    }
                }
            },
            style = LocalTextStyle.current.copy(
                lineHeight = (fontSize.value * 1.3).sp // 减小行高避免溢出
            )
        )
    }
}
