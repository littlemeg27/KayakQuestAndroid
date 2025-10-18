package com.example.kayakquest.operations

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("location")
    val location: Location? = null,

    @SerializedName("current")
    val current: Current? = null,

    @SerializedName("forecast")
    val forecast: Map<String, ForecastDay>? = null
)
{
    data class Location(
        @SerializedName("name")
        val name: String? = null
    )

    data class Current(
        @SerializedName("temperature")
        val temperature: Int = 0,

        @SerializedName("weather_descriptions")
        val weatherDescriptions: List<String>? = null,

        @SerializedName("humidity")
        val humidity: Int = 0,

        @SerializedName("wind_speed")
        val windSpeed: Int = 0,

        @SerializedName("pressure")
        val pressure: Int = 0
    )

    data class ForecastDay(
        @SerializedName("hourly")
        val hourly: List<Hourly>? = null
    )

    data class Hourly(
        @SerializedName("time")
        val time: String? = null,

        @SerializedName("temperature")
        val temperature: Int = 0,

        @SerializedName("weather_descriptions")
        val weatherDescriptions: List<String>? = null,

        @SerializedName("humidity")
        val humidity: Int = 0
    )
}