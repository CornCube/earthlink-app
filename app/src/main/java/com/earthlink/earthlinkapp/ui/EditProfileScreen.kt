package com.earthlink.earthlinkapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavController
import com.earthlink.earthlinkapp.R
import com.earthlink.earthlinkapp.data.PreferencesKeys
import com.earthlink.earthlinkapp.utils.getBioFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(navigation: NavController, dataStore: DataStore<Preferences>) {
    val currentBioFlow: Flow<String> = getBioFlow(dataStore)
    val currentBio by currentBioFlow.collectAsState(initial = "")

    // Remember the userBio state, initialized with the current bio from DataStore
    var userBio by remember { mutableStateOf(currentBio) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Edit Profile",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        EditBio(
            initialBio = userBio,
            onBioChange = { updatedBio ->
                userBio = updatedBio
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Save Changes Button
        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.edit { preferences ->
                        preferences[PreferencesKeys.USER_BIO_KEY] = userBio
                    }
                }
                navigation.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.edit_24px),
                    contentDescription = "Save Changes Icon"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Changes")
            }
        }
        TextButton(
            onClick = { navigation.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}

@Composable
fun EditBio(
    initialBio: String,
    onBioChange: (String) -> Unit
) {
    var bio by remember { mutableStateOf(initialBio) }

    Column {
        Text("Edit Bio", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = bio,
            onValueChange = {
                if (it.length <= MAX_BIO_LENGTH) {
                    bio = it
                    onBioChange(it)
                }
            },
            label = { Text("Tell us more about yourself...") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Characters left: ${MAX_BIO_LENGTH - bio.length}",
                style = TextStyle(fontSize = 12.sp, color = if (bio.length <= MAX_BIO_LENGTH) Color.Gray else Color.Red)
            )

            IconButton(
                onClick = { bio = "" },
                enabled = bio.isNotEmpty(),
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.close_24px),
                    contentDescription = "Clear Bio",
                    tint = if (bio.isNotEmpty()) Color.Gray else Color.Transparent
                )
            }
        }
    }
}

// Maximum character limit for the bio
private const val MAX_BIO_LENGTH = 256
