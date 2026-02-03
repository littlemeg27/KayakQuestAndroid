package com.example.paddlequest.ramps

data class GroupedRamps(
    val waterbody: String,
    val ramps: List<MarkerData>
)

fun groupRampsByWaterbody(markers: List<MarkerData>): List<GroupedRamps>
{
    return markers.groupBy { it.riverName.ifBlank { it.otherName } }
        .map { (waterbody, ramps) ->
            GroupedRamps(waterbody, ramps.sortedBy { it.latitude })
        }
        .filter { it.ramps.size > 1 }
}