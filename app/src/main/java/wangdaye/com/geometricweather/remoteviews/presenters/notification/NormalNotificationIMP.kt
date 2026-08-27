package wangdaye.com.geometricweather.remoteviews.presenters.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.NotificationStyle
import wangdaye.com.geometricweather.common.basic.models.options.NotificationTextColor
import wangdaye.com.geometricweather.common.basic.models.options.unit.TemperatureUnit
import wangdaye.com.geometricweather.common.basic.models.weather.Temperature
import wangdaye.com.geometricweather.common.utils.LanguageUtils
import wangdaye.com.geometricweather.common.utils.ObjectUtils
import wangdaye.com.geometricweather.common.utils.helpers.LunarHelper
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getWeatherPendingIntent
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.isWeekIconDaytime
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider
import java.util.Date

class NormalNotificationIMP : AbstractRemoteViewsPresenter() {

    companion object {
        @JvmStatic
        fun buildNotificationAndSendIt(context: Context, locationList: List<Location>) {
            val location = locationList[0]
            val weather = location.weather ?: return
            val provider = ResourcesProviderFactory.getNewInstance()
            LanguageUtils.setLanguage(
                context,
                SettingsManager.getInstance(context).language.locale
            )
            val settings = SettingsManager.getInstance(context)
            val temperatureUnit = settings.temperatureUnit
            val dayTime = location.isDaylight
            val tempIcon = settings.isNotificationTemperatureIconEnabled
            val canBeCleared = settings.isNotificationCanBeClearedEnabled
            if (settings.notificationStyle == NotificationStyle.NATIVE) {
                NativeNormalNotificationIMP.buildNotificationAndSendIt(
                    context, location, temperatureUnit, dayTime, tempIcon, canBeCleared
                )
                return
            } else if (settings.notificationStyle == NotificationStyle.CITIES) {
                MultiCityNotificationIMP.buildNotificationAndSendIt(
                    context, locationList, temperatureUnit, dayTime, tempIcon, canBeCleared
                )
                return
            }
            val manager = NotificationManagerCompat.from(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    GeometricWeather.NOTIFICATION_CHANNEL_ID_NORMALLY,
                    GeometricWeather.getNotificationChannelName(
                        context,
                        GeometricWeather.NOTIFICATION_CHANNEL_ID_NORMALLY
                    ),
                    NotificationManager.IMPORTANCE_LOW
                )
                channel.setShowBadge(false)
                channel.importance = NotificationManager.IMPORTANCE_HIGH
                channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                manager.createNotificationChannel(channel)
            }
            val builder = NotificationCompat.Builder(
                context,
                GeometricWeather.NOTIFICATION_CHANNEL_ID_NORMALLY
            )
            builder.priority = NotificationCompat.PRIORITY_MAX
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            builder.setSmallIcon(
                if (tempIcon) {
                    ResourceHelper.getTempIconId(
                        context,
                        temperatureUnit.getValueWithoutUnit(
                            if (settings.isNotificationFeelsLike) {
                                ObjectUtils.safeValueOf(weather.current.temperature.realFeelTemperature)
                            } else {
                                weather.current.temperature.temperature
                            }
                        )
                    )
                } else {
                    ResourceHelper.getDefaultMinimalXmlIconId(
                        weather.current.weatherCode,
                        dayTime
                    )
                }
            )
            builder.setContent(
                buildBaseView(
                    context,
                    RemoteViews(context.packageName, R.layout.notification_base),
                    provider,
                    location,
                    temperatureUnit,
                    dayTime
                )
            )
            builder.setContentIntent(
                getWeatherPendingIntent(context, null, GeometricWeather.NOTIFICATION_ID_NORMALLY)
            )
            builder.setCustomBigContentView(
                buildBigView(
                    context,
                    RemoteViews(context.packageName, R.layout.notification_big),
                    settings.notificationStyle == NotificationStyle.DAILY,
                    provider,
                    location,
                    temperatureUnit,
                    dayTime
                )
            )
            builder.setOngoing(!canBeCleared)
            builder.setOnlyAlertOnce(true)
            val notification = builder.build()
            if (!tempIcon && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    notification.javaClass
                        .getMethod("setSmallIcon", Icon::class.java)
                        .invoke(
                            notification,
                            ResourceHelper.getMinimalIcon(
                                provider,
                                weather.current.weatherCode,
                                dayTime
                            )
                        )
                } catch (_: Exception) {
                }
            }
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                manager.notify(GeometricWeather.NOTIFICATION_ID_NORMALLY, notification)
            }
        }

        private fun buildBaseView(
            context: Context,
            views: RemoteViews,
            provider: ResourceProvider,
            location: Location,
            temperatureUnit: TemperatureUnit,
            dayTime: Boolean
        ): RemoteViews {
            val weather = location.weather ?: return views
            val settings = SettingsManager.getInstance(context)
            views.setImageViewUri(
                R.id.notification_base_icon,
                ResourceHelper.getWidgetNotificationIconUri(
                    provider,
                    weather.current.weatherCode,
                    dayTime,
                    false,
                    NotificationTextColor.GREY
                )
            )
            views.setTextViewText(
                R.id.notification_base_realtimeTemp,
                Temperature.getShortTemperature(
                    context,
                    if (settings.isNotificationFeelsLike) {
                        ObjectUtils.safeValueOf(weather.current.temperature.realFeelTemperature)
                    } else {
                        weather.current.temperature.temperature
                    },
                    temperatureUnit
                )
            )
            if (weather.current.airQuality.isValid) {
                views.setTextViewText(
                    R.id.notification_base_aqiAndWind,
                    context.getString(R.string.air_quality) + " - " + weather.current.airQuality.aqiText
                )
            } else {
                views.setTextViewText(
                    R.id.notification_base_aqiAndWind,
                    context.getString(R.string.wind) + " - " + weather.current.wind.level
                )
            }
            views.setTextViewText(R.id.notification_base_weather, weather.current.weatherText)
            val timeStr = StringBuilder()
            timeStr.append(location.getCityName(context))
            if (SettingsManager.getInstance(context).language.isChinese) {
                timeStr.append(", ").append(LunarHelper.getLunarDate(Date()))
            }
            views.setTextViewText(R.id.notification_base_time, timeStr.toString())
            return views
        }

        private fun buildBigView(
            context: Context,
            views: RemoteViews,
            daily: Boolean,
            provider: ResourceProvider,
            location: Location,
            temperatureUnit: TemperatureUnit,
            dayTime: Boolean
        ): RemoteViews {
            val weather = location.weather ?: return views
            var result = buildBaseView(context, views, provider, location, temperatureUnit, dayTime)
            if (daily) {
                val weekIconDaytime = isWeekIconDaytime(
                    SettingsManager.getInstance(context).widgetWeekIconMode,
                    dayTime
                )
                result.setTextViewText(R.id.notification_big_week_1, context.getString(R.string.today))
                result.setTextViewText(
                    R.id.notification_big_temp_1,
                    Temperature.getTrendTemperature(
                        context,
                        weather.dailyForecast[0].night().temperature.temperature,
                        weather.dailyForecast[0].day().temperature.temperature,
                        temperatureUnit
                    )
                )
                result.setImageViewUri(
                    R.id.notification_big_icon_1,
                    ResourceHelper.getWidgetNotificationIconUri(
                        provider,
                        if (weekIconDaytime) weather.dailyForecast[0].day().weatherCode
                        else weather.dailyForecast[0].night().weatherCode,
                        weekIconDaytime, false, NotificationTextColor.GREY
                    )
                )
                for (i in 1..4) {
                    val weekId = when (i) {
                        1 -> R.id.notification_big_week_2
                        2 -> R.id.notification_big_week_3
                        3 -> R.id.notification_big_week_4
                        else -> R.id.notification_big_week_5
                    }
                    val tempId = when (i) {
                        1 -> R.id.notification_big_temp_2
                        2 -> R.id.notification_big_temp_3
                        3 -> R.id.notification_big_temp_4
                        else -> R.id.notification_big_temp_5
                    }
                    val iconId = when (i) {
                        1 -> R.id.notification_big_icon_2
                        2 -> R.id.notification_big_icon_3
                        3 -> R.id.notification_big_icon_4
                        else -> R.id.notification_big_icon_5
                    }
                    result.setTextViewText(weekId, weather.dailyForecast[i].getWeek(context))
                    result.setTextViewText(
                        tempId,
                        Temperature.getTrendTemperature(
                            context,
                            weather.dailyForecast[i].night().temperature.temperature,
                            weather.dailyForecast[i].day().temperature.temperature,
                            temperatureUnit
                        )
                    )
                    result.setImageViewUri(
                        iconId,
                        ResourceHelper.getWidgetNotificationIconUri(
                            provider,
                            if (weekIconDaytime) weather.dailyForecast[i].day().weatherCode
                            else weather.dailyForecast[i].night().weatherCode,
                            weekIconDaytime, false, NotificationTextColor.GREY
                        )
                    )
                }
            } else {
                val hourIds = intArrayOf(
                    R.id.notification_big_week_1, R.id.notification_big_week_2,
                    R.id.notification_big_week_3, R.id.notification_big_week_4,
                    R.id.notification_big_week_5
                )
                val tempIds = intArrayOf(
                    R.id.notification_big_temp_1, R.id.notification_big_temp_2,
                    R.id.notification_big_temp_3, R.id.notification_big_temp_4,
                    R.id.notification_big_temp_5
                )
                val iconIds = intArrayOf(
                    R.id.notification_big_icon_1, R.id.notification_big_icon_2,
                    R.id.notification_big_icon_3, R.id.notification_big_icon_4,
                    R.id.notification_big_icon_5
                )
                for (i in 0..4) {
                    val hourly = weather.hourlyForecast[i]
                    result.setTextViewText(hourIds[i], hourly.getHour(context))
                    result.setTextViewText(
                        tempIds[i],
                        hourly.temperature.getShortTemperature(context, temperatureUnit)
                    )
                    result.setImageViewUri(
                        iconIds[i],
                        ResourceHelper.getWidgetNotificationIconUri(
                            provider,
                            hourly.weatherCode,
                            hourly.isDaylight,
                            false,
                            NotificationTextColor.GREY
                        )
                    )
                }
            }
            return result
        }

        @JvmStatic
        fun cancelNotification(context: Context) {
            NotificationManagerCompat.from(context).cancel(GeometricWeather.NOTIFICATION_ID_NORMALLY)
        }

        @JvmStatic
        fun isEnable(context: Context): Boolean {
            return SettingsManager.getInstance(context).isNotificationEnabled
        }
    }
}
