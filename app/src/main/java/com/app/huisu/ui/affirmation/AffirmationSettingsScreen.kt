package com.app.huisu.ui.affirmation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.huisu.R
import com.app.huisu.data.entity.Affirmation
import com.app.huisu.ui.components.SecondaryButton
import com.app.huisu.ui.theme.Purple667

@Composable
fun AffirmationSettingsScreen(
    viewModel: AffirmationSettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // 标题
            Text(
                text = stringResource(R.string.affirmation_management),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // 暗示语列表标题
            Text(
                text = stringResource(R.string.my_affirmations),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 15.dp)
            )

            // 暗示语列表
            if (uiState.affirmations.isEmpty()) {
                // 空状态显示
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📝",
                            fontSize = 48.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.no_affirmations_yet),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 16.sp,
                                color = Color(0xFF999999)
                            )
                        )
                    }
                }
            } else {
                // 暗示语列表 - 简化版本，不使用嵌套Card
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.affirmations.forEach { affirmation ->
                        SimpleAffirmationItem(
                            affirmation = affirmation,
                            onEdit = { viewModel.showEditDialog(affirmation) },
                            onDelete = { viewModel.deleteAffirmation(affirmation) }
                        )
                    }
                }
            }

            // 添加新暗示语按钮
            Button(
                onClick = { viewModel.showAddDialog() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple667
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "+ ${stringResource(R.string.add_new_affirmation)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // 提醒时间设置
            Text(
                text = stringResource(R.string.reminder_time_settings),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 30.dp, bottom = 15.dp)
            )

            // 早晨提醒
            TimeSettingItem(
                icon = "🌅",
                label = stringResource(R.string.affirmation_morning_reminder),
                time = uiState.timeSettings.morningTime,
                onClick = { viewModel.showTimePickerDialog(TimeType.MORNING) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 中午提醒
            TimeSettingItem(
                icon = "☀️",
                label = stringResource(R.string.affirmation_noon_reminder),
                time = uiState.timeSettings.noonTime,
                onClick = { viewModel.showTimePickerDialog(TimeType.NOON) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 晚上提醒
            TimeSettingItem(
                icon = "🌙",
                label = stringResource(R.string.affirmation_evening_reminder),
                time = uiState.timeSettings.eveningTime,
                onClick = { viewModel.showTimePickerDialog(TimeType.EVENING) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 默念时长
            TimeSettingItem(
                icon = "⏱️",
                label = stringResource(R.string.affirmation_duration),
                time = "${uiState.timeSettings.duration / 60} ${stringResource(R.string.minutes)}",
                onClick = { viewModel.showDurationPickerDialog() }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 返回按钮
            SecondaryButton(
                text = stringResource(R.string.back),
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 添加暗示语对话框
        if (uiState.showAddDialog) {
            AffirmationDialog(
                title = stringResource(R.string.add_affirmation),
                initialContent = "",
                onConfirm = { content ->
                    viewModel.addAffirmation(content)
                    viewModel.hideAddDialog()
                },
                onDismiss = { viewModel.hideAddDialog() }
            )
        }

        // 编辑暗示语对话框
        if (uiState.showEditDialog && uiState.editingAffirmation != null) {
            AffirmationDialog(
                title = stringResource(R.string.edit_affirmation),
                initialContent = uiState.editingAffirmation!!.content,
                onConfirm = { content ->
                    viewModel.updateAffirmation(
                        uiState.editingAffirmation!!.copy(content = content)
                    )
                    viewModel.hideEditDialog()
                },
                onDismiss = { viewModel.hideEditDialog() }
            )
        }

        // 时间选择对话框
        if (uiState.showTimePickerDialog && uiState.currentTimeType != null) {
            val currentTime = when (uiState.currentTimeType) {
                TimeType.MORNING -> uiState.timeSettings.morningTime
                TimeType.NOON -> uiState.timeSettings.noonTime
                TimeType.EVENING -> uiState.timeSettings.eveningTime
                null -> "08:00"
            }

            TimePickerDialog(
                currentTime = currentTime,
                onConfirm = { time ->
                    when (uiState.currentTimeType) {
                        TimeType.MORNING -> viewModel.updateMorningTime(time)
                        TimeType.NOON -> viewModel.updateNoonTime(time)
                        TimeType.EVENING -> viewModel.updateEveningTime(time)
                        null -> {}
                    }
                    viewModel.hideTimePickerDialog()
                },
                onDismiss = { viewModel.hideTimePickerDialog() }
            )
        }

        // 时长选择对话框
        if (uiState.showDurationPickerDialog) {
            DurationPickerDialog(
                currentDuration = uiState.timeSettings.duration / 60,
                onConfirm = { minutes ->
                    viewModel.updateDuration(minutes * 60)
                    viewModel.hideDurationPickerDialog()
                },
                onDismiss = { viewModel.hideDurationPickerDialog() }
            )
        }
    }
}

@Composable
fun SimpleAffirmationItem(
    affirmation: Affirmation,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = affirmation.content,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 2,
                color = Color.Black
            )
            Text(
                text = stringResource(R.string.created_at, formatTimestamp(affirmation.createdAt)),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 14.sp,
                    color = Color(0xFF999999)
                ),
                modifier = Modifier.padding(top = 5.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 编辑按钮
            Text(
                text = "✏️",
                fontSize = 20.sp,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onEdit() }
                    .padding(8.dp),
                color = Color(0xFF666666)
            )

            // 删除按钮
            Text(
                text = "🗑️",
                fontSize = 20.sp,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onDelete() }
                    .padding(8.dp),
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
fun TimeSettingItem(
    icon: String,
    label: String,
    time: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        color = Color(0xFF333333)
                    )
                )
            }
            Text(
                text = time,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    color = Purple667,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun AffirmationDialog(
    title: String,
    initialContent: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var content by remember { mutableStateOf(initialContent) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Text(
                    text = stringResource(R.string.affirmation_content),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text(stringResource(R.string.enter_affirmation)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple667,
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    maxLines = 5
                )

                // 小贴士
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3CD)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(15.dp)
                    ) {
                        Text(
                            text = "💡 ${stringResource(R.string.tips)}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                color = Color(0xFF856404),
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.affirmation_tips),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 13.sp,
                                color = Color(0xFF856404),
                                lineHeight = 20.sp
                            )
                        )
                    }
                }

                // 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 2.dp
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Purple667
                        )
                    ) {
                        Text(stringResource(R.string.cancel))
                    }

                    Button(
                        onClick = {
                            if (content.isNotBlank()) {
                                onConfirm(content)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Purple667
                        ),
                        shape = RoundedCornerShape(10.dp),
                        enabled = content.isNotBlank()
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    currentTime: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val timeParts = currentTime.split(":")
    var hour by remember { mutableStateOf(timeParts[0].toIntOrNull() ?: 8) }
    var minute by remember { mutableStateOf(timeParts[1].toIntOrNull() ?: 0) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.select_time),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 30.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 小时选择
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { hour = (hour + 1) % 24 }) {
                            Text("▲", fontSize = 24.sp)
                        }
                        Text(
                            text = String.format("%02d", hour),
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Purple667
                            )
                        )
                        IconButton(onClick = { hour = if (hour > 0) hour - 1 else 23 }) {
                            Text("▼", fontSize = 24.sp)
                        }
                    }

                    Text(
                        text = ":",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    // 分钟选择
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { minute = (minute + 5) % 60 }) {
                            Text("▲", fontSize = 24.sp)
                        }
                        Text(
                            text = String.format("%02d", minute),
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Purple667
                            )
                        )
                        IconButton(onClick = { minute = if (minute >= 5) minute - 5 else 55 }) {
                            Text("▼", fontSize = 24.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 2.dp
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Purple667
                        )
                    ) {
                        Text(stringResource(R.string.cancel))
                    }

                    Button(
                        onClick = {
                            val timeString = String.format("%02d:%02d", hour, minute)
                            onConfirm(timeString)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Purple667
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}

@Composable
fun DurationPickerDialog(
    currentDuration: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var duration by remember { mutableStateOf(currentDuration) }
    val durationOptions = listOf(1, 2, 3, 5, 10)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.select_duration),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                durationOptions.forEach { minutes ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { duration = minutes },
                        colors = CardDefaults.cardColors(
                            containerColor = if (duration == minutes) Color(0xFFF8F9FF) else Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 2.dp,
                                    color = if (duration == minutes) Purple667 else Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$minutes ${stringResource(R.string.minutes)}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 16.sp,
                                    fontWeight = if (duration == minutes) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                            if (duration == minutes) {
                                Text(
                                    text = "✓",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        color = Purple667,
                                        fontSize = 20.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 2.dp
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Purple667
                        )
                    ) {
                        Text(stringResource(R.string.cancel))
                    }

                    Button(
                        onClick = { onConfirm(duration) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Purple667
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}