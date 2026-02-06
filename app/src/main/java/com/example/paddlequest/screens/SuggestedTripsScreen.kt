package com.example.paddlequest.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.paddlequest.ramps.MarkerData
import com.example.paddlequest.ramps.groupRampsByWaterbody
import com.example.paddlequest.ramps.haversineDistance
import com.example.paddlequest.ramps.loadMarkersForState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestedTripsScreen(
    selectedLocation: LatLng?,
    navController: NavController,
    onDismiss: () -> Unit,
    onSelectTrip: (MarkerData, MarkerData) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var currentState by remember { mutableStateOf("Detecting...") }
    var locationLoading by remember { mutableStateOf(true) }
    var locationError by remember { mutableStateOf<String?>(null) }

    var markers by remember { mutableStateOf(emptyList<MarkerData>()) }
    var isLoadingMarkers by remember { mutableStateOf(true) }

    // Suspend function to load location
    suspend fun loadCurrentLocationInternal() {
        locationLoading = true
        try {
            val location = fusedLocationClient.lastLocation.await()
            if (location != null) {
                currentLocation = LatLng(location.latitude, location.longitude)
                val state = getStateFromLatLng(context, currentLocation!!)
                currentState = state ?: "Unknown"
            } else {
                currentState = "No location"
            }
        } catch (e: SecurityException) {
            locationError = "Location permission denied"
            currentLocation = selectedLocation
        } catch (e: Exception) {
            locationError = "Failed to get location"
            currentLocation = selectedLocation
            Log.e("SuggestedTrips", "Error loading location", e)
        } finally {
            locationLoading = false
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            scope.launch {
                loadCurrentLocationInternal()
            }
        } else {
            locationError = "Location permission denied"
            currentLocation = selectedLocation
            locationLoading = false
        }
    }

    // Check and request permissions
    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            loadCurrentLocationInternal()
        } else {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    // Load markers based on current state
    LaunchedEffect(currentState) {
        if (currentState != "Detecting..." && currentState != "No location" && currentState != "Location error" && currentState != "Permission denied") {
            isLoadingMarkers = true
            markers = loadMarkersForState(context, currentState)
            isLoadingMarkers = false
        }
    }

    val effectiveLocation = currentLocation ?: selectedLocation

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Suggested Trips") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
            if (locationError != null) {
                Text(locationError!!, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    permissionLauncher.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ))
                }) {
                    Text("Retry Permission")
                }
            }

            if (locationLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (effectiveLocation == null) {
                Text("No location available. Select a pin on the map.")
            } else {
                Text(
                    "Trips near: ${String.format(Locale.US, "%.4f", effectiveLocation.latitude)}, ${String.format(Locale.US, "%.4f", effectiveLocation.longitude)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))

                if (isLoadingMarkers) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    LazyColumn {
                        val grouped = groupRampsByWaterbody(markers)

                        grouped.forEach { group ->
                            item {
                                Text(group.waterbody, style = MaterialTheme.typography.titleLarge)
                                Spacer(Modifier.height(8.dp))
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

            Spacer(Modifier.weight(1f))
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Close")
            }
        }
    }
}
