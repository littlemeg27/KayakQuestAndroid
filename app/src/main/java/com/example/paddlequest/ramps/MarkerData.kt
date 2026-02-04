package com.example.paddlequest.ramps

import com.google.android.gms.maps.model.LatLng
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.File
import java.io.InputStreamReader

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
