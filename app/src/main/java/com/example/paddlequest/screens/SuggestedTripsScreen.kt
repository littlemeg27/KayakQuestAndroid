package com.example.paddlequest.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.paddlequest.ramps.MarkerData
import com.google.android.gms.maps.model.LatLng   // ← ONLY this LatLng (Google Maps)
import kotlin.math.*


// Helper data class
data class GroupedRamps(
    val waterbody: String,
    val ramps: List<MarkerData>
)

// Helper functions
fun groupRampsByWaterbody(markers: List<MarkerData>): List<GroupedRamps>
{
    return markers.groupBy { it.riverName.ifBlank { it.otherName } }
        .map { (waterbody, ramps) ->
            GroupedRamps(waterbody, ramps)
        }
        .filter { it.ramps.isNotEmpty() }
}

fun haversineDistance(point1: LatLng, point2: LatLng): Double
{
    val r = 6371.0 // Earth radius in km
    val dLat = Math.toRadians(point2.latitude - point1.latitude)
    val dLon = Math.toRadians(point2.longitude - point1.longitude)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(point1.latitude)) * cos(Math.toRadians(point2.latitude)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}

fun MarkerData.getLatLng(): LatLng = LatLng(latitude, longitude)

@Composable
fun SuggestedTripsScreen(
    selectedLocation: LatLng?,
    onDismiss: () -> Unit,
    onSelectTrip: (MarkerData, MarkerData) -> Unit
) {
    val context = LocalContext.current
    val markers = loadMarkersFromJson(context)
    val grouped = groupRampsByWaterbody(markers)

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Suggested Trips", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        if (selectedLocation == null) {
            Text("Tap a location on the map to see suggestions")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)  // optional: let it take available space
            ) {
                items(grouped) { group ->
                    Text(group.waterbody, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    items(group.ramps) { ramp ->
                        val distance = haversineDistance(selectedLocation, ramp.getLatLng())
                        if (distance < 20) {
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