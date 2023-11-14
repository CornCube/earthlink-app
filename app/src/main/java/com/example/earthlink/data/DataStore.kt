package com.example.earthlink.data

import androidx.datastore.preferences.core.*

object PreferencesKeys {
    val USER_NOTIFICATION_KEY = booleanPreferencesKey("user_notification") // notifications on/off
    val USER_THEME_KEY = stringPreferencesKey("user_theme") // ui theme
    val USER_FILTER_KEY = booleanPreferencesKey("user_filter") // profanity filter
    val USER_NAME_KEY = stringPreferencesKey("user_name") // username
    val USER_PASSWORD_KEY = stringPreferencesKey("user_password") // password
    val USER_ID_KEY = stringPreferencesKey("user_id") // user id

    // key for profile picture. a user can select a photo inside the profile screen to set. see profilepicture() for details.
    val USER_PROFILE_PICTURE_URI_KEY = stringPreferencesKey("user_profile_picture_uri")

    // key for profile bio.
    val USER_BIO_KEY = stringPreferencesKey("user_bio")
}
