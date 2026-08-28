package wangdaye.com.geometricweather.theme

import wangdaye.com.geometricweather.theme.weatherView.WeatherThemeDelegate
import wangdaye.com.geometricweather.theme.weatherView.materialWeatherView.MaterialWeatherThemeDelegate

private val weatherThemeDelegateInstance: WeatherThemeDelegate by lazy {
    MaterialWeatherThemeDelegate()
}

val ThemeManager.weatherThemeDelegate: WeatherThemeDelegate
    get() = weatherThemeDelegateInstance
