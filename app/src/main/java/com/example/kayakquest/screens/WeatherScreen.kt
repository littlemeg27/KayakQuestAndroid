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
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.kayakquest.BuildConfig
import com.example.kayakquest.weather.WeatherApiService
import com.example.kayakquest.weather.WeatherbitResponse
import com.example.kayakquest.weather.WeatherbitHourlyResponse

@Composable
fun WeatherScreen(viewModel: SelectedPinViewModel = viewModel())
{
    val selectedPin: LatLng? by viewModel.getSelectedPin().observeAsState(null)

    val currentWeather = remember { mutableStateOf<WeatherbitResponse?>(null) }
    val hourlyForecast = remember { mutableStateOf<WeatherbitHourlyResponse?>(null) }
    val isLoading = remember { mutableStateOf(false) }
    val errorMsg = remember { mutableStateOf<String?>(null) }

    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-rapidapi-key", BuildConfig.RAPIDAPI_KEY)
                .addHeader("x-rapidapi-host", "weatherbit-v1-mashape.p.rapidapi.com")
                .build()
            chain.proceed(request)
        }
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://weatherbit-v1-mashape.p.rapidapi.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(WeatherApiService::class.java)

    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedPin)
    {
        val latLng = selectedPin ?: LatLng(35.227085, -80.843124)
        isLoading.value = true
        errorMsg.value = null

        scope.launch {
            try
            {
                val curCall: Call<WeatherbitResponse> = api.getCurrentWeather(
                    latLng.longitude,
                    latLng.latitude,
                    "I"
                )
                val curResp: Response<WeatherbitResponse> = curCall.execute()
                if (curResp.isSuccessful) currentWeather.value = curResp.body()
                else errorMsg.value = "Current: ${curResp.code()}"

                val hourCall: Call<WeatherbitHourlyResponse> = api.getHourlyForecast(
                    latLng.longitude,
                    latLng.latitude,
                    "I",
                    "24"
                )
                val hourResp: Response<WeatherbitHourlyResponse> = hourCall.execute()
                if (hourResp.isSuccessful) hourlyForecast.value = hourResp.body()
                else errorMsg.value = "Hourly: ${hourResp.code()}"
            } catch (e: Exception)
            {
                errorMsg.value = e.message
            } finally
            {
                isLoading.value = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading.value)
        {
            CircularProgressIndicator()
        } else if (errorMsg.value != null)
        {
            Text(text = errorMsg.value ?: "Unknown error")
        } else
        {
            val cur = currentWeather.value?.data?.firstOrNull() ?: return@Column
            val hourly = hourlyForecast.value?.data ?: emptyList()

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Location: ${cur.city_name ?: "Unknown"}")
                Text(text = "Temp: ${cur.temp?.toInt() ?: 0}°F")
                Text(text = "Description: ${cur.weather?.description ?: "N/A"}")
                Text(text = "Humidity: ${cur.rh ?: 0}%")
                Text(text = "Wind Speed: ${cur.wind_spd ?: 0.0} mph")
                Text(text = "Pressure: ${cur.pres ?: 0} hPa")

                LazyRow {
                    items(hourly) { h ->
                        Column(modifier = Modifier.padding(8.dp))
                        {
                            Text(
                                text = "Time: ${
                                    SimpleDateFormat("HH:00", Locale.getDefault())
                                        .format(Date(h.ts!! * 1000))
                                }"
                            )
                            Text(text = "Temp: ${h.temp?.toInt() ?: 0}°F")
                            h.weather?.let { wd -> Text(text = wd.description ?: "N/A") }
                        }
                    }
                }
            }
        }
    }
}