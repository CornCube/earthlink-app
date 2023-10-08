package com.example.earthlink

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text

import androidx.compose.ui.res.*
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Main : Screen("main")
    object AddPost : Screen("addPost")
}

@Composable
fun Main() {
    var isAddPostVisible by remember { mutableStateOf(false) }
    var isMenuOverlayVisible by remember { mutableStateOf(false) }

    MapView(
        onLoad = { map ->
            map.setMultiTouchControls(true)
            map.controller.setZoom(9.0)
            map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
            // uncomment below to remove the zoom buttons
            // map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

            // get the current location, and set the map to that location
        }
    )

    if (isAddPostVisible) {
        AddPostModal(onDismissRequest = { isAddPostVisible = false })
    }

    if (isMenuOverlayVisible) {
        MenuOverlay(onDismissRequest = { isMenuOverlayVisible = false })
    }

    Box (Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
        MainMenu(onClick = { isMenuOverlayVisible = true })
    }
    Box (Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        AddPost(onClick = { isAddPostVisible = true })
    }
}

@Composable
fun MainMenu(onClick: () -> Unit) {
    FloatingActionButton(
        modifier = Modifier
            .padding(top = 16.dp, start = 16.dp),
        onClick = { onClick() },
        containerColor = Color.Transparent,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 0.dp
        ),
        contentColor = Color.Black,
    ) {
        Icon(
            painter = painterResource(R.drawable.menu_24px),
            contentDescription = "Main menu button"
        )
    }
}

@Composable
fun AddPost(onClick: () -> Unit) {
    FloatingActionButton(
        modifier = Modifier
            .padding(bottom = 16.dp, end = 16.dp),
        onClick = { onClick() },
        shape = CircleShape,
    ) {
        Icon(
            painter = painterResource(R.drawable.add_24px),
            contentDescription = "Add button"
        )
    }
}

@Composable
fun AddPostModal(onDismissRequest: () -> Unit) {
    // Define your AddPost modal UI here, e.g.,
    // Dialog or some UI component to take post input
}

@Composable
fun MenuButton1(onClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)  // Adds spacing between children
    ) {
        FloatingActionButton(
            modifier = Modifier.padding(bottom = 16.dp, end = 8.dp),
            onClick = { onClick() },
            shape = CircleShape,
        ) {
            Icon(
                painter = painterResource(R.drawable.person_24px),
                contentDescription = "Profile button"
            )
        }
        Text(
            "Profile",
            modifier = Modifier.offset(y = 16.dp)
        )
    }
}

@Composable
fun MenuButton2(onClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)  // Adds spacing between children
    ) {
        FloatingActionButton(
            modifier = Modifier.padding(bottom = 16.dp, end = 8.dp),
            onClick = { onClick() },
            shape = CircleShape,
        ) {
            Icon(
                painter = painterResource(R.drawable.group_24px),
                contentDescription = "Friends button"
            )
        }
        Text(
            "Friends",
            modifier = Modifier.offset(y = 16.dp)
        )
    }
}

@Composable
fun MenuButton3(onClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)  // Adds spacing between children
    ) {
        FloatingActionButton(
            modifier = Modifier.padding(end = 8.dp),
            onClick = { onClick() },
            shape = CircleShape,
        ) {
            Icon(
                painter = painterResource(R.drawable.settings_24px),
                contentDescription = "Settings button"
            )
        }
        Text(
            "Settings",
            modifier = Modifier.offset(y = 16.dp)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MenuOverlay(onDismissRequest: () -> Unit) {
    val visibleState = remember { MutableTransitionState(initialState = false) }
    visibleState.targetState = true

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    onClick = {
                        visibleState.targetState = false
                        onDismissRequest()
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                MenuButton1(onClick = { /* Handle button click */ })
                MenuButton2(onClick = { /* Handle button click */ })
                MenuButton3(onClick = { /* Handle button click */ })
            }
        }
    }
}
