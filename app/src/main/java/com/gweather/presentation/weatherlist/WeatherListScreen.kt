package com.gweather.presentation.weatherlist

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gweather.R
import com.gweather.domain.model.DailyWeather
import com.gweather.presentation.components.GlassCard
import com.gweather.ui.theme.BgBase
import com.gweather.ui.theme.BgGlowTop
import com.gweather.ui.theme.GWeatherTheme
import com.gweather.ui.theme.SkyBlue
import com.gweather.ui.theme.White04
import com.gweather.ui.theme.White07
import com.gweather.ui.theme.White30
import com.gweather.ui.theme.White35
import com.gweather.ui.theme.White40
import com.gweather.ui.theme.White85
import com.gweather.util.WeatherIconMapper
import com.gweather.util.isCurrentHour
import com.gweather.util.toShortDateString
import com.gweather.util.toTimeString
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherListScreen(viewModel: WeatherListViewModel) {
    val context = LocalContext.current
    val weatherItems: LazyPagingItems<DailyWeather> = viewModel.weatherPagingData.collectAsLazyPagingItems()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) viewModel.loadLocation()
    }

    LaunchedEffect(Unit) {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {
            viewModel.loadLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    WeatherListContent(weatherItems = weatherItems)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherListContent(weatherItems: LazyPagingItems<DailyWeather>) {
    val isRefreshing = weatherItems.loadState.refresh is LoadState.Loading
            && weatherItems.itemCount > 0

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().background(BgBase))

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val w = constraints.maxWidth.toFloat()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .background(
                        Brush.radialGradient(
                            colorStops = arrayOf(
                                0f to BgGlowTop,
                                0.55f to BgBase.copy(alpha = 0.85f),
                                1f to Color.Transparent
                            ),
                            center = Offset(w * 0.5f, 0f),
                            radius = w
                        )
                    )
            )
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { weatherItems.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    ForecastHeader()
                }

                when (val refresh = weatherItems.loadState.refresh) {
                    is LoadState.Loading -> {
                        if (weatherItems.itemCount == 0) {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator(color = SkyBlue) }
                            }
                        }
                    }
                    is LoadState.Error -> {
                        if (weatherItems.itemCount == 0) {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ForecastError(
                                        message = refresh.error.localizedMessage
                                            ?: stringResource(R.string.error_failed_to_load_forecast),
                                        onRetry = { weatherItems.retry() }
                                    )
                                }
                            }
                        }
                    }
                    else -> {}
                }

                items(
                    count = weatherItems.itemCount,
                    key = { index -> weatherItems.peek(index)?.dt ?: index }
                ) { index ->
                    weatherItems[index]?.let { ForecastRow(weather = it) }
                }

                item {
                    if (weatherItems.loadState.append is LoadState.Loading) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(modifier = Modifier.size(20.dp), color = SkyBlue) }
                    } else {
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ForecastHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .padding(bottom = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Hourly Forecast",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = White85
        )
    }
}

@Composable
private fun ForecastRow(weather: DailyWeather) {
    val isNow = weather.dt.isCurrentHour()
    val iconRes = WeatherIconMapper.getIcon(weather.weatherConditionId)
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(iconRes))

    val primary = MaterialTheme.colorScheme.primary
    val bgColor = if (isNow) primary.copy(alpha = 0.12f) else White04
    val borderColor = if (isNow) primary.copy(alpha = 0.25f) else White07

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        backgroundColor = bgColor,
        borderColor = borderColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.width(64.dp)) {
                Text(
                    text = weather.dt.toTimeString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = White85
                )
                Text(
                    text = weather.dt.toShortDateString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = White30
                )
            }

            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(56.dp)
            )

            Text(
                text = weather.weatherDescription,
                modifier = Modifier.weight(1f),
                fontSize = 11.sp,
                color = White40,
                lineHeight = 15.sp
            )

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${weather.temp.roundToInt()}°",
                    style = MaterialTheme.typography.labelMedium,
                    color = White85
                )
                Text(
                    text = "💧 ${weather.humidity}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = White35
                )
            }
        }
    }
}

@Composable
private fun ForecastError(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(24.dp)
    ) {
        Icon(
            Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = SkyBlue
        )
        Text(
            text = message,
            style = MaterialTheme.typography.labelLarge,
            color = White40
        )
        Button(onClick = onRetry) {
            Text(stringResource(R.string.btn_retry))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060D1C)
@Composable
private fun ForecastRowPreview() {
    GWeatherTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgBase)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ForecastHeader()
            ForecastRow(
                DailyWeather(
                    dt = System.currentTimeMillis() / 1000,
                    cityName = "London", countryCode = "GB",
                    temp = 18.0, weatherConditionId = 800,
                    weatherDescription = "Clear sky", humidity = 65
                )
            )
            ForecastRow(
                DailyWeather(
                    dt = 1718935200L,
                    cityName = "London", countryCode = "GB",
                    temp = 21.0, weatherConditionId = 801,
                    weatherDescription = "Few clouds", humidity = 58
                )
            )
            ForecastRow(
                DailyWeather(
                    dt = 1718942400L,
                    cityName = "London", countryCode = "GB",
                    temp = 16.0, weatherConditionId = 500,
                    weatherDescription = "Light rain", humidity = 80
                )
            )
        }
    }
}
