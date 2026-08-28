package wangdaye.com.geometricweather.navigation

/**
 * Compose Navigation routes for in-app screens.
 *
 * Cross-feature entry still uses Activity intents so MainActivity contracts stay unchanged
 * ([wangdaye.com.geometricweather.main.MainActivity.ACTION_MANAGEMENT],
 * ACTION_SHOW_ALERTS, ACTION_SHOW_DAILY_FORECAST, search [KEY_LOCATION]).
 * Each Compose Activity hosts a [androidx.navigation.compose.NavHost] starting at one of
 * these destinations. Widget config and live wallpaper stay Activity + View.
 */
object InAppRoute {
    const val HOME = "inapp/home"
    const val SEARCH = "inapp/search"
    const val SETTINGS_ROOT = "wangdaye.com.geometricweather.settings.root"
    const val SETTINGS_APPEARANCE = "wangdaye.com.geometricweather.settings.appearance"
    const val SETTINGS_PROVIDERS = "wangdaye.com.geometricweather.settings.providers"
    const val SETTINGS_ADVANCED = "wangdaye.com.geometricweather.settings.advanced"
    const val SETTINGS_UNIT = "wangdaye.com.geometricweather.settings.unit"
    const val CARD_DISPLAY = "inapp/settings/card_display"
    const val DAILY_TREND_DISPLAY = "inapp/settings/daily_trend_display"
    const val HOURLY_TREND_DISPLAY = "inapp/settings/hourly_trend_display"
    const val PREVIEW_ICON = "inapp/settings/preview_icon"
    const val ABOUT = "inapp/about"
    const val DAILY_WEATHER = "inapp/daily"
    const val ALERT = "inapp/alert"
    const val ALLERGEN = "inapp/allergen"
    const val PROVIDER = SETTINGS_PROVIDERS
}
