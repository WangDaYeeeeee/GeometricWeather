package wangdaye.com.geometricweather.settings.compose

import wangdaye.com.geometricweather.navigation.InAppRoute

sealed class SettingsScreenRouter(val route: String) {
    object Root : SettingsScreenRouter(InAppRoute.SETTINGS_ROOT)
    object Appearance : SettingsScreenRouter(InAppRoute.SETTINGS_APPEARANCE)
    object ServiceProvider : SettingsScreenRouter(InAppRoute.SETTINGS_PROVIDERS)
    object ServiceProviderAdvanced : SettingsScreenRouter(InAppRoute.SETTINGS_ADVANCED)
    object Unit : SettingsScreenRouter(InAppRoute.SETTINGS_UNIT)
    object CardDisplay : SettingsScreenRouter(InAppRoute.CARD_DISPLAY)
    object DailyTrendDisplay : SettingsScreenRouter(InAppRoute.DAILY_TREND_DISPLAY)
    object HourlyTrendDisplay : SettingsScreenRouter(InAppRoute.HOURLY_TREND_DISPLAY)
    object PreviewIcon : SettingsScreenRouter(InAppRoute.PREVIEW_ICON)
}