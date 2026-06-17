package com.gweather.presentation.weatherlist

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gweather.domain.model.DailyWeather
import com.gweather.domain.repository.WeatherRepository

class WeatherPagingSource(
    private val weatherRepository: WeatherRepository,
    private val lat: Double,
    private val lon: Double
) : PagingSource<String, DailyWeather>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, DailyWeather> {
        return try {
            val page = if (params.key == null) {
                weatherRepository.getDailyWeatherPage(lat, lon)
            } else {
                weatherRepository.getDailyWeatherPageNext(params.key!!)
            }
            LoadResult.Page(
                data = page.items,
                prevKey = null,
                nextKey = page.nextUrl
            )
        } catch (e: Exception) {
            Log.e("WeatherPaging", "load() error: ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, DailyWeather>): String? = null
}
