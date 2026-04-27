package com.app.huisu.ui.cloud

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.huisu.ui.components.GlassCard
import com.app.huisu.ui.components.InfoPill
import com.app.huisu.ui.components.PrimaryButton
import com.app.huisu.ui.components.SecondaryButton
import com.app.huisu.ui.components.ZenBackground
import com.app.huisu.ui.theme.DividerColor
import com.app.huisu.ui.theme.ErrorRed
import com.app.huisu.ui.theme.GlassWhite
import com.app.huisu.ui.theme.Purple667
import com.app.huisu.ui.theme.TextPrimary
import com.app.huisu.ui.theme.TextSecondary
import com.app.huisu.ui.theme.TextTertiary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CloudSyncScreen(
    viewModel: CloudSyncViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    uiState.message?.let { message ->
        LaunchedEffect(message) {
            delay(3200)
            viewModel.clearMessage()
        }
    }

    ZenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(PaddingValues(horizontal = 18.dp, vertical = 18.dp))
                .padding(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            GlassCard {
                Text(
                    text = "云同步",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "本地数据库仍是主数据源。关键操作会把本地快照上传到你的同步 API，也可以手动合并云端快照。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    text = "Token 和快照加密密码只保存在本机加密存储中。当前使用 HTTP 测试地址，正式使用建议给服务器配置 HTTPS。",
                    style = MaterialTheme.typography.bodySmall,
                    color = ErrorRed
                )
            }

            GlassCard {
                CloudTextField(
                    value = uiState.serverUrl,
                    onValueChange = viewModel::updateServerUrl,
                    label = "同步服务地址",
                    placeholder = "http://106.53.73.104:18080"
                )
                CloudTextField(
                    value = uiState.apiToken,
                    onValueChange = viewModel::updateApiToken,
                    label = if (uiState.hasSavedToken) {
                        "API Token（已保存，留空则不修改）"
                    } else {
                        "API Token"
                    },
                    placeholder = if (uiState.hasSavedToken) "已加密保存" else "填写服务器 .env 里的 SYNC_API_TOKEN",
                    password = true
                )
                CloudTextField(
                    value = uiState.deviceId,
                    onValueChange = viewModel::updateDeviceId,
                    label = "设备 ID",
                    placeholder = "android-main"
                )
                CloudTextField(
                    value = uiState.encryptionPassword,
                    onValueChange = viewModel::updateEncryptionPassword,
                    label = if (uiState.hasSavedEncryptionPassword) {
                        "快照加密密码（已保存，留空则不修改）"
                    } else {
                        "快照加密密码"
                    },
                    placeholder = if (uiState.hasSavedEncryptionPassword) {
                        "云端快照将继续加密"
                    } else {
                        "建议设置，用于加密服务器快照"
                    },
                    password = true
                )

                PrimaryButton(
                    text = "保存配置",
                    onClick = viewModel::saveConfig,
                    enabled = !uiState.isBusy
                )
            }

            GlassCard {
                SecondaryButton(
                    text = "测试连接",
                    onClick = viewModel::testConnection,
                    enabled = !uiState.isBusy
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SecondaryButton(
                        text = "上传本地快照",
                        onClick = viewModel::uploadNow,
                        enabled = !uiState.isBusy,
                        modifier = Modifier.weight(1f)
                    )
                    SecondaryButton(
                        text = "合并云端到本地",
                        onClick = viewModel::downloadNow,
                        enabled = !uiState.isBusy,
                        modifier = Modifier.weight(1f)
                    )
                }

                SecondaryButton(
                    text = "强制覆盖云端",
                    onClick = viewModel::forceUploadNow,
                    enabled = !uiState.isBusy
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoPill(
                        label = "云端版本 ${uiState.lastRevision}",
                        modifier = Modifier.weight(1f)
                    )
                    InfoPill(
                        label = "上次上传 ${formatCloudTime(uiState.lastUploadAt)}",
                        modifier = Modifier.weight(1f)
                    )
                }
                InfoPill(
                    label = "上次下载 ${formatCloudTime(uiState.lastDownloadAt)}",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            uiState.message?.let { message ->
                MessageCard(
                    message = message,
                    accent = if (message.startsWith("同步失败")) ErrorRed else Purple667
                )
            }
        }
    }
}

@Composable
private fun CloudTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    password: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = TextTertiary) },
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = GlassWhite,
            focusedContainerColor = GlassWhite,
            unfocusedBorderColor = DividerColor,
            focusedBorderColor = Purple667,
            cursorColor = Purple667
        )
    )
}

@Composable
private fun MessageCard(
    message: String,
    accent: androidx.compose.ui.graphics.Color
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

private fun formatCloudTime(timestamp: Long): String {
    if (timestamp <= 0L) return "未执行"
    return SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(timestamp))
}
