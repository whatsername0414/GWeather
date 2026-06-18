package com.gweather.presentation.auth

import com.gweather.R
import com.gweather.domain.AppError
import com.gweather.domain.AppException
import com.gweather.domain.repository.AuthRepository
import com.gweather.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        authRepository = mockk()
        viewModel = LoginViewModel(authRepository)
    }

    @Test
    fun initialState_isIdle() {
        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun login_withBlankEmail_setsErrorState() {
        viewModel.login("", "password")
        assertEquals(AuthUiState.Error(R.string.error_email_password_required), viewModel.uiState.value)
    }

    @Test
    fun login_withBlankPassword_setsErrorState() {
        viewModel.login("user@test.com", "")
        assertEquals(AuthUiState.Error(R.string.error_email_password_required), viewModel.uiState.value)
    }

    @Test
    fun login_withBlankBothFields_setsErrorState() {
        viewModel.login("", "")
        assertEquals(AuthUiState.Error(R.string.error_email_password_required), viewModel.uiState.value)
    }

    @Test
    fun login_withValidCredentials_setsSuccessState() = runTest {
        coEvery { authRepository.login("user@test.com", "password") } returns true

        viewModel.login("user@test.com", "password")

        assertEquals(AuthUiState.Success, viewModel.uiState.value)
    }

    @Test
    fun login_withInvalidCredentials_setsErrorState() = runTest {
        coEvery { authRepository.login("user@test.com", "wrong") } returns false

        viewModel.login("user@test.com", "wrong")

        assertEquals(AuthUiState.Error(R.string.error_invalid_credentials), viewModel.uiState.value)
    }

    @Test
    fun login_whenRepositoryThrowsAppException_setsErrorState() = runTest {
        coEvery { authRepository.login(any(), any()) } throws AppException(AppError.UNKNOWN)

        viewModel.login("user@test.com", "password")

        assertTrue(viewModel.uiState.value is AuthUiState.Error)
    }

    @Test
    fun login_whenRepositoryThrowsGenericException_setsLoginFailedError() = runTest {
        coEvery { authRepository.login(any(), any()) } throws RuntimeException("Network error")

        viewModel.login("user@test.com", "password")

        assertEquals(AuthUiState.Error(R.string.error_login_failed), viewModel.uiState.value)
    }

    @Test
    fun resetState_setsIdleState() = runTest {
        coEvery { authRepository.login(any(), any()) } returns true
        viewModel.login("user@test.com", "password")

        viewModel.resetState()

        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
    }
}
