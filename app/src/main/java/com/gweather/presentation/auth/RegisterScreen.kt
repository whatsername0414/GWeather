package com.gweather.presentation.auth

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gweather.R
import com.gweather.presentation.components.GlassCard
import com.gweather.presentation.components.GlassField
import com.gweather.ui.theme.AuthDark
import com.gweather.ui.theme.AuthMid
import com.gweather.ui.theme.AuthPurple
import com.gweather.ui.theme.GWeatherTheme
import com.gweather.ui.theme.SkyBlue
import com.gweather.ui.theme.SkyBlueDarkSecondary
import com.gweather.ui.theme.SkyBlueSecondary
import com.gweather.ui.theme.White08
import com.gweather.ui.theme.White30
import com.gweather.ui.theme.White40

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AuthUiState.Success -> onRegisterSuccess()
            is AuthUiState.Error -> {
                snackbarHostState.showSnackbar(context.getString(state.messageRes))
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    RegisterScreenContent(
        name = name,
        onNameChange = { name = it },
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        isLoading = uiState is AuthUiState.Loading,
        onRegister = { viewModel.register(name, email, password) },
        onNavigateToLogin = onNavigateToLogin,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun RegisterScreenContent(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onRegister: () -> Unit,
    onNavigateToLogin: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val w = constraints.maxWidth.toFloat()
            val h = constraints.maxHeight.toFloat()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colorStops = arrayOf(
                                0f to AuthPurple,
                                0.6f to AuthMid,
                                1f to AuthDark
                            ),
                            center = Offset(w * 0.5f, h * 1.1f),
                            radius = maxOf(w, h) * 1.2f
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .offset(x = (-60).dp, y = (-60).dp)
                    .size(260.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                                Color.Transparent
                            )
                        )
                    )
                    .then(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                            Modifier.blur(30.dp) else Modifier
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 40.dp, y = (-80).dp)
                    .size(200.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                SkyBlueDarkSecondary.copy(alpha = 0.18f),
                                Color.Transparent
                            )
                        )
                    )
                    .then(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                            Modifier.blur(24.dp) else Modifier
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 28.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                RegisterLogoArea()

                Spacer(Modifier.height(36.dp))

                RegisterCard(
                    name = name,
                    onNameChange = onNameChange,
                    email = email,
                    onEmailChange = onEmailChange,
                    password = password,
                    onPasswordChange = onPasswordChange,
                    isLoading = isLoading,
                    onRegister = {
                        focusManager.clearFocus()
                        onRegister()
                    },
                    onNextFocus = { focusManager.moveFocus(FocusDirection.Down) }
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = White30)) {
                            append(stringResource(R.string.register_have_account))
                        }
                        withStyle(
                            SpanStyle(
                                color = SkyBlue,
                                fontWeight = FontWeight.Medium
                            )
                        ) {
                            append(stringResource(R.string.register_sign_in))
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onNavigateToLogin() }
                )
            }
        }
    }
}

@Composable
private fun RegisterLogoArea() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_logo),
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        Text(
            text = stringResource(R.string.register_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = White40
        )
    }
}

@Composable
private fun RegisterCard(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onRegister: () -> Unit,
    onNextFocus: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        borderColor = White08
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassField(
                label = stringResource(R.string.label_name).uppercase(),
                value = name,
                onValueChange = onNameChange,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { onNextFocus() })
            )

            GlassField(
                label = stringResource(R.string.label_email).uppercase(),
                value = email,
                onValueChange = onEmailChange,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { onNextFocus() })
            )

            GlassField(
                label = stringResource(R.string.label_password).uppercase(),
                value = password,
                onValueChange = onPasswordChange,
                isPassword = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onRegister() })
            )

            Spacer(Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, SkyBlueSecondary),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, 0f)
                        )
                    )
                    .clickable(enabled = !isLoading) { onRegister() },
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = stringResource(R.string.btn_register),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    GWeatherTheme {
        RegisterScreenContent(
            name = "Jane Doe",
            onNameChange = {},
            email = "jane@example.com",
            onEmailChange = {},
            password = "password",
            onPasswordChange = {},
            isLoading = false,
            onRegister = {},
            onNavigateToLogin = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenLoadingPreview() {
    GWeatherTheme {
        RegisterScreenContent(
            name = "Jane Doe",
            onNameChange = {},
            email = "jane@example.com",
            onEmailChange = {},
            password = "password",
            onPasswordChange = {},
            isLoading = true,
            onRegister = {},
            onNavigateToLogin = {}
        )
    }
}
