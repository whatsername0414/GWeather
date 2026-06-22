package com.gweather.presentation

import androidx.annotation.StringRes
import com.gweather.R
import com.gweather.domain.AppError

@StringRes
fun AppError.toMessageRes(): Int = when (this) {
    AppError.EMAIL_ALREADY_EXISTS -> R.string.error_email_already_exists
    AppError.INVALID_CREDENTIALS -> R.string.error_invalid_credentials
    AppError.LOCATION_UNAVAILABLE -> R.string.error_location_unavailable
    AppError.LOCATION_PERMISSION_DENIED -> R.string.error_location_permission_denied
    AppError.UNKNOWN -> R.string.error_unknown
}
