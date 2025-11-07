package com.example.kayakquest.weather

data class WeatherbitResponse(
    val data: List<WeatherData>? = null,
    val minutely: List<Any>? = null,  // Optional
    val count: Int? = null
)