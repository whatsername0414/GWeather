package com.gweather.presentation.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gweather.R
import com.gweather.domain.AppError
import com.gweather.domain.AppException
import com.gweather.domain.model.CurrentWeather
import com.gweather.domain.repository.WeatherRepository
import com.gweather.presentation.toMessageRes
import com.gweather.domain.LocationProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val weather: CurrentWeather) : HomeUiState
    data class Error(@StringRes val messageRes: Int) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val uiMutableStateFlow = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = uiMutableStateFlow.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun loadWeather() {
        if (uiMutableStateFlow.value is HomeUiState.Success) return
        viewModelScope.launch {
            uiMutableStateFlow.value = HomeUiState.Loading
            uiMutableStateFlow.value = fetchWeather()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            uiMutableStateFlow.value = fetchWeather()
            _isRefreshing.value = false
        }
    }

    private suspend fun fetchWeather(): HomeUiState {
        return try {
            val location = locationProvider.getLocation()
                ?: throw AppException(AppError.LOCATION_UNAVAILABLE)
            val weather = weatherRepository.getCurrentWeather(location.latitude, location.longitude)
            HomeUiState.Success(weather)
        } catch (e: AppException) {
            HomeUiState.Error(e.error.toMessageRes())
        } catch (e: Exception) {
            HomeUiState.Error(R.string.error_unknown)
        }
    }
}
