package com.example.paddlequest.screens

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.paddlequest.ramps.MarkerData
import com.example.paddlequest.ramps.groupRampsByWaterbody
import com.example.paddlequest.ramps.haversineDistance
import com.example.paddlequest.ramps.loadMarkersForState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SuggestedTripsScreen(
    selectedLocation: LatLng?,  // From map selection (fallback)
    onDismiss: () -> Unit,
    onSelectTrip: (MarkerData, MarkerData) -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Request permission automatically
    LaunchedEffect(Unit) {
        if (!locationPermissionsState.allPermissionsGranted) {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    // Load location when permission status changes
    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        isLoading = true
        if (locationPermissionsState.allPermissionsGranted) {
            try {
                val location = fusedLocationClient.lastLocation.await()
                if (location != null) {
                    currentLocation = LatLng(location.latitude, location.longitude)
                } else {
                    locationError = "No location data available"
                }
            } catch (e: SecurityException) {
                locationError = "Location permission denied"
                Log.e("SuggestedTrips", "SecurityException: ${e.message}", e)  // ← use 'e'
            } catch (e: Exception) {
                locationError = "Failed to get location: ${e.message}"
                Log.e("SuggestedTrips", "Location error", e)  // ← use 'e'
            }
        } else {
            currentLocation = selectedLocation
            locationError = "Using selected map location (permission denied)"
        }
        isLoading = false
    }

    // Markers loading (example state — make dynamic later)
    var markers by remember { mutableStateOf(emptyList<MarkerData>()) }
    var isLoadingMarkers by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoadingMarkers = true
        markers = loadMarkersForState(context, "Georgia")  // ← change to dynamic state later
        isLoadingMarkers = false
    }

    val effectiveLocation = currentLocation ?: selectedLocation

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Suggested Trips") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Permission feedback
            if (locationPermissionsState.shouldShowRationale) {
                Text(
                    "Location permission is needed to show trips near you.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = { locationPermissionsState.launchMultiplePermissionRequest() }) {
                    Text("Grant Permission")
                }
                Spacer(Modifier.height(16.dp))
            } else if (!locationPermissionsState.allPermissionsGranted) {
                Text("Location permission required for personalized suggestions.")
                Spacer(Modifier.height(8.dp))
                Button(onClick = { locationPermissionsState.launchMultiplePermissionRequest() }) {
                    Text("Grant Permission")
                }
                Spacer(Modifier.height(16.dp))
            }

            if (isLoading || isLoadingMarkers) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (locationError != null) {
                Text(locationError ?: "Unknown error", color = MaterialTheme.colorScheme.error)
            } else if (effectiveLocation == null) {
                Text("No location available. Select a pin on the map.")
            } else {
                Text(
                    "Trips near: ${String.format(Locale.US, "%.4f", effectiveLocation.latitude)}, ${String.format(Locale.US, "%.4f", effectiveLocation.longitude)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))

                LazyColumn {
                    val grouped = groupRampsByWaterbody(markers)

                    grouped.forEach { group ->
                        item {
                            Text(
                                text = group.waterbody,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }

                        val sortedRamps = group.ramps.sortedBy { haversineDistance(effectiveLocation, it.getLatLng()) }

                        sortedRamps.windowed(size = 2, step = 1).forEach { (putIn, takeOut) ->
                            val distanceKm = haversineDistance(putIn.getLatLng(), takeOut.getLatLng())
                            val distanceMiles = distanceKm * 0.621371

                            val minTime = (distanceMiles / 4).toInt().coerceAtLeast(1)
                            val maxTime = (distanceMiles / 2).toInt().coerceAtLeast(minTime)

                            item {
                                Card(
                                    onClick = { onSelectTrip(putIn, takeOut) },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            "${putIn.accessName} → ${takeOut.accessName}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text("Distance: ${String.format(Locale.US, "%.1f", distanceMiles)} miles")
                                        Text("Estimated paddle time: $minTime–$maxTime hours")
                                        Text("River: ${putIn.riverName}")
                                        Text("Type: ${putIn.type} • ${putIn.county}, ${putIn.state}")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Close")
            }
        }
    }
}
