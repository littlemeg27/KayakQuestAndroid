package com.example.kayakquest.location

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CurrentLocationScreen()
{
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            locationPermissionState.status.isGranted -> {
                Text("Location permission granted!", style = MaterialTheme.typography.headlineMedium)
                // Get location here
            }
            locationPermissionState.status.shouldShowRationale -> {
                Text("Location permission is needed to show your position")
                Spacer(Modifier.height(8.dp))
                Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                    Text("Grant Permission")
                }
            }
            else -> {
                Text("Location permission denied")
                Spacer(Modifier.height(8.dp))
                Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                    Text("Request Permission")
                }
            }
        }
    }
}