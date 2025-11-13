package com.app.huisu.ui.navigation

sealed class Screen(val route: String) {
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

    object HotSearch : Screen("hot_search")

    object Statistics : Screen("statistics")
}

sealed class BottomNavItem(
    val route: String,
    val icon: String,
    val label: String
) {
    object Meditation : BottomNavItem(Screen.Meditation.route, "🧘", "冥想")
    object Affirmation : BottomNavItem(Screen.Affirmation.route, "💭", "默塑")
    object Todo : BottomNavItem(Screen.Todo.route, "📋", "TODO")
    object HotSearch : BottomNavItem(Screen.HotSearch.route, "🔥", "热搜")
    object Statistics : BottomNavItem(Screen.Statistics.route, "📊", "统计")
}
