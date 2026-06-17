package com.gweather.domain

import android.location.Location

interface LocationProvider {
    suspend fun getLocation(): Location?
}
