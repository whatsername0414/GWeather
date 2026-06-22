package com.gweather.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

fun Double.formatTemp(): String = "${this.roundToInt()}°C"

fun Long.toTimeString(): String =
    Instant.ofEpochSecond(this)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))

fun Long.toShortDateString(): String =
    Instant.ofEpochSecond(this)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("MMM d", Locale.getDefault()))

fun Long.isCurrentHour(): Boolean {
    val nowSec = System.currentTimeMillis() / 1000
    val hourStart = nowSec - (nowSec % 3600)
    return this >= hourStart && this < hourStart + 3600
}

fun Long.toDayLabel(): String {
    val zone = ZoneId.systemDefault()
    val itemDate = Instant.ofEpochSecond(this).atZone(zone).toLocalDate()
    val today = LocalDate.now(zone)
    return when (itemDate) {
        today -> "Today"
        today.plusDays(1) -> "Tomorrow"
        else -> Instant.ofEpochSecond(this)
            .atZone(zone)
            .format(DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault()))
    }
}
