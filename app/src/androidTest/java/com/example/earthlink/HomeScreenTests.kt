package com.example.earthlink

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
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

    @Test
    fun test_MapVisibility() {
        composeTestRule.onNodeWithTag("GoogleMap").assertIsDisplayed()
    }

    @Test
    fun test_SnackbarDisplay_WhenLoggedIn() {
        composeTestRule.onNodeWithText("Logged in as user").assertIsDisplayed()
    }

    @Test
    fun test_ProfileNavigation_FromMainMenu() {
        composeTestRule.onNodeWithContentDescription("Main menu button").performClick()
        composeTestRule.onNodeWithContentDescription("Profile button").performClick()
        // Assert navigation to Profile screen
    }

    @Test
    fun test_FriendsNavigation_FromMainMenu() {
        composeTestRule.onNodeWithContentDescription("Main menu button").performClick()
        composeTestRule.onNodeWithContentDescription("Friends button").performClick()
        // Assert navigation to Friends screen
    }

    @Test
    fun test_SettingsNavigation_FromMainMenu() {
        composeTestRule.onNodeWithContentDescription("Main menu button").performClick()
        composeTestRule.onNodeWithContentDescription("Settings button").performClick()
        // Assert navigation to Settings screen
    }

    // Additional specific tests can be added as required
}
