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
import com.example.kayakquest.operations.SelectedPinViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.kayakquest.BuildConfig

@Composable
fun WeatherScreen(
    viewModel: SelectedPinViewModel = viewModel()
) {
    val selectedPin: LatLng? by viewModel.getSelectedPin().observeAsState(null)
    val currentWeatherState = remember { mutableStateOf<WeatherbitResponse?>(null) }
    val hourlyForecastState = remember { mutableStateOf<WeatherbitHourlyResponse?>(null) }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.weatherbit.io/")
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
                // Fetch current weather
                val currentCall: Call<WeatherbitResponse> = apiService.getCurrentWeather(
                    latLng.latitude,
                    latLng.longitude,
                    BuildConfig.OPENWEATHER_API_KEY,  // Use your API key (rename if needed)
                    "I"
                )
                val currentResponse: Response<WeatherbitResponse> = currentCall.execute()
                if (currentResponse.isSuccessful) {
                    currentWeatherState.value = currentResponse.body()
                } else {
                    errorMessage.value = "Failed to fetch current weather: ${currentResponse.code()}"
                }

                // Fetch hourly forecast
                val hourlyCall: Call<WeatherbitHourlyResponse> = apiService.getHourlyForecast(
                    latLng.latitude,
                    latLng.longitude,
                    BuildConfig.OPENWEATHER_API_KEY,
                    "I",
                    24
                )
                val hourlyResponse: Response<WeatherbitHourlyResponse> = hourlyCall.execute()
                if (hourlyResponse.isSuccessful) {
                    hourlyForecastState.value = hourlyResponse.body()
                } else {
                    errorMessage.value = "Failed to fetch hourly forecast: ${hourlyResponse.code()}"
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
        } else {
            val currentWeather = currentWeatherState.value?.data?.firstOrNull() ?: return@Column
            val hourlyForecast = hourlyForecastState.value?.data ?: emptyList()

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Location: ${currentWeather.city_name ?: "Unknown"}")
                Text(text = "Temp: ${currentWeather.temp?.toInt() ?: 0}°F")
                Text(text = "Description: ${currentWeather.weather?.description ?: "N/A"}")
                Text(text = "Humidity: ${currentWeather.rh ?: 0}%")
                Text(text = "Wind Speed: ${currentWeather.wind_spd ?: 0.0} mph")
                Text(text = "Pressure: ${currentWeather.pres ?: 0} hPa")

                LazyRow {
                    items(hourlyForecast) { hour ->
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Time: ${
                                    SimpleDateFormat("HH:00", Locale.getDefault()).format(
                                        Date(hour.ts!! * 1000)
                                    )
                                }"
                            )
                            Text(text = "Temp: ${hour.temp?.toInt() ?: 0}°F")
                            hour.weather?.let { weatherDesc ->
                                Text(text = weatherDesc.description ?: "N/A")
                            }
                        }
                    }
                }
            }
        }
    }
}