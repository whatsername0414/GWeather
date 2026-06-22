package com.gweather.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gweather.R
import com.gweather.domain.AppError
import com.gweather.domain.AppException
import com.gweather.domain.WeatherIconMapper
import com.gweather.domain.model.CurrentWeather
import com.gweather.domain.repository.LocationRepository
import com.gweather.domain.repository.WeatherRepository
import com.gweather.presentation.UiState
import com.gweather.presentation.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeData(val weather: CurrentWeather, val iconRes: Int)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val uiMutableStateFlow = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeData>> = uiMutableStateFlow.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun loadWeather() {
        if (uiMutableStateFlow.value is UiState.Success) return
        viewModelScope.launch {
            uiMutableStateFlow.value = UiState.Loading
            uiMutableStateFlow.value = fetchWeather()
        }
    }

    fun onPermissionDenied() {
        uiMutableStateFlow.value = UiState.Error(AppError.LOCATION_PERMISSION_DENIED.toMessageRes())
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            uiMutableStateFlow.value = fetchWeather()
            _isRefreshing.value = false
        }
    }

    private suspend fun fetchWeather(): UiState<HomeData> {
        return try {
            val location = locationRepository.getLocation()
                ?: throw AppException(AppError.LOCATION_UNAVAILABLE)
            val weather = weatherRepository.getCurrentWeather(location.latitude, location.longitude)
            UiState.Success(
                HomeData(
                    weather = weather,
                    iconRes = WeatherIconMapper.getIcon(weather.weatherConditionId, checkMoonRule = true)
                )
            )
        } catch (e: AppException) {
            UiState.Error(e.error.toMessageRes())
        } catch (e: Exception) {
            UiState.Error(R.string.error_unknown)
        }
    }
}
