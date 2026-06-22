package com.gweather.data.location

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.gweather.domain.repository.LocationRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class FusedLocationProvider @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationRepository {

    private var cachedLocation: Location? = null
    private val fetchMutex = Mutex()

    @SuppressLint("MissingPermission")
    override suspend fun getLocation(): Location? {
        cachedLocation?.let { return it }
        return fetchMutex.withLock {
            cachedLocation ?: run {
                try {
                    val last = getLastLocation()
                    (last ?: requestCurrentLocation()).also { cachedLocation = it }
                } catch (e: Exception) {
                    null
                }
            }
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
