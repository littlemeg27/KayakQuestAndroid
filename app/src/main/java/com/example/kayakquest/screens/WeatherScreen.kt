package com.example.kayakquest.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import com.example.kayakquest.Operations.SelectedPinViewModel
import com.example.kayakquest.Operations.OpenWeatherResponse
import com.example.kayakquest.Operations.WeatherApiService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WeatherScreen(
    viewModel: SelectedPinViewModel = viewModel()
) {
    val selectedPin: LatLng? by viewModel.getSelectedPin().observeAsState(initial = null)  // Specify type and initial to fix inference
    val weatherState = remember { mutableStateOf<OpenWeatherResponse?>(null) }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val apiService: WeatherApiService = retrofit.create(WeatherApiService::class.java)

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedPin) {
        val latLng = selectedPin ?: LatLng(35.227085, -80.843124)
        isLoading.value = true
        errorMessage.value = null
        coroutineScope.launch {
            try {
                val call: Call<OpenWeatherResponse> = apiService.getWeather(
                    latLng.latitude,
                    latLng.longitude,
                    "YOUR_API_KEY_HERE",
                    "imperial",
                    "minutely,daily,alerts"
                )
                val response: Response<OpenWeatherResponse> = call.execute()
                if (response.isSuccessful) {
                    weatherState.value = response.body()
                } else {
                    errorMessage.value = "Failed to fetch weather: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage.value = "Error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading.value) {
            CircularProgressIndicator()
        } else if (errorMessage.value != null) {
            Text(text = errorMessage.value ?: "Unknown error")
        }
        else
        {
            val weather = weatherState.value ?: return@Column
            Column(modifier = Modifier.padding(16.dp))
            {
                Text(text = "Location: ${weather.timezone?.split("/")?.lastOrNull() ?: "Unknown"}")
                Text(text = "Temp: ${weather.current?.temperature?.toInt() ?: 0}°F")
                weather.current?.weather?.firstOrNull()?.description?.let { desc: String ->
                    Text(text = "Description: $desc")
                }
                Text(text = "Humidity: ${weather.current?.humidity ?: 0}%")
                Text(text = "Wind Speed: ${weather.current?.windSpeed ?: 0.0} mph")
                Text(text = "Pressure: ${weather.current?.pressure ?: 0} hPa")

                LazyRow {
                    items(weather.hourly ?: emptyList<OpenWeatherResponse.Hourly>()) { hour: OpenWeatherResponse.Hourly ->
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Time: ${
                                    SimpleDateFormat("HH:00", Locale.getDefault()).format(
                                        Date(hour.timestamp * 1000)
                                    )
                                }"
                            )
                            Text(text = "Temp: ${hour.temperature.toInt()}°F")
                            hour.weather?.firstOrNull()?.description?.let { desc: String ->
                                Text(text = desc)
                            }
                        }
                    }
                }
            }
        }
    }
}