package com.app.huisu.ui.affirmation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.app.huisu.ui.theme.Purple667

/**
 * 暗示语管理独立页面
 * 功能：暗示语的增删改查独立管理
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AffirmationManagementScreen(
    viewModel: AffirmationSettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "暗示语管理",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // 暗示语列表
                if (uiState.affirmations.isEmpty()) {
                    // 空状态
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "📝",
                                fontSize = 64.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                text = "暂无暗示语",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF666666)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "点击下方按钮添加您的第一条暗示语",
                                fontSize = 14.sp,
                                color = Color(0xFF999999)
                            )
                        }
                    }
                } else {
                    // 暗示语列表
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.affirmations) { affirmation ->
                            AffirmationManagementItem(
                                affirmation = affirmation,
                                onEdit = { viewModel.showEditDialog(affirmation) },
                                onDelete = { viewModel.deleteAffirmation(affirmation) },
                                onSelect = { viewModel.selectAffirmation(affirmation.id) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 添加按钮
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
                        text = "➕ 添加暗示语",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
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
        }
    }
}

@Composable
fun AffirmationManagementItem(
    affirmation: Affirmation,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSelect: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        colors = CardDefaults.cardColors(
            containerColor = if (affirmation.isSelected) Color(0xFFF0F4FF) else Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (affirmation.isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF667EEA))
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 选中指示器
            if (affirmation.isSelected) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF667EEA), shape = androidx.compose.foundation.shape.CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = affirmation.content,
                        fontSize = 16.sp,
                        fontWeight = if (affirmation.isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (affirmation.isSelected) Color(0xFF667EEA) else Color(0xFF333333),
                        maxLines = 3,
                        modifier = Modifier.weight(1f)
                    )
                    if (affirmation.isSelected) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✓ 当前",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier
                                .background(Color(0xFF667EEA), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "创建于: ${formatTimestamp(affirmation.createdAt)}",
                    fontSize = 12.sp,
                    color = Color(0xFF999999)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 编辑按钮
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(40.dp)
                ) {
                    Text(
                        text = "✏️",
                        fontSize = 20.sp
                    )
                }

                // 删除按钮
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(40.dp)
                ) {
                    Text(
                        text = "🗑️",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}
