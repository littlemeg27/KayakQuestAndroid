package com.example.paddlequest.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    selectedPinViewModel: SelectedPinViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // ───────────────────────────────────────────────
    // State
    // ───────────────────────────────────────────────
    var hasLocationPermission by remember { mutableStateOf(false) }

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var currentState by remember { mutableStateOf("Detecting location…") }
    var isLoadingLocation by remember { mutableStateOf(true) }

    var selectedState by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    var markers by remember { mutableStateOf(emptyList<MarkerData>()) }
    var isLoadingMarkers by remember { mutableStateOf(true) }

    val availableStates = listOf(
        "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware",
        "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky",
        "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi",
        "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico",
        "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania",
        "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont",
        "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(35.2271, -80.8431), 10f) // Charlotte area default
    }

    val selectedPin by selectedPinViewModel.selectedPin.observeAsState()

    // ───────────────────────────────────────────────
    // Local suspend function for loading location
    // ───────────────────────────────────────────────
    suspend fun loadCurrentLocation() {
        try {
            val location = fusedLocationClient.lastLocation.await()
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                currentLocation = latLng
                val state = getStateFromLatLng(context, latLng)
                currentState = state ?: "Unknown state"
            } else {
                currentState = "No recent location"
            }
        } catch (e: SecurityException) {
            currentState = "Permission issue"
            Log.e("MapScreen", "SecurityException", e)
        } catch (e: Exception) {
            currentState = "Location error"
            Log.e("MapScreen", "Location fetch failed", e)
        } finally {
            isLoadingLocation = false
        }
    }

    // ───────────────────────────────────────────────
    // Permission handling
    // ───────────────────────────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        hasLocationPermission = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (hasLocationPermission) {
            scope.launch {
                loadCurrentLocation()
            }
        } else {
            currentState = "Location permission denied"
            isLoadingLocation = false
        }
    }

    // Check permissions on first composition
    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        hasLocationPermission = hasFine || hasCoarse

        if (hasLocationPermission) {
            loadCurrentLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Load markers when state changes
    LaunchedEffect(selectedState, currentState) {
        isLoadingMarkers = true
        val stateToLoad = selectedState ?: currentState
        if (stateToLoad !in listOf(
                "Detecting location…",
                "No recent location",
                "Location error",
                "Location permission denied"
            )
        ) {
            markers = loadMarkersForState(context, stateToLoad)
        } else {
            markers = emptyList()
        }
        isLoadingMarkers = false
    }

    // Center map on current location when available
    LaunchedEffect(currentLocation) {
        currentLocation?.let { loc ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(loc, 13f),
                durationMs = 1000
            )
        }
    }

    // ───────────────────────────────────────────────
    // Map configuration (blue dot + button enabled when permitted)
    // ───────────────────────────────────────────────
    val mapProperties by remember(hasLocationPermission) {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = hasLocationPermission,
                mapType = MapType.HYBRID
            )
        )
    }

    val mapUiSettings by remember(hasLocationPermission) {
        mutableStateOf(
            MapUiSettings(
                myLocationButtonEnabled = hasLocationPermission,
                zoomControlsEnabled = false,
                compassEnabled = true,
                mapToolbarEnabled = false
            )
        )
    }

    // ───────────────────────────────────────────────
    // UI Layout
    // ───────────────────────────────────────────────
    Column(modifier = Modifier.fillMaxSize()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectedState ?: currentState,
                onValueChange = { },
                label = { Text("State") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
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
                properties = mapProperties,
                uiSettings = mapUiSettings,
                onMapClick = { latLng ->
                    selectedPinViewModel.setSelectedPin(latLng)
                }
            ) {
                markers.forEach { marker ->
                    Marker(
                        state = MarkerState(position = LatLng(marker.latitude, marker.longitude)),
                        title = marker.accessName.ifBlank { "Unnamed ramp" },
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

            if (hasLocationPermission) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            try {
                                val loc = fusedLocationClient.lastLocation.await()
                                loc?.let {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(it.latitude, it.longitude), 15f
                                        )
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e("MapScreen", "Re-center failed", e)
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "My location")
                }
            }
            Button(
                onClick = { scope.launch { loadCurrentLocation() } },
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Text("Refresh My Location")
            }
        }

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

// ───────────────────────────────────────────────
// Reverse geocoding helper
// ───────────────────────────────────────────────
private suspend fun getStateFromLatLng(context: Context, latLng: LatLng): String? = withContext(Dispatchers.IO) {
    repeat(3) { attempt ->
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                return@withContext addresses.firstOrNull()?.adminArea ?: "North Carolina"
            }
            delay(1000L * (attempt + 1)) // backoff
        } catch (e: Exception) {
            Log.e("Geocode", "Attempt ${attempt + 1} failed", e)
        }
    }
    Log.e("Geocode", "All attempts failed - using fallback")
    "North Carolina" // or your default state
}