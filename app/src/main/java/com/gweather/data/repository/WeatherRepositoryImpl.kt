package com.gweather.data.repository

import com.gweather.data.location.GeocoderDataSource
import com.gweather.data.remote.api.WeatherApi
import com.gweather.data.remote.dto.toDomain
import com.gweather.domain.model.CurrentWeather
import com.gweather.domain.model.DailyWeather
import com.gweather.domain.model.DailyWeatherPage
import com.gweather.domain.repository.WeatherRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val geocoderDataSource: GeocoderDataSource
) : WeatherRepository {

    override suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeather {
        val response = weatherApi.getCurrentWeather(lat, lon, "metric", "en")
        val data = response.data.first()
        val (city, country) = geocoderDataSource.resolveAddress(lat, lon)
        return CurrentWeather(
            cityName = city,
            countryCode = country,
            temperature = data.temp,
            sunrise = data.sunrise,
            sunset = data.sunset,
            weatherConditionId = data.weather.firstOrNull()?.id ?: 800,
            weatherDescription = data.weather.firstOrNull()?.description
                ?.replaceFirstChar { it.uppercase() } ?: "Clear",
            humidity = data.humidity,
            windSpeed = data.windSpeed,
            visibility = data.visibility
        )
    }

    override suspend fun getDailyWeatherPage(lat: Double, lon: Double): DailyWeatherPage {
        val response = weatherApi.getDailyWeatherList(lat, lon, "metric")
        val (city, country) = geocoderDataSource.resolveAddress(lat, lon)
        return DailyWeatherPage(
            items = response.data.map { it.toDomain(city, country) },
            nextUrl = response.next
        )
    }

    override suspend fun getDailyWeatherPageNext(nextUrl: String): DailyWeatherPage {
        val response = weatherApi.getDailyWeatherListNext(nextUrl, "metric")
        val (city, country) = geocoderDataSource.getLastKnown()
        return DailyWeatherPage(
            items = response.data.map { it.toDomain(city, country) },
            nextUrl = response.next
        )
    }


}
