package com.gweather.domain.model

data class CurrentWeather(
    val cityName: String,
    val countryCode: String,
    val temperature: Double,
    val sunrise: Long,
    val sunset: Long,
    val weatherConditionId: Int,
    val weatherDescription: String
)
