package com.gweather.data.repository

import android.os.Build
import android.location.Geocoder
import android.util.Log
import com.gweather.BuildConfig
import com.gweather.data.remote.api.WeatherApi
import com.gweather.data.remote.dto.DailyDataDto
import com.gweather.domain.model.CurrentWeather
import com.gweather.domain.model.DailyWeather
import com.gweather.domain.model.DailyWeatherPage
import com.gweather.domain.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val geocoder: Geocoder
) : WeatherRepository {

    private var cachedCityCountry: Pair<String, String>? = null

    override suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeather {
        Log.d("WeatherRepositoryImpl", "getCurrentWeather: ${BuildConfig.WEATHER_API_KEY}")
        val response = weatherApi.getCurrentWeather(lat, lon, "metric", "en", BuildConfig.WEATHER_API_KEY)
        val data = response.data.first()
        val (city, country) = resolveAddress(lat, lon)
        return CurrentWeather(
            cityName = city,
            countryCode = country,
            temperature = data.temp,
            sunrise = data.sunrise,
            sunset = data.sunset,
            weatherConditionId = data.weather.firstOrNull()?.id ?: 800,
            weatherDescription = data.weather.firstOrNull()?.description
                ?.replaceFirstChar { it.uppercase() } ?: "Clear"
        )
    }

    override suspend fun getDailyWeatherPage(lat: Double, lon: Double): DailyWeatherPage {
        val response = weatherApi.getDailyWeatherList(lat, lon, "metric", BuildConfig.WEATHER_API_KEY)
        val (city, country) = resolveAddress(lat, lon)
        return DailyWeatherPage(
            items = response.data.map { it.toDomain(city, country) },
            nextUrl = response.next
        )
    }

    override suspend fun getDailyWeatherPageNext(nextUrl: String): DailyWeatherPage {
        val response = weatherApi.getDailyWeatherListNext(nextUrl, "metric")
        val (city, country) = cachedCityCountry ?: Pair("Unknown", "??")
        return DailyWeatherPage(
            items = response.data.map { it.toDomain(city, country) },
            nextUrl = response.next
        )
    }

    private suspend fun resolveAddress(lat: Double, lon: Double): Pair<String, String> {
        cachedCityCountry?.let { return it }
        return withContext(Dispatchers.IO) {
            try {
                val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    resolveAddressAsync(lat, lon)
                } else {
                    resolveAddressSync(lat, lon)
                }
                cachedCityCountry = result
                result
            } catch (e: Exception) {
                Pair("Unknown", "??")
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun resolveAddressSync(lat: Double, lon: Double): Pair<String, String> {
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        val addr = addresses?.firstOrNull()
        return Pair(addr?.locality ?: addr?.subAdminArea ?: "Unknown", addr?.countryCode ?: "??")
    }

    private suspend fun resolveAddressAsync(lat: Double, lon: Double): Pair<String, String> =
        suspendCancellableCoroutine { cont ->
            geocoder.getFromLocation(lat, lon, 1) { addresses ->
                val addr = addresses.firstOrNull()
                val result = Pair(
                    addr?.locality ?: addr?.subAdminArea ?: "Unknown",
                    addr?.countryCode ?: "??"
                )
                if (cont.isActive) cont.resume(result)
            }
        }

    private fun DailyDataDto.toDomain(city: String, country: String) = DailyWeather(
        dt = dt,
        cityName = city,
        countryCode = country,
        tempMin = temp.min,
        tempMax = temp.max,
        sunrise = sunrise,
        sunset = sunset,
        weatherConditionId = 800,
        weatherDescription = "Clear"
    )
}
