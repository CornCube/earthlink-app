package com.earthlink.earthlinkapp.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.*
import androidx.navigation.NavHostController
import com.earthlink.earthlinkapp.R
import com.earthlink.earthlinkapp.model.Message
import com.earthlink.earthlinkapp.model.MessageListFormat
import com.earthlink.earthlinkapp.network.*
import com.earthlink.earthlinkapp.utils.getCurrentLocation
import com.earthlink.earthlinkapp.utils.getFilterFlow
import com.earthlink.earthlinkapp.utils.getThemeFlow
import com.earthlink.earthlinkapp.utils.getUserFlow
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(navigation: NavHostController, dataStore: DataStore<Preferences>, snackbarHostState: SnackbarHostState) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    var isAddPostVisible by remember { mutableStateOf(false) }
    var showPostSnackbar by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var circlePosition by remember { mutableStateOf(LatLng(0.0, 0.0)) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 2f)
    }

    var messages by remember { mutableStateOf<List<MessageListFormat>?>(null) }
    var selectedMessages by remember { mutableStateOf<List<MessageListFormat>>(emptyList()) }

    val userFlow = getUserFlow(dataStore)
    val user by userFlow.collectAsState(initial = "")

    // theme state
    val themeFlow = getThemeFlow(dataStore)
    val theme by themeFlow.collectAsState(initial = "default")
    var mapStyle by remember { mutableStateOf(MapStyleOptions.loadRawResourceStyle(context, R.raw.defaultmap)) }
    LaunchedEffect(theme) {
        mapStyle = when (theme) {
            "Default" -> MapStyleOptions.loadRawResourceStyle(context, R.raw.defaultmap)
            "Light" -> MapStyleOptions.loadRawResourceStyle(context, R.raw.lightmap)
            "Dark" -> MapStyleOptions.loadRawResourceStyle(context, R.raw.darkmap)
            "Brown" -> MapStyleOptions.loadRawResourceStyle(context, R.raw.brownmap)
            "Night" -> MapStyleOptions.loadRawResourceStyle(context, R.raw.nightmap)
            else -> mapStyle
        }
    }

    var uiSettings by remember { mutableStateOf(MapUiSettings()) }
    uiSettings = MapUiSettings(
        compassEnabled = true,
        indoorLevelPickerEnabled = false,
        mapToolbarEnabled = false,
        myLocationButtonEnabled = false,
        rotationGesturesEnabled = true,
        scrollGesturesEnabled = true,
        scrollGesturesEnabledDuringRotateOrZoom = true,
        tiltGesturesEnabled = true,
        zoomControlsEnabled = false,
        zoomGesturesEnabled = true
    )

    var mapProperties by remember { mutableStateOf(MapProperties()) }
    mapProperties = MapProperties(
        isBuildingEnabled = false,
        isIndoorEnabled = false,
        isMyLocationEnabled = true,
        isTrafficEnabled = false,
        latLngBoundsForCameraTarget = null,
        mapStyleOptions = mapStyle,
        mapType = MapType.NORMAL,
        maxZoomPreference = 20f,
        minZoomPreference = 2f
    )

    // update camera position when the lifecycle starts
    DisposableEffect(lifecycleOwner) {
        val listener = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                getCurrentLocation(context) { location ->
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(location, 17f))
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(listener)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(listener)
        }
    }

    // obtain the current location every 10 seconds
    var currentLocation = LatLng(0.0, 0.0)
    LaunchedEffect(Unit) {
        while(true) {
            try {
                getCurrentLocation(context) { location ->
                    currentLocation = location
                    circlePosition = location
                }
            } catch (e: Exception) {
                // Handle exceptions
            }
            delay(10000)
        }
    }

    // every 10 seconds, use the current location to get messages within a 10m radius
    LaunchedEffect(Unit) {
        while(true) {
            try {
                messages = getMessagesRadius(currentLocation.latitude, currentLocation.longitude)
                // TODO: filter overlapping messages here with a new format (need to rethink the data model)
            } catch (e: Exception) {
                // Handle exceptions
            }
            delay(10000)
        }
    }

    // TODO: overlapping messages
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = uiSettings,
        properties = mapProperties,
    ) {
        messages?.forEach { message ->
//            Marker(
//                state = MarkerState(position = LatLng(message.latitude, message.longitude)),
//                snippet = message.message_content,
//                title = message.user_uid,
//            )
            Circle(
                center = LatLng(message.latitude, message.longitude),
                radius = 10.0, // radius in meters
                fillColor = Color.Red.copy(alpha = 0.5f),
                strokePattern = listOf(Dash(10f), Gap(10f)),
                strokeColor = Color.Black,
                strokeWidth = 2f,
                zIndex = 100f,
                clickable = true,
                onClick = {
                    Log.d("Circle", "Clicked")
                    showBottomSheet = true
                    selectedMessages = listOf(message) // TODO
                }
            )
        }

        Circle(
            center = circlePosition,
            radius = 80.0, // radius in meters
            fillColor = Color.Blue.copy(alpha = 0.05f),
            strokeColor = Color.Black,
            strokePattern = listOf(Dash(10f), Gap(10f)),
            strokeWidth = 2f
        )
    }

    Box (Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        LocationButton(cameraPositionState = cameraPositionState, context = context)
    }
    Box (Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        AddPost(onClick = { isAddPostVisible = true }, dataStore)
    }

    if (showBottomSheet && selectedMessages.isNotEmpty()) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                showBottomSheet = false
                selectedMessages = emptyList()
            }
        ) {
            // Sheet content
            Column {
                selectedMessages.forEach { message ->
                    Text("Message: ${message.message_content}")
                    // TODO: other UI elements (card) to display message details
                }
            }

            Button(onClick = {
                coroutineScope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            }) {
                Text("Hide Bottom Sheet")
            }
        }
    }

    if (isAddPostVisible) {
        AddPostModal(
            onDismissRequest = { isAddPostVisible = false },
            onPostSubmit = { message ->
                coroutineScope.launch {
                    val post = Message(
                        message_content = message,
                        latitude = currentLocation.latitude,
                        longitude = currentLocation.longitude,
                        timeStamp = System.currentTimeMillis(),
                        user_uid = user
                    )
                    showPostSnackbar = true
                    try {
                        val response = postMessage(post)
                        response?.let {
                            Log.d("PostMessageResponse", it.toString())
                        } ?: Log.d("PostMessageResponse", "Response was successful")
                    } catch (e: Exception) {
                        Log.e("PostMessage", "Failed to post message", e)
                    }
                }
            },
            dataStore = dataStore
        )
    }

    LaunchedEffect(showPostSnackbar) {
        if (showPostSnackbar) {
            snackbarHostState.showSnackbar("Message posted")
            showPostSnackbar = false
        }
    }
}

// Helper function to replace profanities in the message
fun ProfanityCheck(message: String, filterEnabled: Boolean): String {
    return if (!filterEnabled) {
        message.replace("fuck", "****", ignoreCase = true)
        message.replace("shit", "****", ignoreCase = true)
        message.replace("crap", "****", ignoreCase = true)
    } else {
        message
    }
}

@Composable
fun LocationButton(cameraPositionState: CameraPositionState, context: Context) {
    val isMapCentered = remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    val iconResource = if (isMapCentered.value) R.drawable.my_location_24px else R.drawable.location_searching_24px
    val iconColor = if (isMapCentered.value) Color(0xff8fa8ea) else Color(0xffe5ecf2)

    FloatingActionButton(
        modifier = Modifier
            .padding(bottom = 100.dp, end = 16.dp)
            .testTag("locationButton"),
        onClick = {
            getCurrentLocation(context) { location ->
                coroutineScope.launch {
                    isMapCentered.value = true
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(location, 17f), 1000)
                }
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
fun AddPost(onClick: () -> Unit, dataStore: DataStore<Preferences>) {
    var showAddPostModal by remember { mutableStateOf(false) }

    ExtendedFloatingActionButton(
        modifier = Modifier.padding(bottom = 16.dp, end = 16.dp),
        onClick = { onClick() },
        containerColor = Color(0xff99b1ed),
    ) {
        Icon(
            painter = painterResource(R.drawable.add_24px),
            contentDescription = "Add button"
        )
        Text("  Post")
    }

    if (showAddPostModal) {
        AddPostModal(onDismissRequest = { showAddPostModal = false }, onPostSubmit = { message ->
            println("Posted message: $message")
            showAddPostModal = false
        }, dataStore = dataStore)
    }
}

@Composable
fun AddPostModal(onDismissRequest: () -> Unit, onPostSubmit: (String) -> Unit, dataStore: DataStore<Preferences>) {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        MessagePopup(
            onDismissRequest = {
                showDialog = false
                onDismissRequest()
            },
            onPostClick = { message ->
                println("Posted message: $message")
                onPostSubmit(message)
                showDialog = false
            },
            dataStore = dataStore
        )
    }
}

@Composable
fun MessagePopup(onDismissRequest: () -> Unit, onPostClick: (message: String) -> Unit, dataStore: DataStore<Preferences>) {
    var textState by remember { mutableStateOf("") }

    val filterFlow: Flow<Boolean> = getFilterFlow(dataStore)
    val filterEnabled by filterFlow.collectAsState(initial = false)

    Log.d("filter", filterEnabled.toString())

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.padding(16.dp),
            elevation = cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Write your message")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = textState,
                    onValueChange = { textState = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.End) {
                    Button(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val filteredMessage = ProfanityCheck(textState, filterEnabled)
                        onPostClick(filteredMessage)
                        onDismissRequest()
                    }) {
                        Text("Post")
                    }
                }
            }
        }
    }
}
