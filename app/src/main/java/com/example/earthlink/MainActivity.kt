package com.example.earthlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.earthlink.ui.*
import com.example.earthlink.ui.theme.EarthLinkTheme
import com.example.earthlink.utils.*
import android.content.pm.PackageManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

class MainActivity : ComponentActivity() {
    private val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            EarthLinkTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var hasLocationPermission by remember {
                        mutableStateOf(checkForPermission(this@MainActivity))
                    }

                    Scaffold(
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = if (hasLocationPermission) "login" else "permissions",
                            Modifier.padding(innerPadding)
                        ) {
                            composable("permissions") {
                                LocationPermissionScreen(onPermissionGranted = {
                                    hasLocationPermission = true
                                    navController.navigate("login") {
                                        popUpTo("permissions") { inclusive = true }
                                    }
                                })
                            }
                            composable("login") { LoginScreen(navController) }
                            composable("signup") { SignUpScreen(navController) }

                            // Your existing composable destinations
                            composable(Screen.Home.route) { Main(navigation = navController, dataStore, snackbarHostState) }
                            composable(Screen.Profile.route) { ProfileScreen(navigation = navController, dataStore) }
                            composable(Screen.Friends.route) { FriendScreen(navigation = navController) }
                            composable(Screen.Settings.route) { SettingsScreen(navigation = navController, dataStore, snackbarHostState) }
                            composable("EditProfileScreen") { EditProfileScreen(navigation = navController, dataStore) }
                            // Add other composable destinations as needed here
                        }
                    }
                }
            }
        }
    }

    private fun checkForPermission(context: ComponentActivity): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}