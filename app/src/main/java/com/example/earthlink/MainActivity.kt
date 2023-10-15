package com.example.earthlink

import org.osmdroid.config.Configuration
import android.os.Bundle
import androidx.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.earthlink.ui.theme.EarthLinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        setContent {
            EarthLinkTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "HomeScreen") {
                        composable("HomeScreen") {
                            Main(navigation = navController)
                        }
//                        composable("ProfileScreen") {
//                            ProfileScreen(navigation = navController)
//                        }
//                        composable("FriendScreen") {
//                            FriendScreen(navigation = navController)
//                        }
//                        composable("SettingsScreen") {
//                            SettingsScreen(navigation = navController)
//                        }
                    }
                }
            }
        }
    }
}
