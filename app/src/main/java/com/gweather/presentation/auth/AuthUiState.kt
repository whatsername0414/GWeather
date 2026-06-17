package com.gweather.presentation.auth

import androidx.annotation.StringRes

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data object Success : AuthUiState
    data class Error(@StringRes val messageRes: Int) : AuthUiState
}
