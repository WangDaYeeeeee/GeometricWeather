package wangdaye.com.geometricweather.background.polling

import android.content.Context
import android.os.Build
import wangdaye.com.geometricweather.background.polling.services.permanent.PermanentServiceHelper
import wangdaye.com.geometricweather.background.polling.work.WorkerHelper
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.utils.helpers.AsyncHelper
import wangdaye.com.geometricweather.common.utils.helpers.IntentHelper
import wangdaye.com.geometricweather.common.utils.helpers.startAboutActivity
import wangdaye.com.geometricweather.common.utils.helpers.startAlertActivity
import wangdaye.com.geometricweather.common.utils.helpers.startAllergenActivity
import wangdaye.com.geometricweather.common.utils.helpers.startAwakeForegroundUpdateService
import wangdaye.com.geometricweather.common.utils.helpers.startCardDisplayManageActivity
import wangdaye.com.geometricweather.common.utils.helpers.startDailyTrendDisplayManageActivity
import wangdaye.com.geometricweather.common.utils.helpers.startDailyWeatherActivity
import wangdaye.com.geometricweather.common.utils.helpers.startHourlyTrendDisplayManageActivityForResult
import wangdaye.com.geometricweather.common.utils.helpers.startLiveWallpaperActivity
import wangdaye.com.geometricweather.common.utils.helpers.startMainActivity
import wangdaye.com.geometricweather.common.utils.helpers.startMainActivityForManagement
import wangdaye.com.geometricweather.common.utils.helpers.startPreviewIconActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSearchActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSelectProviderActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSettingsActivity
import wangdaye.com.geometricweather.common.utils.helpers.buildAwakeUpdateActivityIntent
import wangdaye.com.geometricweather.common.utils.helpers.buildMainActivityIntent
import wangdaye.com.geometricweather.common.utils.helpers.buildMainActivityShowAlertsIntent
import wangdaye.com.geometricweather.common.utils.helpers.buildMainActivityShowDailyForecastIntent
import wangdaye.com.geometricweather.common.utils.helpers.getAwakeForegroundUpdateServiceIntent
import wangdaye.com.geometricweather.db.DatabaseHelper
import wangdaye.com.geometricweather.remoteviews.NotificationHelper
import wangdaye.com.geometricweather.remoteviews.WidgetHelper
import wangdaye.com.geometricweather.settings.SettingsManager

object PollingManager {

    @JvmStatic
    fun resetAllBackgroundTask(context: Context, forceRefresh: Boolean) {
        val settings = SettingsManager.getInstance(context)
        if (forceRefresh) {
            forceRefresh(context)
            return
        }
        if (settings.isBackgroundFree) {
            PermanentServiceHelper.stopPollingService(context)
            WorkerHelper.setNormalPollingWork(
                context,
                SettingsManager.getInstance(context).updateInterval.intervalInHour
            )
            if (settings.isTodayForecastEnabled) {
                WorkerHelper.setTodayForecastUpdateWork(context, settings.todayForecastTime, false)
            } else {
                WorkerHelper.cancelTodayForecastUpdateWork(context)
            }
            if (settings.isTomorrowForecastEnabled) {
                WorkerHelper.setTomorrowForecastUpdateWork(context, settings.tomorrowForecastTime, false)
            } else {
                WorkerHelper.cancelTomorrowForecastUpdateWork(context)
            }
        } else {
            WorkerHelper.cancelNormalPollingWork(context)
            WorkerHelper.cancelTodayForecastUpdateWork(context)
            WorkerHelper.cancelTomorrowForecastUpdateWork(context)
            PermanentServiceHelper.startPollingService(context)
        }
    }

    @JvmStatic
    fun resetNormalBackgroundTask(context: Context, forceRefresh: Boolean) {
        val settings = SettingsManager.getInstance(context)
        if (forceRefresh) {
            forceRefresh(context)
            return
        }
        if (settings.isBackgroundFree) {
            PermanentServiceHelper.stopPollingService(context)
            WorkerHelper.setNormalPollingWork(
                context,
                SettingsManager.getInstance(context).updateInterval.intervalInHour
            )
        } else {
            WorkerHelper.cancelNormalPollingWork(context)
            WorkerHelper.cancelTodayForecastUpdateWork(context)
            WorkerHelper.cancelTomorrowForecastUpdateWork(context)
            PermanentServiceHelper.startPollingService(context)
        }
    }

    @JvmStatic
    fun resetTodayForecastBackgroundTask(context: Context, forceRefresh: Boolean, nextDay: Boolean) {
        val settings = SettingsManager.getInstance(context)
        if (forceRefresh) {
            forceRefresh(context)
            return
        }
        if (settings.isBackgroundFree) {
            PermanentServiceHelper.stopPollingService(context)
            if (settings.isTodayForecastEnabled) {
                WorkerHelper.setTodayForecastUpdateWork(context, settings.todayForecastTime, nextDay)
            } else {
                WorkerHelper.cancelTodayForecastUpdateWork(context)
            }
        } else {
            WorkerHelper.cancelNormalPollingWork(context)
            WorkerHelper.cancelTodayForecastUpdateWork(context)
            WorkerHelper.cancelTomorrowForecastUpdateWork(context)
            PermanentServiceHelper.startPollingService(context)
        }
    }

    @JvmStatic
    fun resetTomorrowForecastBackgroundTask(context: Context, forceRefresh: Boolean, nextDay: Boolean) {
        val settings = SettingsManager.getInstance(context)
        if (forceRefresh) {
            forceRefresh(context)
            return
        }
        if (settings.isBackgroundFree) {
            PermanentServiceHelper.stopPollingService(context)
            if (settings.isTomorrowForecastEnabled) {
                WorkerHelper.setTomorrowForecastUpdateWork(context, settings.tomorrowForecastTime, nextDay)
            } else {
                WorkerHelper.cancelTomorrowForecastUpdateWork(context)
            }
        } else {
            WorkerHelper.cancelNormalPollingWork(context)
            WorkerHelper.cancelTodayForecastUpdateWork(context)
            WorkerHelper.cancelTomorrowForecastUpdateWork(context)
            PermanentServiceHelper.startPollingService(context)
        }
    }

    private fun forceRefresh(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AsyncHelper.runOnIO {
                val locationList = DatabaseHelper.getInstance(context).readLocationList()
                for (i in locationList.indices) {
                    locationList[i] = Location.copy(
                        locationList[i],
                        DatabaseHelper.getInstance(context).readWeather(locationList[i])
                    )
                }
                WidgetHelper.updateWidgetIfNecessary(context, locationList[0])
                WidgetHelper.updateWidgetIfNecessary(context, locationList)
                NotificationHelper.updateNotificationIfNecessary(context, locationList)
            }
            WorkerHelper.setExpeditedPollingWork(context)
        } else {
            IntentHelper.startAwakeForegroundUpdateService(context)
        }
    }
}
