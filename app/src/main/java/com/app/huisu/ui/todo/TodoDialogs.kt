package com.app.huisu.ui.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.app.huisu.data.entity.TodoCategory
import com.app.huisu.data.entity.TodoPriority
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoDialog(
    categories: List<TodoCategory>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long, TodoPriority, Long?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<TodoCategory?>(null) }
    var selectedPriority by remember { mutableStateOf(TodoPriority.MEDIUM) }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker && selectedDate != null) {
        DatePickerDialog(
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "添加新TODO",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // 内容输入（移除标题，直接使用描述作为主要内容）
                Text(
                    text = "内容 *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("输入TODO内容...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF667EEA),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    maxLines = 5
                )

                // 分类选择
                Text(
                    text = "分类 *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (categories.isEmpty()) {
                    Text(
                        text = "暂无分类，请先创建分类",
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(categories) { category ->
                            CategorySelectionChip(
                                category = category,
                                isSelected = selectedCategory?.id == category.id,
                                onClick = { selectedCategory = category }
                            )
                        }
                    }
                }

                // 优先级选择
                Text(
                    text = "优先级",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TodoPriority.values().forEach { priority ->
                        PriorityChip(
                            priority = priority,
                            isSelected = selectedPriority == priority,
                            onClick = { selectedPriority = priority },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 截止日期
                Text(
                    text = "截止日期（可选）",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = selectedDate?.let { formatDate(it) } ?: "",
                    onValueChange = { },
                    placeholder = { Text("选择截止日期") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF667EEA),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    readOnly = true,
                    trailingIcon = {
                        Text(
                            text = "📅",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                )

                // 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF667EEA))
                    ) {
                        Text("取消", fontSize = 16.sp)
                    }

                    Button(
                        onClick = {
                            selectedCategory?.let { category ->
                                val dueDate = selectedDate?.time
                                // 使用描述内容的前50个字符作为标题（用于显示），完整内容存在description
                                val displayTitle = if (description.length > 50) description.take(50) + "..." else description
                                onConfirm(displayTitle, description, category.id, selectedPriority, dueDate)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667EEA)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = description.isNotBlank() && selectedCategory != null
                    ) {
                        Text("添加", fontSize = 16.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#FF667EEA") }
    var selectedIcon by remember { mutableStateOf("📌") }

    val colors = listOf(
        "#FF667EEA", "#FF10B981", "#FFF59E0B", "#FFEF4444", "#FF8B5CF6",
        "#FF3B82F6", "#FFEC4899", "#FF14B8A6", "#FFF97316", "#FF6366F1"
    )

    val icons = listOf("📌", "💼", "📚", "🏠", "💪", "🎯", "⭐", "🔥", "💡", "🎨", "🚀", "💎")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "添加分类",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // 分类名称
                Text(
                    text = "分类名称 *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("输入分类名称...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF667EEA),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    singleLine = true
                )

                // 颜色选择
                Text(
                    text = "选择颜色",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    items(colors.size) { index ->
                        val color = colors[index]
                        ColorChip(
                            color = Color(android.graphics.Color.parseColor(color)),
                            isSelected = selectedColor == color,
                            onClick = { selectedColor = color }
                        )
                    }
                }

                // 图标选择
                Text(
                    text = "选择图标",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    items(icons.size) { index ->
                        val icon = icons[index]
                        IconChip(
                            icon = icon,
                            isSelected = selectedIcon == icon,
                            onClick = { selectedIcon = icon }
                        )
                    }
                }

                // 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF667EEA))
                    ) {
                        Text("取消", fontSize = 16.sp)
                    }

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(name, selectedColor, selectedIcon)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667EEA)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = name.isNotBlank()
                    ) {
                        Text("添加", fontSize = 16.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySelectionChip(
    category: TodoCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color(android.graphics.Color.parseColor(category.color)).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(20.dp),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                Color(android.graphics.Color.parseColor(category.color))
            )
        } else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${category.icon} ${category.name}",
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = Color(android.graphics.Color.parseColor(category.color))
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriorityChip(
    priority: TodoPriority,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (priority) {
        TodoPriority.HIGH -> "高" to Color(0xFFEF4444)
        TodoPriority.MEDIUM -> "中" to Color(0xFFF59E0B)
        TodoPriority.LOW -> "低" to Color(0xFF10B981)
    }

    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color else color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else color
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorChip(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(40.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Text(
                    text = "✓",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IconChip(
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF667EEA).copy(alpha = 0.1f) else Color(0xFFF5F5F5)
        ),
        shape = RoundedCornerShape(8.dp),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF667EEA))
        } else null
    ) {
        Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 20.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(Date(millis))
                    }
                    onDismiss()
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

private fun formatDate(date: Date): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.format(date)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoDialog(
    todo: com.app.huisu.data.entity.TodoItem,
    categories: List<TodoCategory>,
    onDismiss: () -> Unit,
    onConfirm: (Long, String, String, Long, TodoPriority, Long?) -> Unit
) {
    var description by remember { mutableStateOf(todo.description) }
    var selectedCategory by remember { mutableStateOf<TodoCategory?>(categories.find { it.id == todo.categoryId }) }
    var selectedPriority by remember { mutableStateOf(todo.priority) }
    var selectedDate by remember { mutableStateOf<Date?>(todo.dueDate?.let { Date(it) }) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "编辑TODO",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // 内容输入
                Text(
                    text = "内容 *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("输入TODO内容...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF667EEA),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    maxLines = 5
                )

                // 分类选择
                Text(
                    text = "分类 *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (categories.isEmpty()) {
                    Text(
                        text = "暂无分类，请先创建分类",
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(categories) { category ->
                            CategorySelectionChip(
                                category = category,
                                isSelected = selectedCategory?.id == category.id,
                                onClick = { selectedCategory = category }
                            )
                        }
                    }
                }

                // 优先级选择
                Text(
                    text = "优先级",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TodoPriority.values().forEach { priority ->
                        PriorityChip(
                            priority = priority,
                            isSelected = selectedPriority == priority,
                            onClick = { selectedPriority = priority },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 截止日期
                Text(
                    text = "截止日期（可选）",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = selectedDate?.let { formatDate(it) } ?: "",
                        onValueChange = { },
                        placeholder = { Text("选择截止日期") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 24.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF667EEA),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            disabledTextColor = Color(0xFF333333),
                            disabledBorderColor = Color(0xFFE0E0E0),
                            disabledPlaceholderColor = Color(0xFF999999),
                            disabledTrailingIconColor = Color(0xFF333333)
                        ),
                        readOnly = true,
                        enabled = false,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Text(
                                    text = "📅",
                                    fontSize = 20.sp
                                )
                            }
                        }
                    )
                    if (selectedDate != null) {
                        OutlinedButton(
                            onClick = { selectedDate = null },
                            modifier = Modifier.padding(bottom = 24.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("清除")
                        }
                    }
                }

                // 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF667EEA))
                    ) {
                        Text("取消", fontSize = 16.sp)
                    }

                    Button(
                        onClick = {
                            selectedCategory?.let { category ->
                                val dueDate = selectedDate?.time
                                // 使用描述内容的前50个字符作为标题（用于显示），完整内容存在description
                                val displayTitle = if (description.length > 50) description.take(50) + "..." else description
                                onConfirm(todo.id, displayTitle, description, category.id, selectedPriority, dueDate)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667EEA)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = description.isNotBlank() && selectedCategory != null
                    ) {
                        Text("保存", fontSize = 16.sp, color = Color.White)
                    }
                }
            }
        }
    }
}