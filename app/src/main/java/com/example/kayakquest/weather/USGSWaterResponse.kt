package com.example.kayakquest.weather

data class USGSWaterResponse(
    val value: USGSValueContainer? = null
)

data class USGSValueContainer(
    val timeSeries: List<TimeSeries>? = null
)

data class TimeSeries(
    val values: List<ValuesWrapper>? = null
)

data class ValuesWrapper(
    val value: List<WaterValue>? = null
)

data class WaterValue(
    val value: String? = null,
    val qualifiers: List<String>? = null,
    val dateTime: String? = null
)