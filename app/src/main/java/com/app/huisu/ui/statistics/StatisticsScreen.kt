package com.app.huisu.ui.statistics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.huisu.data.entity.Achievement
import com.app.huisu.data.entity.AchievementLevel
import com.app.huisu.ui.components.GlassCard
import com.app.huisu.ui.components.InfoPill
import com.app.huisu.ui.components.SectionHeader
import com.app.huisu.ui.components.StatBox
import com.app.huisu.ui.components.ZenBackground
import com.app.huisu.ui.theme.CardBackground
import com.app.huisu.ui.theme.DividerColor
import com.app.huisu.ui.theme.ErrorRed
import com.app.huisu.ui.theme.GlassWhite
import com.app.huisu.ui.theme.Mist400
import com.app.huisu.ui.theme.Purple667
import com.app.huisu.ui.theme.Sage400
import com.app.huisu.ui.theme.SurfaceLight
import com.app.huisu.ui.theme.TextPrimary
import com.app.huisu.ui.theme.TextSecondary
import com.app.huisu.ui.theme.TextTertiary
import java.util.Calendar
import java.util.Locale

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
    onNavigateToCloudSync: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
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
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                    animationSpec = tween(600),
                    initialOffsetY = { -36 }
                )
            ) {
                StatisticsHero(
                    statistics = uiState.statistics,
                    calendarData = uiState.calendarData,
                    onNavigateToCloudSync = onNavigateToCloudSync
                )
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(660, delayMillis = 70))
            ) {
                StatisticsSummaryGrid(statistics = uiState.statistics)
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(720, delayMillis = 120))
            ) {
                CalendarCard(calendarData = uiState.calendarData)
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(780, delayMillis = 170))
            ) {
                AchievementOverviewCard(
                    achievements = uiState.achievements,
                    onAchievementClick = viewModel::onAchievementClick
                )
            }
        }
    }

    uiState.selectedAchievement?.let { achievement ->
        AchievementDetailDialog(
            achievement = achievement,
            onDismiss = viewModel::dismissAchievementDialog
        )
    }

    uiState.newlyUnlockedAchievement?.let { achievement ->
        AchievementUnlockDialog(
            achievement = achievement,
            onDismiss = viewModel::dismissUnlockAnimation
        )
    }
}

@Composable
private fun StatisticsHero(
    statistics: StatisticsData,
    calendarData: CalendarData,
    onNavigateToCloudSync: () -> Unit
) {
    val totalSessions = statistics.totalMeditationCount + statistics.totalAffirmationCount

    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoPill(label = "本月活跃 ${calendarData.completedDays.size} 天")
            InfoPill(label = "双完成 ${calendarData.bothCompletedDays.size} 天")
            InfoPill(label = "累计练习 $totalSessions 次")
        }
        TextButton(onClick = onNavigateToCloudSync) {
            Text(
                text = "云同步设置",
                style = MaterialTheme.typography.labelLarge,
                color = Purple667
            )
        }
    }
}

@Composable
private fun StatisticsSummaryGrid(statistics: StatisticsData) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatBox(
                title = "冥想时长",
                value = String.format(Locale.getDefault(), "%.1f", statistics.totalMeditationDuration / 3600f),
                unit = "小时",
                modifier = Modifier.weight(1f)
            )
            StatBox(
                title = "默念时长",
                value = String.format(Locale.getDefault(), "%.1f", statistics.totalAffirmationDuration / 3600f),
                unit = "小时",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatBox(
                title = "冥想次数",
                value = statistics.totalMeditationCount.toString(),
                unit = "次",
                modifier = Modifier.weight(1f)
            )
            StatBox(
                title = "默念次数",
                value = statistics.totalAffirmationCount.toString(),
                unit = "次",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CalendarCard(calendarData: CalendarData) {
    val monthLabel = rememberCurrentMonthLabel()

    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "本月打卡日历",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Text(
                    text = "冥想与默念会在这里留下每日痕迹。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            InfoPill(label = monthLabel)
        }

        CalendarGrid(calendarData = calendarData)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LegendPill(label = "双完成", color = Sage400)
            LegendPill(label = "单项完成", color = Mist400)
            LegendPill(label = "今日", color = Purple667)
        }
    }
}

@Composable
private fun CalendarGrid(calendarData: CalendarData) {
    val weekLabels = listOf("日", "一", "二", "三", "四", "五", "六")
    val cells = rememberCalendarCells()
    val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            weekLabels.forEach { label ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
            }
        }

        cells.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                week.forEach { day ->
                    if (day == null) {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    } else {
                        CalendarDayCell(
                            day = day,
                            isToday = day == today,
                            isBothCompleted = calendarData.bothCompletedDays.contains(day),
                            isPartialCompleted = calendarData.meditationOnlyDays.contains(day) ||
                                calendarData.affirmationOnlyDays.contains(day),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: Int,
    isToday: Boolean,
    isBothCompleted: Boolean,
    isPartialCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val background = when {
        isBothCompleted -> Sage400.copy(alpha = 0.26f)
        isPartialCompleted -> Mist400.copy(alpha = 0.28f)
        else -> Color.White.copy(alpha = 0.56f)
    }
    val border = when {
        isToday -> BorderStroke(1.dp, Purple667.copy(alpha = 0.34f))
        isBothCompleted -> BorderStroke(1.dp, Sage400.copy(alpha = 0.18f))
        else -> BorderStroke(1.dp, DividerColor)
    }

    Card(
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        border = border,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday) FontWeight.SemiBold else FontWeight.Medium,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun LegendPill(
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color.copy(alpha = 0.8f), CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun AchievementOverviewCard(
    achievements: List<Achievement>,
    onAchievementClick: (Achievement) -> Unit
) {
    GlassCard {
        SectionHeader(
            eyebrow = "成就进度",
            title = "每一次练习都在积累",
            subtitle = "点击卡片查看细节和下一个里程碑。"
        )

        if (achievements.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂时还没有成就数据",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                achievements.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { achievement ->
                            AchievementItemCard(
                                achievement = achievement,
                                onClick = { onAchievementClick(achievement) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementItemCard(
    achievement: Achievement,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = levelColor(achievement.level)
    val progress = achievementProgress(achievement)
    val remaining = achievementRemainingLabel(achievement)

    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.unlocked) {
                accent.copy(alpha = 0.14f)
            } else {
                GlassWhite
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (achievement.unlocked) accent.copy(alpha = 0.18f) else DividerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = achievement.icon,
                    fontSize = 28.sp
                )
                InfoPill(
                    label = achievement.level.displayName,
                    backgroundColor = accent.copy(alpha = 0.14f),
                    contentColor = accent
                )
            }

            Text(
                text = achievement.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = accent,
                trackColor = accent.copy(alpha = 0.18f)
            )

            Text(
                text = achievementProgressLabel(achievement),
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )

            remaining?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
        }
    }
}

@Composable
private fun AchievementDetailDialog(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    val accent = levelColor(achievement.level)
    val progress = achievementProgress(achievement)
    val milestones = achievementMilestones(achievement.key)
    val nextMilestone = milestones.firstOrNull { it > achievement.currentValue }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceLight,
        shape = RoundedCornerShape(28.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = achievement.icon,
                    fontSize = 34.sp
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = achievement.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "${achievement.level.icon} ${achievement.level.displayName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = accent
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "当前进度",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = achievementProgressLabel(achievement),
                            style = MaterialTheme.typography.bodyMedium,
                            color = accent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = accent,
                        trackColor = accent.copy(alpha = 0.18f)
                    )

                    Text(
                        text = "完成度 ${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }

                nextMilestone?.let {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.10f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "下一个里程碑",
                                style = MaterialTheme.typography.labelLarge,
                                color = accent
                            )
                            Text(
                                text = formatTarget(it, achievement.key),
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (milestones.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "里程碑",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary
                        )
                        milestones.forEach { target ->
                            val reached = achievement.currentValue >= target
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (reached) "已达成" else "待达成",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (reached) accent else TextTertiary
                                )
                                Text(
                                    text = formatTarget(target, achievement.key),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                achievement.unlockedDate?.let {
                    Text(
                        text = "解锁时间 ${formatDate(it)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭", color = Purple667)
            }
        }
    )
}

@Composable
private fun AchievementUnlockDialog(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    val accent = levelColor(achievement.level)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceLight,
        shape = RoundedCornerShape(28.dp),
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = achievement.icon,
                    fontSize = 58.sp
                )
                Text(
                    text = "成就解锁",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                InfoPill(
                    label = "${achievement.level.icon} ${achievement.level.displayName}",
                    backgroundColor = accent.copy(alpha = 0.14f),
                    contentColor = accent
                )
                Text(
                    text = achievement.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = unlockMessage(achievement),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("继续", color = Purple667)
            }
        }
    )
}

@Composable
private fun rememberCurrentMonthLabel(): String {
    val calendar = remember { Calendar.getInstance() }
    return "${calendar.get(Calendar.MONTH) + 1} 月"
}

@Composable
private fun rememberCalendarCells(): List<Int?> {
    val calendar = remember { Calendar.getInstance() }
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val leadingEmptyDays = calendar.get(Calendar.DAY_OF_WEEK) - 1
    return List(leadingEmptyDays) { null } + (1..daysInMonth).toList()
}

private fun achievementProgress(achievement: Achievement): Float {
    return if (achievement.targetValue <= 0) {
        0f
    } else {
        (achievement.currentValue.toFloat() / achievement.targetValue).coerceIn(0f, 1f)
    }
}

private fun achievementProgressLabel(achievement: Achievement): String {
    return when (achievement.key) {
        "meditation_duration" -> {
            val current = achievement.currentValue / 3600f
            val target = achievement.targetValue / 3600f
            String.format(Locale.getDefault(), "%.1f / %.0f 小时", current, target)
        }

        "streak" -> "${achievement.currentValue} / ${achievement.targetValue} 天"
        else -> "${achievement.currentValue} / ${achievement.targetValue} 次"
    }
}

private fun achievementRemainingLabel(achievement: Achievement): String? {
    if (achievement.unlocked) return null

    val remaining = (achievement.targetValue - achievement.currentValue).coerceAtLeast(0)
    if (remaining == 0) return null

    return when (achievement.key) {
        "meditation_duration" -> {
            String.format(Locale.getDefault(), "还差 %.1f 小时", remaining / 3600f)
        }

        "streak" -> "还差 $remaining 天"
        else -> "还差 $remaining 次"
    }
}

private fun achievementMilestones(key: String): List<Int> {
    return when (key) {
        "streak" -> listOf(7, 30, 100, 365, 1000)
        "meditation_count" -> listOf(7, 30, 100, 365, 1000)
        "meditation_duration" -> listOf(18_000, 72_000, 180_000, 360_000, 1_800_000)
        "affirmation_count" -> listOf(20, 50, 200, 500, 1000)
        else -> emptyList()
    }
}

private fun formatTarget(target: Int, key: String): String {
    return when (key) {
        "meditation_duration" -> "${target / 3600} 小时"
        "streak" -> "$target 天"
        else -> "$target 次"
    }
}

private fun formatDate(timestamp: Long): String {
    val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
    return String.format(
        Locale.getDefault(),
        "%04d-%02d-%02d",
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH)
    )
}

private fun unlockMessage(achievement: Achievement): String {
    return when (achievement.key) {
        "streak" -> "你已经达成 ${achievement.targetValue} 天持续练习。"
        "meditation_count" -> "你已经完成 ${achievement.targetValue} 次冥想练习。"
        "meditation_duration" -> "你的冥想累计已达到 ${achievement.targetValue / 3600} 小时。"
        "affirmation_count" -> "你已经完成 ${achievement.targetValue} 次默念。"
        else -> "新的阶段已经被点亮。"
    }
}

private fun levelColor(level: AchievementLevel): Color {
    return when (level) {
        AchievementLevel.BRONZE -> Color(level.color)
        AchievementLevel.SILVER -> Color(level.color)
        AchievementLevel.GOLD -> Color(level.color)
        AchievementLevel.DIAMOND -> Color(level.color)
        AchievementLevel.LEGEND -> Color(level.color)
    }
}
