# GWeather

An Android weather app built with Jetpack Compose that shows current conditions and a daily forecast based on your device location.

## Tech Stack

- Kotlin + Jetpack Compose
- Hilt (dependency injection)
- Room (local database)
- Retrofit + OkHttp (networking)
- Paging 3 (paginated weather list)
- Fused Location Provider

## Prerequisites

- Android Studio Ladybug or newer
- Android SDK 26+

## API Key Setup

1. Copy the key attached in the email
2. In the project root, open `local.properties` (create it if it doesn't exist)
3. Add the following line:

```
WEATHER_API_KEY=your_api_key_here
```

> `local.properties` is excluded from version control — never commit your API key.

## Running the App

1. Clone the repository:
   ```bash
   git clone https://github.com/whatsername0414/GWeather.git
   ```

2. Open the project in Android Studio.

3. Add your API key to `local.properties` as described above.

4. Connect a device or start an emulator (API 26+).

5. Click **Run** or press `Shift + F10`.

The app requires location permission at runtime. Grant it when prompted to load weather data for your current location.
