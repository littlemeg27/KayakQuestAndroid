package com.example.paddlequest.weather

import com.google.gson.annotations.SerializedName

data class OpenWeatherResponse(
    @SerializedName("current")
    val current: Current,
    @SerializedName("hourly")
    val hourly: List<Hourly>,
    @SerializedName("timezone")
    val timezone: String
)
{
    data class Current(
        @SerializedName("temp")
        val temperature: Double,
        @SerializedName("humidity")
        val humidity: Int,
        @SerializedName("wind_speed")
        val windSpeed: Double,
        @SerializedName("pressure")
        val pressure: Int,
        @SerializedName("weather")
        val weather: List<Weather>
    )

    data class Weather(
        @SerializedName("description")
        val description: String
    )

    data class Hourly(
        @SerializedName("dt")
        val timestamp: Long,
        @SerializedName("temp")
        val temperature: Double,
        @SerializedName("weather")
        val weather: List<Weather>
    )
}