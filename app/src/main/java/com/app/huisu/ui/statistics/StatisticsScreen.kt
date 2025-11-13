package com.app.huisu.ui.statistics

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.huisu.R
import com.app.huisu.data.entity.Achievement
import com.app.huisu.data.entity.AchievementLevel
import com.app.huisu.ui.components.StatBox
import java.util.Calendar

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 进入动画状态
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // 标题 - 带渐入动画
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                animationSpec = tween(500),
                initialOffsetY = { -30 }
            )
        ) {
            Text(
                text = stringResource(R.string.tab_statistics),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 本月完成日历 - 带渐入和缩放动画
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 100)) +
                    scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        initialScale = 0.9f
                    )
        ) {
            ChartCard(title = stringResource(R.string.stats_calendar)) {
                CalendarView(calendarData = uiState.calendarData)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 成就徽章 - 带渐入和缩放动画
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) +
                    scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        initialScale = 0.9f
                    )
        ) {
            ChartCard(title = stringResource(R.string.stats_achievements)) {
                Column {
                    AchievementGrid(
                        achievements = uiState.achievements,
                        onAchievementClick = { viewModel.onAchievementClick(it) }
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    // 成就说明
                    Text(
                        text = "💡 每个成就有5个等级:青铜→白银→黄金→钻石→传奇",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 总体统计 - 带渐入和滑入动画
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 300)) +
                    slideInVertically(
                        animationSpec = tween(600, delayMillis = 300),
                        initialOffsetY = { 40 }
                    )
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    StatBox(
                        title = "总冥想时长",
                        value = String.format("%.1f", uiState.statistics.totalMeditationDuration / 3600f),
                        unit = stringResource(R.string.hours),
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        title = "总默念时长",
                        value = String.format("%.1f", uiState.statistics.totalAffirmationDuration / 3600f),
                        unit = stringResource(R.string.hours),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    StatBox(
                        title = "冥想总次数",
                        value = "${uiState.statistics.totalMeditationCount}",
                        unit = stringResource(R.string.times),
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        title = "默念总次数",
                        value = "${uiState.statistics.totalAffirmationCount}",
                        unit = stringResource(R.string.times),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // 成就详情对话框
    uiState.selectedAchievement?.let { achievement ->
        AchievementDetailDialog(
            achievement = achievement,
            onDismiss = { viewModel.dismissAchievementDialog() }
        )
    }

    // 成就解锁动画弹窗
    uiState.newlyUnlockedAchievement?.let { achievement ->
        AchievementUnlockDialog(
            achievement = achievement,
            onDismiss = { viewModel.dismissUnlockAnimation() }
        )
    }
}

@Composable
private fun ChartCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(15.dp))
            content()
        }
    }
}

@Composable
private fun CalendarView(calendarData: CalendarData) {
    val days = listOf("日", "一", "二", "三", "四", "五", "六")
    val cal = Calendar.getInstance()
    val today = cal.get(Calendar.DAY_OF_MONTH)
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    // 获取本月第一天是星期几
    cal.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sunday

    Column {
        // 星期标题
        Row(modifier = Modifier.fillMaxWidth()) {
            days.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF999999)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 日期网格
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(250.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 添加空白格子以对齐第一天
            items(firstDayOfWeek) {
                Box(modifier = Modifier.aspectRatio(1f))
            }

            // 添加日期
            items((1..daysInMonth).toList()) { day ->
                CalendarDayItem(
                    day = day,
                    isBothCompleted = calendarData.bothCompletedDays.contains(day),
                    isPartial = calendarData.meditationOnlyDays.contains(day) || calendarData.affirmationOnlyDays.contains(day),
                    isToday = day == today
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
            LegendItem("🟣", stringResource(R.string.stats_all_completed))
            LegendItem("🟡", stringResource(R.string.stats_partial_completed))
            LegendItem("⚪", stringResource(R.string.stats_not_started))
        }
    }
}

@Composable
private fun CalendarDayItem(
    day: Int,
    isBothCompleted: Boolean,
    isPartial: Boolean,
    isToday: Boolean
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(
                color = when {
                    isBothCompleted -> Color(0xFF667EEA)
                    isPartial -> Color(0xFFFFD93D)
                    else -> Color(0xFFF5F5F5)
                },
                shape = RoundedCornerShape(8.dp)
            )
            .then(
                if (isToday) Modifier.padding(2.dp)
                    .background(Color.Transparent, RoundedCornerShape(8.dp))
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isBothCompleted) Color.White else Color(0xFF333333)
        )
    }
}

@Composable
private fun LegendItem(icon: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = icon, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF666666)
        )
    }
}

@Composable
private fun AchievementGrid(
    achievements: List<Achievement>,
    onAchievementClick: (Achievement) -> Unit
) {
    if (achievements.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "暂无成就数据",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF999999)
            )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            // 按2列布局显示成就
            achievements.chunked(2).forEach { rowAchievements ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    rowAchievements.forEach { achievement ->
                        Box(modifier = Modifier.weight(1f)) {
                            AchievementItem(
                                achievement = achievement,
                                onClick = { onAchievementClick(achievement) }
                            )
                        }
                    }
                    // 如果最后一行只有1个元素,添加空白占位
                    if (rowAchievements.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementItem(
    achievement: Achievement,
    onClick: () -> Unit
) {
    val isUnlocked = achievement.unlocked
    val levelEmoji = achievement.level.icon
    val levelName = achievement.level.displayName

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) {
                Color.Transparent
            } else {
                Color(0xFFF8F9FA)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isUnlocked) {
                        Modifier.background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFFD93D),
                                    Color(0xFFFFB800)
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(15.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 成就图标
                Text(
                    text = achievement.icon,
                    style = MaterialTheme.typography.displayMedium,
                    fontSize = 40.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 成就名称
                Text(
                    text = achievement.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isUnlocked) Color.White else Color(0xFF333333),
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 等级显示
                Text(
                    text = "$levelEmoji $levelName",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isUnlocked) Color.White.copy(alpha = 0.9f) else Color(0xFF666666),
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 进度条
                val progress = if (achievement.targetValue > 0) {
                    (achievement.currentValue.toFloat() / achievement.targetValue).coerceIn(0f, 1f)
                } else 0f

                val progressPercentage = (progress * 100).toInt()

                // 渐变进度条
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(
                            color = if (isUnlocked) Color.White.copy(alpha = 0.3f) else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(4.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(
                                brush = if (isUnlocked) {
                                    Brush.horizontalGradient(listOf(Color.White, Color.White))
                                } else {
                                    Brush.horizontalGradient(listOf(Color(0xFF667EEA), Color(0xFF764BA2)))
                                },
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 进度文字和百分比
                val (progressText, remainingText) = when (achievement.key) {
                    "meditation_duration" -> {
                        val hours = achievement.currentValue / 3600f
                        val targetHours = achievement.targetValue / 3600f
                        val remaining = targetHours - hours
                        Pair(
                            String.format("%.1f/%.0f 小时", hours, targetHours),
                            if (!isUnlocked && remaining > 0) String.format("还需 %.1f 小时", remaining) else null
                        )
                    }
                    "streak" -> {
                        val remaining = achievement.targetValue - achievement.currentValue
                        Pair(
                            "${achievement.currentValue}/${achievement.targetValue} 天",
                            if (!isUnlocked && remaining > 0) "还需 $remaining 天" else null
                        )
                    }
                    "affirmation_count" -> {
                        val remaining = achievement.targetValue - achievement.currentValue
                        Pair(
                            "${achievement.currentValue}/${achievement.targetValue} 次",
                            if (!isUnlocked && remaining > 0) "还需 $remaining 次" else null
                        )
                    }
                    else -> {
                        val remaining = achievement.targetValue - achievement.currentValue
                        Pair(
                            "${achievement.currentValue}/${achievement.targetValue} 次",
                            if (!isUnlocked && remaining > 0) "还需 $remaining 次" else null
                        )
                    }
                }

                // 进度数值
                Text(
                    text = progressText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isUnlocked) Color.White.copy(alpha = 0.9f) else Color(0xFF333333),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )

                // 剩余提示 (仅未解锁时显示)
                if (!isUnlocked && remainingText != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = remainingText,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF999999),
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementDetailDialog(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    val levelColor = Color(achievement.level.color)

    // 定义等级目标值，用于显示下一级信息
    val levelTargets = when (achievement.key) {
        "streak" -> listOf(7, 30, 100, 365, 1000) // 连续打卡
        "meditation_count" -> listOf(7, 30, 100, 365, 1000) // 冥想大师
        "meditation_duration" -> listOf(18000, 72000, 180000, 360000, 1800000) // 冥想时长
        "affirmation_count" -> listOf(20, 50, 200, 500, 1000) // 默念达人
        else -> listOf()
    }

    val levelNames = listOf("青铜", "白银", "黄金", "钻石", "传奇")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 成就图标
                Text(
                    text = achievement.icon,
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 40.sp
                )
                // 成就名称和等级
                Column {
                    Text(achievement.name, style = MaterialTheme.typography.headlineSmall)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = achievement.level.icon,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = achievement.level.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = levelColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        text = {
            Column {
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(15.dp))

                // 当前等级进度条
                val progress = if (achievement.targetValue > 0) {
                    (achievement.currentValue.toFloat() / achievement.targetValue).coerceIn(0f, 1f)
                } else 0f

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "当前等级进度",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${achievement.currentValue} / ${achievement.targetValue}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = levelColor
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        color = levelColor,
                        trackColor = levelColor.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "${(progress * 100).toInt()}% 完成",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                }

                // 显示所有等级里程碑
                if (levelTargets.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = "等级里程碑",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val allLevels = listOf(
                        AchievementLevel.BRONZE,
                        AchievementLevel.SILVER,
                        AchievementLevel.GOLD,
                        AchievementLevel.DIAMOND,
                        AchievementLevel.LEGEND
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        levelTargets.forEachIndexed { index, target ->
                            val level = allLevels[index]
                            val isCompleted = achievement.currentValue >= target
                            val isCurrent = level == achievement.level && achievement.unlocked

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = if (isCompleted) "✓" else "○",
                                        color = if (isCompleted) Color(level.color) else Color(0xFFCCCCCC),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = level.icon,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = levelNames[index],
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isCompleted) Color(0xFF333333) else Color(0xFF999999)
                                    )
                                }
                                Text(
                                    text = formatTarget(target, achievement.key),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isCompleted) Color(0xFF333333) else Color(0xFF999999)
                                )
                            }
                        }
                    }
                }

                if (achievement.unlocked && achievement.unlockedDate != null) {
                    Spacer(modifier = Modifier.height(15.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = levelColor.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🎉", style = MaterialTheme.typography.bodyMedium)
                            Column {
                                Text(
                                    text = "当前等级已解锁",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = levelColor
                                )
                                Text(
                                    text = formatDate(achievement.unlockedDate),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF999999)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}

private fun formatTarget(target: Int, key: String): String {
    return when (key) {
        "meditation_duration" -> "${target / 3600}小时"
        "meditation_count" -> "${target}次"
        "streak" -> "${target}天"
        "affirmation_count" -> "${target}次"
        else -> "$target"
    }
}

private fun formatDate(timestamp: Long): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = timestamp
    return String.format(
        "%04d-%02d-%02d",
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH) + 1,
        cal.get(Calendar.DAY_OF_MONTH)
    )
}

@Composable
private fun AchievementUnlockDialog(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    val levelColor = Color(achievement.level.color)

    // 获取解锁消息
    val unlockMessage = when (achievement.key) {
        "streak" -> "恭喜你达成连续打卡${achievement.targetValue}天!"
        "meditation_count" -> "恭喜你完成冥想${achievement.targetValue}次!"
        "meditation_duration" -> "恭喜你累计冥想${achievement.targetValue / 3600}小时!"
        "affirmation_count" -> "恭喜你完成默念${achievement.targetValue}次!"
        else -> "恭喜你解锁新成就!"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = null,
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 成就图标 - 带动画效果
                Text(
                    text = achievement.icon,
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = 80.sp,
                    modifier = Modifier.padding(vertical = 15.dp)
                )

                // 成就解锁标题
                Text(
                    text = "🎉 成就解锁 🎉",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 等级显示 - 金色渐变
                Text(
                    text = "${achievement.level.icon} ${achievement.level.displayName}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = levelColor,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(15.dp))

                // 成就名称
                Text(
                    text = achievement.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 解锁描述
                Text(
                    text = unlockMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF667EEA),
                                    Color(0xFF764BA2)
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "太棒了!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
