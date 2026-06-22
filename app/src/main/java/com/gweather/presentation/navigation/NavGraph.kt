package com.gweather.presentation.navigation

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
            var selectedTab by rememberSaveable { mutableIntStateOf(0) }

            Scaffold(
                containerColor = BgBase,
                bottomBar = {
                    GWeatherBottomNav(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
            ) { innerPadding ->
                AnimatedContent(
                    targetState = selectedTab,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    label = "tab_transition"
                ) { tab ->
                    when (tab) {
                        0 -> HomeScreen(viewModel = hiltViewModel(it))
                        else -> WeatherListScreen(viewModel = hiltViewModel(it))
                    }
                }
            }
        }
    }
}

@Composable
private fun GWeatherBottomNav(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("🏠" to "HOME", "📅" to "FORECAST")

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
                tabs.forEachIndexed { index, (icon, label) ->
                    val isActive = selectedTab == index
                    NavItem(
                        icon = icon,
                        label = label,
                        isActive = isActive,
                        onClick = { onTabSelected(index) }
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
