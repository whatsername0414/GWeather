package com.gweather.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

fun Double.formatTemp(): String = "${this.roundToInt()}°C"

fun Long.toTimeString(): String {
    val format = SimpleDateFormat("h:mm a", Locale.getDefault())
    return format.format(Date(this * 1000L))
}

fun Long.toDayLabel(): String {
    val itemCal = Calendar.getInstance().apply { timeInMillis = this@toDayLabel * 1000L }
    val today = Calendar.getInstance()
    val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
    return when {
        isSameDay(itemCal, today) -> "Today"
        isSameDay(itemCal, tomorrow) -> "Tomorrow"
        else -> SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date(this * 1000L))
    }
}

private fun isSameDay(a: Calendar, b: Calendar): Boolean {
    return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
            a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
}
