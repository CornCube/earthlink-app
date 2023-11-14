package com.example.earthlink.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavController
import com.example.earthlink.R
import com.example.earthlink.model.MessageListFormat
import com.example.earthlink.utils.getBioFlow
import kotlinx.coroutines.flow.Flow
import com.example.earthlink.network.getMessagesFromUser
import com.example.earthlink.network.deleteMessage
import com.example.earthlink.utils.getUserFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navigation: NavController, dataStore: DataStore<Preferences>, snackbarHostState: SnackbarHostState) {
    var posts by remember { mutableStateOf<Map<String, MessageListFormat>?>(null) }
    var refresh by remember { mutableStateOf(true) }

    val userFlow = getUserFlow(dataStore)
    val user by userFlow.collectAsState(initial = "")

    val onRefreshChange = { newRefresh: Boolean ->
        refresh = newRefresh
    }

    LaunchedEffect(refresh) {
        if (refresh) {
            try {
                posts = getMessagesFromUser(user)
            } catch (e: Exception) {
                // Handle exceptions
            }
            refresh = false
        }
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(16.dp)
        ) {
            ProfilePicture(dataStore)
            Spacer(modifier = Modifier.width(16.dp))
            UserInfo(dataStore, user)
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier.padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                UserMilestones()
            }
        }
        Posts(posts, snackbarHostState, onRefreshChange)
    }
    Box (Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
        EditProfileButton(navigation = navigation)
    }
}

@Composable
fun ProfilePicture(dataStore: DataStore<Preferences>) {
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

    // Display the profile picture or a default placeholder if none exists
    Box(modifier = Modifier.clickable {
        val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(pickImageIntent)
    }) {
        // Display the profile picture or a default placeholder
        profilePictureState.value?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = "User Profile Picture")
        } ?: Image( painter = painterResource(R.drawable.person_24px),
            contentDescription = "Profile Picture Placeholder",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray))
    }
}

@Composable
fun UserInfo(dataStore: DataStore<Preferences>, user: String) {
    val bioBlow: Flow<String> = getBioFlow(dataStore)
    val bio by bioBlow.collectAsState(initial = "Personal Info")

    val displayedUser = if (user.length > 10) user.take(10) + "..." else user

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        Text(
            text = displayedUser,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp)
        )
        Text(
            text = "User Since: 11-06-2023",
            style = TextStyle(fontSize = 16.sp)
        )
        Text(
            text = bio,
            style = TextStyle(fontSize = 20.sp)
        )
    }
}

@Composable
fun EditProfileButton(navigation: NavController) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    FloatingActionButton(
        onClick = { navigation.navigate("EditProfileScreen") },
        modifier = Modifier
            .padding(top = 48.dp, end = 16.dp)
            .testTag("editProfileButton"), // Adding a test tag
        containerColor = Color(0xff99b1ed),
    ) {
        Icon(
            painter = painterResource(R.drawable.edit_24px),
            contentDescription = "Edit Profile"
        )
    }
}

@Composable
fun UserMilestones() {
    LazyColumn {
        item {
            Text("User Milestones", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        items(achievements) { achievement ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.trophy_24px),
                    contentDescription = "Achievement Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = achievement.name,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = achievement.stats,
                    style = TextStyle(fontSize = 14.sp)
                )
            }
        }
    }
}

data class Achievement(val name: String, val stats: String)

val achievements = listOf(
    Achievement("Post 10 messages", "10 points"),
    Achievement("Post 100 messages", "20 points"),
    Achievement("Post 1000 messages", "30 points")
)

@Composable
fun Posts(
    posts: Map<String, MessageListFormat>?,
    snackbarHostState: SnackbarHostState,
    onRefreshChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(start = 12.dp)
            .padding(16.dp),
    ) {
        Text("Posts", fontWeight = FontWeight.Bold, fontSize = 20.sp)
    }
    Column(
        modifier = Modifier
            .padding(start = contentPadding, end = contentPadding)
            .verticalScroll(rememberScrollState(), flingBehavior = null),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        posts?.forEach { post ->
            PostCard(post, snackbarHostState, onRefreshChange)
        }
    }
}

@Composable
fun DeleteButton(
    messageId: String,
    snackbarHostState: SnackbarHostState,
    onRefreshChange: (Boolean) -> Unit,
) {
    var showDeleteSnackbar by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    showDeleteSnackbar = true
                    val response = deleteMessage(messageId)
                    response?.let {
                        Log.d("user", it)
                    }
                } catch (e: Exception) {
                    // Handle exceptions
                }
                onRefreshChange(true)
            }
        },
        modifier = Modifier
            .padding(start = 300.dp, top = 8.dp)
            .fillMaxWidth(),
        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Red),
        content = {
            Icon(
                painter = painterResource(R.drawable.delete_24px),
                contentDescription = "Delete Message"
            )
        }
    )

    LaunchedEffect(showDeleteSnackbar) {
        if (showDeleteSnackbar) {
            snackbarHostState.showSnackbar("Message deleted")
            showDeleteSnackbar = false
        }
    }
}

@Composable
fun PostCard(
    post: Map.Entry<String, MessageListFormat>,
    snackbarHostState: SnackbarHostState,
    onRefreshChange: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(text = post.value.message_content, style = TextStyle(fontSize = 16.sp))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = post.value.timeStamp.toString(), style = TextStyle(fontSize = 14.sp))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "By ${post.value.user_uid}", style = TextStyle(fontSize = 12.sp))
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopEnd)
            ) {
                DeleteButton(post.key, snackbarHostState, onRefreshChange)
            }
        }
    }
}
