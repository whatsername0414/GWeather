package com.gweather.util

import com.gweather.R
import java.util.Calendar

object WeatherIconMapper {

    fun getIcon(conditionId: Int, checkMoonRule: Boolean = false): Int {
        return when (conditionId) {
            in 200..232 -> R.drawable.ic_weather_thunderstorm
            in 300..321 -> R.drawable.ic_weather_drizzle
            in 500..531 -> R.drawable.ic_weather_rain
            in 600..622 -> R.drawable.ic_weather_snow
            in 700..781 -> R.drawable.ic_weather_fog
            800 -> if (checkMoonRule && isAfter6PM()) R.drawable.ic_weather_moon else R.drawable.ic_weather_sun
            in 801..804 -> R.drawable.ic_weather_cloud
            else -> R.drawable.ic_weather_sun
        }
    }

    fun isAfter6PM(): Boolean {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 18
    }

    fun isTodayAndAfter6PM(dtSeconds: Long): Boolean {
        val now = Calendar.getInstance()
        val item = Calendar.getInstance().apply { timeInMillis = dtSeconds * 1000L }
        val isToday = now.get(Calendar.YEAR) == item.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == item.get(Calendar.DAY_OF_YEAR)
        return isToday && isAfter6PM()
    }
}
