package com.earthlink.earthlinkapp.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.material.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
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
        // Header or Title
        androidx.compose.material3.Text(
            text = "Edit Profile",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        UpdateProfilePicture()
        androidx.compose.material3.Text(
            text = "to edit profile, click on the profile picture on the previous screen",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                // TODO: show snackbar notification here
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
        // Optionally, you can add a Cancel button
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
        androidx.compose.material3.Text("Edit Bio", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        androidx.compose.material3.OutlinedTextField(
            value = bio,
            onValueChange = {
                if (it.length <= MAX_BIO_LENGTH) {
                    bio = it
                    onBioChange(it) // Notify the parent about changes
                }
            },
            label = { androidx.compose.material3.Text("Tell us more about yourself...") },
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
                    painter = painterResource(R.drawable.close_24px), // Replace with your icon
                    contentDescription = "Clear Bio",
                    tint = if (bio.isNotEmpty()) Color.Gray else Color.Transparent
                )
            }
        }
    }
}

// Maximum character limit for the bio
private const val MAX_BIO_LENGTH = 256

@Composable
fun UpdateProfilePicture() {
    val context = LocalContext.current
    val profilePictureState = remember { mutableStateOf<Bitmap?>(null) }

    // Launcher to pick an image from the gallery
    val galleryLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                val imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                profilePictureState.value = imageBitmap
            }
        }
    }

    // Composable to display the profile picture and the button to change it
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        profilePictureState.value?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = "User Profile Picture")
        } ?: androidx.compose.material3.Text("No profile picture selected")

        Spacer(modifier = Modifier.height(16.dp))

        androidx.compose.material3.Button(onClick = {
            val pickImageIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(pickImageIntent)
        }) {
            androidx.compose.material3.Text("Change Profile Picture")
        }
    }
}
