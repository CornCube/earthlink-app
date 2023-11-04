package com.example.earthlink.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.*
import androidx.navigation.NavHostController
import com.example.earthlink.R
import com.example.earthlink.utils.getCurrentLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*

@Composable
fun Main(navigation: NavHostController) {
    val context = LocalContext.current

    var isAddPostVisible by remember { mutableStateOf(false) }

    var uiSettings by remember { mutableStateOf(MapUiSettings()) }
    var mapProperties by remember { mutableStateOf(MapProperties()) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 10f) // Default position
    }

    uiSettings = MapUiSettings(
        compassEnabled = true,
        indoorLevelPickerEnabled = false,
        mapToolbarEnabled = false,
        myLocationButtonEnabled = false,
        rotationGesturesEnabled = true,
        scrollGesturesEnabled = true,
        scrollGesturesEnabledDuringRotateOrZoom = true,
        tiltGesturesEnabled = false,
        zoomControlsEnabled = false,
        zoomGesturesEnabled = true
    )

    mapProperties = MapProperties(
        isBuildingEnabled = false,
        isIndoorEnabled = false,
        isMyLocationEnabled = true,
        isTrafficEnabled = false,
        latLngBoundsForCameraTarget = null,
        mapStyleOptions = null,
        mapType = MapType.NORMAL,
        maxZoomPreference = 20f,
        minZoomPreference = 2f
    )

    // Update map to user's current location when the composable enters the Composition
    DisposableEffect(lifecycleOwner) {
        val listener = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                getCurrentLocation(context) { location ->
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(location, 15f))
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(listener)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(listener)
        }
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = uiSettings,
        properties = mapProperties,

    ) {
    }

    Box (Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        LocationButton(cameraPositionState = cameraPositionState, context = context)
    }
    Box (Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        AddPost(onClick = { isAddPostVisible = true })
    }

    if (isAddPostVisible) {
        AddPostModal(onDismissRequest = { isAddPostVisible = false })
    }
}

@Composable
fun LocationButton(cameraPositionState: CameraPositionState, context: Context) {
    val isMapCentered = remember { mutableStateOf(true) }

    val iconResource = if (isMapCentered.value) R.drawable.my_location_24px else R.drawable.location_searching_24px
    val iconColor = if (isMapCentered.value) Color(0xff8fa8ea) else Color(0xffe5ecf2)

    FloatingActionButton(
        modifier = Modifier
            .padding(bottom = 100.dp, end = 16.dp),
        onClick = {
            getCurrentLocation(context) { location ->
                isMapCentered.value = true
                cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
            }
        },
        containerColor = Color(0xff2a2b2d),
        shape = CircleShape,
    ) {
        Icon(
            painter = painterResource(id = iconResource),
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
