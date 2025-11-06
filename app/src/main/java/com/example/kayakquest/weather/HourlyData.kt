package com.example.kayakquest.weather

import com.example.kayakquest.weather.WeatherDescription

data class HourlyData(
    val app_temp: Double? = null,
    val clouds: Int? = null,
    val clouds_hi: Int? = null,
    val clouds_low: Int? = null,
    val clouds_mid: Int? = null,
    val datetime: String? = null,
    val dewpt: Double? = null,
    val dhi: Int? = null,
    val dni: Int? = null,
    val ghi: Int? = null,
    val ozone: Int? = null,
    val pod: String? = null,
    val pop: Int? = null,
    val precip: Int? = null,
    val pres: Int? = null,
    val rh: Int? = null,
    val slp: Int? = null,
    val snow: Int? = null,
    val snow_depth: Int? = null,
    val solar_rad: Int? = null,
    val temp: Double? = null,
    val timestamp_local: String? = null,
    val timestamp_utc: String? = null,
    val ts: Long? = null,
    val uv: Int? = null,
    val vis: Double? = null,
    val weather: WeatherDescription? = null,
    val wind_cdir: String? = null,
    val wind_cdir_full: String? = null,
    val wind_dir: Int? = null,
    val wind_gust_spd: Double? = null,
    val wind_spd: Double? = null
)