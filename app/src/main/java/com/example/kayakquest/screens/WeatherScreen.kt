package com.example.kayakquest.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import com.example.kayakquest.operations.SelectedPinViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import com.example.kayakquest.weather.*

@Composable
fun WeatherScreen(viewModel: SelectedPinViewModel = viewModel())
{
    val selectedPin by viewModel.getSelectedPin().observeAsState(null)
    val latLng = selectedPin ?: LatLng(35.227085, -80.843124)

    var currentWeather by remember { mutableStateOf<WeatherbitResponse?>(null) }
    var hourlyForecast by remember { mutableStateOf<WeatherbitHourlyResponse?>(null) }
    var riverLevels by remember { mutableStateOf<USGSWaterResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val weatherRetrofit = Retrofit.Builder()
        .baseUrl("https://api.weatherbit.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val usgsRetrofit = Retrofit.Builder()
        .baseUrl("https://waterservices.usgs.gov/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val weatherApi = weatherRetrofit.create(WeatherApiService::class.java)
    val usgsApi = usgsRetrofit.create(WeatherApiService::class.java)

    val scope = rememberCoroutineScope()

    LaunchedEffect(latLng)
    {
        isLoading = true
        error = null
        scope.launch {
            try
            {
                // Weatherbit
                val currentResponse = withContext(Dispatchers.IO)
                {
                    weatherApi.getCurrentWeather(latLng.latitude, latLng.longitude, "4abbf7b7bee04946849422a2ba6c716c", "I", "en").execute()
                }
                currentWeather = currentResponse.body()

                val hourlyResponse = withContext(Dispatchers.IO)
                {
                    weatherApi.getHourlyForecast(latLng.latitude, latLng.longitude, "4abbf7b7bee04946849422a2ba6c716c", "I", 24, "en").execute()
                }
                hourlyForecast = hourlyResponse.body()

                // USGS River Levels — WORKING STATION WITH GAGE HEIGHT DATA
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                val sevenDaysAgo = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L))

                val waterResponse = withContext(Dispatchers.IO)
                {
                    usgsApi.getWaterLevels(
                        site = "02146000",  // ← Catawba River Near Rock Hill, SC — HAS GAGE HEIGHT!
                        startDate = sevenDaysAgo,
                        endDate = today
                    ).execute()
                }
                riverLevels = waterResponse.body()

                Log.d("USGS", "River response: ${riverLevels?.value?.timeSeries?.size} series")

            }
            catch (e: Exception)
            {
                Log.e("Weather", "Failed", e)
                error = "Network error: ${e.message}"
            }
            finally
            {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        if (isLoading)
        {
            CircularProgressIndicator()
            Text("Loading weather & river levels…")
        }
        else if (error != null)
        {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        }
        else
        {
            val weatherData = currentWeather?.data?.firstOrNull()
            val forecast = hourlyForecast?.data.orEmpty()
            val riverSeries = riverLevels?.value?.timeSeries?.firstOrNull()?.values?.firstOrNull()?.value.orEmpty()

            // Weather
            if (weatherData != null)
            {
                Text("Weather in ${weatherData.city_name}", style = MaterialTheme.typography.headlineMedium)
                Text("${weatherData.temp?.toInt() ?: 0}°F", style = MaterialTheme.typography.headlineLarge)
                Text(weatherData.weather?.description ?: "No description")

                Spacer(Modifier.height(16.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp))
                {
                    items(forecast.take(12)) { h ->
                        Card {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(SimpleDateFormat("h a", Locale.getDefault()).format(Date(h.ts!! * 1000)))
                                Text("${h.temp?.toInt() ?: 0}°")
                                h.weather?.description?.let { Text(it) }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }

            // River Levels
            Text("Catawba River Level (Last 7 Days)", style = MaterialTheme.typography.headlineSmall)
            if (riverSeries.isEmpty())
            {
                Text("No river data available", color = MaterialTheme.colorScheme.error)
            }
            else
            {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp))
                {
                    items(riverSeries.take(7)) { reading ->
                        Card {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally)
                            {
                                Text(SimpleDateFormat("MMM dd", Locale.getDefault()).format(
                                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(reading.dateTime ?: "")!!
                                ))
                                Text("${reading.value} ft", style = MaterialTheme.typography.titleLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}