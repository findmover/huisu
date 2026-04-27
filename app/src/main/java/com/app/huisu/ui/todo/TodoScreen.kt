package com.app.huisu.ui.todo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.huisu.data.entity.TodoCategory
import com.app.huisu.data.entity.TodoItem
import com.app.huisu.data.entity.TodoPriority
import com.app.huisu.ui.components.GlassCard
import com.app.huisu.ui.components.InfoPill
import com.app.huisu.ui.components.PrimaryButton
import com.app.huisu.ui.components.SecondaryButton
import com.app.huisu.ui.components.ZenBackground
import com.app.huisu.ui.theme.DividerColor
import com.app.huisu.ui.theme.ErrorRed
import com.app.huisu.ui.theme.GlassWhite
import com.app.huisu.ui.theme.Mist400
import com.app.huisu.ui.theme.Purple667
import com.app.huisu.ui.theme.Sage400
import com.app.huisu.ui.theme.TextPrimary
import com.app.huisu.ui.theme.TextSecondary
import com.app.huisu.ui.theme.TextTertiary
import com.app.huisu.ui.theme.WarningYellow
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TodoScreen(
    viewModel: TodoViewModel = hiltViewModel(),
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateToCategoryManagement: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val categoryMap = remember(uiState.categories) { uiState.categories.associateBy { it.id } }

    var showAddTodoDialog by remember { mutableStateOf(false) }
    var todoToEdit by remember { mutableStateOf<TodoItem?>(null) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    uiState.error?.let { error ->
        LaunchedEffect(error) {
            delay(2400)
            viewModel.clearError()
        }
    }

    ZenBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 18.dp,
                top = 18.dp,
                end = 18.dp,
                bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                        animationSpec = tween(600),
                        initialOffsetY = { -36 }
                    )
                ) {
                    TodoStatsHeader(
                        overallStats = uiState.overallStats,
                        showCompletedOnly = uiState.showCompletedOnly,
                        onManageCategory = onNavigateToCategoryManagement
                    )
                }
            }

            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(650, delayMillis = 70))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PrimaryButton(
                            text = "新增任务",
                            onClick = { showAddTodoDialog = true },
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryButton(
                            text = if (uiState.showCompletedOnly) "查看全部" else "只看已完成",
                            onClick = viewModel::toggleShowCompleted,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(700, delayMillis = 120))
                ) {
                    CategoryFilter(
                        categories = uiState.categories,
                        selectedCategoryId = uiState.selectedCategoryId,
                        categoryStats = uiState.categoryStats,
                        onCategorySelected = viewModel::selectCategory
                    )
                }
            }

            uiState.error?.let { error ->
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(720, delayMillis = 150))
                    ) {
                        MessageCard(
                            message = error,
                            accent = ErrorRed
                        )
                    }
                }
            }

            if (visible) {
                if (uiState.todos.isEmpty()) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            EmptyTodoState(
                                hasCategories = uiState.categories.isNotEmpty(),
                                showCompletedOnly = uiState.showCompletedOnly,
                                onToggleCompleted = viewModel::toggleShowCompleted
                            )
                        }
                    }
                } else {
                    items(uiState.todos, key = { it.id }) { todo ->
                        TodoItemCard(
                            todo = todo,
                            category = categoryMap[todo.categoryId],
                            onToggle = viewModel::toggleTodoCompletion,
                            onOpenDetail = onNavigateToDetail,
                            onEdit = { todoToEdit = todo },
                            onDelete = viewModel::deleteTodo
                        )
                    }
                }
            }
        }
    }

    if (showAddTodoDialog) {
        AddTodoDialog(
            categories = uiState.categories,
            onDismiss = { showAddTodoDialog = false },
            onConfirm = { title, description, categoryId, priority, dueDate ->
                viewModel.addTodo(title, description, categoryId, priority, dueDate)
                showAddTodoDialog = false
            }
        )
    }

    todoToEdit?.let { todo ->
        EditTodoDialog(
            todo = todo,
            categories = uiState.categories,
            onDismiss = { todoToEdit = null },
            onConfirm = { _, title, description, categoryId, priority, dueDate ->
                viewModel.updateTodo(
                    todo.copy(
                        title = title,
                        description = description,
                        categoryId = categoryId,
                        priority = priority,
                        dueDate = dueDate
                    )
                )
                todoToEdit = null
            }
        )
    }
}

@Composable
private fun TodoStatsHeader(
    overallStats: OverallStats,
    showCompletedOnly: Boolean,
    onManageCategory: () -> Unit
) {
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FocusStatPill(
                label = "待办",
                value = overallStats.pendingTodos.toString(),
                accent = Purple667,
                modifier = Modifier.weight(1f)
            )
            FocusStatPill(
                label = "完成",
                value = overallStats.completedTodos.toString(),
                accent = Sage400,
                modifier = Modifier.weight(1f)
            )
            FocusStatPill(
                label = "逾期",
                value = overallStats.overdueCount.toString(),
                accent = ErrorRed,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfoPill(
                label = if (showCompletedOnly) {
                    "当前筛选: 已完成"
                } else {
                    "完成率 ${overallStats.overallCompletionRate.toInt()}%"
                }
            )
            TextButton(onClick = onManageCategory) {
                Text(
                    text = "分类管理",
                    style = MaterialTheme.typography.labelLarge,
                    color = Purple667
                )
            }
        }
    }
}

@Composable
private fun FocusStatPill(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = accent
            )
        }
    }
}

@Composable
private fun CategoryFilter(
    categories: List<TodoCategory>,
    selectedCategoryId: Long?,
    categoryStats: Map<Long, CategoryStats>,
    onCategorySelected: (Long?) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CategoryChip(
            name = "全部",
            count = categoryStats.values.sumOf { it.total },
            isSelected = selectedCategoryId == null,
            color = Purple667,
            onClick = { onCategorySelected(null) }
        )

        categories.forEach { category ->
            val stats = categoryStats[category.id] ?: CategoryStats()
            CategoryChip(
                name = "${category.icon} ${category.name}",
                count = stats.total,
                isSelected = selectedCategoryId == category.id,
                color = parseCategoryColor(category.color),
                onClick = { onCategorySelected(category.id) }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    name: String,
    count: Int,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.16f) else GlassWhite
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) color.copy(alpha = 0.28f) else DividerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isSelected) color else TextSecondary
            )
            if (count > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) color else TextTertiary
                )
            }
        }
    }
}

@Composable
private fun MessageCard(
    message: String,
    accent: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.12f)),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.18f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
        )
    }
}

@Composable
private fun EmptyTodoState(
    hasCategories: Boolean,
    showCompletedOnly: Boolean,
    onToggleCompleted: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = when {
                showCompletedOnly -> "还没有已完成的任务"
                hasCategories -> "今天的任务列表还是空的"
                else -> "先建立一个分类，再开始整理任务"
            },
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
        )
        Text(
            text = when {
                showCompletedOnly -> "切回全部任务，看看还有哪些事情在等待推进。"
                hasCategories -> "把最重要的一件事先写下来，执行会比计划更快开始。"
                else -> "先有分类，再慢慢把事情放进合适的位置。"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        if (showCompletedOnly) {
            SecondaryButton(
                text = "查看全部任务",
                onClick = onToggleCompleted
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TodoItemCard(
    todo: TodoItem,
    category: TodoCategory?,
    onToggle: (Long, Boolean) -> Unit,
    onOpenDetail: (Long) -> Unit,
    onEdit: () -> Unit,
    onDelete: (TodoItem) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val priorityColor = priorityColor(todo.priority)
    val categoryColor = category?.let { parseCategoryColor(it.color) } ?: Purple667
    val completionLabel = todo.completedAt?.let {
        "完成于 ${SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date(it))}"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(1.dp, DividerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { onOpenDetail(todo.id) },
                        onLongClick = { showMenu = true }
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .width(4.dp)
                        .height(64.dp)
                        .background(priorityColor, RoundedCornerShape(999.dp))
                )

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = if (todo.isCompleted) {
                                Sage400.copy(alpha = 0.20f)
                            } else {
                                priorityColor.copy(alpha = 0.12f)
                            },
                            shape = CircleShape
                        )
                        .clickable { onToggle(todo.id, todo.isCompleted) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (todo.isCompleted) {
                            Icons.Outlined.CheckCircle
                        } else {
                            Icons.Outlined.RadioButtonUnchecked
                        },
                        contentDescription = if (todo.isCompleted) "已完成" else "未完成",
                        tint = if (todo.isCompleted) Sage400 else priorityColor
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        category?.let {
                            InfoPill(
                                label = "${it.icon} ${it.name}",
                                backgroundColor = categoryColor.copy(alpha = 0.14f),
                                contentColor = categoryColor
                            )
                        }
                        completionLabel?.let {
                            InfoPill(
                                label = it,
                                backgroundColor = Sage400.copy(alpha = 0.14f),
                                contentColor = TextSecondary
                            )
                        }
                    }

                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (todo.isCompleted) TextSecondary else TextPrimary,
                        textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null
                    )

                    if (todo.description.isNotBlank() && todo.description != todo.title) {
                        Text(
                            text = todo.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PriorityBadge(priority = todo.priority)
                        todo.dueDate?.let { DueDateBadge(dueDate = it, isCompleted = todo.isCompleted) }
                        if (todo.dueDate == null && !todo.isCompleted) {
                            InfoPill(
                                label = "无截止时间",
                                backgroundColor = Color.White.copy(alpha = 0.65f),
                                contentColor = TextTertiary
                            )
                        }
                    }
                }

                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreHoriz,
                        contentDescription = "更多操作",
                        tint = TextSecondary
                    )
                }
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("编辑") },
                    onClick = {
                        showMenu = false
                        onEdit()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "编辑",
                            tint = Purple667
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text("删除", color = ErrorRed) },
                    onClick = {
                        showMenu = false
                        onDelete(todo)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "删除",
                            tint = ErrorRed
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun PriorityBadge(priority: TodoPriority) {
    val (label, color) = when (priority) {
        TodoPriority.HIGH -> "高优先级" to ErrorRed
        TodoPriority.MEDIUM -> "中优先级" to WarningYellow
        TodoPriority.LOW -> "低优先级" to Mist400
    }

    InfoPill(
        label = label,
        backgroundColor = color.copy(alpha = 0.14f),
        contentColor = TextPrimary
    )
}

@Composable
private fun DueDateBadge(
    dueDate: Long,
    isCompleted: Boolean
) {
    val isOverdue = !isCompleted && dueDate < System.currentTimeMillis()
    val accent = when {
        isOverdue -> ErrorRed
        isCompleted -> Sage400
        else -> Mist400
    }

    InfoPill(
        label = formatDueDate(dueDate),
        backgroundColor = accent.copy(alpha = 0.16f),
        contentColor = if (isOverdue) ErrorRed else TextSecondary
    )
}

private fun formatDueDate(dueDate: Long): String {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val dueStart = Calendar.getInstance().apply {
        timeInMillis = dueDate
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val days = (dueStart - today) / (24 * 60 * 60 * 1000)

    return when {
        days < 0 -> "已逾期"
        days == 0L -> "今天截止"
        days == 1L -> "明天截止"
        days <= 7L -> "${days}天后截止"
        else -> SimpleDateFormat("MM-dd 截止", Locale.getDefault()).format(Date(dueDate))
    }
}

private fun priorityColor(priority: TodoPriority): Color {
    return when (priority) {
        TodoPriority.HIGH -> ErrorRed
        TodoPriority.MEDIUM -> WarningYellow
        TodoPriority.LOW -> Purple667
    }
}

private fun parseCategoryColor(color: String): Color {
    return runCatching {
        Color(android.graphics.Color.parseColor(color))
    }.getOrDefault(Purple667)
}
