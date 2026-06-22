package com.gweather.data.location

import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class GeocoderDataSource @Inject constructor(
    private val geocoder: Geocoder
) {
    private var cached: Pair<String, String>? = null

    suspend fun resolveAddress(lat: Double, lon: Double): Pair<String, String> {
        cached?.let { return it }
        return withContext(Dispatchers.IO) {
            try {
                val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    resolveAsync(lat, lon)
                } else {
                    resolveSync(lat, lon)
                }
                cached = result
                result
            } catch (_: Exception) {
                Pair("Unknown", "??")
            }
        }
    }

    fun getLastKnown(): Pair<String, String> = cached ?: Pair("Unknown", "??")

    @Suppress("DEPRECATION")
    private fun resolveSync(lat: Double, lon: Double): Pair<String, String> {
        val addr = geocoder.getFromLocation(lat, lon, 1)?.firstOrNull()
        return Pair(addr?.locality ?: addr?.subAdminArea ?: "Unknown", addr?.countryCode ?: "??")
    }

    private suspend fun resolveAsync(lat: Double, lon: Double): Pair<String, String> =
        suspendCancellableCoroutine { cont ->
            geocoder.getFromLocation(lat, lon, 1) { addresses ->
                val addr = addresses.firstOrNull()
                val result = Pair(
                    addr?.locality ?: addr?.subAdminArea ?: "Unknown",
                    addr?.countryCode ?: "??"
                )
                if (cont.isActive) cont.resume(result)
            }
        }
}
