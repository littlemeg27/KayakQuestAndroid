package com.example.kayakquest.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kayakquest.operations.MarkerData
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.kayakquest.operations.SelectedPinViewModel
import androidx.compose.ui.platform.LocalContext  // Add this import for LocalContext
import androidx.compose.foundation.layout.fillMaxSize  // Add for fillMaxSize
import java.io.InputStreamReader

@Composable
fun MapScreen(viewModel: SelectedPinViewModel = viewModel()) {
    val context = LocalContext.current  // Get context here
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(35.227085, -80.843124), 10f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
            viewModel.setSelectedPin(latLng)
        }
    ) {
        // Load markers from JSON (implement loadMarkersFromJson in Kotlin)
        val markers = loadMarkersFromJson(context) // Pass context
        markers.forEach { marker ->
            Marker(
                state = MarkerState(position = LatLng(marker.latitude, marker.longitude)),
                title = marker.title,
                snippet = marker.snippet,
                onClick = {
                    viewModel.setSelectedPin(it.position)
                    false
                }
            )
        }
    }
}

// Port loadMarkersFromJson to Kotlin (place in a utility file or here)
fun loadMarkersFromJson(context: android.content.Context): List<MarkerData> {
    val gson = Gson()
    val inputStream = context.assets.open("prepopulated_markers.json")
    val reader = InputStreamReader(inputStream)
    val markerType = object : TypeToken<List<MarkerData>>() {}.type
    val markers: List<MarkerData> = gson.fromJson(reader, markerType)
    reader.close()
    return markers
}