package com.example.paddlequest.weather



data class HourlyData(
    val temp: Double? = null,
    val ts: Long? = null,
    val weather: WeatherDescription? = null
)