package com.example.earthlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.*
import com.example.earthlink.ui.*
import com.example.earthlink.ui.theme.EarthLinkTheme
import com.example.earthlink.utils.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

class MainActivity : ComponentActivity() {
    private val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EarthLinkTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var hasLocationPermission by remember {
                        mutableStateOf(checkForPermission(this))
                    }

                    // Determine if the bottom bar should be shown
                    val shouldShowBottomBar = navController.currentBackStackEntryAsState().value?.destination?.route in listOf(
                        Screen.Home.route,
                        Screen.Friends.route,
                        Screen.Profile.route,
                        Screen.Settings.route
                    )

                    Scaffold(
                        bottomBar = {
                            if (shouldShowBottomBar) {
                                BottomNavigationBar(navController = navController)
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "LoginScreen",
                            Modifier.padding(innerPadding)
                        ) {
                            composable("LoginScreen") {
                                LoginScreen(
                                    onPermissionGranted = {
                                        hasLocationPermission = true
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo("LoginScreen") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable(Screen.Home.route) { Main(navigation = navController, dataStore) }
                            composable(Screen.Profile.route) { ProfileScreen(navigation = navController) }
                            composable(Screen.Friends.route) { FriendScreen(navigation = navController) }
                            composable(Screen.Settings.route) { SettingsScreen(navigation = navController, dataStore) }
                            composable("EditProfileScreen"){ EditProfileScreen(navigation = navController) }
                            // Add other composable destinations as needed here
                        }
                    }
                }
            }
        }
    }
}
