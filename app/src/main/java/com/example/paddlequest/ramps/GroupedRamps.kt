package com.example.paddlequest.ramps

import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

data class GroupedRamps(
    val waterbody: String,
    val ramps: List<MarkerData>
)

fun groupRampsByWaterbody(markers: List<MarkerData>): List<GroupedRamps> {
    return markers.groupBy { it.riverName.ifBlank { it.otherName } }
        .map { (waterbody, ramps) -> GroupedRamps(waterbody, ramps) }
        .filter { it.ramps.isNotEmpty() }
}

fun haversineDistance(point1: LatLng, point2: LatLng): Double {
    val r = 6371.0
    val dLat = Math.toRadians(point2.latitude - point1.latitude)
    val dLon = Math.toRadians(point2.longitude - point1.longitude)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(point1.latitude)) * cos(Math.toRadians(point2.latitude)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}

fun MarkerData.getLatLng(): LatLng = LatLng(latitude, longitude)