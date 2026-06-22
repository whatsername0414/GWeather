package com.gweather.data.remote.dto

import com.gweather.domain.WeatherIconMapper
import com.gweather.domain.model.DailyWeather

fun DailyDataDto.toDomain(city: String, country: String): DailyWeather {
    val conditionId = weather.firstOrNull()?.id ?: 800
    return DailyWeather(
        dt = dt,
        cityName = city,
        countryCode = country,
        temp = temp,
        weatherConditionId = conditionId,
        weatherDescription = weather.firstOrNull()?.description
            ?.replaceFirstChar { it.uppercase() } ?: "Clear",
        humidity = humidity,
        iconRes = WeatherIconMapper.getIcon(
            conditionId = conditionId,
            checkMoonRule = WeatherIconMapper.isTodayAndAfter6PM(dt)
        )
    )
}
