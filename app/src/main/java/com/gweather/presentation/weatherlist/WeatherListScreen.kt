package com.gweather.presentation.weatherlist

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.gweather.R
import com.gweather.domain.model.DailyWeather
import com.gweather.ui.theme.GWeatherTheme
import com.gweather.util.toDayLabel
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

    WeatherListScreenContent(weatherItems = weatherItems)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherListScreenContent(weatherItems: LazyPagingItems<DailyWeather>) {
    val isRefreshing = weatherItems.loadState.refresh is LoadState.Loading
            && weatherItems.itemCount > 0

    Scaffold { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { weatherItems.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (weatherItems.loadState.refresh) {
                    is LoadState.Loading -> {
                        if (weatherItems.itemCount == 0) {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator() }
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
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.padding(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Warning,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                        Text(
                                            text = stringResource(R.string.error_failed_to_load_forecast),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Button(onClick = { weatherItems.retry() }) {
                                            Text(stringResource(R.string.btn_retry))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else -> item { Spacer(Modifier.height(8.dp)) }
                }

                items(
                    count = weatherItems.itemCount,
                    key = { index -> weatherItems.peek(index)?.dt ?: index }
                ) { index ->
                    weatherItems[index]?.let { DailyWeatherCard(weather = it) }
                }

                item {
                    if (weatherItems.loadState.append is LoadState.Loading) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(modifier = Modifier.size(24.dp)) }
                    } else {
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherListScreenPreview() {
    val sampleItems = listOf(
        DailyWeather(
            dt = 1718928000L,
            cityName = "London",
            countryCode = "GB",
            tempMin = 12.0,
            tempMax = 18.0,
            sunrise = 1718937600L,
            sunset = 1718992800L,
            weatherConditionId = 800,
            weatherDescription = "Clear sky",
            humidity = 65
        ),
        DailyWeather(
            dt = 1719014400L,
            cityName = "London",
            countryCode = "GB",
            tempMin = 14.0,
            tempMax = 21.0,
            sunrise = 1719024000L,
            sunset = 1719079200L,
            weatherConditionId = 801,
            weatherDescription = "Few clouds",
            humidity = 58
        ),
        DailyWeather(
            dt = 1719100800L,
            cityName = "London",
            countryCode = "GB",
            tempMin = 10.0,
            tempMax = 16.0,
            sunrise = 1719110400L,
            sunset = 1719165600L,
            weatherConditionId = 500,
            weatherDescription = "Light rain",
            humidity = 80
        )
    )
    GWeatherTheme {
        Scaffold { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                        )
                    )
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                items(sampleItems, key = { it.dt }) { DailyWeatherCard(it) }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
internal fun DailyWeatherCard(weather: DailyWeather) {
    val glassColor = MaterialTheme.colorScheme.primary
    val borderColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colorStops = arrayOf(
                        0.0f to glassColor.copy(alpha = 0f),
                        0.4f to glassColor.copy(alpha = 0.35f),
                        1.0f to glassColor.copy(alpha = 0.75f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colorStops = arrayOf(
                        0.0f to borderColor.copy(alpha = 0f),
                        0.5f to borderColor.copy(alpha = 0.15f),
                        1.0f to borderColor.copy(alpha = 0.35f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = weather.dt.toDayLabel(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${weather.cityName}, ${weather.countryCode}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${weather.tempMin.roundToInt()}° / ${weather.tempMax.roundToInt()}°C",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Humidity: ${weather.humidity}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                LabeledValue(label = stringResource(R.string.label_sunrise), value = weather.sunrise.toTimeString())
                LabeledValue(label = stringResource(R.string.label_sunset), value = weather.sunset.toTimeString())
            }
        }
    }
}

@Composable
private fun LabeledValue(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
