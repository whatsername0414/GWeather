package com.gweather.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary      = SkyBlueDark,
    secondary    = SkyBlueDarkSecondary,
    background   = BgBase,
    surface      = BgBase,
    onBackground = Color.White,
    onSurface    = Color.White,
    onPrimary    = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary   = SkyBlue,
    secondary = SkyBlueSecondary,
)

@Composable
fun GWeatherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        shapes      = Shapes,
        content     = content
    )
}
