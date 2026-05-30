package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.HomeScreen
import com.example.ui.PlatformScreen
import com.example.ui.SettingsScreen
import com.example.ui.TrendingDetailScreen
import com.example.ui.FocusModeSettingsScreen
import com.example.ui.RelatedContentScreen
 
/**
 * Navigation graph setting up all screens and passing corresponding callbacks.
 */
@Composable
fun SocialNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onPlatformClick = { platformId ->
                    navController.navigate(Screen.Platform.createRoute(platformId))
                },
                onTrendClick = { trendId ->
                    navController.navigate(Screen.TrendingDetail.createRoute(trendId))
                }
            )
        }
        
        composable(
            route = Screen.Platform.route,
            arguments = listOf(
                navArgument("platformId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val platformId = backStackEntry.arguments?.getString("platformId") ?: ""
            PlatformScreen(
                platformId = platformId,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onFocusModeSettingsClick = {
                    navController.navigate(Screen.FocusModeSettings.route)
                },
                onRelatedContentClick = {
                    navController.navigate(Screen.RelatedContent.route)
                }
            )
        }
        
        composable(Screen.RelatedContent.route) {
            RelatedContentScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.FocusModeSettings.route) {
            FocusModeSettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.TrendingDetail.route,
            arguments = listOf(
                navArgument("trendId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val trendId = backStackEntry.arguments?.getString("trendId") ?: ""
            TrendingDetailScreen(
                trendId = trendId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
