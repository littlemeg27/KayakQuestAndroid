package com.example.kayakquest.weather

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService
{
    @GET("v2.0/current")
    fun getCurrentWeather(
        @Query("lon") lon: Double,
        @Query("lat") lat: Double,
        @Query("units") units: String = "I"
    ): Call<WeatherbitResponse>

    @GET("v2.0/forecast/hourly")
    fun getHourlyForecast(
        @Query("lon") lon: Double,
        @Query("lat") lat: Double,
        @Query("units") units: String = "I",
        @Query("hours") hours: String = "24"
    ): Call<WeatherbitHourlyResponse>
}