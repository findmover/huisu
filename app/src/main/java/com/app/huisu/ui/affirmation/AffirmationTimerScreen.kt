package com.app.huisu.ui.affirmation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.app.huisu.ui.theme.Mist400
import com.app.huisu.ui.theme.TextPrimary
import com.app.huisu.ui.theme.TextTertiary
import kotlinx.coroutines.delay
import kotlin.math.floor

@Composable
fun AffirmationTimerScreen(
    viewModel: AffirmationViewModel,
    onBack: () -> Unit
) {
    val timerState by viewModel.timerState.collectAsState()
    val completedSeconds = (timerState.totalSeconds - timerState.remainingSeconds).coerceAtLeast(0)

    LaunchedEffect(timerState.isRunning, timerState.remainingSeconds, timerState.totalSeconds) {
        if (timerState.isRunning && timerState.remainingSeconds > 0) {
            delay(1000)
            viewModel.updateTimer(timerState.remainingSeconds - 1)
        } else if (timerState.remainingSeconds == 0 && timerState.totalSeconds > 0) {
            viewModel.completeAffirmation()
            onBack()
        }
    }

    AnimatedAffirmationBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                AutoSizeText(
                    text = timerState.affirmationText.ifBlank { "保持当下" },
                    maxFontSize = 56.sp,
                    minFontSize = 23.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = formatMinuteSecond(timerState.remainingSeconds),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    color = TextPrimary.copy(alpha = 0.9f)
                )

                if (!timerState.isRunning) {
                    InfoPill(
                        label = "已暂停",
                        backgroundColor = Mist400.copy(alpha = 0.2f),
                        contentColor = TextPrimary
                    )
                }
            }

            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimerMetaCard(
                        title = "剩余",
                        value = formatMinuteSecond(timerState.remainingSeconds),
                        modifier = Modifier.weight(1f)
                    )
                    TimerMetaCard(
                        title = "已完成",
                        value = formatMinuteSecond(completedSeconds),
                        modifier = Modifier.weight(1f)
                    )
                    TimerMetaCard(
                        title = "总时长",
                        value = formatMinuteSecond(timerState.totalSeconds),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SecondaryButton(
                        text = if (timerState.isRunning) "暂停" else "继续",
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
                        text = "完成",
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
private fun AnimatedAffirmationBackground(
    content: @Composable () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "affirmation_gradient")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 34_000, easing = LinearEasing)
        ),
        label = "affirmation_gradient_phase"
    )

    val colors = rememberAnimatedGradientColors(phase)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = colors,
                    start = androidx.compose.ui.geometry.Offset.Zero,
                    end = androidx.compose.ui.geometry.Offset.Infinite
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.34f),
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}

@Composable
private fun rememberAnimatedGradientColors(phase: Float): List<Color> {
    val palettes = listOf(
        listOf(Color(0xFFE6F5EC), Color(0xFFD9EFF4), Color(0xFFF5E3D7), Color(0xFFF9F4DD)),
        listOf(Color(0xFFDCEFE8), Color(0xFFE9E0F4), Color(0xFFF7E6CC), Color(0xFFD9EEF2)),
        listOf(Color(0xFFF4EAD7), Color(0xFFDDEFD9), Color(0xFFD6E7F2), Color(0xFFF1DCE6)),
        listOf(Color(0xFFEAF1D8), Color(0xFFD8ECEA), Color(0xFFF2DDD2), Color(0xFFE2E3F4))
    )
    val scaled = phase.coerceIn(0f, 1f) * palettes.size
    val fromIndex = floor(scaled).toInt() % palettes.size
    val toIndex = (fromIndex + 1) % palettes.size
    val localProgress = scaled - floor(scaled)
    return palettes[fromIndex].zip(palettes[toIndex]).map { (from, to) ->
        blendColor(from, to, localProgress)
    }
}

private fun blendColor(from: Color, to: Color, fraction: Float): Color {
    val progress = fraction.coerceIn(0f, 1f)
    return Color(
        red = from.red + (to.red - from.red) * progress,
        green = from.green + (to.green - from.green) * progress,
        blue = from.blue + (to.blue - from.blue) * progress,
        alpha = from.alpha + (to.alpha - from.alpha) * progress
    )
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
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.58f)),
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
        val maxWidthPx = constraints.maxWidth
        val maxHeightPx = constraints.maxHeight

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
                .align(Alignment.Center)
                .then(if (readyToDraw) Modifier else Modifier.alpha(0f)),
            onTextLayout = { result ->
                if (!readyToDraw) {
                    val hasOverflow = result.hasVisualOverflow ||
                        result.didOverflowHeight ||
                        result.didOverflowWidth ||
                        (maxHeightPx > 0 && result.size.height > maxHeightPx * 0.9f) ||
                        (maxWidthPx > 0 && result.size.width > maxWidthPx * 0.96f)

                    if (hasOverflow && fontSize.value > minFontSize.value) {
                        fontSize = (fontSize.value - 1.4f).coerceAtLeast(minFontSize.value).sp
                    } else {
                        readyToDraw = true
                    }
                }
            },
            style = LocalTextStyle.current.copy(lineHeight = (fontSize.value * 1.36f).sp)
        )
    }
}
