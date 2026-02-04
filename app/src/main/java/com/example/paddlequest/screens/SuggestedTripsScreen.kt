package com.example.paddlequest.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.paddlequest.navigation.Screen
import com.example.paddlequest.ramps.MarkerData
import com.example.paddlequest.ramps.groupRampsByWaterbody
import com.example.paddlequest.ramps.haversineDistance
import com.example.paddlequest.ramps.loadMarkersForState
import com.google.android.gms.maps.model.LatLng

@Composable
fun SuggestedTripsScreen(
    selectedLocation: LatLng?,
    navController: NavController  // ← added param
) {
    val context = LocalContext.current
    val markers = loadMarkersForState(context)
    val grouped = groupRampsByWaterbody(markers)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Suggested Trips") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() })
                    {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to Map")
                    }
                }
            )
        }
    )
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (selectedLocation == null)
            {
                Text("No location selected")
            }
            else
            {
                LazyColumn {
                    items(grouped) { group ->
                        Text(group.waterbody, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))

                        items(group.ramps)
                        { ramp ->
                            val distance = haversineDistance(selectedLocation, ramp.getLatLng())
                            if (distance < 20)
                            {
                                Card(
                                    onClick =
                                        {
                                        val takeOut = group.ramps.lastOrNull() ?: ramp
                                        navController.navigate(Screen.FloatPlan.route + "?putIn=${ramp.accessName}&takeOut=${takeOut.accessName}")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                )
                                {
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
        }
    }
}

