package com.example.paddlequest.screens

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.paddlequest.location.getStateFromLatLng
import com.example.paddlequest.ramps.MarkerData
import com.example.paddlequest.ramps.groupRampsByWaterbody
import com.example.paddlequest.ramps.haversineDistance
import com.example.paddlequest.ramps.loadMarkersForState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.tasks.await
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestedTripsScreen(
    selectedLocation: LatLng?,           // ← passed from MapScreen pin or current location
    navController: NavController,
    onDismiss: () -> Unit,
    onSelectTrip: (MarkerData, MarkerData) -> Unit
) {
    val context = LocalContext.current

    // ───────────────────────────────────────────────
    // State
    // ───────────────────────────────────────────────
    var effectiveLocation by remember { mutableStateOf(selectedLocation) }
    var currentState by remember { mutableStateOf("Loading state…") }
    var locationLoading by remember { mutableStateOf(true) }
    var locationError by remember { mutableStateOf<String?>(null) }

    var markers by remember { mutableStateOf(emptyList<MarkerData>()) }
    var isLoadingMarkers by remember { mutableStateOf(true) }

    // ───────────────────────────────────────────────
    // Immediately use passed location (from map pin or current)
    // ───────────────────────────────────────────────
    LaunchedEffect(selectedLocation) {
        Log.d("SuggestedTrips", "Screen entered with selectedLocation=$selectedLocation")

        if (selectedLocation != null) {
            // Priority: use the passed location (from pin or current)
            effectiveLocation = selectedLocation

            // Get state for this location
            val state = getStateFromLatLng(context, selectedLocation)
            currentState = state ?: "Unknown area"
            Log.d("SuggestedTrips", "Using passed location → state=$currentState")
            locationLoading = false
        } else {
            // Fallback: try device location (only if no pin passed)
            Log.d("SuggestedTrips", "No passed location → trying device GPS")
            locationLoading = true

            val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

            if (hasFine || hasCoarse) {
                try {
                    val fused = LocationServices.getFusedLocationProviderClient(context)
                    val location = fused.lastLocation.await()
                    if (location != null) {
                        effectiveLocation = LatLng(location.latitude, location.longitude)
                        val state = getStateFromLatLng(context, effectiveLocation!!)
                        currentState = state ?: "Unknown"
                        Log.d("SuggestedTrips", "Device location loaded → state=$currentState")
                    } else {
                        currentState = "No device location"
                    }
                } catch (e: Exception) {
                    locationError = "Failed to get device location"
                    Log.e("SuggestedTrips", "Device location error", e)
                }
            } else {
                locationError = "Location permission needed"
            }
            locationLoading = false
        }
    }

    // ───────────────────────────────────────────────
    // Load markers once we have valid state + location
    // ───────────────────────────────────────────────
    LaunchedEffect(key1 = currentState, key2 = effectiveLocation) {
        Log.d("SuggestedTrips", "Marker loading triggered → state='$currentState', loc=$effectiveLocation")

        isLoadingMarkers = true

        if (effectiveLocation == null) {
            Log.d("SuggestedTrips", "No location available → clearing markers")
            markers = emptyList()
            isLoadingMarkers = false
            return@LaunchedEffect
        }

        if (currentState.isBlank() ||
            currentState.contains("Detecting", ignoreCase = true) ||
            currentState.contains("Loading", ignoreCase = true) ||
            currentState.contains("No ", ignoreCase = true) ||
            currentState.contains("error", ignoreCase = true) ||
            currentState.contains("Permission", ignoreCase = true) ||
            currentState.contains("Unknown", ignoreCase = true)
        ) {
            Log.d("SuggestedTrips", "Invalid state → clearing markers")
            markers = emptyList()
            isLoadingMarkers = false
            return@LaunchedEffect
        }

        Log.d("SuggestedTrips", "Loading markers for $currentState")
        try {
            val loaded = loadMarkersForState(context, currentState)
            markers = loaded
            Log.d("SuggestedTrips", "Loaded ${loaded.size} markers")
        } catch (e: Throwable) {
            Log.e("SuggestedTrips", "Marker load failed", e)
            markers = emptyList()
        } finally {
            isLoadingMarkers = false
            Log.d("SuggestedTrips", "Marker loading complete")
        }
    }

    // ───────────────────────────────────────────────
    // UI
    // ───────────────────────────────────────────────
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Suggested Trips") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (locationLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Determining location...")
            } else if (locationError != null) {
                Text(locationError!!, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss) {
                    Text("Go Back")
                }
            } else if (effectiveLocation == null) {
                Text("No location selected. Go back and pick a pin on the map.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss) {
                    Text("Go Back")
                }
            } else {
                Text(
                    "Showing trips near:\n${String.format(Locale.US, "%.4f", effectiveLocation!!.latitude)}, " +
                            String.format(Locale.US, "%.4f", effectiveLocation!!.longitude),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("State/Area: $currentState", style = MaterialTheme.typography.labelMedium)

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoadingMarkers) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading nearby ramps...")
                } else if (markers.isEmpty()) {
                    Text("No kayak ramps found near this location in $currentState.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onDismiss) {
                        Text("Go Back")
                    }
                } else {
                    LazyColumn {
                        val grouped = groupRampsByWaterbody(markers)

                        grouped.forEach { group ->
                            item {
                                Text(
                                    group.waterbody,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                )
                            }

                            val sortedRamps = group.ramps.sortedBy {
                                haversineDistance(effectiveLocation!!, it.getLatLng())
                            }

                            sortedRamps.windowed(size = 2, step = 1).forEach { (putIn, takeOut) ->
                                val distanceKm = haversineDistance(putIn.getLatLng(), takeOut.getLatLng())
                                val distanceMiles = distanceKm * 0.621371

                                val minTime = (distanceMiles / 4).toInt().coerceAtLeast(1)
                                val maxTime = (distanceMiles / 2).toInt().coerceAtLeast(minTime)

                                item {
                                    Card(
                                        onClick = { onSelectTrip(putIn, takeOut) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(
                                                "${putIn.accessName} → ${takeOut.accessName}",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Text("Distance: ${String.format(Locale.US, "%.1f", distanceMiles)} miles")
                                            Text("Estimated time: $minTime–$maxTime hours")
                                            Text("River: ${putIn.riverName}")
                                            Text("Type: ${putIn.type} • ${putIn.county}, ${putIn.state}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Close")
            }
        }
    }
}
