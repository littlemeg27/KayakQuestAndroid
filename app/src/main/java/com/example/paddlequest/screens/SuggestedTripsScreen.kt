package com.example.paddlequest.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.paddlequest.ramps.MarkerData
import com.example.paddlequest.ramps.groupRampsByWaterbody
import com.example.paddlequest.ramps.haversineDistance
import com.google.android.gms.maps.model.LatLng

@Composable
fun SuggestedTripsScreen(
    selectedLocation: LatLng?,
    onDismiss: () -> Unit,
    onSelectTrip: (MarkerData, MarkerData) -> Unit
) {
    val context = LocalContext.current
    val currentState = "Georgia"

    var markers by remember { mutableStateOf(emptyList<MarkerData>()) }

    LaunchedEffect(currentState) {
        markers = loadMarkersForState(context, currentState)
    }

    val grouped = remember(markers) { groupRampsByWaterbody(markers) }

    Column(modifier = Modifier.padding(16.dp)) 
    {
        Text("Suggested Trips", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        if (selectedLocation == null) {
            Text("Tap a location on the map to see suggestions")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                grouped.forEach { group ->
                    val nearbyRamps = group.ramps.filter {
                        haversineDistance(selectedLocation, it.getLatLng()) < 20
                    }

                    if (nearbyRamps.isNotEmpty()) {
                        item {
                            Text(group.waterbody, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                        }

                        items(nearbyRamps) { ramp ->
                            val distance = haversineDistance(selectedLocation, ramp.getLatLng())
                            Card(
                                onClick = {
                                    val takeOut = group.ramps.lastOrNull() ?: ramp
                                    onSelectTrip(ramp, takeOut)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(ramp.accessName, style = MaterialTheme.typography.titleMedium)
                                    Text("Distance: ${String.format("%.1f", distance)} km")
                                    Text("River: ${ramp.riverName}")
                                    Text("Type: ${ramp.type} • ${ramp.county}, ${ramp.state}")
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
