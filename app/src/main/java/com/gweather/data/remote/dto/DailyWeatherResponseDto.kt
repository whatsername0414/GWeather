package com.gweather.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DailyWeatherResponseDto(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("data") val data: List<DailyDataDto>,
    @SerializedName("next") val next: String?
)

data class DailyDataDto(
    @SerializedName("dt") val dt: Long,
    @SerializedName("temp") val temp: Double,
    @SerializedName("humidity") val humidity: Int = 0,
    @SerializedName("weather") val weather: List<WeatherConditionDto> = emptyList()
)
