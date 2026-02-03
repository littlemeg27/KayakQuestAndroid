package com.example.paddlequest.operations

import com.google.android.gms.maps.model.LatLng

data class MarkerData(
    val latitude: Double,
    val longitude: Double,
    val department: String,
    val state: String,
    val county: String,
    val accessName: String,
    val riverName: String,
    val type: String,
    val otherName: String
)
{
    fun getLatLng(): LatLng = LatLng(latitude, longitude)
}