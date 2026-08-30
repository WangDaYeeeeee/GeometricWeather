package wangdaye.com.geometricweather.settings

import android.content.Context
import android.content.Intent
import wangdaye.com.geometricweather.BuildConfig
import wangdaye.com.geometricweather.background.polling.PollingManager
import wangdaye.com.geometricweather.common.utils.helpers.IntentHelper
import wangdaye.com.geometricweather.common.utils.helpers.startLiveWallpaperActivity
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.remoteviews.config.ClockDayDetailsWidgetConfigActivity
import wangdaye.com.geometricweather.remoteviews.config.ClockDayHorizontalWidgetConfigActivity
import wangdaye.com.geometricweather.remoteviews.config.ClockDayVerticalWidgetConfigActivity
import wangdaye.com.geometricweather.remoteviews.config.ClockDayWeekWidgetConfigActivity
import wangdaye.com.geometricweather.remoteviews.config.DailyTrendWidgetConfigActivity
import wangdaye.com.geometricweather.remoteviews.config.DayWeekWidgetConfigActivity
import wangdaye.com.geometricweather.remoteviews.config.DayWidgetConfigActivity
import wangdaye.com.geometricweather.remoteviews.config.HourlyTrendWidgetConfigActivity
import wangdaye.com.geometricweather.remoteviews.config.MultiCityWidgetConfigActivity
import wangdaye.com.geometricweather.remoteviews.config.TextWidgetConfigActivity
import wangdaye.com.geometricweather.remoteviews.config.WeekWidgetConfigActivity
import wangdaye.com.geometricweather.remoteviews.presenters.ClockDayDetailsWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.ClockDayHorizontalWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.ClockDayVerticalWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.ClockDayWeekWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.DailyTrendWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.DayWeekWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.DayWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.HourlyTrendWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.MultiCityWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.TextWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.WeekWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.notification.NormalNotificationIMP

object AppSettingsAppCallbacks : SettingsAppCallbacks {

    override fun resetNormalBackgroundTask(context: Context, forceRefresh: Boolean) {
        PollingManager.resetNormalBackgroundTask(context, forceRefresh)
    }

    override fun resetTodayForecastBackgroundTask(
        context: Context,
        forceRefresh: Boolean,
        nextDay: Boolean,
    ) {
        PollingManager.resetTodayForecastBackgroundTask(context, forceRefresh, nextDay)
    }

    override fun resetTomorrowForecastBackgroundTask(
        context: Context,
        forceRefresh: Boolean,
        nextDay: Boolean,
    ) {
        PollingManager.resetTomorrowForecastBackgroundTask(context, forceRefresh, nextDay)
    }

    override fun cancelNotification(context: Context) {
        NormalNotificationIMP.cancelNotification(context)
    }

    override fun startLiveWallpaper(context: Context) {
        IntentHelper.startLiveWallpaperActivity(context)
    }

    override fun enabledWidgetConfigLinks(context: Context): List<WidgetConfigLink> {
        val links = ArrayList<WidgetConfigLink>()
        if (DayWidgetIMP.isEnable(context)) {
            links.add(WidgetConfigLink(R.string.widget_day) {
                it.startActivity(Intent(it, DayWidgetConfigActivity::class.java))
            })
        }
        if (WeekWidgetIMP.isEnable(context)) {
            links.add(WidgetConfigLink(R.string.widget_week) {
                it.startActivity(Intent(it, WeekWidgetConfigActivity::class.java))
            })
        }
        if (DayWeekWidgetIMP.isEnable(context)) {
            links.add(WidgetConfigLink(R.string.widget_day_week) {
                it.startActivity(Intent(it, DayWeekWidgetConfigActivity::class.java))
            })
        }
        if (ClockDayHorizontalWidgetIMP.isEnable(context)) {
            links.add(WidgetConfigLink(R.string.widget_clock_day_horizontal) {
                it.startActivity(Intent(it, ClockDayHorizontalWidgetConfigActivity::class.java))
            })
        }
        if (ClockDayDetailsWidgetIMP.isEnable(context)) {
            links.add(WidgetConfigLink(R.string.widget_clock_day_details) {
                it.startActivity(Intent(it, ClockDayDetailsWidgetConfigActivity::class.java))
            })
        }
        if (ClockDayVerticalWidgetIMP.isEnable(context)) {
            links.add(WidgetConfigLink(R.string.widget_clock_day_vertical) {
                it.startActivity(Intent(it, ClockDayVerticalWidgetConfigActivity::class.java))
            })
        }
        if (ClockDayWeekWidgetIMP.isEnable(context)) {
            links.add(WidgetConfigLink(R.string.widget_clock_day_week) {
                it.startActivity(Intent(it, ClockDayWeekWidgetConfigActivity::class.java))
            })
        }
        if (TextWidgetIMP.isEnable(context)) {
            links.add(WidgetConfigLink(R.string.widget_text) {
                it.startActivity(Intent(it, TextWidgetConfigActivity::class.java))
            })
        }
        if (DailyTrendWidgetIMP.isEnable(context)) {
            links.add(WidgetConfigLink(R.string.widget_trend_daily) {
                it.startActivity(Intent(it, DailyTrendWidgetConfigActivity::class.java))
            })
        }
        if (HourlyTrendWidgetIMP.isEnable(context)) {
            links.add(WidgetConfigLink(R.string.widget_trend_hourly) {
                it.startActivity(Intent(it, HourlyTrendWidgetConfigActivity::class.java))
            })
        }
        if (MultiCityWidgetIMP.isEnable(context)) {
            links.add(WidgetConfigLink(R.string.widget_multi_city) {
                it.startActivity(Intent(it, MultiCityWidgetConfigActivity::class.java))
            })
        }
        return links
    }

    override val restrictLocationProvidersForStoreFlavor: Boolean
        get() = BuildConfig.FLAVOR.contains("fdroid") || BuildConfig.FLAVOR.contains("gplay")
}
