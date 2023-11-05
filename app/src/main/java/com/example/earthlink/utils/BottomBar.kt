package com.example.earthlink.utils

import androidx.annotation.DrawableRes
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.earthlink.R
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class Screen(val route: String, @DrawableRes val icon: Int, val label: String) {
    object Home : Screen("home", R.drawable.home_24px, "Home")
    object Friends : Screen("friends", R.drawable.group_24px, "Friends")
    object Profile : Screen("profile", R.drawable.person_24px, "Profile")
    object Settings : Screen("settings", R.drawable.settings_24px, "Settings")
}

val items = listOf(
    Screen.Home,
    Screen.Friends,
    Screen.Profile,
    Screen.Settings
)


// Basic bottom bar with items for home, friends, profile, and settings
@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    BottomNavigation (
        backgroundColor = Color.Transparent,
        contentColor = Color.White,
        elevation = 0.dp
    ) {
        items.forEach { screen ->
            val isSelected = currentDestination?.route == screen.route
            BottomNavigationItem(
                icon = { Icon(painterResource(id = screen.icon), contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                selectedContentColor = Color.Blue, // Change this to your selected color
                unselectedContentColor = Color.White // Change this to your unselected color
            )
        }
    }
}

