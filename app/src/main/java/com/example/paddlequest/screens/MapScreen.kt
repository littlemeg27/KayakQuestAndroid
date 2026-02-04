package com.example.paddlequest.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: SelectedPinViewModel = viewModel())
{
    val context = LocalContext.current
    var markers by remember { mutableStateOf<List<MarkerData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val selectedPin by viewModel.selectedPin.observeAsState()

    // Bottom sheet state
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showTripSuggestions by remember { mutableStateOf(false) }

    LaunchedEffect(Unit)
    {
        withContext(Dispatchers.IO)
        {
            markers = loadMarkersFromJson(context)
            isLoading = false
        }
    }

    if (isLoading)
    {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
        {
            CircularProgressIndicator()
        }
    }
    else
    {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = rememberCameraPositionState
            {
                position = CameraPosition.fromLatLngZoom(LatLng(35.227085, -80.843124), 10f)
            }
        ) {
            markers.forEach { marker ->
                Marker(
                    state = MarkerState(position = LatLng(marker.latitude, marker.longitude)),
                    title = marker.accessName.ifBlank { "Unnamed" },
                    snippet = buildString {
                        append(marker.riverName.ifBlank { "No river" })
                        if (marker.otherName.isNotBlank()) append(" • ${marker.otherName}")
                        append("${marker.type} • ${marker.county}, ${marker.state}")
                    }
                )
            }
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