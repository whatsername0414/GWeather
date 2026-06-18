package com.gweather.util

import org.junit.Assert.*
import org.junit.Test

class ExtensionsTest {

    @Test
    fun formatTemp_roundsDownCorrectly() {
        assertEquals("20°C", 20.0.formatTemp())
        assertEquals("20°C", 20.4.formatTemp())
    }

    @Test
    fun formatTemp_roundsUpCorrectly() {
        assertEquals("21°C", 20.6.formatTemp())
    }

    @Test
    fun formatTemp_handlesNegativeTemperatures() {
        assertEquals("-5°C", (-4.6).formatTemp())
        assertEquals("-4°C", (-4.4).formatTemp())
    }

    @Test
    fun toTimeString_returnsNonBlankString() {
        val result = 1700000000L.toTimeString()
        assertTrue(result.isNotBlank())
    }

    @Test
    fun toTimeString_containsTimeSeparator() {
        val result = 1700000000L.toTimeString()
        assertTrue(result.contains(":"))
    }

    @Test
    fun toDayLabel_forDistantPast_returnsFormattedDate() {
        val label = 0L.toDayLabel()
        assertNotEquals("Today", label)
        assertNotEquals("Tomorrow", label)
        assertTrue(label.isNotBlank())
    }

    @Test
    fun toDayLabel_forToday_returnsToday() {
        val nowSeconds = System.currentTimeMillis() / 1000L
        assertEquals("Today", nowSeconds.toDayLabel())
    }
}
