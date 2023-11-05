package com.example.earthlink.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

val titleSize = 32.sp
val headerSize = 28.sp
val verticalSpacing = 8.dp
val contentPadding = 15.dp

//main screen for settings screen, calls other composables
@Composable
fun SettingsScreen(navigation: NavController) {

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
            Appearance()
            Notification()
            AccountSettings()
            HelpSupport()
            About()
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
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
    
    HelperCheckBox(text = "Mute Notifications")
    HelperCheckBox(text = "Notification setting 2")
    HelperCheckBox(text = "Notification setting 3")

    Spacer(Modifier.height(12.dp))

}

//Composable component for the appearance portion of the settings
@Composable
fun Appearance() {
    SettingHeader(text = "Appearance")
    ThemeSelector()
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

//Helper function that creates a checkbox
@Composable
fun HelperCheckBox(text: String){

    val isCheck = remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween){

        Text(text, textAlign = TextAlign.Center, fontSize = 20.sp)
        Checkbox(modifier = Modifier.size(32.dp),
            checked = isCheck.value,
            onCheckedChange = {isCheck.value = it})
    }
}

// Column of radio buttons to swap between different themes
// currently there is brown, dark, default, light, night, and system
@Composable
fun ThemeSelector() {
    val selectedTheme = remember { mutableStateOf("default") }
    Column {
        RadioButtonRow("Brown", selectedTheme)
        RadioButtonRow("Dark", selectedTheme)
        RadioButtonRow("Default", selectedTheme)
        RadioButtonRow("Light", selectedTheme)
        RadioButtonRow("Night", selectedTheme)
        RadioButtonRow("Follow System", selectedTheme)
    }
}

@Composable
fun RadioButtonRow(themeName: String, selectedTheme: MutableState<String>) {
    Row(
        verticalAlignment = Alignment.CenterVertically // Center-align the content vertically
    ) {
        RadioButton(
            selected = selectedTheme.value == themeName,
            onClick = { selectedTheme.value = themeName }
        )
        Text(text = themeName)
    }
}
