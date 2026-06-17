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
    @SerializedName("sunrise") val sunrise: Long,
    @SerializedName("sunset") val sunset: Long,
    @SerializedName("temp") val temp: DailyTempDto
)

data class DailyTempDto(
    @SerializedName("day") val day: Double,
    @SerializedName("min") val min: Double,
    @SerializedName("max") val max: Double,
    @SerializedName("night") val night: Double,
    @SerializedName("eve") val eve: Double,
    @SerializedName("morn") val morn: Double
)
