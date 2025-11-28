package com.example.kayakquest.weather

data class USGSWaterResponse(
    val value: USGSValueContainer? = null
)

data class USGSValueContainer(
    val timeSeries: List<USGSTimeSeries>? = null
)

data class USGSTimeSeries(
    val values: List<USGSValuesWrapper>? = null
)

data class USGSValuesWrapper(
    val value: List<USGSWaterReading>? = null
)

data class USGSWaterReading(
    val value: String? = null,
    val dateTime: String? = null,
    val qualifiers: List<String>? = null
)