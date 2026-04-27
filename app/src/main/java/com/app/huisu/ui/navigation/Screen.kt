package com.app.huisu.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Equalizer
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object QuickNote : Screen("quick_note")

    object Meditation : Screen("meditation")
    object MeditationTimer : Screen("meditation_timer")
    object MeditationRecords : Screen("meditation_records")
    object VideoSettings : Screen("video_settings")

    object Affirmation : Screen("affirmation")
    object AffirmationTimer : Screen("affirmation_timer")
    object AffirmationRecords : Screen("affirmation_records")
    object AffirmationSettings : Screen("affirmation_settings")
    object AffirmationManagement : Screen("affirmation_management")

    object Todo : Screen("todo")
    object TodoDetail : Screen("todo_detail/{todoId}") {
        fun createRoute(todoId: Long) = "todo_detail/$todoId"
    }
    object TodoCategoryManagement : Screen("todo_category_management")

    object CloudSync : Screen("cloud_sync")

    object Statistics : Screen("statistics")
}

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object QuickNote : BottomNavItem(Screen.QuickNote.route, Icons.Outlined.Edit, "速记")
    object Meditation : BottomNavItem(Screen.Meditation.route, Icons.Outlined.SelfImprovement, "冥想")
    object Affirmation : BottomNavItem(Screen.Affirmation.route, Icons.Outlined.AutoAwesome, "默念")
    object Todo : BottomNavItem(Screen.Todo.route, Icons.Outlined.ListAlt, "待办")
    object Statistics : BottomNavItem(Screen.Statistics.route, Icons.Outlined.Equalizer, "统计")
}
