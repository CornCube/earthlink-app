package com.example.earthlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.earthlink.ui.FriendScreen
import com.example.earthlink.ui.Main
import com.example.earthlink.ui.ProfileScreen
import com.example.earthlink.ui.SettingsScreen
import com.example.earthlink.ui.LoginScreen
import com.example.earthlink.ui.theme.EarthLinkTheme
import com.example.earthlink.utils.checkForPermission

class MainActivity : ComponentActivity() {
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

                    NavHost(navController = navController, startDestination = "LoginScreen") {
                        composable("LoginScreen") {
                            LoginScreen(
                                onPermissionGranted = {
                                    hasLocationPermission = true
                                    navController.navigate("HomeScreen")
                                }
                            )
                        }
                        composable("HomeScreen") {
                            Main(navigation = navController)
                        }
                        composable("ProfileScreen") {
                            ProfileScreen(navigation = navController)
                        }
                        composable("FriendScreen") {
                            FriendScreen(navigation = navController)
                        }
                        composable("SettingsScreen") {
                            SettingsScreen(navigation = navController)
                        }
                        composable("EditProfileScreen"){
                            EditProfileScreen(navigation = navController)
                        }
                    }
                }
            }
        }
    }
}
