package com.gweather.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponseDto(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("timezone_offset") val timezoneOffset: Int,
    @SerializedName("data") val data: List<CurrentDataDto>
)

data class CurrentDataDto(
    @SerializedName("dt") val dt: Long,
    @SerializedName("sunrise") val sunrise: Long,
    @SerializedName("sunset") val sunset: Long,
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("clouds") val clouds: Int,
    @SerializedName("wind_speed") val windSpeed: Double = 0.0,
    @SerializedName("visibility") val visibility: Int = 0,
    @SerializedName("weather") val weather: List<WeatherConditionDto>
)

data class WeatherConditionDto(
    @SerializedName("id") val id: Int,
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)
