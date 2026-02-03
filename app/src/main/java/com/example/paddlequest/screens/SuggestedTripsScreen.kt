package com.example.paddlequest.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.paddlequest.ramps.MarkerData
import com.example.paddlequest.ramps.groupRampsByWaterbody
import com.google.type.LatLng


@Composable
fun SuggestedTripsScreen(
    selectedLocation: LatLng?,
    onDismiss: () -> Unit,
    onSelectTrip: (MarkerData, MarkerData) -> Unit
) {
    val markers = loadMarkersFromJson(LocalContext.current)  // Or pass from parent
    val grouped = groupRampsByWaterbody(markers)

    Column(modifier = Modifier.padding(16.dp))
    {
        Text("Suggested Trips", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        if (selectedLocation == null)
        {
            Text("Tap a location on the map to see suggestions")
        }
        else
        {
            LazyColumn{
                items(grouped)
                { group ->
                    Text(group.waterbody, style = MaterialTheme.typography.titleMedium)
                    group.ramps.forEach
                    { ramp ->
                        // Simple suggestion: ramps near selected location
                        val distance = haversineDistance(selectedLocation, ramp.getLatLng())
                        if (distance < 20) {  // within 20km
                            Card(onClick = {
                                // Example: suggest next ramp downstream
                                onSelectTrip(ramp, group.ramps.last())  // simplistic
                            }) {
                                Text("${ramp.accessName} • ${distance}km")
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = onDismiss) {
            Text("Close")
        }
    }
}