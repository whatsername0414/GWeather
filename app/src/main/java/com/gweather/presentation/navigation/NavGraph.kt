package com.gweather.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gweather.R
import com.gweather.presentation.auth.LoginScreen
import com.gweather.presentation.auth.RegisterScreen
import com.gweather.presentation.home.HomeScreen
import com.gweather.presentation.weatherlist.WeatherListScreen
import com.gweather.ui.theme.BgBase
import com.gweather.ui.theme.NavBackground
import com.gweather.ui.theme.SkyBlue
import com.gweather.ui.theme.White06
import com.gweather.ui.theme.White70

private const val ROUTE_LOGIN = "login"
private const val ROUTE_REGISTER = "register"
private const val ROUTE_MAIN = "main"
private const val ROUTE_HOME = "home"
private const val ROUTE_FORECAST = "forecast"

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ROUTE_LOGIN) {
        composable(ROUTE_LOGIN) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(ROUTE_REGISTER) },
                onLoginSuccess = {
                    navController.navigate(ROUTE_MAIN) {
                        popUpTo(ROUTE_LOGIN) { inclusive = true }
                    }
                },
                viewModel = hiltViewModel(it)
            )
        }

        composable(ROUTE_REGISTER) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(ROUTE_MAIN) {
                        popUpTo(ROUTE_LOGIN) { inclusive = true }
                    }
                },
                viewModel = hiltViewModel(it)
            )
        }

        composable(ROUTE_MAIN) {
            val tabNavController = rememberNavController()
            val backStackEntry by tabNavController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route

            Scaffold(
                containerColor = BgBase,
                bottomBar = {
                    GWeatherBottomNav(
                        currentRoute = currentRoute,
                        onTabSelected = { route ->
                            tabNavController.navigate(route) {
                                popUpTo(tabNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            ) { innerPadding ->
                NavHost(
                    navController = tabNavController,
                    startDestination = ROUTE_HOME,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                ) {
                    composable(ROUTE_HOME) {
                        HomeScreen(
                            viewModel = hiltViewModel(),
                        )
                    }
                    composable(ROUTE_FORECAST) {
                        WeatherListScreen(
                            viewModel = hiltViewModel(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GWeatherBottomNav(
    currentRoute: String?,
    onTabSelected: (String) -> Unit
) {
    val tabs = listOf(
        Triple("🏠", stringResource(R.string.tab_home), ROUTE_HOME),
        Triple("📅", stringResource(R.string.tab_forecast), ROUTE_FORECAST)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(White06)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { (icon, label, route) ->
                    NavItem(
                        icon = icon,
                        label = label,
                        isActive = currentRoute == route,
                        onClick = { onTabSelected(route) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavItem(
    icon: String,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .alpha(if (isActive) 1f else 0.35f)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = icon,
            fontSize = 18.sp
        )
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isActive) SkyBlue else White70,
            letterSpacing = 0.06.sp
        )
        if (isActive) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(SkyBlue)
            )
        }
    }
}
