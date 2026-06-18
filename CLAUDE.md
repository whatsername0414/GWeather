# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Build debug APK
.\gradlew assembleDebug

# Run all unit tests
.\gradlew testDebugUnitTest

# Run a single test class
.\gradlew testDebugUnitTest --tests "com.gweather.presentation.auth.LoginViewModelTest"

# Run a single test method
.\gradlew testDebugUnitTest --tests "com.gweather.presentation.auth.LoginViewModelTest.login_withBlankEmail_setsErrorState"

# Lint
.\gradlew lintDebug
```

Test reports are written to `app/build/reports/tests/testDebugUnitTest/index.html`.

## API Key

The app requires an OpenWeatherMap API key. Add it to `local.properties` (never commit this file):

```
WEATHER_API_KEY=your_key_here
```

It is injected at compile time via `BuildConfig.WEATHER_API_KEY` and used directly in `WeatherRepositoryImpl`.

## Architecture

Clean architecture with three layers:

- **`data/`** — Retrofit API, Room DAO/entities, repository implementations
- **`domain/`** — interfaces (`WeatherRepository`, `AuthRepository`, `LocationProvider`), domain models, `AppError`/`AppException`
- **`presentation/`** — ViewModels + Compose screens, organized by feature (`auth/`, `home/`, `weatherlist/`)

The `di/` package contains Hilt modules: `NetworkModule`, `DatabaseModule`, `RepositoryModule`, `LocationModule`.

## Navigation

`NavGraph.kt` owns the full nav graph. Three routes: `login → register` (or back), and `login/register → main`. The `main` route is a bottom-nav shell (`AnimatedContent`) that swaps between `HomeScreen` and `WeatherListScreen` without a NavController — just an `Int` tab index.

## Data Flow

**Auth:** `LoginViewModel` / `RegisterViewModel` → `AuthRepository` → `UserDao` (Room). Passwords are never stored in plain text — `PasswordHasher` salts and SHA-256 hashes before insert, and verifies on login.

**Weather:** `HomeViewModel` → `LocationProvider` (Fused Location) → `WeatherRepository` → `WeatherApi` (OpenWeatherMap One Call 4.0). City/country are resolved via `Geocoder` and cached in-memory on `WeatherRepositoryImpl` for the session.

**Forecast list:** `WeatherListViewModel` uses Paging 3. `WeatherPagingSource` uses a `String` key (the `next` URL from the API response) instead of a page number — `null` key = first page, non-null = subsequent pages.

## Error Handling

`AppError` (enum) + `AppException` (wrapper) carry typed errors across layers. `AppErrorMapper.kt` maps them to string resource IDs for the UI. All ViewModels catch both `AppException` and generic `Exception` separately.

## Testing Conventions

Unit tests live in `app/src/test/`. `MainDispatcherRule` (in `util/`) replaces `Dispatchers.Main` with `UnconfinedTestDispatcher` so `viewModelScope.launch` runs eagerly in tests. Use `mockk` for mocking and `runTest` for coroutine tests.
