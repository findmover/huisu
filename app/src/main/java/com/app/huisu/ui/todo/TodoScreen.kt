package com.app.huisu.ui.todo

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.huisu.data.entity.TodoCategory
import com.app.huisu.data.entity.TodoItem
import com.app.huisu.data.entity.TodoPriority
import com.app.huisu.ui.components.PrimaryButton
import com.app.huisu.ui.components.SecondaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    viewModel: TodoViewModel = hiltViewModel(),
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateToCategoryManagement: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddTodoDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    // 进入动画
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
            .padding(bottom = 80.dp) // 为底部导航栏预留空间
    ) {
        // 顶部操作栏 - 统一样式
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                animationSpec = tween(600),
                initialOffsetY = { -40 }
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SecondaryButton(
                    text = "+ 添加",
                    onClick = { showAddTodoDialog = true },
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = "⚙️ 分类",
                    onClick = onNavigateToCategoryManagement,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 分类筛选
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 100)) +
                    slideInVertically(animationSpec = tween(600, delayMillis = 100))
        ) {
            CategoryFilter(
                categories = uiState.categories,
                selectedCategoryId = uiState.selectedCategoryId,
                categoryStats = uiState.categoryStats,
                onCategorySelected = viewModel::selectCategory
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TODO列表
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) +
                    slideInVertically(animationSpec = tween(600, delayMillis = 200)),
            modifier = Modifier.weight(1f, fill = false) // 移到 AnimatedVisibility，使用 fill = false
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (uiState.todos.isEmpty()) {
                    EmptyTodoState(
                        hasCategories = uiState.categories.isNotEmpty(),
                        showCompletedOnly = uiState.showCompletedOnly,
                        onToggleCompleted = viewModel::toggleShowCompleted
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uiState.todos) { todo ->
                            TodoItemCard(
                                todo = todo,
                                onToggle = viewModel::toggleTodoCompletion,
                                onEdit = { onNavigateToDetail(todo.id) },
                                onDelete = viewModel::deleteTodo
                            )
                        }
                    }
                }
            }
        }

    }

    // 添加TODO对话框
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

    // 添加分类对话框
    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onConfirm = { name, color, icon ->
                viewModel.addCategory(name, color, icon)
                showAddCategoryDialog = false
            }
        )
    }

    // 错误提示
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // 这里可以显示一个Toast或Snackbar
            viewModel.clearError()
        }
    }
}

@Composable
private fun TodoStatsHeader(
    overallStats: OverallStats,
    onAddCategory: () -> Unit,
    onManageCategory: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📋 我的TODO",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = onManageCategory) {
                        Text(
                            text = "管理分类 >",
                            fontSize = 14.sp,
                            color = Color(0xFF667EEA)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "总计",
                    value = overallStats.totalTodos.toString(),
                    color = Color(0xFF667EEA),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "已完成",
                    value = overallStats.completedTodos.toString(),
                    color = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "完成率",
                    value = "${overallStats.overallCompletionRate.toInt()}%",
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = color.copy(alpha = 0.8f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryFilter(
    categories: List<TodoCategory>,
    selectedCategoryId: Long?,
    categoryStats: Map<Long, CategoryStats>,
    onCategorySelected: (Long?) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
                // 全部选项
                CategoryChip(
                    name = "全部",
                    count = categoryStats.values.sumOf { it.total },
                    isSelected = selectedCategoryId == null,
                    color = Color(0xFF667EEA),
                    onClick = { onCategorySelected(null) }
                )

            categories.forEach { category ->
                val stats = categoryStats[category.id] ?: CategoryStats()
                CategoryChip(
                    name = "${category.icon} ${category.name}",
                    count = stats.total,
                    isSelected = selectedCategoryId == category.id,
                    color = Color(android.graphics.Color.parseColor(category.color)),
                    onClick = { onCategorySelected(category.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryChip(
    name: String,
    count: Int,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color else Color(0xFFF5F5F5)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else color
            )
            if (count > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "($count)",
                    fontSize = 12.sp,
                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else color.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun EmptyTodoState(
    hasCategories: Boolean,
    showCompletedOnly: Boolean,
    onToggleCompleted: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (showCompletedOnly) "📭" else if (hasCategories) "📝" else "🏷️",
            fontSize = 64.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = when {
                showCompletedOnly -> "暂无已完成的TODO"
                !hasCategories -> "还没有分类，先创建一个分类吧"
                else -> "还没有TODO，添加一个开始吧"
            },
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (showCompletedOnly) {
            TextButton(onClick = onToggleCompleted) {
                Text("查看全部TODO", color = Color(0xFF667EEA))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoItemCard(
    todo: TodoItem,
    onToggle: (Long, Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: (TodoItem) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
            animationSpec = tween(300),
            initialOffsetY = { 20 }
        )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 完成状态复选框
                    IconButton(
                        onClick = { onToggle(todo.id, todo.isCompleted) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (todo.isCompleted) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = if (todo.isCompleted) "已完成" else "未完成",
                            tint = if (todo.isCompleted) Color(0xFF10B981) else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = todo.title,
                            fontSize = 14.sp,
                            fontWeight = if (todo.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                            color = if (todo.isCompleted) Color.Gray else Color(0xFF333333),
                            textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null,
                            maxLines = 1
                        )

                        if (todo.description.isNotEmpty()) {
                            Text(
                                text = todo.description,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                maxLines = 1
                            )
                        }

                        // 优先级和截止时间
                        Row(
                            modifier = Modifier.padding(top = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PriorityBadge(priority = todo.priority)
                            if (todo.dueDate != null) {
                                DueDateBadge(dueDate = todo.dueDate)
                            }
                        }
                    }
                }

                // 操作按钮
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "编辑",
                            tint = Color(0xFF667EEA),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = { onDelete(todo) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "删除",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriorityBadge(priority: TodoPriority) {
    val (text, color) = when (priority) {
        TodoPriority.HIGH -> "高" to Color(0xFFEF4444)
        TodoPriority.MEDIUM -> "中" to Color(0xFFF59E0B)
        TodoPriority.LOW -> "低" to Color(0xFF10B981)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DueDateBadge(dueDate: Long) {
    val now = System.currentTimeMillis()
    val isOverdue = dueDate < now

    Card(
        colors = CardDefaults.cardColors(containerColor = if (isOverdue) Color(0xFFEF4444).copy(alpha = 0.1f) else Color(0xFF667EEA).copy(alpha = 0.1f)),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = "📅 ${formatDueDate(dueDate)}",
            fontSize = 10.sp,
            color = if (isOverdue) Color(0xFFEF4444) else Color(0xFF667EEA),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

private fun formatDueDate(dueDate: Long): String {
    val now = System.currentTimeMillis()
    val diff = dueDate - now
    val days = diff / (1000 * 60 * 60 * 24)

    return when {
        days < 0 -> "已过期"
        days == 0L -> "今天"
        days == 1L -> "明天"
        days <= 7L -> "${days}天后"
        else -> {
            val date = java.util.Date(dueDate)
            val format = java.text.SimpleDateFormat("MM-dd", java.util.Locale.getDefault())
            format.format(date)
        }
    }
}