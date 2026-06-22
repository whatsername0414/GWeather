package com.gweather.data.remote.api

import com.gweather.data.remote.dto.CurrentWeatherResponseDto
import com.gweather.data.remote.dto.DailyWeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface WeatherApi {

    @GET("data/4.0/onecall/current")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("lang") lang: String,
    ): CurrentWeatherResponseDto

    @GET("data/4.0/onecall/timeline/1h")
    suspend fun getDailyWeatherList(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
    ): DailyWeatherResponseDto

    @GET
    suspend fun getDailyWeatherListNext(
        @Url url: String,
        @Query("units") units: String
    ): DailyWeatherResponseDto
}
