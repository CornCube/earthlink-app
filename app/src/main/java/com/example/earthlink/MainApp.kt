package com.example.earthlink

import android.app.Activity
import android.util.Log
import android.widget.Toast
import android.graphics.Rect
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.Marker
import java.util.regex.Pattern

import org.osmdroid.api.IMapController
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.TilesOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun ExampleForegroundLocationTrackerScreen() {
    ForegroundLocationTracker(snackbarHostState = SnackbarHostState()){
        Log.i("LogTag", "Current location is: $it")
    }
}

@Composable
fun Main() {
    lateinit var mMyLocationOverlay: MyLocationNewOverlay;
    val context = LocalContext.current
    var map by remember { mutableStateOf<MapView?>(null) }

    var isAddPostVisible by remember { mutableStateOf(false) }
    var isMenuOverlayVisible by remember { mutableStateOf(false) }
    var isMapCenteredOnLocation by remember { mutableStateOf(true) }

    MapView(
        onLoad = { loadedMap ->
            loadedMap.apply {
                setMultiTouchControls(true)
                controller.setZoom(20.0)
                setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
                overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)

                mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), this)
                mMyLocationOverlay.enableMyLocation()
                mMyLocationOverlay.enableFollowLocation()
                mMyLocationOverlay.isDrawAccuracyEnabled = false
                controller.animateTo(mMyLocationOverlay.myLocation)

                overlays.add(mMyLocationOverlay)

                addMapListener(object : MapListener {
                    override fun onScroll(event: ScrollEvent?): Boolean {
                        isMapCenteredOnLocation = false
                        return true
                    }

                    override fun onZoom(event: ZoomEvent?): Boolean {
                        isMapCenteredOnLocation = false
                        return true
                    }
                })
            }

            // Update the map state
            map = loadedMap
        }
    )

    Box (Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
        MainMenu(onClick = { isMenuOverlayVisible = true })
    }
    Box (Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        LocationButton(isMapCentered = isMapCenteredOnLocation, onClick = {
            map?.controller?.animateTo(mMyLocationOverlay.myLocation)
        })
    }
    Box (Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        AddPost(onClick = { isAddPostVisible = true })
    }

    if (isAddPostVisible) {
        AddPostModal(onDismissRequest = { isAddPostVisible = false })
    }

    if (isMenuOverlayVisible) {
        MenuOverlay(onDismissRequest = { isMenuOverlayVisible = false })
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
        contentColor = Color.White,
    ) {
        Icon(
            painter = painterResource(R.drawable.menu_24px),
            contentDescription = "Main menu button"
        )
    }
}

@Composable
fun LocationButton(isMapCentered: Boolean, onClick: () -> Unit) {
    val iconResource = if (isMapCentered) R.drawable.my_location_24px else R.drawable.location_searching_24px
    val iconColor = if (isMapCentered) Color(0xff8fa8ea) else Color(0xffe5ecf2)
    FloatingActionButton(
        modifier = Modifier
            .padding(bottom = 100.dp, end = 16.dp),
        onClick = { onClick() },
        containerColor = Color(0xff2a2b2d),
        shape = CircleShape,
    ) {
        Icon(
            painter = painterResource(iconResource),
            contentDescription = "Location button",
            tint = iconColor
        )
    }
}

@Composable
fun AddPost(onClick: () -> Unit) {
    FloatingActionButton(
        modifier = Modifier
            .padding(bottom = 16.dp, end = 16.dp),
        onClick = { onClick() },
        containerColor = Color(0xff99b1ed),
    ) {
        Icon(
            painter = painterResource(R.drawable.edit_24px),
            contentDescription = "Add button"
        )
    }
}

@Composable
fun AddPostModal(onDismissRequest: () -> Unit) {
    // TODO: Define AddPost modal UI here
}

@Composable
fun MenuButton1(onClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FloatingActionButton(
            modifier = Modifier.padding(bottom = 16.dp, end = 8.dp),
            onClick = { onClick() },
            shape = CircleShape,
            containerColor = Color(0xff99b1ed),
        ) {
            Icon(
                painter = painterResource(R.drawable.person_24px),
                contentDescription = "Profile button"
            )
        }
        Text(
            "Profile",
            modifier = Modifier.offset(y = 16.dp),
            color = Color(0xff99b1ed)
        )
    }
}

@Composable
fun MenuButton2(onClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FloatingActionButton(
            modifier = Modifier.padding(bottom = 16.dp, end = 8.dp),
            onClick = { onClick() },
            shape = CircleShape,
            containerColor = Color(0xff99b1ed),
        ) {
            Icon(
                painter = painterResource(R.drawable.group_24px),
                contentDescription = "Friends button"
            )
        }
        Text(
            "Friends",
            modifier = Modifier.offset(y = 16.dp),
            color = Color(0xff99b1ed)
        )
    }
}

@Composable
fun MenuButton3(onClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FloatingActionButton(
            modifier = Modifier.padding(end = 8.dp),
            onClick = { onClick() },
            shape = CircleShape,
            containerColor = Color(0xff99b1ed),
        ) {
            Icon(
                painter = painterResource(R.drawable.settings_24px),
                contentDescription = "Settings button"
            )
        }
        Text(
            "Settings",
            modifier = Modifier.offset(y = 16.dp),
            color = Color(0xff99b1ed)
        )
    }
}

@Composable
fun MenuOverlay(onDismissRequest: () -> Unit) {
    val visibleState = remember { MutableTransitionState(initialState = false) }
    visibleState.targetState = true

    LaunchedEffect(Unit) {
        if (!visibleState.targetState) {
            visibleState.targetState = true
        }
    }

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
                MenuButton1(onClick = { /* TODO: Handle button click */ })
                MenuButton2(onClick = { /* TODO: Handle button click */ })
                MenuButton3(onClick = { /* TODO: Handle button click */ })
            }
        }
    }
}
