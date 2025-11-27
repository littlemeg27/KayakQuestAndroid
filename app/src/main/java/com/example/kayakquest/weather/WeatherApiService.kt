package com.example.kayakquest.weather

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
        @Query("units") units: String = "I",
        @Query("lang") lang: String = "en"
    ): Call<WeatherbitResponse>

    @GET("v2.0/forecast/hourly")
    fun getHourlyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("key") apiKey: String,
        @Query("units") units: String = "I",
        @Query("hours") hours: Int = 24,
        @Query("lang") lang: String = "en"
    ): Call<WeatherbitHourlyResponse>

    @GET("nwis/dv/")
    fun getWaterLevels(
        @Query("site") site: String,
        @Query("parameterCd") parameterCd: String = "00065",  // Gage height
        @Query("startDT") startDate: String,
        @Query("endDT") endDate: String? = null,
        @Query("format") format: String = "json"
    ): Call<USGSWaterResponse>
}

