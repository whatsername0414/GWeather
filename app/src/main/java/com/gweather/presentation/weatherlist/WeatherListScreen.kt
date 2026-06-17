package com.gweather.presentation.weatherlist

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.gweather.R
import com.gweather.domain.model.DailyWeather
import com.gweather.util.WeatherIconMapper
import com.gweather.util.toDayLabel
import com.gweather.util.toTimeString
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherListScreen(viewModel: WeatherListViewModel = hiltViewModel()) {
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

    // Only show the pull-to-refresh indicator when already have items (not on initial load)
    val isRefreshing = weatherItems.loadState.refresh is LoadState.Loading
            && weatherItems.itemCount > 0

    Scaffold { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { weatherItems.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (val refresh = weatherItems.loadState.refresh) {
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
                            Modifier.fillMaxWidth().padding(16.dp),
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

@Composable
private fun DailyWeatherCard(weather: DailyWeather) {
    val applyMoonRule = WeatherIconMapper.isTodayAndAfter6PM(weather.dt)
    val iconRes = WeatherIconMapper.getIcon(weather.weatherConditionId, checkMoonRule = applyMoonRule)
    val iconTint = dailyIconTint(weather.weatherConditionId, applyMoonRule)

    val glassColor = MaterialTheme.colorScheme.surfaceVariant
    val borderColor = MaterialTheme.colorScheme.onSurface

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
                        0.5f to borderColor.copy(alpha = 0.08f),
                        1.0f to borderColor.copy(alpha = 0.18f)
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = weather.weatherDescription,
                        modifier = Modifier.size(40.dp),
                        tint = iconTint
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${weather.tempMin.roundToInt()}° / ${weather.tempMax.roundToInt()}°C",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = weather.weatherDescription,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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

private fun dailyIconTint(conditionId: Int, showMoon: Boolean): Color {
    return when {
        conditionId in 200..232 -> Color(0xFF78909C)
        conditionId in 300..321 -> Color(0xFF90CAF9)
        conditionId in 500..531 -> Color(0xFF42A5F5)
        conditionId in 600..622 -> Color(0xFFB3E5FC)
        conditionId in 700..781 -> Color(0xFFCFD8DC)
        conditionId == 800 -> if (showMoon) Color(0xFFB0BEC5) else Color(0xFFFFC107)
        conditionId in 801..804 -> Color(0xFF90A4AE)
        else -> Color(0xFFFFC107)
    }
}
