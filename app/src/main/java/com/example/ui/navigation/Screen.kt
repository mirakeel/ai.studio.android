package com.example.ui.navigation

/**
 * Route definitions for the SocialDash navigation architecture which map
 * directly to screens in our application.
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    
    object Platform : Screen("platform/{platformId}") {
        fun createRoute(platformId: String) = "platform/$platformId"
    }
    
    object Settings : Screen("settings")
    
    object FocusModeSettings : Screen("focus_mode_settings")
    
    object RelatedContent : Screen("related_content")
    
    object TrendingDetail : Screen("trending/{trendId}") {
        fun createRoute(trendId: String) = "trending/$trendId"
    }
}
