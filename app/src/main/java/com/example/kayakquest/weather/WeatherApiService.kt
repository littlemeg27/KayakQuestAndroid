package com.example.kayakquest.weather

import com.example.kayakquest.weather.WeatherbitHourlyResponse
import com.example.kayakquest.weather.WeatherbitResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService
{
    @GET("v2.0/current")
    fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("key") apiKey: String,
        @Query("units") units: String = "I"  // Imperial
    ): Call<WeatherbitResponse>

    @GET("v2.0/forecast/hourly")
    fun getHourlyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("key") apiKey: String,
        @Query("units") units: String = "I",
        @Query("hours") hours: Int = 24  // Next 24 hours
    ): Call<WeatherbitHourlyResponse>
}