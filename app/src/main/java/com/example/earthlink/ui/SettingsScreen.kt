package com.example.earthlink.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import com.example.earthlink.utils.getThemeFlow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavController
import com.example.earthlink.R
import com.example.earthlink.data.PreferencesKeys
import com.example.earthlink.utils.getFilterFlow
import kotlinx.coroutines.flow.Flow

val titleSize = 32.sp
val headerSize = 28.sp
val verticalSpacing = 8.dp
val contentPadding = 15.dp

//main screen for settings screen, calls other composables
@Composable
fun SettingsScreen(navigation: NavController, dataStore: DataStore<Preferences>) {
    //Scaffold is the overall layout of the page, including topbar, floating action button, and
    //body components that make up the page
    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = contentPadding, end = contentPadding)
                .verticalScroll(rememberScrollState(), flingBehavior = null),
            verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        ) {
            Spacer(modifier = Modifier.height(verticalSpacing))
            Appearance(dataStore)
            FilterSettings(dataStore)
            Notification()
            AccountSettings()
            HelpSupport()
            About()
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

// Composable component for the filter settings
@Composable
fun FilterSettings(dataStore: DataStore<Preferences>) {
    SettingHeader(text = "Message Filter")

    val currentFilterFlow: Flow<Boolean> = getFilterFlow(dataStore)
    val currentFilter by currentFilterFlow.collectAsState(initial = false)

    // Remember a mutable state initialized with the current filter setting
    var allowProfanity by remember { mutableStateOf(currentFilter) }

    // Update the DataStore when the allowProfanity state changes
    LaunchedEffect(allowProfanity) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_FILTER_KEY] = allowProfanity
        }
    }

    LaunchedEffect(currentFilter) {
        allowProfanity = currentFilter
    }

    // Pass the mutable state and its setter to the HelperCheckBox
    HelperCheckBox(
        text = "Allow Profanity",
        checked = allowProfanity,
        onCheckedChange = { allowProfanity = it }
    )

    Spacer(Modifier.height(12.dp))
}

//Composable component for the account portion of the settings
@Composable
fun AccountSettings(){
    SettingHeader(text = "Account")
    HelperButton(text = "Change username/password")

    Spacer(Modifier.height(12.dp))
}

//Composable component for the notification portion of the settings
@Composable
fun Notification(){
    SettingHeader(text = "Notifications")
    
    HelperCheckBox(text = "Mute Notifications", checked = false, onCheckedChange = { /*TODO*/ })
    HelperCheckBox(text = "Notification setting 2", checked = false, onCheckedChange = { /*TODO*/ })
    HelperCheckBox(text = "Notification setting 3", checked = false, onCheckedChange = { /*TODO*/ })

    Spacer(Modifier.height(12.dp))

}

//Composable component for the appearance portion of the settings
@Composable
fun Appearance(dataStore: DataStore<Preferences>) {
    SettingHeader(text = "Appearance")
    ThemeSelector(dataStore)
    Spacer(Modifier.height(12.dp))

}

//Composable component for the help and support portion of the settings
@Composable
fun HelpSupport(){
    SettingHeader(text = "Help and Support")
    HelperButton(text = "FAQ")
    HelperButton(text = "Contact Support")
    HelperButton(text = "Report")
    Spacer(Modifier.height(12.dp))
}

//Composable component for the about portion of the settings
@Composable
fun About(){
    SettingHeader(text = "About")
    HelperButton(text = "Guidelines")
    HelperButton(text = "Terms of Services")

    Spacer(Modifier.height(12.dp))
}

//Helper function to create the header for each portion of the settings
@Composable
fun SettingHeader(text: String){
    Text(text=text, fontSize = headerSize)
    Divider(color = Color.Black,
        thickness = 1.dp)
    Spacer(modifier = Modifier.height(8.dp))
}

//Helper function that creates a button for style consistency
@Composable
fun HelperButton(text: String){
    OutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30),
        onClick = { /*TODO*/ }) {
        Text(text, fontSize = 18.sp)
    }
}

// Helper function that creates a checkbox
@Composable
fun HelperCheckBox(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text, textAlign = TextAlign.Center, fontSize = 20.sp)
        Checkbox(
            modifier = Modifier.size(32.dp),
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

// Column of radio buttons to swap between different themes
// currently there is default, light, dark, brown, night
@Composable
fun ThemeSelector(dataStore: DataStore<Preferences>) {
    val currentThemeFlow: Flow<String> = getThemeFlow(dataStore)
    val currentTheme by currentThemeFlow.collectAsState(initial = "default")

    // Remember the selected theme based on the current theme from DataStore
    var selectedTheme by remember { mutableStateOf(currentTheme) }

    // Update the DataStore when the selected theme changes
    LaunchedEffect(selectedTheme) {
        if (currentTheme != selectedTheme) {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.USER_THEME_KEY] = selectedTheme
            }
        }
    }

    // Whenever the theme in the DataStore changes, update our state accordingly
    LaunchedEffect(currentTheme) {
        selectedTheme = currentTheme
    }

    Column {
        ThemeRadioButton("Default", selectedTheme) { selectedTheme = it }
        ThemeRadioButton("Light", selectedTheme) { selectedTheme = it }
        ThemeRadioButton("Dark", selectedTheme) { selectedTheme = it }
        ThemeRadioButton("Brown", selectedTheme) { selectedTheme = it }
        ThemeRadioButton("Night", selectedTheme) { selectedTheme = it }
    }
}

@Composable
fun ThemeRadioButton(themeName: String, selectedTheme: String, onThemeSelected: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selectedTheme == themeName,
            onClick = { onThemeSelected(themeName) }
        )
        Text(text = themeName)
    }
}
