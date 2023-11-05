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
