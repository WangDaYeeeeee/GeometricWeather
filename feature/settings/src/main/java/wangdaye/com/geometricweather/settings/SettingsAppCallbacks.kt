package wangdaye.com.geometricweather.settings

import android.content.Context
import androidx.annotation.StringRes

data class WidgetConfigLink(
    @StringRes val titleRes: Int,
    val onClick: (Context) -> Unit,
)

/**
 * App-owned side effects (widgets, polling, flavor) so `:feature:settings` does not
 * depend on AppWidget presenters or `:app` types.
 */
interface SettingsAppCallbacks {
    fun resetNormalBackgroundTask(context: Context, forceRefresh: Boolean)
    fun resetTodayForecastBackgroundTask(
        context: Context,
        forceRefresh: Boolean,
        nextDay: Boolean,
    )
    fun resetTomorrowForecastBackgroundTask(
        context: Context,
        forceRefresh: Boolean,
        nextDay: Boolean,
    )
    fun cancelNotification(context: Context)
    fun startLiveWallpaper(context: Context)
    fun enabledWidgetConfigLinks(context: Context): List<WidgetConfigLink>
    val restrictLocationProvidersForStoreFlavor: Boolean
}

object SettingsAppBridge {
    lateinit var callbacks: SettingsAppCallbacks
}
