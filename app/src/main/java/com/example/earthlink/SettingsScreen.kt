package com.example.earthlink

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navigation: NavController) {

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Settings")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigation.navigate("HomeScreen") },
                content = {
                    Icon(
                        painter = painterResource(R.drawable.close_24px),
                        contentDescription = "Settings button"
                    )
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding).padding(all = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Spacer(Modifier.height(10.dp))
            AccountSettings()
            Notification()
            Appearance()
            HelpSupport()
            About()
        }
    }
}

@Composable
fun AccountSettings(){
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)){
        Text("Account", fontSize = 20.sp)
        Divider(color = Color.Black,
            thickness = 1.dp)
        Row{
            Column {
                OutlinedButton(modifier=Modifier.padding(top = 8.dp, bottom = 8.dp),
                    onClick = { /*TODO*/ }) {
                    Text("Change username/password")
                }
            }
        }

    }
}


@Composable
fun Notification(){
    Row{
        Text("Notifications",
            fontSize = 20.sp)
    }
    Divider(color = Color.Black,
        thickness = 1.dp,
        modifier = Modifier.padding(bottom = 8.dp))
    Row{
        Column {
            val checkedState1 = remember { mutableStateOf(false) }
            val checkedState2 = remember { mutableStateOf(false) }
            val checkedState3 = remember { mutableStateOf(false) }

            /* TODO: put checkboxes into helper function */
            Row (modifier = Modifier.padding(all = 0.dp)) {
                Checkbox(
                    checked = checkedState1.value,
                    onCheckedChange = { checkedState1.value = it },
                )
                Text(text = "Mute notifications", modifier = Modifier.padding(top = 12.dp))
            }
            Row {
                Checkbox(
                    checked = checkedState2.value,
                    onCheckedChange = { checkedState2.value = it },
                )
                Text(text = "Notif option 2", modifier = Modifier.padding(top = 12.dp))
            }
            Row {
                Checkbox(
                    checked = checkedState3.value,
                    onCheckedChange = { checkedState3.value = it },
                )
                Text(text = "Notif option 3", modifier = Modifier.padding(top = 12.dp))
            }
        }
    }
}

@Composable
fun Appearance() {
    Row {
        Text(
            "Appearance",
            fontSize = 20.sp
        )
    }
    Divider(color = Color.Black, thickness = 1.dp)

    /* TODO: Include or remove? Changing themes and stuff */
}

@Composable
fun HelpSupport(){
    Row{
        Text("Help and Support",
            fontSize = 20.sp)
    }
    Divider(color = Color.Black, thickness = 1.dp)

    /* TODO: Put some random placeholders for help contact */
}

@Composable
fun About(){
    Row{
        Text("About",
            fontSize = 20.sp)
    }
    Divider(color = Color.Black, thickness = 1.dp)

    /* TODO: Add some random about things maybe route to about page*/
}

