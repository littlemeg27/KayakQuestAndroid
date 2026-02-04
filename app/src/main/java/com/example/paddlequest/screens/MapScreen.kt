package com.example.paddlequest.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.paddlequest.ramps.MarkerData
import com.example.paddlequest.ramps.SelectedPinViewModel
import com.example.paddlequest.ramps.loadMarkersForState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: SelectedPinViewModel = viewModel())
{
    val context = LocalContext.current

    // State selector
    var selectedState by remember { mutableStateOf("North Carolina") }

    // Available states (add all you have files for)
    val availableStates = listOf(
        "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware",
        "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky",
        "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi",
        "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico",
        "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania",
        "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont",
        "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"
    )

    var markers by remember { mutableStateOf(emptyList<MarkerData>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(selectedState)
    {
        isLoading = true
        markers = loadMarkersForState(context, selectedState)
        isLoading = false
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(35.227085, -80.843124), 10f)
    }

    Column(modifier = Modifier.fillMaxSize())
    {
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = { /* toggle if you want */ }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectedState,
                onValueChange = { },
                label = { Text("Select State") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            ExposedDropdownMenu(
                expanded = false,
                onDismissRequest = { }
            )
            {
                availableStates.forEach { state ->
                    DropdownMenuItem(
                        text = { Text(state) },
                        onClick = {
                            selectedState = state
                        }
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1f))
        {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    viewModel.setSelectedPin(latLng)
                }
            ) {
                markers.forEach { marker ->
                    Marker(
                        state = MarkerState(position = LatLng(marker.latitude, marker.longitude)),
                        title = marker.accessName.ifBlank { "Unnamed Access" },
                        snippet = buildString {
                            append(marker.riverName.ifBlank { "No river specified" })

                            if (marker.otherName.isNotBlank()) append(" • ${marker.otherName}")
                            append("\n${marker.type}")
                            append(" • ${marker.county}, ${marker.state}")
                            if (marker.department.isNotBlank()) append("\n${marker.department}")
                        },
                        onClick = { clickedMarker ->
                            viewModel.setSelectedPin(clickedMarker.position)
                            false
                        }
                    )
                }
            }

            if (isLoading)
            {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        // TODO: Your bottom sheet or navigation button to SuggestedTripsScreen
        // (we'll replace bottom sheet in Step 2)
    }
}

