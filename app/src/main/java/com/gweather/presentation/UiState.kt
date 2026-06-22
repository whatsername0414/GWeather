package com.gweather.presentation

import androidx.annotation.StringRes

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(@StringRes val messageRes: Int) : UiState<Nothing>
}
