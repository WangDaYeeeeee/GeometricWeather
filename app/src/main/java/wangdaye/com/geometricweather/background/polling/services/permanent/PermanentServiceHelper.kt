package wangdaye.com.geometricweather.background.polling.services.permanent

import android.content.Context
import android.content.Intent
import android.os.Build
import wangdaye.com.geometricweather.background.polling.services.permanent.observer.TimeObserverService
import wangdaye.com.geometricweather.settings.SettingsManager

object PermanentServiceHelper {

    @JvmStatic
    fun startPollingService(context: Context) {
        val settings = SettingsManager.getInstance(context)
        if (!settings.isBackgroundFree) {
            val intent = Intent(context, TimeObserverService::class.java)
                .putExtra(TimeObserverService.KEY_CONFIG_CHANGED, true)
                .putExtra(
                    TimeObserverService.KEY_POLLING_RATE,
                    settings.updateInterval.intervalInHour
                ).putExtra(
                    TimeObserverService.KEY_TODAY_FORECAST_TIME,
                    if (settings.isTodayForecastEnabled) settings.todayForecastTime else ""
                ).putExtra(
                    TimeObserverService.KEY_TOMORROW_FORECAST_TIME,
                    if (settings.isTomorrowForecastEnabled) settings.tomorrowForecastTime else ""
                )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    @JvmStatic
    fun updatePollingService(context: Context, pollingFailed: Boolean) {
        if (!SettingsManager.getInstance(context).isBackgroundFree) {
            val intent = Intent(context, TimeObserverService::class.java)
            intent.putExtra(TimeObserverService.KEY_POLLING_FAILED, pollingFailed)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    @JvmStatic
    fun stopPollingService(context: Context) {
        val intent = Intent(context, TimeObserverService::class.java)
        context.stopService(intent)
    }
}
