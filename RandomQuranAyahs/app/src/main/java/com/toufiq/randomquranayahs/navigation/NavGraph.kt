package com.toufiq.randomquranayahs.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.toufiq.randomquranayahs.ui.screen.QuranScreen
import com.toufiq.randomquranayahs.ui.screen.SettingsScreen
import kotlinx.serialization.Serializable

// Type-safe route definitions using Kotlin Serialization
@Serializable
data object QuranRoute : NavKey

@Serializable
data object SettingsRoute : NavKey

@Composable
fun AppNavigation() {
    // Create a back stack with the initial route
    val backStack = rememberNavBackStack(QuranRoute)

    // NavDisplay renders the current destination
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<QuranRoute> {
                QuranScreen(
                    onSettingsClick = {
                        backStack.add(SettingsRoute)
                    }
                )
            }

            entry<SettingsRoute> {
                SettingsScreen(
                    onBackClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}
