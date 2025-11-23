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
import androidx.compose.runtime.livedata.observeAsState  // ← THIS WAS MISSING
import com.example.kayakquest.operations.SelectedPinViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import com.example.kayakquest.BuildConfig
import com.example.kayakquest.weather.*

@Composable
fun WeatherScreen(viewModel: SelectedPinViewModel = viewModel())
{
    val selectedPin by viewModel.getSelectedPin().observeAsState(null)
    val latLng = selectedPin ?: LatLng(35.227085, -80.843124)  // Charlotte fallback

    var currentWeather by remember { mutableStateOf<WeatherbitResponse?>(null) }
    var hourlyForecast by remember { mutableStateOf<WeatherbitHourlyResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }


    val logging = HttpLoggingInterceptor { message -> Log.d("WeatherAPI", message) }
        .apply { level = HttpLoggingInterceptor.Level.BODY }

    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader("x-rapidapi-key", BuildConfig.RAPIDAPI_KEY)
                    .addHeader("x-rapidapi-host", "weatherbit-v1-mashape.p.rapidapi.com")
                    .build()
            )
        }
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://weatherbit-v1-mashape.p.rapidapi.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(WeatherApiService::class.java)
    val scope = rememberCoroutineScope()

    LaunchedEffect(latLng) {
        isLoading = true
        error = null
        scope.launch {
            try {
                val cur = api.getCurrentWeather(latLng.longitude, latLng.latitude, "I").execute()
                val hour = api.getHourlyForecast(latLng.longitude, latLng.latitude, "I", "24").execute()

                currentWeather = cur.body()
                hourlyForecast = hour.body()

                if (currentWeather == null && hourlyForecast == null) {
                    error = "Empty response – check API key / quota"
                }
            } catch (e: Exception) {
                Log.e("WeatherAPI", "Request failed", e)
                error = "Network error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (isLoading) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text("Loading weather…")
        } else if (error != null) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        } else {
            val data = currentWeather?.data?.firstOrNull()
            val forecast = hourlyForecast?.data.orEmpty()

            if (data != null) {
                Text("Weather in ${data.city_name}", style = MaterialTheme.typography.headlineMedium)
                Text("${data.temp?.toInt() ?: 0}°F", style = MaterialTheme.typography.headlineLarge)
                Text(data.weather?.description ?: "No description")

                Spacer(Modifier.height(16.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
            }
            else
            {
                Text("No weather data")
            }
        }
    }
}