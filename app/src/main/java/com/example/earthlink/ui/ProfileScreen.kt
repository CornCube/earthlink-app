package com.example.earthlink.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
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
import androidx.navigation.NavController
import com.example.earthlink.R

@Composable
fun ProfileScreen(navigation: NavController) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(16.dp)
        ) {
            ProfilePicture()
            Spacer(modifier = Modifier.width(16.dp))
            UserInfo()
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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier.padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                TopPosts()
            }
        }
    }
    Box (Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        EditProfileButton(navigation = navigation)
    }
}

@Composable
fun ProfilePicture() {
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
    Box(
        modifier = Modifier.clickable {
            val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(pickImageIntent)
        }
    ) {
        profilePictureState.value?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = "User Profile Picture")
        } ?: Image( painter = painterResource(R.drawable.person_24px),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp)) // corner radius
                .background(Color.LightGray))
    }
}

@Composable
fun UserInfo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        Text(
            text = "John Doe",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp)
        )
        Text(
            text = "User Since: xx-xx-xxx",
            style = TextStyle(fontSize = 16.sp)
        )
        Text(
            text = "Personal Info",
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
            .padding(bottom = 16.dp, end = 16.dp),
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
    Achievement("Achievement 1", "10 points"),
    Achievement("Achievement 2", "20 points"),
    Achievement("Achievement 3", "30 points")
)

@Composable
fun TopPosts() {
    Column {
        Text("Top Posts", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        LazyRow(
            content = {
                items(posts) { post ->
                    PostCard(post)
                }
            }
        )
    }
}

data class Post(val title: String, val content: String, val author: String)

val posts = listOf(
    Post("Post 1", "Content for post 1", "Author 1"),
    Post("Post 2", "Content for post 2", "Author 2"),
    Post("Post 3", "Content for post 3", "Author 3")
)

@Composable
fun PostCard(post: Post) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = post.title, fontWeight = FontWeight.Bold)
            Text(text = post.content, style = TextStyle(fontSize = 14.sp))
            Text(text = "By ${post.author}", style = TextStyle(fontSize = 12.sp))
        }
    }
}


