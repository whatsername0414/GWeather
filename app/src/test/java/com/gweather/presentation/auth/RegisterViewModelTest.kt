package com.gweather.presentation.auth

import com.gweather.R
import com.gweather.domain.AppError
import com.gweather.domain.AppException
import com.gweather.domain.repository.AuthRepository
import com.gweather.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        authRepository = mockk()
        viewModel = RegisterViewModel(authRepository)
    }

    @Test
    fun initialState_isIdle() {
        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun register_withBlankName_setsAllFieldsRequiredError() {
        viewModel.register("", "alice@test.com", "password123")
        assertEquals(AuthUiState.Error(R.string.error_all_fields_required), viewModel.uiState.value)
    }

    @Test
    fun register_withBlankEmail_setsAllFieldsRequiredError() {
        viewModel.register("Alice", "", "password123")
        assertEquals(AuthUiState.Error(R.string.error_all_fields_required), viewModel.uiState.value)
    }

    @Test
    fun register_withBlankPassword_setsAllFieldsRequiredError() {
        viewModel.register("Alice", "alice@test.com", "")
        assertEquals(AuthUiState.Error(R.string.error_all_fields_required), viewModel.uiState.value)
    }

    @Test
    fun register_withShortPassword_setsPasswordTooShortError() {
        viewModel.register("Alice", "alice@test.com", "abc")
        assertEquals(AuthUiState.Error(R.string.error_password_too_short), viewModel.uiState.value)
    }

    @Test
    fun register_withPasswordExactlyFiveChars_setsPasswordTooShortError() {
        viewModel.register("Alice", "alice@test.com", "12345")
        assertEquals(AuthUiState.Error(R.string.error_password_too_short), viewModel.uiState.value)
    }

    @Test
    fun register_withValidInput_setsSuccessState() = runTest {
        coJustRun { authRepository.register("Alice", "alice@test.com", "password123") }

        viewModel.register("Alice", "alice@test.com", "password123")

        assertEquals(AuthUiState.Success, viewModel.uiState.value)
    }

    @Test
    fun register_withPasswordExactlySixChars_setsSuccessState() = runTest {
        coJustRun { authRepository.register("Alice", "alice@test.com", "123456") }

        viewModel.register("Alice", "alice@test.com", "123456")

        assertEquals(AuthUiState.Success, viewModel.uiState.value)
    }

    @Test
    fun register_whenEmailAlreadyExists_setsErrorState() = runTest {
        coEvery { authRepository.register(any(), "alice@test.com", any()) } throws
            AppException(AppError.EMAIL_ALREADY_EXISTS)

        viewModel.register("Alice", "alice@test.com", "password123")

        assertTrue(viewModel.uiState.value is AuthUiState.Error)
    }

    @Test
    fun register_whenRepositoryThrowsGenericException_setsRegistrationFailedError() = runTest {
        coEvery { authRepository.register(any(), any(), any()) } throws RuntimeException("DB error")

        viewModel.register("Alice", "alice@test.com", "password123")

        assertEquals(AuthUiState.Error(R.string.error_registration_failed), viewModel.uiState.value)
    }

    @Test
    fun resetState_setsIdleState() {
        viewModel.register("", "", "")
        viewModel.resetState()
        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
    }
}
