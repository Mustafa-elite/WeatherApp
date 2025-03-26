package com.example.weatherforcast

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector


data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

sealed class Screen(val rout: String) {
    object Splash: Screen("splash_screen")
    object Home: Screen("home_screen")
    object Favourites: Screen("favourites_screen")
    object Alerts: Screen("alerts_screen")
    object Setting: Screen("setting_screen")
}


val navigationItems = listOf(
    NavigationItem(
        title = "Home",
        icon = Icons.Default.Home,
        route = Screen.Home.rout
    ),
    NavigationItem(
        title = "Favourites",
        icon = Icons.Default.Favorite,
        route = Screen.Favourites.rout
    ),
    NavigationItem(
        title = "Alerts",
        icon = Icons.Default.Notifications,
        route = Screen.Alerts.rout
    ),
    NavigationItem(
        title = "Setting",
        icon = Icons.Default.Settings,
        route = Screen.Setting.rout
    )
)