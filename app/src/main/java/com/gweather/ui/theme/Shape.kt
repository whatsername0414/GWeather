package com.gweather.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),  // input fields
    small      = RoundedCornerShape(14.dp),  // list item cards, buttons
    medium     = RoundedCornerShape(16.dp),  // GlassCard default
    large      = RoundedCornerShape(18.dp),  // WeatherStatsRow card
    extraLarge = RoundedCornerShape(20.dp)   // auth cards
)
