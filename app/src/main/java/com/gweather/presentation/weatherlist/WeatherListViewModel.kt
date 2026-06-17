package com.gweather.presentation.weatherlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gweather.domain.model.DailyWeather
import com.gweather.domain.repository.WeatherRepository
import com.gweather.domain.LocationProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherListViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private data class LatLon(val lat: Double, val lon: Double)

    private val locationMutableState = MutableStateFlow<LatLon?>(null)

    val weatherPagingData: Flow<PagingData<DailyWeather>> = locationMutableState
        .filterNotNull()
        .flatMapLatest { (lat, lon) ->
            Pager(
                config = PagingConfig(pageSize = 10, enablePlaceholders = false),
                pagingSourceFactory = { WeatherPagingSource(weatherRepository, lat, lon) }
            ).flow
        }
        .cachedIn(viewModelScope)

    fun loadLocation() {
        if (locationMutableState.value != null) return
        viewModelScope.launch {
            val location = locationProvider.getLocation() ?: return@launch
            locationMutableState.value = LatLon(location.latitude, location.longitude)
        }
    }
}
