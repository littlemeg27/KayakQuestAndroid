package com.example.kayakquest.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kayakquest.operations.MarkerData
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.kayakquest.operations.SelectedPinViewModel

@Composable
fun MapScreen(viewModel: SelectedPinViewModel = viewModel()) {
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
        val markers = loadMarkersFromJson() // Convert your Java method to Kotlin
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

// Port loadMarkersFromJson to Kotlin (place in a utility file)
fun loadMarkersFromJson(): List<MarkerData> {
    // Implement similar to Java, using context.assets.open("prepopulated_markers.json")
    // Return list of MarkerData
}