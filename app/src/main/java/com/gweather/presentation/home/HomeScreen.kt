package com.gweather.presentation.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.gweather.presentation.UiState
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gweather.R
import com.gweather.domain.model.CurrentWeather
import com.gweather.presentation.components.GlassCard
import com.gweather.ui.theme.BgBase
import com.gweather.ui.theme.BgGlowMid
import com.gweather.ui.theme.BgGlowTop
import com.gweather.ui.theme.GWeatherTheme
import com.gweather.ui.theme.OrangeAccent
import com.gweather.ui.theme.OrangeAccentLight
import com.gweather.ui.theme.SkyBlueDarkSecondary
import com.gweather.ui.theme.TemperatureTextStyle
import com.gweather.ui.theme.TemperatureUnitTextStyle
import com.gweather.ui.theme.White03
import com.gweather.ui.theme.White06
import com.gweather.ui.theme.White07
import com.gweather.ui.theme.White30
import com.gweather.ui.theme.White45
import com.gweather.ui.theme.White50
import com.gweather.ui.theme.White60
import com.gweather.ui.theme.White85
import com.gweather.ui.theme.White90
import com.gweather.util.toTimeString
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) viewModel.loadWeather()
        else viewModel.onPermissionDenied()
    }

    LaunchedEffect(Unit) {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {
            viewModel.loadWeather()
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    HomeScreenContent(
        uiState = uiState,
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        onRetry = { viewModel.refresh() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: UiState<HomeData>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(containerColor = Color.Transparent) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val w = constraints.maxWidth.toFloat()

            Box(modifier = Modifier.fillMaxSize().background(BgBase))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.65f)
                    .background(
                        Brush.radialGradient(
                            colorStops = arrayOf(
                                0f to BgGlowTop,
                                0.5f to BgGlowMid.copy(alpha = 0.85f),
                                1f to Color.Transparent
                            ),
                            center = Offset(w * 0.5f, 0f),
                            radius = w
                        )
                    )
            )

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                when (uiState) {
                    is UiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    is UiState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = White50
                                )
                                Text(
                                    text = stringResource(uiState.messageRes),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = White60
                                )
                                Button(
                                    onClick = onRetry,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text(stringResource(R.string.btn_try_again))
                                }
                            }
                        }
                    }
                    is UiState.Success -> WeatherContent(uiState.data.weather, uiState.data.iconRes)
                }
            }
        }
    }
}

@Composable
private fun WeatherContent(weather: CurrentWeather, iconRes: Int) {
    val iconComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(iconRes))

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .defaultMinSize(minHeight = maxHeight),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LocationBar(cityName = weather.cityName, countryCode = weather.countryCode)

        Spacer(Modifier.height(16.dp))

        LottieAnimation(
            composition = iconComposition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(144.dp)
        )

        Spacer(Modifier.height(8.dp))

        TemperatureDisplay(weather.temperature)

        Spacer(Modifier.height(6.dp))

        Text(
            text = weather.weatherDescription.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = White45
        )

        Spacer(Modifier.height(28.dp))

        WeatherStatsRow(weather)

        Spacer(Modifier.height(16.dp))

        SunStrip(
            sunrise = weather.sunrise,
            sunset = weather.sunset
        )

        Spacer(Modifier.height(80.dp))
    }
    } // BoxWithConstraints
}

@Composable
private fun LocationBar(cityName: String, countryCode: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .rotate(-45f)
                .clip(
                    RoundedCornerShape(
                        topStart = 6.dp,
                        topEnd = 6.dp,
                        bottomEnd = 6.dp,
                        bottomStart = 0.dp
                    )
                )
                .background(
                    Brush.linearGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, SkyBlueDarkSecondary)
                    )
                )
        )
        Text(
            text = "$cityName, $countryCode",
            style = MaterialTheme.typography.labelLarge,
            color = White85
        )
    }
}

@Composable
private fun TemperatureDisplay(temperature: Double) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = temperature.roundToInt().toString(),
            style = TemperatureTextStyle,
            color = Color.White,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip
        )
        Text(
            text = "°C",
            style = TemperatureUnitTextStyle,
            color = White50,
            modifier = Modifier.padding(top = 8.dp),
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun WeatherStatsRow(weather: CurrentWeather) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeatherStat(
                icon = "💧",
                value = "${weather.humidity}%",
                label = stringResource(R.string.label_humidity),
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight(0.6f)
                    .background(White07)
            )
            WeatherStat(
                icon = "💨",
                value = "${weather.windSpeed.roundToInt()} km/h",
                label = stringResource(R.string.label_wind),
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight(0.6f)
                    .background(White07)
            )
            WeatherStat(
                icon = "👁️",
                value = "${weather.visibility / 1000} km",
                label = stringResource(R.string.label_visibility),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun WeatherStat(
    icon: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(text = icon, fontSize = 15.sp)
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = White90
        )
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = White30
        )
    }
}

@Composable
private fun SunStrip(sunrise: Long, sunset: Long) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = White03,
        borderColor = White06
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SunItem(emoji = "🌅", time = sunrise.toTimeString(), label = stringResource(R.string.label_sunrise))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(2.dp)
                    .offset(y = (-6).dp)
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                OrangeAccent.copy(alpha = 0.4f),
                                OrangeAccentLight.copy(alpha = 0.7f),
                                OrangeAccent.copy(alpha = 0.4f)
                            )
                        )
                    )
            )
            SunItem(emoji = "🌇", time = sunset.toTimeString(), label = stringResource(R.string.label_sunset))
        }
    }
}

@Composable
private fun SunItem(emoji: String, time: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(text = emoji, fontSize = 18.sp)
        Text(
            text = time,
            style = MaterialTheme.typography.labelMedium,
            color = White85
        )
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = White30
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenSuccessPreview() {
    GWeatherTheme {
        HomeScreenContent(
            uiState = UiState.Success(
                HomeData(
                    weather = CurrentWeather(
                        cityName = "London",
                        countryCode = "GB",
                        temperature = 18.5,
                        sunrise = 1718937600L,
                        sunset = 1718992800L,
                        weatherConditionId = 800,
                        weatherDescription = "Clear sky",
                        humidity = 65,
                        windSpeed = 12.0,
                        visibility = 10000
                    ),
                    iconRes = R.raw.ic_weather_sun
                )
            ),
            isRefreshing = false,
            onRefresh = {},
            onRetry = {}
        )
    }
}
