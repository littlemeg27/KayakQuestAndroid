package com.example.kayakquest.weather

data class WeatherbitHourlyResponse(
    val city_name: String? = null,
    val country_code: String? = null,
    val data: List<HourlyData>? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val state_code: String? = null,
    val timezone: String? = null
)