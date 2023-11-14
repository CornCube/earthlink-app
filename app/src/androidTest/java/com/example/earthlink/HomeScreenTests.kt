package com.example.earthlink

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.rule.GrantPermissionRule

@RunWith(AndroidJUnit4::class)
class HomeScreenTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_NETWORK_STATE
    )

    @Test
    fun test_LocationScreen_IsVisible() {
        // check if location screen is visible
        composeTestRule.onNodeWithText("Location Permission Required").assertIsDisplayed()
        composeTestRule.onNodeWithText("Grant Location Permission").performClick()

        // check if login screen is displayed
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()

        // press the sign up button
        composeTestRule.onNodeWithText("Sign Up").performClick()

        // check if sign up screen is visible
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Confirm Password").assertIsDisplayed()

        // sign up
        composeTestRule.onNodeWithText("Email").performTextInput("b@gmail.com")
        composeTestRule.onNodeWithText("Password").performTextInput("123456")
        composeTestRule.onNodeWithText("Confirm Password").performTextInput("12345")
        composeTestRule.onNodeWithText("Sign Up").performClick()

        // check for error
        composeTestRule.onNodeWithText("Confirm Password").assertIsDisplayed()

        // press sign up button
        composeTestRule.onNodeWithText("Sign Up").performClick()

        // press back to login
        composeTestRule.onNodeWithText("Back to Login").performClick()

        // Enter invalid credentials
        composeTestRule.onNodeWithText("Email").performTextInput("hello@gmail.com")
        composeTestRule.onNodeWithText("Password").performTextInput("1")
        composeTestRule.onNodeWithText("Login").performClick()

        // check if error message is displayed
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()

        // Enter invalid credentials
        composeTestRule.onNodeWithText("Email").performTextInput("hello")
        composeTestRule.onNodeWithText("Password").performTextInput("123456")
        composeTestRule.onNodeWithText("Login").performClick()

        composeTestRule.onNodeWithText("Email").assertIsDisplayed()

        // Enter invalid credentials
        composeTestRule.onNodeWithText("Email").performTextInput("hello@gmail.com")
        composeTestRule.onNodeWithText("Password").performTextInput("123456")
        composeTestRule.onNodeWithText("Login").performClick()

        composeTestRule.onNodeWithText("Email").assertIsDisplayed()

        // enter credentials
        composeTestRule.onNodeWithText("Sign Up").performClick()
        composeTestRule.onNodeWithText("Back to Login").performClick()
        composeTestRule.onNodeWithText("Email").performTextInput("a@gmail.com")
        composeTestRule.onNodeWithText("Password").performTextInput("123456")

        // click login button
        composeTestRule.onNodeWithText("Login").performClick()

        Thread.sleep(10000)
        composeTestRule.waitForIdle()

        // check if home screen is visible
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()

        // check if friends screen is visible
        composeTestRule.onNodeWithText("Friends").assertIsDisplayed()

        // check if profile screen is visible
        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()

        // check if settings screen is visible
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()

        Thread.sleep(10000)

        // click the post button (need to comment out the snackbar from homescreen to find this -_-)
        composeTestRule.onNodeWithText("  Post").performClick()
        composeTestRule.onNodeWithText("  Post").performClick()
        composeTestRule.onNodeWithText("  Post").performClick()

        // check if post screen is visible
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()

        // click the cancel button
        composeTestRule.onNodeWithText("Cancel").performClick()

        // click the profile screen button
        composeTestRule.onNodeWithText("Profile").performClick()
        composeTestRule.onNodeWithText("Profile").performClick()
        composeTestRule.onNodeWithText("Profile").performClick()

        // check if profile screen is visible
        composeTestRule.onNodeWithText("User Milestones").assertIsDisplayed()
        composeTestRule.onNodeWithText("Post 10 messages").assertIsDisplayed()
        composeTestRule.onNodeWithText("Post 100 messages").assertIsDisplayed()
        composeTestRule.onNodeWithText("Post 1000 messages").assertIsDisplayed()

        // press edit profile button
        composeTestRule.onNodeWithTag("editProfileButton").assertExists()
        composeTestRule.onNodeWithTag("editProfileButton").performClick()

        // press cancel button
        composeTestRule.onNodeWithText("Cancel").assertExists()
        composeTestRule.onNodeWithText("Cancel").performClick()

        // press friends button
        composeTestRule.onNodeWithText("Friends").performClick()

        // check if friends screen is visible
        composeTestRule.onNodeWithText("Alice").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bob").assertIsDisplayed()
        composeTestRule.onNodeWithText("Charlie").assertIsDisplayed()
        composeTestRule.onNodeWithText("David").assertIsDisplayed()

        // click friend request button
        composeTestRule.onNodeWithText("Friend Requests").performClick()
        composeTestRule.onNodeWithText("Friend Requests").performClick()
        composeTestRule.onNodeWithText("Friend Requests").performClick()

        // check if friend request screen is visible
        composeTestRule.onNodeWithText("Friend Requests").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ian").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jack").assertIsDisplayed()
        composeTestRule.onNodeWithText("Katie").assertIsDisplayed()
        composeTestRule.onNodeWithText("Leo").assertIsDisplayed()

        // press search
        composeTestRule.onNodeWithText("Search").performClick()
        composeTestRule.onNodeWithText("Search").performClick()
        composeTestRule.onNodeWithText("Search").performClick()

        // click settings
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Settings").performClick()

        // check if settings screen is visible
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Appearance").assertIsDisplayed()
        composeTestRule.onNodeWithText("Message Filter").assertIsDisplayed()

        // try clicking on appearance
        composeTestRule.onNodeWithText("Default").performClick()

        // try clicking the allow profanity checkbox
        composeTestRule.onNodeWithText("Allow Profanity").performClick()

        // press home button
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.onNodeWithText("Home").performClick()

        // try clicking the node with tag locationButton
        composeTestRule.onNodeWithTag("locationButton").performClick()
        composeTestRule.onNodeWithTag("locationButton").performClick()

        // go back to settings
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Settings").performClick()

        // change theme
        composeTestRule.onNodeWithText("Dark").performClick()
        composeTestRule.onNodeWithText("Allow Profanity").performClick()

        // go back home
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.onNodeWithText("Home").performClick()
    }
}
