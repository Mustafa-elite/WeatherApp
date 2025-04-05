package com.example.weatherforcast.MainApp.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavHostController
import com.example.weatherforcast.R

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val selectedNavigationIndex = rememberSaveable {
        mutableIntStateOf(0)
    }
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect {
            val currentRoute = it.destination.route
            val index = navigationItems.indexOfFirst { it.route == currentRoute }
            if (index != -1) {
                selectedNavigationIndex.intValue = index
            }
        }

    }
    NavigationBar(
        containerColor = colorResource(R.color.Default_Colour)
    ) {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedNavigationIndex.intValue == index,
                onClick = {
                    selectedNavigationIndex.intValue = index
                    navController.navigate(item.route) {
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                },
                label = {
                    if (index == selectedNavigationIndex.intValue) {
                        Text(
                            item.title,
                            color = Color.White
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = Color.LightGray,
                    selectedIconColor = Color.White,
                    indicatorColor = colorResource(R.color.Default_Colour2)
                )

            )
        }
    }
}