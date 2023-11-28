package com.earthlink.earthlinkapp.ui

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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TabPosition
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import com.earthlink.earthlinkapp.R

//Main Composable for the Friend screen. Includes 3 tabs: Friend list, search friend, and friend
//requests
@Composable
fun FriendScreen(navigation: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomTabs { selectedIndex ->
            when (selectedIndex) {
                0 -> FriendsList(navigation)
                1 -> FriendRequestsList(navigation)
                2 -> SearchFriends(navigation)
            }
        }
    }
}

//Composable for the tab to switch between screens for friend feature
@Composable
fun CustomTabs(content: @Composable (selectedIndex: Int) -> Unit) {
    var selectedIndex = remember { mutableStateOf(0) }

    val list = listOf("Friends", "Friend Requests", "Search")

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

//Friends list feature of friends
@Composable
fun FriendsList(navigation: NavController) {
    val friends = listOf("Alice", "Bob", "Charlie", "David")

    LazyColumn(
    ) {
        items(friends) { friend ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Implement logic to navigate to the friend's profile or perform other actions
                        // Example: navigation.navigate("FriendProfile/$friend")
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .size(60.dp)
                        .background(Color.Gray, shape = CircleShape)
                        .padding(6.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = friend, fontSize = 24.sp)
            }
            Divider(color = Color.Gray.copy(alpha = 0.5f), thickness = 1.dp)
        }
    }
}

//Component that builds the search friends portion
@Composable
fun SearchFriends(navigation: NavController) {
    var searchText by remember { mutableStateOf("") }

    Column(
    ) {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = {Text("Search by username") },
            modifier = Modifier.fillMaxWidth())
    }

    // You can fetch real search results and display them here
    // Example placeholder:
    var searchResults = listOf("temp", "temp") // TODO: actually store stuff?

    LazyColumn(
    ) {
        items(searchResults) { result ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Implement logic to navigate to the search result's profile or perform other actions
                        // Example: navigation.navigate("FriendProfile/$result")
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(40.dp)
                        .background(Color.Gray, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = result, fontSize = 20.sp)
            }
        }
    }
}

//Composable that builds the friends request list
@Composable
fun FriendRequestsList(navigation: NavController) {
    val requests = listOf("Ian", "Jack", "Katie", "Leo")

    LazyColumn(
    ) {
        items(requests) { request ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Implement logic to navigate to the friend request's profile or perform other actions
                        // Example: navigation.navigate("FriendProfile/$request")
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .size(60.dp)
                            .background(Color.Gray, shape = CircleShape)
                            .padding(6.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(text = request, fontSize = 24.sp, modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = {
                            // Implement logic to accept the friend request
                            // Example: navigate to "FriendProfile/$request" or perform other action
                        },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.add_24px), // Replace with your icon resource
                            contentDescription = "Add"
                        )
                    }
                    IconButton(
                        onClick = {
                            // Implement logic to decline the friend request
                            // Example: remove the request from the list or perform other action
                        },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.close_24px), // Replace with your icon resource
                            contentDescription = "Decline"
                        )
                    }
                }
            }
            Divider(color = Color.Gray.copy(alpha = 0.5f), thickness = 1.dp)
        }
    }
}
