package com.gweather.domain

enum class AppError {
    EMAIL_ALREADY_EXISTS,
    INVALID_CREDENTIALS,
    LOCATION_UNAVAILABLE,
    LOCATION_PERMISSION_DENIED,
    UNKNOWN
}

class AppException(val error: AppError) : Exception()
