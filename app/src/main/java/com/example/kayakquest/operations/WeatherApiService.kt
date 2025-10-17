package com.example.kayakquest.operations

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService
{
    @GET("data/3.0/onecall")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String,
        @Query("exclude") exclude: String
    ): Response<OpenWeatherResponse>
}