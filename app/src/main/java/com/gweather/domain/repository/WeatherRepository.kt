package com.gweather.domain.repository

import com.gweather.domain.model.CurrentWeather
import com.gweather.domain.model.DailyWeatherPage

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeather
    suspend fun getDailyWeatherPage(lat: Double, lon: Double): DailyWeatherPage
    suspend fun getDailyWeatherPageNext(nextUrl: String): DailyWeatherPage
}
