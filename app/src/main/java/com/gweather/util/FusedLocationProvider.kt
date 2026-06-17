package com.gweather.util

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.gweather.domain.LocationProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class FusedLocationProvider @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationProvider {

    private var cachedLocation: Location? = null

    @SuppressLint("MissingPermission")
    override suspend fun getLocation(): Location? {
        cachedLocation?.let { return it }
        return try {
            val last = getLastLocation()
            if (last != null) {
                cachedLocation = last
                last
            } else {
                val current = requestCurrentLocation()
                cachedLocation = current
                current
            }
        } catch (e: Exception) {
            null
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastLocation(): Location? = suspendCancellableCoroutine { cont ->
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location -> if (cont.isActive) cont.resume(location) }
            .addOnFailureListener { e -> if (cont.isActive) cont.resumeWithException(e) }
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestCurrentLocation(): Location? = suspendCancellableCoroutine { cont ->
        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .build()
        fusedLocationClient.getCurrentLocation(request, null)
            .addOnSuccessListener { location -> if (cont.isActive) cont.resume(location) }
            .addOnFailureListener { e -> if (cont.isActive) cont.resumeWithException(e) }
    }
}
