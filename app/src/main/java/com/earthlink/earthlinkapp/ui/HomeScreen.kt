package com.earthlink.earthlinkapp.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.*
import androidx.navigation.NavHostController
import com.earthlink.earthlinkapp.R
import com.earthlink.earthlinkapp.model.Message
import com.earthlink.earthlinkapp.model.MessageListFormat
import com.earthlink.earthlinkapp.model.ReactionData
import com.earthlink.earthlinkapp.network.*
import com.earthlink.earthlinkapp.utils.ProfanityCheck
import com.earthlink.earthlinkapp.utils.formatTimestamp
import com.earthlink.earthlinkapp.utils.getCurrentLocation
import com.earthlink.earthlinkapp.utils.getFilterFlow
import com.earthlink.earthlinkapp.utils.getThemeFlow
import com.earthlink.earthlinkapp.utils.getUserFlow
import com.earthlink.earthlinkapp.utils.updateDislikes
import com.earthlink.earthlinkapp.utils.updateLikes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    var currentLocation by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    var circlePosition by remember { mutableStateOf(LatLng(0.0, 0.0)) }

    var maxNumber by remember { mutableStateOf(25) }
    var sortType by remember { mutableStateOf(0) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 2f)
    }

    var messages by remember { mutableStateOf<List<List<MessageListFormat>>?>(null) }
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
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(location, 18f))
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(listener)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(listener)
        }
    }

    // obtain the current location every 10 seconds
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
                messages = getMessagesRadius(currentLocation.latitude, currentLocation.longitude, maxNumber, sortType)
            } catch (e: Exception) {
                // Handle exceptions
            }
            delay(10000)
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = uiSettings,
        properties = mapProperties,
    ) {
        var it = 0
        messages?.forEach { messageList ->
            Circle(
                center = LatLng(messageList[it].latitude, messageList[it].longitude),
                radius = 5.0, // radius in meters
                fillColor = Color.Red.copy(alpha = 0.5f),
                strokeWidth = 2f,
                zIndex = 100f,
                clickable = true,
                onClick = {
                    showBottomSheet = true
                    selectedMessages = messageList
                }
            )
            it++
        }

        // range circle
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
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .verticalScroll(rememberScrollState(), flingBehavior = null),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                selectedMessages.forEach { message ->
                    SheetPostCard(message = message, dataStore = dataStore)
                }
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
                        timestamp = System.currentTimeMillis(),
                        user_uid = user,
                        likes = 0,
                        dislikes = 0
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
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(location, 18f), 1000)
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
            Log.d("AddPost", "Posted message: $message")
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
                Log.d("AddPostModal", "Posted message: $message")
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
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Characters left: ${256 - textState.length}",
                        style = TextStyle(fontSize = 12.sp, color = if (textState.length <= 256) Color.Gray else Color.Red)
                    )
                    IconButton(
                        onClick = { textState = "" },
                        enabled = textState.isNotEmpty(),
                        modifier = Modifier.size(24.dp)

                    ) {
                        Icon(
                            painter = painterResource(R.drawable.close_24px),
                            contentDescription = "Clear Message",
                            tint = if (textState.isNotEmpty()) Color.Gray else Color.Transparent
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onDismissRequest,
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            onPostClick(textState)
                            onDismissRequest()
                        },
                        shape = MaterialTheme.shapes.small,
                        enabled = textState.isNotEmpty()
                    ) {
                        Text("Post")
                    }
                }
            }
        }
    }
}

@Composable
fun InteractRow(message: MessageListFormat) {
    // variable 1 or -1. if 1, then it is liked. if -1, then it is disliked. only one can be true at a time, so only
    // one icon can be filled at a time
    var reaction by remember { mutableIntStateOf(0) }
    val likes = remember { mutableIntStateOf(message.likes) }
    val dislikes = remember { mutableIntStateOf(message.dislikes) }

    val clipboardManager = LocalClipboardManager.current

    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row {
            Icon(
                painter = if (reaction == 1) painterResource(R.drawable.thumb_up_filled_24px) else painterResource(R.drawable.thumb_up_24px),
                contentDescription = "Likes",
                tint = Color(0xff8fa8ea),
                modifier = Modifier.clickable {
                    val newValue = if (reaction == 1) 0 else 1
                    reaction = newValue
                    updateLikes(newValue, likes, dislikes)
                    val reactionData = ReactionData(
                        user_uid = message.user_uid,
                        message_id = message.message_id,
                        reaction_type = reaction,
                        timestamp = message.timestamp
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
        }
        Row {
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
                            user_uid = message.user_uid,
                            message_id = message.message_id,
                            reaction_type = reaction,
                            timestamp = message.timestamp
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
        }
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        )
        {
            Icon(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable {
                        val copy = AnnotatedString(message.message_content)
                        clipboardManager.setText(copy)
                    },
                painter = painterResource(R.drawable.content_paste_24px),
                contentDescription = "Copy",
            )
        }
    }
}

@Composable
fun SheetPostCard(message:MessageListFormat, dataStore: DataStore<Preferences>) {
    val nameClicked = remember { mutableStateOf(false) }
    val filterFlow: Flow<Float> = getFilterFlow(dataStore)
    val filterEnabled by filterFlow.collectAsState(initial = 2f)

    val author = message.user_uid
    val timestamp = formatTimestamp(message.timestamp)
    val content = ProfanityCheck(message.message_content, filterEnabled)

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
            InteractRow(message)
        }
        Divider(modifier = Modifier.fillMaxWidth())
    }
}
