package com.example.earthlink

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun test_MainMenuButton_IsVisible() {
        composeTestRule.onNodeWithContentDescription("Main menu button").assertIsDisplayed()
    }

    @Test
    fun test_ClickingMainMenuButton_ShowsMenuOverlay() {
        composeTestRule.onNodeWithContentDescription("Main menu button").performClick()
        composeTestRule.onNodeWithContentDescription("Profile button").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Friends button").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Settings button").assertIsDisplayed()
    }

    @Test
    fun test_LocationButton_IsVisible() {
        composeTestRule.onNodeWithContentDescription("Location button").assertIsDisplayed()
    }

    @Test
    fun test_AddPostButton_IsVisible() {
        composeTestRule.onNodeWithContentDescription("Add button").assertIsDisplayed()
    }

//    @Test
//    fun test_ClickingAddPostButton_ShowsAddPostModal() {
//        composeTestRule.onNodeWithContentDescription("Add button").performClick()
//        // Assuming you have a unique content description or text in the AddPostModal
//        composeTestRule.onNodeWithText("Add Post Modal Unique Text").assertIsDisplayed()
//    }

//    @Test
//    fun test_ClickingProfileButton_NavigatesToProfileScreen() {
//        val navController = TestNavHostController(
//            ApplicationProvider.getApplicationContext()
//        ).apply {
//            setGraph(R.navigation.nav_graph) // Use your actual navigation graph resource id
//            setCurrentState(State.CREATED)
//        }
//        composeTestRule.setContent {
//            EarthLinkApp(navController = navController)
//        }
//
//        composeTestRule.onNodeWithContentDescription("Profile button").performClick()
//
//        assertEquals("ProfileScreen", navController.currentDestination?.route)
//    }

    // Additional tests for other navigation buttons and UI components
}
