package com.example.earthlink.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.earthlink.data.PreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun getThemeFlow(dataStore: DataStore<Preferences>): Flow<String> {
    val themeFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USER_THEME_KEY] ?: "default"
        }

    return themeFlow
}

fun getFilterFlow(dataStore: DataStore<Preferences>): Flow<Boolean> {
    val filterFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USER_FILTER_KEY] ?: false
        }

    return filterFlow
}

fun getProfilePictureFlow(dataStore: DataStore<Preferences>): Flow<String> {
    val profilePictureFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USER_PROFILE_PICTURE_URI_KEY] ?: ""
        }

    return profilePictureFlow
}

fun getBioFlow(dataStore: DataStore<Preferences>): Flow<String> {
    val bioFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USER_BIO_KEY] ?: ""
        }

    return bioFlow
}
