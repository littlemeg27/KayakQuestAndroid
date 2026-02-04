package com.example.paddlequest.ramps

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

suspend fun loadMarkersForState(context: Context, stateName: String): List<MarkerData> {
    return withContext(Dispatchers.IO)
    {
        try
        {
            val normalized = stateName.lowercase().replace(" ", "_")
            val fileName = "markers_by_state/markers_$normalized.json"
            val inputStream = context.assets.open(fileName)
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<MarkerData>>() {}.type
            Gson().fromJson<List<MarkerData>>(reader, type).also { reader.close() }
        }
        catch (e: Exception)
        {
            android.util.Log.e("MarkerLoader", "Failed to load $stateName markers", e)
            emptyList()
        }
    }
}