package com.app.huisu.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.huisu.data.entity.Achievement
import com.app.huisu.ui.affirmation.AffirmationRecordsScreen
import com.app.huisu.ui.affirmation.AffirmationScreen
import com.app.huisu.ui.affirmation.AffirmationSettingsScreen
import com.app.huisu.ui.affirmation.AffirmationTimerScreen
import com.app.huisu.ui.meditation.MeditationRecordsScreen
import com.app.huisu.ui.meditation.MeditationScreen
import com.app.huisu.ui.meditation.MeditationTimerScreen
import com.app.huisu.ui.statistics.StatisticsScreen
import com.app.huisu.ui.statistics.StatisticsViewModel
import com.app.huisu.ui.theme.Purple667
import com.app.huisu.ui.theme.Purple764
import androidx.compose.foundation.shape.RoundedCornerShape
import com.app.huisu.ui.video.VideoSettingsScreen
import com.app.huisu.ui.hotsearch.HotSearchScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScaffold(navController: NavHostController) {
    // 使用共享的 ViewModel 来检测成就解锁
    val statisticsViewModel: StatisticsViewModel = hiltViewModel()
    val uiState by statisticsViewModel.uiState.collectAsState()

    // 共享的 AffirmationViewModel - 确保主页和计时页面使用同一个实例
    val affirmationViewModel: com.app.huisu.ui.affirmation.AffirmationViewModel = hiltViewModel()

    // 用于主页面滑动切换的Pager状态 (5个页面)
    val pagerState = rememberPagerState(initialPage = 0) { 5 }
    val coroutineScope = rememberCoroutineScope()

    // 监听导航变化来同步Pager
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 用于避免循环触发的标志
    var isNavigating by remember { mutableStateOf(false) }

    // 根据路由更新pager位置
    LaunchedEffect(currentRoute) {
        if (!isNavigating) {
            when (currentRoute) {
                Screen.Meditation.route -> if (pagerState.currentPage != 0) {
                    isNavigating = true
                    pagerState.animateScrollToPage(0)
                    isNavigating = false
                }
                Screen.Affirmation.route -> if (pagerState.currentPage != 1) {
                    isNavigating = true
                    pagerState.animateScrollToPage(1)
                    isNavigating = false
                }
                Screen.Todo.route -> if (pagerState.currentPage != 2) {
                    isNavigating = true
                    pagerState.animateScrollToPage(2)
                    isNavigating = false
                }
                Screen.HotSearch.route -> if (pagerState.currentPage != 3) {
                    isNavigating = true
                    pagerState.animateScrollToPage(3)
                    isNavigating = false
                }
                Screen.Statistics.route -> if (pagerState.currentPage != 4) {
                    isNavigating = true
                    pagerState.animateScrollToPage(4)
                    isNavigating = false
                }
            }
        }
    }

    // 监听pager滑动来同步导航
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        // 只在滑动结束后同步导航，避免滑动过程中触发
        if (!pagerState.isScrollInProgress && !isNavigating) {
            val targetRoute = when (pagerState.currentPage) {
                0 -> Screen.Meditation.route
                1 -> Screen.Affirmation.route
                2 -> Screen.Todo.route
                3 -> Screen.HotSearch.route
                4 -> Screen.Statistics.route
                else -> Screen.Meditation.route
            }
            if (currentRoute != targetRoute) {
                isNavigating = true
                navController.navigate(targetRoute) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
                isNavigating = false
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                onNavigate = { page ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(page)
                    }
                }
            )
        }
    ) { paddingValues ->
        // 检查当前是否在子页面
        val currentRoute = navBackStackEntry?.destination?.route
        val isMainPage = currentRoute in listOf(
            Screen.Meditation.route,
            Screen.Affirmation.route,
            Screen.Todo.route,
            Screen.HotSearch.route,
            Screen.Statistics.route
        )

        if (isMainPage) {
            // 主页面 - 使用HorizontalPager
            Box(modifier = Modifier.padding(paddingValues)) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = false, // 禁用滑动切换，避免误触
                    beyondBoundsPageCount = 0 // 只预加载当前页，减少资源占用
                ) { page ->
                    when (page) {
                        0 -> MeditationScreen(
                            onNavigateToTimer = {
                                navController.navigate(Screen.MeditationTimer.route)
                            },
                            onNavigateToVideoSettings = {
                                navController.navigate(Screen.VideoSettings.route)
                            },
                            onNavigateToRecords = {
                                navController.navigate(Screen.MeditationRecords.route)
                            }
                        )
                        1 -> com.app.huisu.ui.affirmation.AffirmationScreen(
                            viewModel = affirmationViewModel,
                            onNavigateToTimer = {
                                navController.navigate(Screen.AffirmationTimer.route)
                            },
                            onNavigateToSettings = {
                                navController.navigate(Screen.AffirmationSettings.route)
                            },
                            onNavigateToRecords = {
                                navController.navigate(Screen.AffirmationRecords.route)
                            },
                            onNavigateToManagement = {
                                navController.navigate(Screen.AffirmationManagement.route)
                            }
                        )
                        2 -> com.app.huisu.ui.todo.TodoScreen(
                            onNavigateToDetail = { todoId ->
                                navController.navigate(Screen.TodoDetail.createRoute(todoId))
                            },
                            onNavigateToCategoryManagement = {
                                navController.navigate(Screen.TodoCategoryManagement.route)
                            }
                        )
                        3 -> HotSearchScreen()
                        4 -> StatisticsScreen()
                    }
                }
            }
        } else {
            // 子页面 - 使用NavHost
            NavHost(
                navController = navController,
                startDestination = Screen.Meditation.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                // 主页面占位
                composable(Screen.Meditation.route) { }
                composable(Screen.Affirmation.route) { }
                composable(Screen.Todo.route) { }
                composable(Screen.HotSearch.route) { }
                composable(Screen.Statistics.route) { }

                composable(Screen.MeditationTimer.route) {
                    MeditationTimerScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.MeditationRecords.route) {
                    MeditationRecordsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.VideoSettings.route) {
                    VideoSettingsScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.AffirmationTimer.route) {
                    AffirmationTimerScreen(
                        viewModel = affirmationViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.AffirmationRecords.route) {
                    AffirmationRecordsScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.AffirmationSettings.route) {
                    AffirmationSettingsScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.AffirmationManagement.route) {
                    com.app.huisu.ui.affirmation.AffirmationManagementScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.TodoDetail.route) { backStackEntry ->
                    val todoId = backStackEntry.arguments?.getString("todoId")?.toLongOrNull() ?: 0L
                    com.app.huisu.ui.todo.TodoDetailScreen(
                        todoId = todoId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.TodoCategoryManagement.route) {
                    com.app.huisu.ui.todo.TodoCategoryManagementScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }

        // 全局成就解锁动画 - 在任何页面都能显示
        uiState.newlyUnlockedAchievement?.let { achievement ->
            AchievementUnlockDialog(
                achievement = achievement,
                onDismiss = { statisticsViewModel.dismissUnlockAnimation() }
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(
    navController: NavHostController,
    onNavigate: (Int) -> Unit
) {
    val items = listOf(
        BottomNavItem.Meditation,
        BottomNavItem.Affirmation,
        BottomNavItem.Todo,
        BottomNavItem.HotSearch,
        BottomNavItem.Statistics
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // 顶部分隔线
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE0E0E0))
        )

        // 导航栏内容 - 超紧凑版
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 4.dp),  // 从 6.dp 减小到 4.dp
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            items.forEachIndexed { index, item ->
                val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            onClick = { onNavigate(index) },
                            indication = null,
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp),  // 从 8dp/6dp 改为 6dp/4dp
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .then(
                                if (selected) {
                                    Modifier
                                        .background(
                                            color = Color(0xFFF8F9FF),
                                            shape = RoundedCornerShape(6.dp)  // 从 8.dp 减小到 6.dp
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)  // 从 12dp/6dp 减小到 10dp/4dp
                                } else {
                                    Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                }
                            )
                    ) {
                        // 图标 - 超紧凑版
                        Text(
                            text = item.icon,
                            fontSize = 18.sp,  // 从 20.sp 减小到 18.sp
                            color = if (selected) Purple667 else Color.Gray
                        )

                        Spacer(modifier = Modifier.height(2.dp))  // 从 3.dp 减小到 2.dp

                        // 标签
                        Text(
                            text = item.label,
                            fontSize = 10.sp,  // 从 11.sp 减小到 10.sp
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) Purple667 else Color.Gray
                        )

                        // 底部渐变指示器 - 超紧凑版
                        if (selected) {
                            Spacer(modifier = Modifier.height(1.dp))  // 从 2.dp 减小到 1.dp
                            Box(
                                modifier = Modifier
                                    .width(20.dp)  // 从 24.dp 减小到 20.dp
                                    .height(2.dp)  // 保持 2.dp
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF667EEA),
                                                Color(0xFF764BA2)
                                            )
                                        ),
                                        shape = RoundedCornerShape(1.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
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
