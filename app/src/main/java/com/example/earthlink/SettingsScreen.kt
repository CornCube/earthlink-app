package com.example.earthlink

import android.annotation.SuppressLint
import android.graphics.fonts.FontStyle
import android.preference.PreferenceActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.earthlink.ui.theme.*


val titleSize = 32.sp
val headerSize = 28.sp
val verticalSpacing = 8.dp
val contentPadding = 15.dp


//main screen for settings screen, calls other composables
@Composable
fun SettingsScreen(navigation: NavController) {

    //Scaffold is the overall layout of the page, including topbar, floating action button, and
    //body components that make up the page
    Scaffold(
        topBar = {
            TopBar()
        },
        floatingActionButton = {
            ExitButton(navigation = navigation)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = contentPadding, end = contentPadding)
                .verticalScroll(rememberScrollState(), flingBehavior = null),
            verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        ) {
            Spacer(modifier = Modifier.height(verticalSpacing))
            AccountSettings()
            Notification()
            Appearance()
            HelpSupport()
            About()
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

//Composable component for the topbar of the screen
@Composable
fun TopBar(){
    Surface(color = Color.LightGray.copy(alpha = 0.7f)){
        Text(text="Settings",
            modifier = Modifier
                .padding(all = contentPadding)
                .fillMaxWidth(),
            fontWeight = FontWeight.SemiBold,
            fontSize = titleSize
        )
    }

}

//Composable component for the account portion of the settings
@Composable
fun AccountSettings(){
    SettingHeader(text = "Account")
    HelperButton(text = "Change user/password")

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
    HelperCheckBox(text = "Dark Mode")
    HelperCheckBox(text = "Theme 1")
    HelperCheckBox(text = "Theme 2")
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

//Component for the exit button to go back to the home screen
@Composable
fun ExitButton(navigation: NavController){
    Box (Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
        FloatingActionButton(
            containerColor = Color.LightGray.copy(alpha = 0.8f),
            modifier = Modifier.padding(start = 32.dp),
            onClick = { navigation.navigate("HomeScreen") },
            shape = CircleShape,
            content = {
                Icon(
                    painter = painterResource(R.drawable.back_24px),
                    contentDescription = "Settings button"
                )
            }
        )
    }
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