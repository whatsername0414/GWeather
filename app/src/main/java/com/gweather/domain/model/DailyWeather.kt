package com.gweather.domain.model

data class DailyWeather(
    val dt: Long,
    val cityName: String,
    val countryCode: String,
    val tempMin: Double,
    val tempMax: Double,
    val sunrise: Long,
    val sunset: Long,
    val weatherConditionId: Int,
    val weatherDescription: String
)

data class DailyWeatherPage(
    val items: List<DailyWeather>,
    val nextUrl: String?
)
