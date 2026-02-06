package com.example.paddlequest.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.paddlequest.ramps.MarkerData
import com.example.paddlequest.ramps.SelectedPinViewModel
import com.example.paddlequest.ramps.loadMarkersForState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel: SelectedPinViewModel = viewModel()

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Current device location from GPS
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var currentState by remember { mutableStateOf("Detecting...") }
    var isLoadingLocation by remember { mutableStateOf(true) }

    // User-selected state override (null = use current)
    var selectedState by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val availableStates = listOf(
        "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware",
        "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky",
        "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi",
        "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico",
        "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania",
        "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont",
        "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"
    )

    // Markers for current/selected state
    var markers by remember { mutableStateOf(emptyList<MarkerData>()) }
    var isLoadingMarkers by remember { mutableStateOf(true) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            scope.launch {
                loadCurrentLocation(context, fusedLocationClient)
            }
        } else {
            currentState = "Permission denied"
            isLoadingLocation = false
        }
    }

    // Check and request permissions on first open
    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            loadCurrentLocation(context, fusedLocationClient)
        } else {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    // Load markers for selected state (or current if none selected)
    LaunchedEffect(selectedState, currentState) {
        isLoadingMarkers = true
        val stateToLoad = selectedState ?: currentState
        if (stateToLoad != "Detecting..." && stateToLoad != "No location" && stateToLoad != "Location error" && stateToLoad != "Permission denied") {
            markers = loadMarkersForState(context, stateToLoad)
        } else {
            markers = emptyList()
        }
        isLoadingMarkers = false
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(33.7490, -84.3880), 10f)  // Atlanta default
    }

    // Center map on current location when available
    LaunchedEffect(currentLocation) {
        currentLocation?.let { loc ->
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(loc, 12f),
                durationMs = 1000
            )
        }
    }

    val selectedPin by viewModel.selectedPin.collectAsState()  // Assuming StateFlow now

    Column(modifier = Modifier.fillMaxSize()) {
        // State selector dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectedState ?: currentState,
                onValueChange = { },
                label = { Text("State") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availableStates.forEach { state ->
                    DropdownMenuItem(
                        text = { Text(state) },
                        onClick = {
                            selectedState = state
                            expanded = false
                        }
                    )
                }
                DropdownMenuItem(
                    text = { Text("Use Current Location") },
                    onClick = {
                        selectedState = null
                        expanded = false
                    }
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    viewModel.setSelectedPin(latLng)
                }
            ) {
                // Current location marker (blue dot)
                currentLocation?.let { loc ->
                    Marker(
                        state = MarkerState(position = loc),
                        title = "Current Location",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
                }

                markers.forEach { marker ->
                    Marker(
                        state = MarkerState(position = LatLng(marker.latitude, marker.longitude)),
                        title = marker.accessName.ifBlank { "Unnamed" },
                        snippet = buildString {
                            append(marker.riverName.ifBlank { "No river" })
                            if (marker.otherName.isNotBlank()) append(" • ${marker.otherName}")
                            append("\n${marker.type} • ${marker.county}, ${marker.state}")
                        }
                    )
                }
            }

            if (isLoadingLocation || isLoadingMarkers) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        // Button to SuggestedTripsScreen
        Button(
            onClick = {
                val loc = currentLocation ?: selectedPin
                if (loc != null) {
                    navController.navigate("suggested_trips/${loc.latitude}/${loc.longitude}")
                } else {
                    Toast.makeText(context, "No location available", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Suggest Trips Near Location")
        }
    }
}

// Helper function to load current location (explicit permission check)
suspend fun loadCurrentLocation(context: Context, fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        Log.e("MapScreen", "Location permission not granted")
        return
    }

    try {
        val location = fusedLocationClient.lastLocation.await()
        if (location != null) {
            val latLng = LatLng(location.latitude, location.longitude)
            currentLocation = latLng
            val state = getStateFromLatLng(context, latLng)
            currentState = state ?: "Unknown"
        } else {
            currentState = "No location"
        }
    } catch (e: SecurityException) {
        currentState = "Permission denied"
        Log.e("MapScreen", "SecurityException: ${e.message}", e)
    } catch (e: Exception) {
        currentState = "Location error"
        Log.e("MapScreen", "Error getting location", e)
    }
}

// Helper function for reverse geocoding
suspend fun getStateFromLatLng(context: Context, latLng: LatLng): String? {
    return withContext(Dispatchers.IO) {
        try {
            val placesClient = Places.createClient(context)
            val request = FindCurrentPlaceRequest.newInstance(listOf(Place.Field.ADDRESS_COMPONENTS))
            val response = placesClient.findCurrentPlace(request).await()
            response.placeLikelihoods.firstOrNull()?.place?.addressComponents?.asList()
                ?.find { it.types.contains("administrative_area_level_1") }?.name
        } catch (e: Exception) {
            Log.e("Geocode", "Failed to get state", e)
            null
        }
    }
}