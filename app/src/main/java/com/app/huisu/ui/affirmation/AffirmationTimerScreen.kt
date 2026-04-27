package com.app.huisu.ui.affirmation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.huisu.ui.components.GlassCard
import com.app.huisu.ui.components.InfoPill
import com.app.huisu.ui.components.PrimaryButton
import com.app.huisu.ui.components.SecondaryButton
import com.app.huisu.ui.components.ZenBackground
import com.app.huisu.ui.theme.Mist400
import com.app.huisu.ui.theme.TextPrimary
import com.app.huisu.ui.theme.TextTertiary
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun AffirmationTimerScreen(
    viewModel: AffirmationViewModel,
    onBack: () -> Unit
) {
    val timerState by viewModel.timerState.collectAsState()
    val completedSeconds = (timerState.totalSeconds - timerState.remainingSeconds).coerceAtLeast(0)
    val progress = if (timerState.totalSeconds > 0) {
        timerState.remainingSeconds.toFloat() / timerState.totalSeconds
    } else {
        0f
    }
    val motion = rememberHourglassMotion()

    LaunchedEffect(timerState.isRunning, timerState.remainingSeconds, timerState.totalSeconds) {
        if (timerState.isRunning && timerState.remainingSeconds > 0) {
            delay(1000)
            viewModel.updateTimer(timerState.remainingSeconds - 1)
        } else if (timerState.remainingSeconds == 0 && timerState.totalSeconds > 0) {
            viewModel.completeAffirmation()
            onBack()
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
                AutoSizeText(
                    text = timerState.affirmationText.ifBlank { "\u4fdd\u6301\u5f53\u4e0b" },
                    maxFontSize = 26.sp,
                    minFontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SandHourglass(
                        progress = progress,
                        isRunning = timerState.isRunning,
                        tilt = motion.tilt,
                        motionEnergy = motion.energy,
                        modifier = Modifier
                            .fillMaxWidth(0.72f)
                            .aspectRatio(0.64f)
                            .graphicsLayer {
                                rotationZ = motion.tilt * 5.5f
                                scaleX = 1f - motion.energy * 0.015f
                                scaleY = 1f + motion.energy * 0.018f
                            }
                    )

                    Text(
                        text = formatMinuteSecond(timerState.remainingSeconds),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        color = TextPrimary
                    )

                    if (!timerState.isRunning) {
                        InfoPill(
                            label = "\u5df2\u6682\u505c",
                            backgroundColor = Mist400.copy(alpha = 0.18f),
                            contentColor = TextPrimary
                        )
                    }
                }
            }

            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimerMetaCard(
                        title = "\u5269\u4f59",
                        value = formatMinuteSecond(timerState.remainingSeconds),
                        modifier = Modifier.weight(1f)
                    )
                    TimerMetaCard(
                        title = "\u5df2\u5b8c\u6210",
                        value = formatMinuteSecond(completedSeconds),
                        modifier = Modifier.weight(1f)
                    )
                    TimerMetaCard(
                        title = "\u603b\u65f6\u957f",
                        value = formatMinuteSecond(timerState.totalSeconds),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SecondaryButton(
                        text = if (timerState.isRunning) "\u6682\u505c" else "\u7ee7\u7eed",
                        onClick = {
                            if (timerState.isRunning) {
                                viewModel.pauseAffirmation()
                            } else {
                                viewModel.resumeAffirmation()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    PrimaryButton(
                        text = "\u5b8c\u6210",
                        onClick = {
                            viewModel.completeAffirmation()
                            onBack()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SandHourglass(
    progress: Float,
    isRunning: Boolean,
    tilt: Float,
    motionEnergy: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sand_drip")
    val dripProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 720, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sand_drip_progress"
    )

    Canvas(modifier = modifier) {
        val frameColor = Color(0xFF92A18F)
        val edgeLight = Color.White.copy(alpha = 0.6f)
        val glassTop = Color.White.copy(alpha = 0.48f)
        val glassMid = Color(0xFFF4F7F2).copy(alpha = 0.18f)
        val glassLow = Color(0xFFDDE6DF).copy(alpha = 0.12f)
        val sandLight = Color(0xFFF5E2C4)
        val sandCore = Color(0xFFE3C8A0)
        val sandShadow = Color(0xFFC8A579)
        val haloColor = Mist400.copy(alpha = 0.18f + motionEnergy * 0.08f)

        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val topY = height * 0.08f
        val neckY = height * 0.5f
        val bottomY = height * 0.92f
        val bulbHalfWidth = width * 0.28f
        val neckHalfWidth = width * 0.042f
        val topBulbHeight = neckY - topY
        val bottomBulbHeight = bottomY - neckY
        val stroke = width * 0.03f
        val clamped = progress.coerceIn(0f, 1f)
        val filledBottom = (1f - clamped).coerceIn(0f, 1f)
        val motionShift = width * 0.075f * tilt + (dripProgress - 0.5f) * motionEnergy * width * 0.035f
        val liveOffset = motionEnergy * width * 0.015f

        val topGlass = Path().apply {
            moveTo(centerX - bulbHalfWidth, topY)
            quadraticBezierTo(centerX, topY - height * 0.055f, centerX + bulbHalfWidth, topY)
            lineTo(centerX + neckHalfWidth, neckY)
            lineTo(centerX - neckHalfWidth, neckY)
            close()
        }

        val bottomGlass = Path().apply {
            moveTo(centerX - bulbHalfWidth, bottomY)
            quadraticBezierTo(centerX, bottomY + height * 0.055f, centerX + bulbHalfWidth, bottomY)
            lineTo(centerX + neckHalfWidth, neckY)
            lineTo(centerX - neckHalfWidth, neckY)
            close()
        }

        drawOval(
            color = Color(0x16000000),
            topLeft = Offset(centerX - width * 0.19f + motionShift * 0.12f, bottomY + height * 0.018f),
            size = Size(width * 0.38f, height * 0.052f)
        )

        drawCircle(
            color = haloColor,
            radius = width * (0.48f + motionEnergy * 0.04f),
            center = Offset(centerX, height / 2f),
            style = Stroke(width = width * 0.02f)
        )

        clipPath(topGlass) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(glassTop, glassMid, glassLow)
                ),
                size = size
            )
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.32f), Color.Transparent),
                    center = Offset(centerX - bulbHalfWidth * 0.42f - motionShift * 0.1f, topY + topBulbHeight * 0.3f),
                    radius = width * 0.22f
                ),
                topLeft = Offset(centerX - bulbHalfWidth * 0.92f, topY + topBulbHeight * 0.08f),
                size = Size(width * 0.26f, topBulbHeight * 0.58f)
            )
        }

        clipPath(bottomGlass) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(glassTop, glassMid, glassLow)
                ),
                size = size
            )
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.26f), Color.Transparent),
                    center = Offset(centerX + bulbHalfWidth * 0.3f - motionShift * 0.08f, neckY + bottomBulbHeight * 0.42f),
                    radius = width * 0.24f
                ),
                topLeft = Offset(centerX + bulbHalfWidth * 0.08f, neckY + bottomBulbHeight * 0.08f),
                size = Size(width * 0.22f, bottomBulbHeight * 0.52f)
            )
        }

        drawPath(
            path = topGlass,
            color = frameColor,
            style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        drawPath(
            path = bottomGlass,
            color = frameColor,
            style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        drawLine(
            color = edgeLight.copy(alpha = 0.55f),
            start = Offset(centerX - bulbHalfWidth * 0.75f, topY + stroke * 0.2f),
            end = Offset(centerX + bulbHalfWidth * 0.75f, topY + stroke * 0.2f),
            strokeWidth = stroke * 0.45f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = frameColor.copy(alpha = 0.55f),
            start = Offset(centerX - bulbHalfWidth * 0.75f, bottomY - stroke * 0.2f),
            end = Offset(centerX + bulbHalfWidth * 0.75f, bottomY - stroke * 0.2f),
            strokeWidth = stroke * 0.45f,
            cap = StrokeCap.Round
        )

        if (clamped > 0f) {
            val topSandHeight = topBulbHeight * clamped
            val topSandTopY = neckY - topSandHeight
            val ratio = (topSandHeight / topBulbHeight).coerceIn(0f, 1f)
            val topSandWidth = bulbHalfWidth * (0.25f + ratio * 0.75f)
            val surfaceTilt = motionShift * 0.15f
            val leftSurfaceY = (topSandTopY + surfaceTilt).coerceIn(topY + stroke, neckY - stroke * 1.8f)
            val rightSurfaceY = (topSandTopY - surfaceTilt).coerceIn(topY + stroke, neckY - stroke * 1.8f)

            val topSand = Path().apply {
                moveTo(centerX - topSandWidth, leftSurfaceY)
                lineTo(centerX + topSandWidth, rightSurfaceY)
                lineTo(centerX + neckHalfWidth, neckY)
                lineTo(centerX - neckHalfWidth, neckY)
                close()
            }

            clipPath(topGlass) {
                drawPath(
                    path = topSand,
                    brush = Brush.verticalGradient(
                        colors = listOf(sandLight, sandCore, sandShadow)
                    )
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.34f),
                    start = Offset(centerX - topSandWidth * 0.72f, leftSurfaceY + stroke * 0.08f),
                    end = Offset(centerX + topSandWidth * 0.72f, rightSurfaceY + stroke * 0.08f),
                    strokeWidth = stroke * 0.2f,
                    cap = StrokeCap.Round
                )
            }
        }

        if (filledBottom > 0f) {
            val pileHeight = bottomBulbHeight * filledBottom * 0.9f
            val pileTopY = bottomY - pileHeight
            val pileWidth = bulbHalfWidth * (0.35f + filledBottom * 0.65f)
            val crestShift = motionShift * (0.7f + motionEnergy * 0.5f)
            val crestX = (centerX + crestShift).coerceIn(
                centerX - bulbHalfWidth * 0.46f,
                centerX + bulbHalfWidth * 0.46f
            )
            val crestY = pileTopY - bottomBulbHeight * 0.1f * filledBottom
            val leftShoulderX = centerX - pileWidth
            val rightShoulderX = centerX + pileWidth
            val leftShoulderY = pileTopY + abs(crestShift) * 0.08f + liveOffset * 0.4f
            val rightShoulderY = pileTopY + abs(crestShift) * 0.08f + liveOffset * 0.4f

            val bottomSand = Path().apply {
                moveTo(centerX - bulbHalfWidth + stroke * 0.65f, bottomY - stroke * 0.6f)
                lineTo(centerX + bulbHalfWidth - stroke * 0.65f, bottomY - stroke * 0.6f)
                lineTo(rightShoulderX, rightShoulderY)
                quadraticBezierTo(crestX, crestY, leftShoulderX, leftShoulderY)
                close()
            }

            clipPath(bottomGlass) {
                drawPath(
                    path = bottomSand,
                    brush = Brush.verticalGradient(
                        colors = listOf(sandLight, sandCore, sandShadow)
                    )
                )
                drawOval(
                    color = Color.White.copy(alpha = 0.14f),
                    topLeft = Offset(crestX - width * 0.08f, pileTopY + bottomBulbHeight * 0.06f),
                    size = Size(width * 0.16f, bottomBulbHeight * 0.08f)
                )
            }
        }

        if (isRunning && clamped > 0f && filledBottom < 1f) {
            val streamStartX = centerX + motionShift * 0.28f
            val streamEndX = centerX + motionShift * 0.06f
            val streamStartY = neckY + stroke * 0.75f
            val streamEndY = neckY + bottomBulbHeight * 0.57f
            val streamWidth = stroke * (0.32f + motionEnergy * 0.12f)

            drawLine(
                color = sandCore,
                start = Offset(streamStartX, streamStartY),
                end = Offset(streamEndX, streamEndY),
                strokeWidth = streamWidth,
                cap = StrokeCap.Round
            )

            repeat(3) { index ->
                val travel = (dripProgress + index * 0.26f) % 1f
                val x = streamStartX + (streamEndX - streamStartX) * travel + motionShift * 0.04f
                val y = streamStartY + (streamEndY - streamStartY) * travel
                drawCircle(
                    color = sandLight.copy(alpha = 0.68f - index * 0.15f),
                    radius = stroke * (0.34f - index * 0.06f),
                    center = Offset(x, y)
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
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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

private fun formatMinuteSecond(seconds: Int): String {
    val safeSeconds = seconds.coerceAtLeast(0)
    val minutes = safeSeconds / 60
    val secs = safeSeconds % 60
    return String.format("%d:%02d", minutes, secs)
}

@Composable
private fun AutoSizeText(
    text: String,
    maxFontSize: TextUnit,
    minFontSize: TextUnit,
    fontWeight: FontWeight,
    color: Color,
    textAlign: TextAlign,
    modifier: Modifier = Modifier
) {
    var fontSize by remember(text) { mutableStateOf(maxFontSize) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val maxHeightPx = with(density) { maxHeight.toPx() }
        val maxWidthPx = with(density) { maxWidth.toPx() }

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
                .then(if (readyToDraw) Modifier else Modifier.alpha(0f)),
            onTextLayout = { result ->
                if (!readyToDraw) {
                    val hasOverflow = result.hasVisualOverflow ||
                        result.didOverflowHeight ||
                        result.didOverflowWidth ||
                        (maxHeightPx > 0 && result.size.height > maxHeightPx * 0.95f) ||
                        (maxWidthPx > 0 && result.size.width > maxWidthPx * 0.95f)

                    if (hasOverflow && fontSize.value > minFontSize.value) {
                        fontSize = (fontSize.value - 1f).coerceAtLeast(minFontSize.value).sp
                    } else {
                        readyToDraw = true
                    }
                }
            },
            style = LocalTextStyle.current.copy(lineHeight = (fontSize.value * 1.32f).sp)
        )
    }
}

@Composable
private fun rememberHourglassMotion(): HourglassMotionState {
    val context = LocalContext.current
    val sensorManager = remember(context) {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    var targetTilt by remember { mutableStateOf(0f) }
    var targetEnergy by remember { mutableStateOf(0f) }

    val tilt by animateFloatAsState(
        targetValue = targetTilt,
        animationSpec = tween(durationMillis = 220),
        label = "hourglass_tilt"
    )
    val energy by animateFloatAsState(
        targetValue = targetEnergy,
        animationSpec = tween(durationMillis = 180),
        label = "hourglass_energy"
    )

    DisposableEffect(sensorManager) {
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        if (gyroscope == null) {
            targetTilt = 0f
            targetEnergy = 0f
            onDispose { }
        } else {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    if (event.values.size < 3) return
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    val nextTilt = targetTilt * 0.72f + ((-y + x * 0.35f) * 0.12f)
                    targetTilt = nextTilt.coerceIn(-1f, 1f)
                    targetEnergy = ((abs(x) + abs(y) + abs(z)) / 7.5f).coerceIn(0f, 1f)
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }

            sensorManager.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_UI)
            onDispose {
                sensorManager.unregisterListener(listener)
                targetTilt = 0f
                targetEnergy = 0f
            }
        }
    }

    return HourglassMotionState(
        tilt = tilt,
        energy = energy
    )
}

private data class HourglassMotionState(
    val tilt: Float,
    val energy: Float
)
