package com.gweather.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gweather.R
import com.gweather.domain.AppException
import com.gweather.domain.repository.AuthRepository
import com.gweather.presentation.toMessageRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error(R.string.error_all_fields_required)
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error(R.string.error_password_too_short)
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            _uiState.value = try {
                authRepository.register(name.trim(), email.trim(), password)
                AuthUiState.Success
            } catch (e: AppException) {
                AuthUiState.Error(e.error.toMessageRes())
            } catch (e: Exception) {
                AuthUiState.Error(R.string.error_registration_failed)
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
