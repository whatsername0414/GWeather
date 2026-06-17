package com.gweather.domain

enum class AppError {
    EMAIL_ALREADY_EXISTS,
    INVALID_CREDENTIALS,
    LOCATION_UNAVAILABLE,
    UNKNOWN
}

class AppException(val error: AppError) : Exception()
