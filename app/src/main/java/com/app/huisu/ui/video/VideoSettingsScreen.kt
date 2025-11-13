package com.app.huisu.ui.video

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
import com.app.huisu.data.entity.VideoLink
import com.app.huisu.ui.components.SecondaryButton
import com.app.huisu.ui.theme.Purple667

@Composable
fun VideoSettingsScreen(
    viewModel: VideoSettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // 标题
        Text(
            text = stringResource(R.string.video_link_management),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // 视频链接列表
        uiState.videoLinks.forEach { videoLink ->
            VideoLinkCard(
                videoLink = videoLink,
                isSelected = videoLink.isDefault,
                onSelect = { viewModel.setDefaultVideoLink(videoLink) },
                onEdit = { viewModel.showEditDialog(videoLink) },
                onDelete = { viewModel.deleteVideoLink(videoLink) }
            )
            Spacer(modifier = Modifier.height(15.dp))
        }

        // 添加新链接按钮
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
                text = "+ ${stringResource(R.string.add_new_link)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 返回按钮
        SecondaryButton(
            text = stringResource(R.string.back),
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        )
    }

    // 添加对话框
    if (uiState.showAddDialog) {
        VideoLinkDialog(
            title = stringResource(R.string.add_video_link),
            initialTitle = "",
            initialLink = "",
            onConfirm = { title, link ->
                viewModel.addVideoLink(title, link)
                viewModel.hideAddDialog()
            },
            onDismiss = { viewModel.hideAddDialog() }
        )
    }

    // 编辑对话框
    if (uiState.showEditDialog && uiState.editingVideoLink != null) {
        VideoLinkDialog(
            title = stringResource(R.string.edit_video_link),
            initialTitle = uiState.editingVideoLink!!.title,
            initialLink = uiState.editingVideoLink!!.link,
            onConfirm = { title, link ->
                viewModel.updateVideoLink(
                    uiState.editingVideoLink!!.copy(
                        title = title,
                        link = link
                    )
                )
                viewModel.hideEditDialog()
            },
            onDismiss = { viewModel.hideEditDialog() }
        )
    }
}

@Composable
fun VideoLinkCard(
    videoLink: VideoLink,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .border(
                width = 2.dp,
                color = if (isSelected) Purple667 else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF8F9FF) else Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = videoLink.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                Text(
                    text = videoLink.link,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = Purple667
                    ),
                    maxLines = 1
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

@Composable
fun VideoLinkDialog(
    title: String,
    initialTitle: String,
    initialLink: String,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var linkTitle by remember { mutableStateOf(initialTitle) }
    var linkUrl by remember { mutableStateOf(initialLink) }

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

                // 标题输入框
                Text(
                    text = stringResource(R.string.link_title),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = linkTitle,
                    onValueChange = { linkTitle = it },
                    placeholder = { Text(stringResource(R.string.enter_link_title)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple667,
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )

                // 链接输入框
                Text(
                    text = stringResource(R.string.video_link),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = linkUrl,
                    onValueChange = { linkUrl = it },
                    placeholder = { Text(stringResource(R.string.enter_video_link)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple667,
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )

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
                            if (linkTitle.isNotBlank() && linkUrl.isNotBlank()) {
                                onConfirm(linkTitle, linkUrl)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Purple667
                        ),
                        shape = RoundedCornerShape(10.dp),
                        enabled = linkTitle.isNotBlank() && linkUrl.isNotBlank()
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}
