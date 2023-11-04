package com.example.earthlink.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.material.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun EditProfileScreen(navigation: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        UpdateProfilePicture()

        Spacer(modifier = Modifier.height(16.dp))

        EditBio() // New composable to edit bio

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            navigation.popBackStack()
        }) {
            Text("Save Changes")
        }
    }
}

@Composable
fun EditBio() {
    val bioState = remember { mutableStateOf("") }

    OutlinedTextField(
        value = bioState.value,
        onValueChange = { bioState.value = it },
        label = { Text("Edit Bio") },
        modifier = Modifier.fillMaxWidth()
    )
}



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
