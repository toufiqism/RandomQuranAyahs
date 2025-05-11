package com.toufiq.randomquranayahs.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.toufiq.randomquranayahs.ui.screen.QuranScreen
import com.toufiq.randomquranayahs.ui.screen.SettingsScreen

sealed class Screen(val route: String) {
    object Quran : Screen("quran")
    object Settings : Screen("settings")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Quran.route
    ) {
        composable(Screen.Quran.route) {
            QuranScreen(
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
} 