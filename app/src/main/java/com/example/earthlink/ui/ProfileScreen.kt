package com.example.earthlink.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.earthlink.R

@Composable
fun ProfileScreen(navigation: NavController) {
    EditProfileButton()
    ReturnToHomeButton(navigation)
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
                modifier = Modifier.padding(8.dp), // Add internal padding here
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
                modifier = Modifier.padding(8.dp), // Add internal padding here
                contentAlignment = Alignment.Center
            ) {
                TopPosts()
            }
        }
    }
}

@Composable
fun ProfilePicture() {
    // You can replace the Image with your actual profile picture.
    Image(
        painter = painterResource(R.drawable.person_24px), // Replace with your image resource
        contentDescription = "Profile Picture",
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(12.dp)) // Adjust the corner radius as needed
            .background(Color.LightGray)
    )
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
fun EditProfileButton() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    FloatingActionButton(
        onClick = { /* TODO: Navigate to the edit profile screen */ },
        modifier = Modifier
            .padding(top = 16.dp, bottom = screenHeight - 64.dp, start = screenWidth - 64.dp, end = 16.dp),
        containerColor = Color(0xff99b1ed),
    ) {
        Icon(
            painter = painterResource(R.drawable.edit_24px),
            contentDescription = "Edit Profile"
        )
    }
}

@Composable
fun ReturnToHomeButton(navigation: NavController) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    FloatingActionButton(
        onClick = { navigation.navigate("HomeScreen") },
        modifier = Modifier
            .padding(bottom = 16.dp, top = screenHeight - 64.dp, end = screenWidth - 64.dp, start = 16.dp),
        containerColor = Color(0xff99b1ed),
    ) {
        Icon(
            painter = painterResource(R.drawable.close_24px),
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
                // Replace with your achievement icon
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
            .width(200.dp) // Adjust the width as needed
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




