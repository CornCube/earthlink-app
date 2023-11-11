package com.example.earthlink.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.earthlink.data.PreferencesKeys
import com.example.earthlink.model.Message
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

fun parseMessages(jsonString: String): List<Message> {
    val gson = Gson()
    val type = object : TypeToken<Map<String, Message>>() {}.type
    val messagesMap: Map<String, Message> = gson.fromJson(jsonString, type)

    // Convert the map to a list of messages
    return messagesMap.values.toList()
}

