package com.example.paddlequest.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.paddlequest.ramps.MarkerData
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.paddlequest.ramps.SelectedPinViewModel
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: SelectedPinViewModel = viewModel())
{
    val context = LocalContext.current
    val selectedPin by viewModel.selectedPin.observeAsState()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(35.227085, -80.843124), 10f)
    }

    // Bottom sheet state
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showTripSuggestions by remember { mutableStateOf(false) }

    LaunchedEffect(selectedPin)
    {
        if (selectedPin != null)
        {
            showTripSuggestions = true
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick =
            {
            latLng ->
            viewModel.setSelectedPin(latLng)
        }
    )
    {
        val markers = loadMarkersFromJson(context)

        markers.forEach { marker ->
            Marker(
                state = MarkerState(position = LatLng(marker.latitude, marker.longitude)),
                title = marker.accessName.ifBlank { "Unnamed Access" },
                snippet = buildString
                {
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

    if (showTripSuggestions)
    {
        ModalBottomSheet(
            onDismissRequest = { showTripSuggestions = false },
            sheetState = sheetState
        )
        {
            SuggestedTripsScreen(
                selectedLocation = selectedPin,
                onDismiss = { showTripSuggestions = false },
                onSelectTrip = { putIn, takeOut ->
                    // TODO: Navigate to FloatPlanScreen or pre-fill it
                    // For example:
                    // navController.navigate("float_plan?putIn=${putIn.accessName}&takeOut=${takeOut.accessName}")
                    showTripSuggestions = false
                }
            )
        }
    }
}

fun loadMarkersFromJson(context: android.content.Context): List<MarkerData>
{
    return try
    {
        val gson = Gson()
        val inputStream = context.assets.open("prepopulated_markers.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<MarkerData>>() {}.type
        gson.fromJson<List<MarkerData>>(reader, type).also { reader.close() }
    }
    catch (e: Exception)
    {
        android.util.Log.e("MapScreen", "Failed to load markers", e)
        emptyList()
    }
}