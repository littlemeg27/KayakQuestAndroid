package com.example.paddlequest.location

import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

suspend fun getStateFromLatLng(context: Context, latLng: LatLng): String? {
    if (!Geocoder.isPresent()) {
        return null
    }
    val geocoder = Geocoder(context)
    return withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                var state: String? = null
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) { addresses ->
                    state = addresses.firstOrNull()?.adminArea
                }
                state
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                addresses?.firstOrNull()?.adminArea
            }
        } catch (e: IOException) {
            // Network or other I/O issues
            null
        }
    }
}
