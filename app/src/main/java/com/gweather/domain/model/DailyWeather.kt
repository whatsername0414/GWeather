package com.gweather.domain.model

data class DailyWeather(
    val dt: Long,
    val cityName: String,
    val countryCode: String,
    val temp: Double,
    val weatherConditionId: Int,
    val weatherDescription: String,
    val humidity: Int,
    val iconRes: Int
)

data class DailyWeatherPage(
    val items: List<DailyWeather>,
    val nextUrl: String?
)
