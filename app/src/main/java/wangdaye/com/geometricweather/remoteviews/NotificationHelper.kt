package wangdaye.com.geometricweather.remoteviews

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.weather.Alert
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
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
import wangdaye.com.geometricweather.remoteviews.presenters.notification.NormalNotificationIMP
import wangdaye.com.geometricweather.settings.ConfigStore
import wangdaye.com.geometricweather.settings.SettingsManager
import java.text.DateFormat

object NotificationHelper {

    private const val NOTIFICATION_GROUP_KEY = "geometric_weather_alert_notification_group"
    private const val PREFERENCE_NOTIFICATION = "NOTIFICATION_PREFERENCE"
    private const val KEY_NOTIFICATION_ID = "NOTIFICATION_ID"

    private const val PREFERENCE_SHORT_TERM_PRECIPITATION_ALERT = "SHORT_TERM_PRECIPITATION_ALERT_PREFERENCE"
    private const val KEY_PRECIPITATION_LOCATION_KEY = "PRECIPITATION_LOCATION_KEY"
    private const val KEY_PRECIPITATION_DATE = "PRECIPITATION_DATE"

    @JvmStatic
    fun updateNotificationIfNecessary(context: Context, locationList: List<Location>) {
        if (NormalNotificationIMP.isEnable(context)) {
            NormalNotificationIMP.buildNotificationAndSendIt(context, locationList)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun getAlertNotificationChannel(context: Context, @ColorInt color: Int): NotificationChannel {
        val channel = NotificationChannel(
            GeometricWeather.NOTIFICATION_CHANNEL_ID_ALERT,
            GeometricWeather.getNotificationChannelName(
                context, GeometricWeather.NOTIFICATION_CHANNEL_ID_ALERT
            ),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.setShowBadge(true)
        channel.lightColor = color
        return channel
    }

    private fun getNotificationBuilder(
        context: Context,
        @DrawableRes iconId: Int,
        title: String,
        subtitle: String,
        content: String,
        @ColorInt color: Int,
        intent: PendingIntent
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, GeometricWeather.NOTIFICATION_CHANNEL_ID_ALERT)
            .setSmallIcon(iconId)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher))
            .setContentTitle(title)
            .setSubText(subtitle)
            .setContentText(content)
            .setColor(color)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setContentIntent(intent)
    }

    @JvmStatic
    fun checkAndSendAlert(context: Context, location: Location, oldResult: Weather?) {
        val weather = location.weather
        if (weather == null || !SettingsManager.getInstance(context).isAlertPushEnabled) {
            return
        }

        val alertList = ArrayList<Alert>()
        if (oldResult == null) {
            alertList.addAll(weather.alertList)
        } else {
            val idSet = HashSet<Long>()
            val desSet = HashSet<String>()
            for (alert in oldResult.alertList) {
                idSet.add(alert.alertId)
                desSet.add(alert.description)
            }
            for (alert in weather.alertList) {
                if (!idSet.contains(alert.alertId) && !desSet.contains(alert.description)) {
                    alertList.add(alert)
                }
            }
        }

        for (i in alertList.indices) {
            sendAlertNotification(context, location, alertList[i], alertList.size > 1)
        }
    }

    private fun sendAlertNotification(
        context: Context,
        location: Location,
        alert: Alert,
        inGroup: Boolean
    ) {
        val manager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                getAlertNotificationChannel(context, getColor(context, location))
            )
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val notificationId = getAlertNotificationId(context)
            manager.notify(
                notificationId,
                buildSingleAlertNotification(context, location, alert, inGroup, notificationId)
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && inGroup) {
                manager.notify(
                    GeometricWeather.NOTIFICATION_ID_ALERT_GROUP,
                    buildAlertGroupSummaryNotification(context, location, alert, notificationId)
                )
            }
        }
    }

    @SuppressLint("InlinedApi")
    private fun buildSingleAlertNotification(
        context: Context,
        location: Location,
        alert: Alert,
        inGroup: Boolean,
        notificationId: Int
    ): Notification {
        val time = DateFormat.getDateTimeInstance(
            DateFormat.LONG, DateFormat.DEFAULT
        ).format(alert.date)

        val builder = getNotificationBuilder(
            context,
            R.drawable.ic_alert,
            alert.description,
            time,
            alert.content,
            getColor(context, location),
            PendingIntent.getActivity(
                context,
                notificationId,
                IntentHelper.buildMainActivityShowAlertsIntent(location),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        ).setStyle(
            NotificationCompat.BigTextStyle()
                .setBigContentTitle(alert.description)
                .setSummaryText(time)
                .bigText(alert.content)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && inGroup) {
            builder.setGroup(NOTIFICATION_GROUP_KEY)
        }
        return builder.build()
    }

    @SuppressLint("InlinedApi")
    private fun buildAlertGroupSummaryNotification(
        context: Context,
        location: Location,
        alert: Alert,
        notificationId: Int
    ): Notification {
        return NotificationCompat.Builder(context, GeometricWeather.NOTIFICATION_CHANNEL_ID_ALERT)
            .setSmallIcon(R.drawable.ic_alert)
            .setContentTitle(alert.description)
            .setGroup(NOTIFICATION_GROUP_KEY)
            .setColor(getColor(context, location))
            .setGroupSummary(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    notificationId,
                    IntentHelper.buildMainActivityShowAlertsIntent(location),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            ).build()
    }

    private fun getAlertNotificationId(context: Context): Int {
        val config = ConfigStore.getInstance(context, PREFERENCE_NOTIFICATION)
        var id = config.getInt(
            KEY_NOTIFICATION_ID, GeometricWeather.NOTIFICATION_ID_ALERT_MIN
        ) + 1
        if (id > GeometricWeather.NOTIFICATION_ID_ALERT_MAX) {
            id = GeometricWeather.NOTIFICATION_ID_ALERT_MIN
        }
        config.edit()
            .putInt(KEY_NOTIFICATION_ID, id)
            .apply()
        return id
    }

    @SuppressLint("InlinedApi")
    @JvmStatic
    fun checkAndSendPrecipitationForecast(context: Context, location: Location) {
        if (!SettingsManager.getInstance(context).isPrecipitationPushEnabled ||
            location.weather == null
        ) {
            return
        }
        val manager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                getAlertNotificationChannel(context, getColor(context, location))
            )
        }

        val weather = location.weather!!
        val config = ConfigStore.getInstance(
            context,
            PREFERENCE_SHORT_TERM_PRECIPITATION_ALERT
        )
        val timestamp = config.getLong(KEY_PRECIPITATION_DATE, 0)

        if (isSameDay(timestamp, System.currentTimeMillis())) {
            return
        }

        if (isShortTermLiquid(weather) || isLiquidDay(weather)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                manager.notify(
                    GeometricWeather.NOTIFICATION_ID_PRECIPITATION,
                    getNotificationBuilder(
                        context,
                        R.drawable.ic_precipitation,
                        context.getString(R.string.precipitation_overview),
                        weather.dailyForecast[0]
                            .getDate(context.getString(R.string.date_format_widget_long)),
                        context.getString(
                            if (isShortTermLiquid(weather)) {
                                R.string.feedback_short_term_precipitation_alert
                            } else {
                                R.string.feedback_today_precipitation_alert
                            }
                        ),
                        getColor(context, location),
                        PendingIntent.getActivity(
                            context,
                            GeometricWeather.NOTIFICATION_ID_PRECIPITATION,
                            IntentHelper.buildMainActivityIntent(location),
                            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    ).build()
                )

                config.edit()
                    .putString(KEY_PRECIPITATION_LOCATION_KEY, location.formattedId)
                    .putLong(KEY_PRECIPITATION_DATE, System.currentTimeMillis())
                    .apply()
            }
        }
    }

    private fun isLiquidDay(weather: Weather): Boolean {
        return weather.dailyForecast[0].day().weatherCode.isPrecipitation ||
            weather.dailyForecast[0].night().weatherCode.isPrecipitation
    }

    private fun isShortTermLiquid(weather: Weather): Boolean {
        for (i in 0 until 4) {
            if (weather.hourlyForecast[i].weatherCode.isPrecipitation) {
                return true
            }
        }
        return false
    }

    private fun isSameDay(time1: Long, time2: Long): Boolean {
        val day1 = time1 / 1000 / 60 / 60 / 24
        val day2 = time2 / 1000 / 60 / 60 / 24
        return day1 != day2
    }

    @ColorInt
    private fun getColor(context: Context, location: Location): Int {
        return ContextCompat.getColor(
            context,
            if (location.isDaylight) R.color.lightPrimary_5 else R.color.darkPrimary_5
        )
    }
}
