package com.app.huisu.ui.hotsearch

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.huisu.data.entity.HotSearchItem
import com.app.huisu.data.entity.HotSearchPlatform

/**
 * 热搜榜单页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotSearchScreen(
    viewModel: HotSearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 16.dp, vertical = 12.dp)  // 从 20.dp 改为 16dp/12dp，减少白色遮挡
    ) {
        // 顶部超紧凑型标题栏
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(12.dp),  // 从 16.dp 减小到 12.dp
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)  // 从 4.dp 减小到 2.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFF6B6B),
                                Color(0xFFFF8E53)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp)  // 从 20dp/15dp 减小到 16dp/10dp
            ) {
                Column {
                    // 标题和刷新按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🔥 热搜榜单",
                                fontSize = 18.sp,  // 从 20.sp 减小到 18.sp
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))  // 从 3.dp 减小到 2.dp
                            Text(
                                text = "实时热点 · 一手掌握",
                                fontSize = 11.sp,  // 从 12.sp 减小到 11.sp
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        // 刷新按钮 - 超紧凑版
                        RefreshButton(
                            onClick = { viewModel.refresh() },
                            isLoading = uiState.isLoading,
                            modifier = Modifier.size(36.dp)  // 从 40.dp 减小到 36.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))  // 从 12.dp 减小到 8.dp

                    // 平台标签选择器 - 紧凑版
                    PlatformTabs(
                        selectedPlatform = uiState.selectedPlatform,
                        onPlatformSelected = { viewModel.selectPlatform(it) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))  // 从 15.dp 减小到 10.dp

        // 加载状态
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF667EEA))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "加载中...", fontSize = 18.sp, color = Color(0xFF667EEA))
                }
            }
        }

        // 错误提示
        uiState.errorMessage?.let { errorMsg ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4757)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "加载失败: $errorMsg",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // 3秒后自动隐藏错误提示
            LaunchedEffect(errorMsg) {
                kotlinx.coroutines.delay(3000)
                viewModel.clearError()
            }
        }

        // 热搜列表 - 超紧凑版
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp)  // 从 8.dp 减小到 6.dp
        ) {
            items(uiState.hotSearchList) { item ->
                HotSearchItemCard(
                    item = item,
                    onClick = {
                        // 在浏览器中打开链接
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

/**
 * 平台标签选择器 - 横向滚动版
 */
@Composable
private fun PlatformTabs(
    selectedPlatform: HotSearchPlatform,
    onPlatformSelected: (HotSearchPlatform) -> Unit
) {
    // 横向滚动布局
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PlatformTab(HotSearchPlatform.WEIBO, selectedPlatform, onPlatformSelected)
        PlatformTab(HotSearchPlatform.ZHIHU, selectedPlatform, onPlatformSelected)
        PlatformTab(HotSearchPlatform.WEIXIN, selectedPlatform, onPlatformSelected)
        PlatformTab(HotSearchPlatform.TOUTIAO, selectedPlatform, onPlatformSelected)
        PlatformTab(HotSearchPlatform.DOUYIN, selectedPlatform, onPlatformSelected)
        PlatformTab(HotSearchPlatform.BILIBILI, selectedPlatform, onPlatformSelected)
        PlatformTab(HotSearchPlatform.HISTORY, selectedPlatform, onPlatformSelected)
    }
}

/**
 * 单个平台标签 - 固定宽度
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlatformTab(
    platform: HotSearchPlatform,
    selectedPlatform: HotSearchPlatform,
    onPlatformSelected: (HotSearchPlatform) -> Unit
) {
    val isSelected = platform == selectedPlatform

    Card(
        onClick = { onPlatformSelected(platform) },
        modifier = Modifier.width(75.dp),  // 固定宽度
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f)
        ),
        border = BorderStroke(
            width = 1.5.dp,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = platform.icon,
                fontSize = 16.sp,
                color = if (isSelected) Color(0xFFFF6B6B) else Color.White
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = platform.displayName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color(0xFFFF6B6B) else Color.White
            )
        }
    }
}

/**
 * 刷新按钮组件
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RefreshButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    // 旋转动画
    val infiniteTransition = rememberInfiniteTransition(label = "refresh")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Card(
        onClick = onClick,
        modifier = modifier.size(48.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.25f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        ),
        enabled = !isLoading
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "刷新",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(if (isLoading) rotation else 0f)
            )
        }
    }
}

/**
 * 热搜条目卡片 - 超紧凑版
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HotSearchItemCard(
    item: HotSearchItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),  // 从 12.dp 减小到 10.dp
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,  // 从 2.dp 减小到 1.dp
            pressedElevation = 0.dp,  // 从 1.dp 减小到 0.dp
            hoveredElevation = 3.dp   // 从 4.dp 减小到 3.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),  // 从 14dp/12dp 减小到 12dp/10dp
            horizontalArrangement = Arrangement.spacedBy(10.dp),  // 从 12.dp 减小到 10.dp
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 排名徽章 - 使用渐变背景 - 超紧凑版
            Box(
                modifier = Modifier
                    .size(26.dp)  // 从 30.dp 减小到 26.dp
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = when (item.index) {
                                1 -> listOf(Color(0xFFFF6B6B), Color(0xFFFF4757))
                                2 -> listOf(Color(0xFFFF8E53), Color(0xFFFF6348))
                                3 -> listOf(Color(0xFFFFA502), Color(0xFFFF8800))
                                else -> listOf(Color(0xFFFFD93D), Color(0xFFFFAA00))
                            }
                        ),
                        shape = RoundedCornerShape(5.dp)  // 从 6.dp 减小到 5.dp
                    )
                    .shadow(1.dp, RoundedCornerShape(5.dp)),  // 从 2.dp 减小到 1.dp
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.index.toString(),
                    fontSize = if (item.index == 1) 17.sp else 16.sp,  // 从 20/18.sp 减小到 17/16.sp
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // 内容区域
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)  // 从 4.dp 减小到 3.dp
            ) {
                // 标题 - 支持最多2行显示
                Text(
                    text = item.title,
                    fontSize = 14.sp,  // 从 15.sp 减小到 14.sp
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    lineHeight = 18.sp,  // 从 20.sp 减小到 18.sp
                    maxLines = 2,  // 最多显示2行
                    overflow = TextOverflow.Ellipsis
                )

                // 热度值
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),  // 从 3.dp 减小到 2.dp
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🔥", fontSize = 11.sp)  // 从 12.sp 减小到 11.sp
                    Text(
                        text = item.hotValue,
                        fontSize = 11.sp,  // 从 12.sp 减小到 11.sp
                        color = Color(0xFF999999)
                    )
                }
            }

            // 箭头图标 - 超紧凑版
            Text(
                text = "›",
                fontSize = 18.sp,  // 从 20.sp 减小到 18.sp
                color = Color(0xFFDDDDDD),
                fontWeight = FontWeight.Light
            )
        }
    }
}
