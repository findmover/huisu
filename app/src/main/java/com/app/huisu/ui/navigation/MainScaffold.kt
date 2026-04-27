package com.app.huisu.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.app.huisu.ui.cloud.CloudSyncScreen
import com.app.huisu.ui.meditation.MeditationRecordsScreen
import com.app.huisu.ui.meditation.MeditationScreen
import com.app.huisu.ui.meditation.MeditationTimerScreen
import com.app.huisu.ui.quicknote.QuickNoteScreen
import com.app.huisu.ui.statistics.StatisticsScreen
import com.app.huisu.ui.statistics.StatisticsViewModel
import com.app.huisu.ui.theme.Mist400
import com.app.huisu.ui.theme.Purple667
import com.app.huisu.ui.video.VideoSettingsScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScaffold(navController: NavHostController) {
    val statisticsViewModel: StatisticsViewModel = hiltViewModel()
    val uiState by statisticsViewModel.uiState.collectAsState()
    val affirmationViewModel: com.app.huisu.ui.affirmation.AffirmationViewModel = hiltViewModel()

    val pagerState = rememberPagerState(initialPage = 0) { 5 }
    val coroutineScope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var isNavigating by remember { mutableStateOf(false) }

    LaunchedEffect(currentRoute) {
        if (!isNavigating) {
            when (getTopLevelRoute(currentRoute)) {
                Screen.QuickNote.route -> if (pagerState.currentPage != 0) {
                    isNavigating = true
                    pagerState.animateScrollToPage(0)
                    isNavigating = false
                }

                Screen.Meditation.route -> if (pagerState.currentPage != 1) {
                    isNavigating = true
                    pagerState.animateScrollToPage(1)
                    isNavigating = false
                }

                Screen.Affirmation.route -> if (pagerState.currentPage != 2) {
                    isNavigating = true
                    pagerState.animateScrollToPage(2)
                    isNavigating = false
                }

                Screen.Todo.route -> if (pagerState.currentPage != 3) {
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

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress && !isNavigating) {
            val targetRoute = when (pagerState.currentPage) {
                0 -> Screen.QuickNote.route
                1 -> Screen.Meditation.route
                2 -> Screen.Affirmation.route
                3 -> Screen.Todo.route
                4 -> Screen.Statistics.route
                else -> Screen.QuickNote.route
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
                currentRoute = currentRoute,
                onNavigate = { page ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(page)
                    }
                }
            )
        }
    ) { paddingValues ->
        val route = navBackStackEntry?.destination?.route
        val isMainPage = route in listOf(
            Screen.QuickNote.route,
            Screen.Meditation.route,
            Screen.Affirmation.route,
            Screen.Todo.route,
            Screen.Statistics.route
        )

        if (isMainPage) {
            Box(modifier = Modifier.padding(paddingValues)) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    userScrollEnabled = false,
                    beyondBoundsPageCount = 0
                ) { page ->
                    when (page) {
                        0 -> QuickNoteScreen()

                        1 -> MeditationScreen(
                            onNavigateToTimer = { navController.navigate(Screen.MeditationTimer.route) },
                            onNavigateToVideoSettings = { navController.navigate(Screen.VideoSettings.route) },
                            onNavigateToRecords = { navController.navigate(Screen.MeditationRecords.route) }
                        )

                        2 -> AffirmationScreen(
                            viewModel = affirmationViewModel,
                            onNavigateToTimer = { navController.navigate(Screen.AffirmationTimer.route) },
                            onNavigateToSettings = { navController.navigate(Screen.AffirmationSettings.route) },
                            onNavigateToRecords = { navController.navigate(Screen.AffirmationRecords.route) },
                            onNavigateToManagement = { navController.navigate(Screen.AffirmationManagement.route) }
                        )

                        3 -> com.app.huisu.ui.todo.TodoScreen(
                            onNavigateToDetail = { todoId ->
                                navController.navigate(Screen.TodoDetail.createRoute(todoId))
                            },
                            onNavigateToCategoryManagement = {
                                navController.navigate(Screen.TodoCategoryManagement.route)
                            }
                        )

                        4 -> StatisticsScreen(
                            onNavigateToCloudSync = {
                                navController.navigate(Screen.CloudSync.route)
                            }
                        )
                    }
                }
            }
        } else {
            NavHost(
                navController = navController,
                startDestination = Screen.QuickNote.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screen.QuickNote.route) { }
                composable(Screen.Meditation.route) { }
                composable(Screen.Affirmation.route) { }
                composable(Screen.Todo.route) { }
                composable(Screen.Statistics.route) { }

                composable(Screen.MeditationTimer.route) {
                    MeditationTimerScreen(onBack = { navController.popBackStack() })
                }

                composable(Screen.MeditationRecords.route) {
                    MeditationRecordsScreen(onBack = { navController.popBackStack() })
                }

                composable(Screen.VideoSettings.route) {
                    VideoSettingsScreen(onNavigateBack = { navController.popBackStack() })
                }

                composable(Screen.AffirmationTimer.route) {
                    AffirmationTimerScreen(
                        viewModel = affirmationViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.AffirmationRecords.route) {
                    AffirmationRecordsScreen(onBack = { navController.popBackStack() })
                }

                composable(Screen.AffirmationSettings.route) {
                    AffirmationSettingsScreen(onNavigateBack = { navController.popBackStack() })
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

                composable(Screen.CloudSync.route) {
                    CloudSyncScreen()
                }
            }
        }

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
    currentRoute: String?,
    onNavigate: (Int) -> Unit
) {
    val items = listOf(
        BottomNavItem.QuickNote,
        BottomNavItem.Meditation,
        BottomNavItem.Affirmation,
        BottomNavItem.Todo,
        BottomNavItem.Statistics
    )

    Surface(
        color = Color.White.copy(alpha = 0.94f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val selected = item.route == getTopLevelRoute(currentRoute)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            onClick = { onNavigate(index) },
                            indication = null,
                            interactionSource = remember {
                                androidx.compose.foundation.interaction.MutableInteractionSource()
                            }
                        )
                        .background(
                            color = if (selected) Purple667.copy(alpha = 0.12f) else Color.Transparent,
                            shape = RoundedCornerShape(18.dp)
                        )
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selected) Purple667 else Color(0xFF97A09A),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (selected) Purple667 else Color(0xFF97A09A)
                    )
                    Box(
                        modifier = Modifier
                            .width(14.dp)
                            .height(2.dp)
                            .background(
                                brush = if (selected) {
                                    Brush.horizontalGradient(listOf(Purple667, Mist400))
                                } else {
                                    Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                                },
                                shape = RoundedCornerShape(999.dp)
                            )
                    )
                }
            }
        }
    }
}

private fun getTopLevelRoute(route: String?): String? {
    return when (route) {
        Screen.QuickNote.route -> Screen.QuickNote.route

        Screen.Meditation.route,
        Screen.MeditationTimer.route,
        Screen.MeditationRecords.route,
        Screen.VideoSettings.route -> Screen.Meditation.route

        Screen.Affirmation.route,
        Screen.AffirmationTimer.route,
        Screen.AffirmationRecords.route,
        Screen.AffirmationSettings.route,
        Screen.AffirmationManagement.route -> Screen.Affirmation.route

        Screen.Todo.route,
        Screen.TodoDetail.route,
        Screen.TodoCategoryManagement.route -> Screen.Todo.route

        Screen.Statistics.route,
        Screen.CloudSync.route -> Screen.Statistics.route
        else -> route
    }
}

@Composable
private fun AchievementUnlockDialog(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    val levelColor = Color(achievement.level.color)
    val unlockMessage = when (achievement.key) {
        "streak" -> "你已经连续完成 ${achievement.targetValue} 天。"
        "meditation_count" -> "你已经完成 ${achievement.targetValue} 次冥想。"
        "meditation_duration" -> "你累计冥想 ${achievement.targetValue / 3600} 小时。"
        "affirmation_count" -> "你已经完成 ${achievement.targetValue} 次默念。"
        else -> "你解锁了一枚新的成长成就。"
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
                Text(
                    text = achievement.icon,
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = 72.sp
                )
                Text(
                    text = "成就解锁",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${achievement.level.icon} ${achievement.level.displayName}",
                    style = MaterialTheme.typography.labelLarge,
                    color = levelColor,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Text(
                    text = achievement.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = unlockMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6A726D),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Purple667, Mist400)
                            ),
                            shape = RoundedCornerShape(18.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "继续前进",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
