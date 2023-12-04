package com.earthlink.earthlinkapp.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import com.earthlink.earthlinkapp.R
import com.earthlink.earthlinkapp.model.MessageListFormat
import com.earthlink.earthlinkapp.model.ReactionData
import com.earthlink.earthlinkapp.network.changeReactions
import com.earthlink.earthlinkapp.utils.getBioFlow
import kotlinx.coroutines.flow.Flow
import com.earthlink.earthlinkapp.network.getMessagesFromUser
import com.earthlink.earthlinkapp.network.deleteMessage
import com.earthlink.earthlinkapp.network.getMessagesRadius
import com.earthlink.earthlinkapp.utils.formatTimestamp
import com.earthlink.earthlinkapp.utils.getUserFlow
import com.earthlink.earthlinkapp.utils.updateDislikes
import com.earthlink.earthlinkapp.utils.updateLikes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navigation: NavController, dataStore: DataStore<Preferences>, snackbarHostState: SnackbarHostState) {
    var posts by remember { mutableStateOf<List<MessageListFormat>?>(null) }
    var sortType by remember { mutableStateOf("Latest") }
    var refresh by remember { mutableStateOf(true) }

    val userFlow = getUserFlow(dataStore)
    val user by userFlow.collectAsState(initial = "")

    val onRefreshChange = { newRefresh: Boolean ->
        refresh = newRefresh
    }

    val onSortTypeChange = { newSortType: String ->
        sortType = newSortType
    }

    LaunchedEffect(refresh) {
        if (refresh) {
            try {
                posts = if (sortType == "Latest") {
                    getMessagesFromUser(user, 0)
                } else {
                    getMessagesFromUser(user, 1)
                }
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
        Posts(posts, snackbarHostState, onRefreshChange, onSortTypeChange)
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
    Achievement("Post your first message", "1 point"),
    Achievement("Post 10 messages", "10 points"),
    Achievement("Post 100 messages", "50 points")
)
@Composable
fun Posts(
    posts: List<MessageListFormat>?,
    snackbarHostState: SnackbarHostState,
    onRefreshChange: (Boolean) -> Unit,
    onSortTypeChange: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var searchText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Likes", "Latest")
    var selectedIndex by remember { mutableIntStateOf(0) }
    var curOption by remember { mutableStateOf(options[0]) }

    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
    ) {
        Text(
            "Posts",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search messages") },
                modifier = Modifier.weight(1f),
            )

            // Wrap the button and the dropdown menu in a Box
            Box {
                TextButton(onClick = { expanded = !expanded }) { // Toggle the expanded state
                    Text(text = "Sort by")
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown Menu")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd) // Align the dropdown to the end of the TextButton
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    options.forEachIndexed { index, option ->
                        DropdownMenuItem(text = { Text(text = option)},
                            onClick = {
                                selectedIndex = index
                                curOption = option
                                expanded = false
                                // Show snackbar on changing sorting preference
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Sorting by $option",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                                onSortTypeChange(option)
                                onRefreshChange(true)
                            })
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(start = contentPadding, end = contentPadding)
            .verticalScroll(rememberScrollState(), flingBehavior = null),
    ) {
        posts?.forEach { post ->
            PostCard(post, snackbarHostState, onRefreshChange)
        }
    }
}

@Composable
fun InteractRowProfile(post: MessageListFormat, snackbarHostState: SnackbarHostState, onRefreshChange: (Boolean) -> Unit) {
    // variable 1 or -1. if 1, then it is liked. if -1, then it is disliked. only one can be true at a time, so only
    // one icon can be filled at a time
    var reaction by remember { mutableIntStateOf(0) }
    val likes = remember { mutableIntStateOf(post.likes) }
    val dislikes = remember { mutableIntStateOf(post.dislikes) }

    var showDeleteSnackbar by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(
            painter = if (reaction == 1) painterResource(R.drawable.thumb_up_filled_24px) else painterResource(R.drawable.thumb_up_24px),
            contentDescription = "Likes",
            tint = Color(0xff8fa8ea),
            modifier = Modifier.clickable {
                val newValue = if (reaction == 1) 0 else 1
                reaction = newValue
                updateLikes(newValue, likes, dislikes)
                val reactionData = ReactionData(
                    user_uid = post.user_uid,
                    message_id = post.message_id,
                    reaction_type = reaction,
                    timestamp = post.timestamp
                )
                Log.d("ChangeReactions", reactionData.toString())
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = changeReactions(reactionData)
                        response?.let {
                            Log.d("ChangeReactions", it.toString())
                        } ?: Log.d("ChangeReactions", "Response was successful")
                    } catch (e: Exception) {
                        Log.e("ChangeReactions", "Failed to change reactions", e)
                    }
                }
            }
        )
        Text(
            modifier = Modifier.padding(start = 5.dp, top = 3.dp),
            text = likes.value.toString(),
            style = MaterialTheme.typography.labelLarge,
        )
        Icon(
            painter = if (reaction == -1) painterResource(R.drawable.thumb_down_filled_24px) else painterResource(R.drawable.thumb_down_24px),
            contentDescription = "Dislikes",
            tint = Color(0xffe57373),
            modifier = Modifier
                .padding(start = 10.dp)
                .clickable {
                    val newValue = if (reaction == -1) 0 else -1
                    reaction = newValue
                    updateDislikes(newValue, likes, dislikes)
                    val reactionData = ReactionData(
                        user_uid = post.user_uid,
                        message_id = post.message_id,
                        reaction_type = reaction,
                        timestamp = post.timestamp
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = changeReactions(reactionData)
                            response?.let {
                                Log.d("ChangeReactions", it.toString())
                            } ?: Log.d("ChangeReactions", "Response was successful")
                        } catch (e: Exception) {
                            Log.e("ChangeReactions", "Failed to change reactions", e)
                        }
                    }
                },
        )
        Text(
            modifier = Modifier.padding(start = 5.dp, top = 3.dp),
            text = dislikes.value.toString(),
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(Modifier.weight(1f))
        Icon(
            painter = painterResource(R.drawable.delete_24px),
            contentDescription = "Delete",
            tint = Color(0xffff7373),
            modifier = Modifier
                .clickable {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            showDeleteSnackbar = true
                            val response = deleteMessage(post.message_id)
                            response?.let {
                                Log.d("user", it)
                            }
                        } catch (e: Exception) {
                            // Handle exceptions
                        }
                        onRefreshChange(true)
                    }
                }
        )
        Icon(
            modifier = Modifier
                .padding(start = 10.dp)
                .clickable {
                    val copy = AnnotatedString(post.message_content)
                    clipboardManager.setText(copy)
                },
            painter = painterResource(R.drawable.content_paste_24px),
            contentDescription = "Copy",
        )
    }

    LaunchedEffect(showDeleteSnackbar) {
        if (showDeleteSnackbar) {
            snackbarHostState.showSnackbar("Message deleted")
            showDeleteSnackbar = false
        }
    }
}

@Composable
fun PostCard(
    post: MessageListFormat,
    snackbarHostState: SnackbarHostState,
    onRefreshChange: (Boolean) -> Unit,
) {
    val nameClicked = remember { mutableStateOf(false) }
    val author = post.user_uid
    val timestamp = formatTimestamp(post.timestamp)
    val content = post.message_content

    Box(
        modifier = Modifier
            .padding(vertical = 5.dp, horizontal = 10.dp),
    ) {
        Divider(modifier = Modifier.fillMaxWidth())
        Column(
            modifier = Modifier
                .padding(5.dp, top = 30.dp, bottom = 20.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Anonymous",
                    color = Color(0xff8fa8ea),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.clickable { nameClicked.value = !nameClicked.value }
                )
                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = timestamp,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            if (nameClicked.value) {
                Text(
                    text = "ID.$author",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(20.dp))
            InteractRowProfile(post, snackbarHostState, onRefreshChange)
        }
    }
}
