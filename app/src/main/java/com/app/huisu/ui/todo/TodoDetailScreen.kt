package com.app.huisu.ui.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.huisu.data.entity.TodoItem
import com.app.huisu.data.entity.TodoPriority
import com.app.huisu.ui.components.PrimaryButton
import com.app.huisu.ui.components.SecondaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    todoId: Long,
    viewModel: TodoViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var todo by remember { mutableStateOf<TodoItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(todoId, uiState.todos) {
        // 查找TODO项目，并同步更新本地的todo
        val foundTodo = uiState.todos.find { it.id == todoId }
        todo = foundTodo
        isLoading = false  // 无论是否找到都设置为false，避免无限加载
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    todo?.let { currentTodo ->
        // 显示编辑对话框
        if (showEditDialog) {
            EditTodoDialog(
                todo = currentTodo,
                categories = uiState.categories,
                onDismiss = { showEditDialog = false },
                onConfirm = { id, title, description, categoryId, priority, dueDate ->
                    viewModel.updateTodo(
                        currentTodo.copy(
                            title = title,
                            description = description,
                            categoryId = categoryId,
                            priority = priority,
                            dueDate = dueDate
                        )
                    )
                    showEditDialog = false
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // 顶部栏
            TopAppBar(
                title = { Text("TODO详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "编辑", tint = Color(0xFF667EEA))
                    }
                    IconButton(
                        onClick = {
                            viewModel.toggleTodoCompletion(currentTodo.id, currentTodo.isCompleted)
                        }
                    ) {
                        Icon(
                            if (currentTodo.isCompleted) Icons.Default.Check else Icons.Default.Check,
                            contentDescription = if (currentTodo.isCompleted) "标记为未完成" else "标记为完成",
                            tint = if (currentTodo.isCompleted) Color(0xFF10B981) else Color.Gray
                        )
                    }
                    IconButton(
                        onClick = {
                            viewModel.deleteTodo(currentTodo)
                            onNavigateBack()
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "删除", tint = Color(0xFFEF4444))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )

            // 内容区域
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 基本信息
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "基本信息",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )

                        // 内容（只显示description）
                        Text(
                            text = "内容",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF666666)
                        )
                        Text(
                            text = currentTodo.description.ifEmpty { currentTodo.title },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333),
                            textDecoration = if (currentTodo.isCompleted) TextDecoration.LineThrough else null,
                            lineHeight = 24.sp
                        )

                        // 状态
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "状态",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = if (currentTodo.isCompleted) "已完成" else "进行中",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentTodo.isCompleted) Color(0xFF10B981) else Color(0xFFF59E0B)
                            )
                        }
                    }
                }

                // 优先级和截止时间
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "优先级和截止时间",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )

                        // 优先级
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "优先级",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF666666)
                            )
                            PriorityBadge(priority = currentTodo.priority)
                        }

                        // 截止时间
                        if (currentTodo.dueDate != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "截止时间",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF666666)
                                )
                                val now = System.currentTimeMillis()
                                val isOverdue = currentTodo.dueDate!! < now
                                Text(
                                    text = formatDueDate(currentTodo.dueDate!!),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOverdue) Color(0xFFEF4444) else Color(0xFF667EEA)
                                )
                            }
                        } else {
                            Text(
                                text = "无截止时间",
                                fontSize = 14.sp,
                                color = Color(0xFF999999)
                            )
                        }
                    }
                }

                // 时间信息
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "时间信息",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "创建时间",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = formatTimestamp(currentTodo.createdAt),
                                fontSize = 14.sp,
                                color = Color(0xFF333333)
                            )
                        }

                        if (currentTodo.completedAt != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "完成时间",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF666666)
                                )
                                Text(
                                    text = formatTimestamp(currentTodo.completedAt),
                                    fontSize = 14.sp,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "更新时间",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = formatTimestamp(currentTodo.updatedAt),
                                fontSize = 14.sp,
                                color = Color(0xFF333333)
                            )
                        }
                    }
                }
            }
        }
    } ?: run {
        // TODO项目不存在
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "📭",
                    fontSize = 64.sp,
                    color = Color.Gray
                )
                Text(
                    text = "TODO项目不存在",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
                SecondaryButton(
                    text = "返回",
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth()
                )
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

private fun formatTimestamp(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}