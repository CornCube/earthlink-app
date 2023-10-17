package com.example.earthlink

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.material3.TextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TabPosition
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun FriendScreen(navigation: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 0.dp)
    ) {
        CustomTabs { selectedIndex ->
            when (selectedIndex) {
                0 -> FriendsList()
                1 -> SearchFriends()
                2 -> FriendRequestsList()
            }
        }
    }
    BackButton(navigation = navigation)
}

@Composable
fun CustomTabs(content: @Composable (selectedIndex: Int) -> Unit) {
    var selectedIndex = remember { mutableStateOf(0) }

    val list = listOf("Friends", "Search Friends", "Friend Requests")

    Column {
        TabRow(selectedTabIndex = selectedIndex.value,
            containerColor = Color(0xff1E76DA),
            indicator = { tabPositions: List<TabPosition> ->
                Box {}
            }
        ) {
            list.forEachIndexed { index, text ->
                val selected = (selectedIndex.value == index)
                Tab(
                    modifier = if (selected) Modifier
                        .background(
                            Color(0xff1E76DA)
                        )
                    else Modifier
                        .background(
                            Color.White
                        ),
                    selected = selected,
                    onClick = { selectedIndex.value = index },
                    text = { Text(text = text, color = Color(0xff6FAAEE)) }
                )
            }
        }
        content(selectedIndex.value)
    }
}

@Composable
fun FriendsList() {
    val friends = listOf("Alice", "Bob", "Charlie", "David","Alice", "Bob",
        "Charlie", "David","Alice", "Bob", "Charlie", "David")  // Sample data

    Box(

    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(1.dp)  // Reduced space between items
        ) {
            items(friends) { friend ->
                Row(
                    modifier = Modifier.fillMaxSize()
                        .clickable(onClick={})
                        .background(Color.LightGray),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,

                ) {
                    // Placeholder for the profile picture
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.Gray, shape = CircleShape)
                            .padding(10.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))  // Space between the icon and the name

                    Text(
                        text = friend,
                        fontSize = 24.sp
                    )
                }
                Divider(color = Color.Black, thickness = 1.dp)

            }
        }
    }
}
@Composable
fun SearchFriends() {
    var searchText = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        TextField(
            value = searchText.value,
            onValueChange = { searchText.value = it },
            label = { Text("Search by username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        val searchResults = listOf("Eve", "Frank", "Grace", "Hannah") // Sample data

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(searchResults) { result ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable(onClick = {
                            println("Clicked on $result")
                        }),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Blue, shape = CircleShape)
                            .padding(10.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = result)
                }

            }
        }
    }
}
@Composable
fun FriendRequestsList() {
    val requests = listOf("Ian", "Jack", "Katie", "Leo")  // Sample data

    Box() {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(requests) { request ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .clickable(onClick = {
                        }),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.Gray, shape = CircleShape)
                            .padding(10.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))  // Space between the icon and the name

                    Text(text = request,fontSize = 24.sp)
                }
                Divider(color = Color.Black, thickness = 1.dp)

            }
        }
    }
}

@Composable
fun BackButton(navigation: NavController){
    Box (Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
        FloatingActionButton(
            containerColor = Color.LightGray.copy(alpha = 0.8f),
            modifier = Modifier.padding(start = 24.dp, bottom = 24.dp),
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
