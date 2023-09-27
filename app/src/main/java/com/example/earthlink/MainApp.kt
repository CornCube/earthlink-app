package com.example.earthlink

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun Main() {
    // add maps stuff here, it is the base. all rows and cols are over it.
    MapView(
        onLoad = { map ->
            map.setMultiTouchControls(true)
            map.controller.setZoom(9.0)
        }
    )

    Box (Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
        MainMenu(onClick = { /*TODO: opens the main menu screen*/ })
    }
    Box (Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        AddPost(onClick = { /*TODO: opens the message post screen*/ })
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
        Icon(Icons.Filled.Menu, "Main menu")
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
        Icon(Icons.Filled.Add, "Add button")
    }
}
