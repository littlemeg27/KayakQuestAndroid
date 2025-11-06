package com.example.kayakquest.weather

import com.example.kayakquest.weather.WeatherData

data class WeatherbitResponse(
    val data: List<WeatherData>? = null,
    val minutely: List<Any>? = null,  // Optional, as per example
    val count: Int? = null
)