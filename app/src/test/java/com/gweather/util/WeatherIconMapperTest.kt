package com.gweather.util

import com.gweather.R
import org.junit.Assert.*
import org.junit.Test

class WeatherIconMapperTest {

    @Test
    fun getIcon_thunderstormRange_returnsThunderstormIcon() {
        assertEquals(R.drawable.ic_weather_thunderstorm, WeatherIconMapper.getIcon(200))
        assertEquals(R.drawable.ic_weather_thunderstorm, WeatherIconMapper.getIcon(216))
        assertEquals(R.drawable.ic_weather_thunderstorm, WeatherIconMapper.getIcon(232))
    }

    @Test
    fun getIcon_drizzleRange_returnsDrizzleIcon() {
        assertEquals(R.drawable.ic_weather_drizzle, WeatherIconMapper.getIcon(300))
        assertEquals(R.drawable.ic_weather_drizzle, WeatherIconMapper.getIcon(321))
    }

    @Test
    fun getIcon_rainRange_returnsRainIcon() {
        assertEquals(R.drawable.ic_weather_rain, WeatherIconMapper.getIcon(500))
        assertEquals(R.drawable.ic_weather_rain, WeatherIconMapper.getIcon(531))
    }

    @Test
    fun getIcon_snowRange_returnsSnowIcon() {
        assertEquals(R.drawable.ic_weather_snow, WeatherIconMapper.getIcon(600))
        assertEquals(R.drawable.ic_weather_snow, WeatherIconMapper.getIcon(622))
    }

    @Test
    fun getIcon_fogRange_returnsFogIcon() {
        assertEquals(R.drawable.ic_weather_fog, WeatherIconMapper.getIcon(700))
        assertEquals(R.drawable.ic_weather_fog, WeatherIconMapper.getIcon(781))
    }

    @Test
    fun getIcon_clearSky_withoutMoonRule_returnsSunIcon() {
        assertEquals(R.drawable.ic_weather_sun, WeatherIconMapper.getIcon(800, checkMoonRule = false))
    }

    @Test
    fun getIcon_cloudyRange_returnsCloudIcon() {
        assertEquals(R.drawable.ic_weather_cloud, WeatherIconMapper.getIcon(801))
        assertEquals(R.drawable.ic_weather_cloud, WeatherIconMapper.getIcon(804))
    }

    @Test
    fun getIcon_unknown_returnsSunIcon() {
        assertEquals(R.drawable.ic_weather_sun, WeatherIconMapper.getIcon(999))
        assertEquals(R.drawable.ic_weather_sun, WeatherIconMapper.getIcon(0))
    }

    @Test
    fun isTodayAndAfter6PM_forDistantPast_returnsFalse() {
        assertFalse(WeatherIconMapper.isTodayAndAfter6PM(0L))
    }

    @Test
    fun isTodayAndAfter6PM_forFarFuture_returnsFalse() {
        val farFuture = System.currentTimeMillis() / 1000L + 60 * 60 * 24 * 365 * 10
        assertFalse(WeatherIconMapper.isTodayAndAfter6PM(farFuture))
    }
}
