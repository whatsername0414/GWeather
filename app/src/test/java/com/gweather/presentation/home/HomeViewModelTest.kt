package com.gweather.presentation.home

import android.location.Location
import com.gweather.R
import com.gweather.domain.AppError
import com.gweather.domain.AppException
import com.gweather.domain.WeatherIconMapper
import com.gweather.domain.repository.LocationRepository
import com.gweather.presentation.UiState
import com.gweather.domain.model.CurrentWeather
import com.gweather.domain.repository.WeatherRepository
import com.gweather.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var locationRepository: LocationRepository
    private lateinit var viewModel: HomeViewModel

    private val fakeWeather = CurrentWeather(
        cityName = "London",
        countryCode = "GB",
        temperature = 15.0,
        sunrise = 1700000000L,
        sunset = 1700040000L,
        weatherConditionId = 800,
        weatherDescription = "Clear",
        humidity = 60,
        windSpeed = 10.0,
        visibility = 10000
    )

    @Before
    fun setup() {
        weatherRepository = mockk()
        locationRepository = mockk()
        viewModel = HomeViewModel(weatherRepository, locationRepository)
    }

    private fun mockLocation(lat: Double = 51.5, lon: Double = -0.12): Location {
        return mockk<Location>().also {
            every { it.latitude } returns lat
            every { it.longitude } returns lon
        }
    }

    @Test
    fun initialState_isLoading() {
        assertTrue(viewModel.uiState.value is UiState.Loading)
    }

    @Test
    fun loadWeather_onSuccess_setsSuccessState() = runTest {
        coEvery { locationRepository.getLocation() } returns mockLocation()
        coEvery { weatherRepository.getCurrentWeather(51.5, -0.12) } returns fakeWeather

        viewModel.loadWeather()

        assertEquals(
            UiState.Success(HomeData(fakeWeather, WeatherIconMapper.getIcon(fakeWeather.weatherConditionId, checkMoonRule = true))),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadWeather_whenLocationNull_setsErrorState() = runTest {
        coEvery { locationRepository.getLocation() } returns null

        viewModel.loadWeather()

        assertTrue(viewModel.uiState.value is UiState.Error)
    }

    @Test
    fun loadWeather_whenLocationUnavailable_setsLocationUnavailableError() = runTest {
        coEvery { locationRepository.getLocation() } throws AppException(AppError.LOCATION_UNAVAILABLE)

        viewModel.loadWeather()

        assertTrue(viewModel.uiState.value is UiState.Error)
    }

    @Test
    fun loadWeather_whenApiThrows_setsUnknownError() = runTest {
        coEvery { locationRepository.getLocation() } returns mockLocation()
        coEvery { weatherRepository.getCurrentWeather(any(), any()) } throws RuntimeException("API error")

        viewModel.loadWeather()

        assertEquals(UiState.Error(R.string.error_unknown), viewModel.uiState.value)
    }

    @Test
    fun loadWeather_whenAlreadySuccess_doesNotReload() = runTest {
        coEvery { locationRepository.getLocation() } returns mockLocation()
        coEvery { weatherRepository.getCurrentWeather(51.5, -0.12) } returns fakeWeather

        viewModel.loadWeather()
        assertEquals(
            UiState.Success(HomeData(fakeWeather, WeatherIconMapper.getIcon(fakeWeather.weatherConditionId, checkMoonRule = true))),
            viewModel.uiState.value
        )

        coEvery { weatherRepository.getCurrentWeather(any(), any()) } throws RuntimeException("should not be called")
        viewModel.loadWeather()

        assertEquals(
            UiState.Success(HomeData(fakeWeather, WeatherIconMapper.getIcon(fakeWeather.weatherConditionId, checkMoonRule = true))),
            viewModel.uiState.value
        )
    }

    @Test
    fun refresh_onSuccess_setsSuccessAndClearsRefreshingFlag() = runTest {
        coEvery { locationRepository.getLocation() } returns mockLocation()
        coEvery { weatherRepository.getCurrentWeather(51.5, -0.12) } returns fakeWeather

        viewModel.refresh()

        assertEquals(
            UiState.Success(HomeData(fakeWeather, WeatherIconMapper.getIcon(fakeWeather.weatherConditionId, checkMoonRule = true))),
            viewModel.uiState.value
        )
        assertFalse(viewModel.isRefreshing.value)
    }

    @Test
    fun refresh_onFailure_clearsRefreshingFlag() = runTest {
        coEvery { locationRepository.getLocation() } returns null

        viewModel.refresh()

        assertFalse(viewModel.isRefreshing.value)
        assertTrue(viewModel.uiState.value is UiState.Error)
    }
}
