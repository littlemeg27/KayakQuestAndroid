package com.example.paddlequest.operations

import com.google.android.gms.maps.model.LatLng

data class MarkerData(
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val snippet: String
)
{
    fun getLatLng(): LatLng = LatLng(latitude, longitude)
}