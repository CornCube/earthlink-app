package com.earthlink.earthlinkapp.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil


fun checkForPermission(context: Context): Boolean {
    return !(ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) != PackageManager.PERMISSION_GRANTED)
}

fun String.capitaliseIt() = this.lowercase().capitalize(Locale.current)

fun calculateDistance(latlngList: List<LatLng>): Double {
    var totalDistance = 0.0

    for (i in 0 until latlngList.size - 1) {
        totalDistance += SphericalUtil.computeDistanceBetween(latlngList[i],latlngList[i + 1])

    }

    return (totalDistance * 0.001)
}

fun calculateSurfaceArea(latlngList: List<LatLng>): Double {
    if (latlngList.size < 3) {
        return 0.0
    }
    return SphericalUtil.computeArea(latlngList)
}

fun formattedValue(value: Double) = String.format("%.2f",value)

@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context, onLocationFetched: (location: LatLng) -> Unit) {
    var loc: LatLng
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                loc = LatLng(latitude,longitude)
                onLocationFetched(loc)
            }
        }
        .addOnFailureListener { exception: Exception ->
            // Handle failure to get location
            Log.d("MAP-EXCEPTION",exception.message.toString())
        }
}

// function to convert timestamp to datetime, looks like "2023-11-14T10:29:29.298199". timestamp
// should be converted into the following "11/14/2023 10:29 AM". It should use 12 hour time format.
fun formatTimestamp(timestamp: String): String {
    val date = timestamp.split("T")[0]
    val time = timestamp.split("T")[1].split(".")[0]
    val year = date.split("-")[0]
    val month = date.split("-")[1]
    val day = date.split("-")[2]
    val hour = time.split(":")[0]
    val minute = time.split(":")[1]
    val second = time.split(":")[2]

    val hourInt = hour.toInt()
    val minuteInt = minute.toInt()

    val ampm = if (hourInt < 12) "AM" else "PM"
    val hour12 = if (hourInt > 12) hourInt - 12 else hourInt
    val hourString = if (hour12 < 10) "0$hour12" else hour12.toString()
    val minuteString = if (minuteInt < 10) "0$minuteInt" else minuteInt.toString()

    return "$month/$day/$year $hourString:$minuteString $ampm"
}
