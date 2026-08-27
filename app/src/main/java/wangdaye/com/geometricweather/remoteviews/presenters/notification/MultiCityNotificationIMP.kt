package wangdaye.com.geometricweather.remoteviews.presenters.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.NotificationTextColor
import wangdaye.com.geometricweather.common.basic.models.options.unit.TemperatureUnit
import wangdaye.com.geometricweather.common.basic.models.weather.Temperature
import wangdaye.com.geometricweather.common.utils.LanguageUtils
import wangdaye.com.geometricweather.common.utils.ObjectUtils
import wangdaye.com.geometricweather.common.utils.helpers.LunarHelper
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getWeatherPendingIntent
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider
import java.util.Date

class MultiCityNotificationIMP : AbstractRemoteViewsPresenter() {

    companion object {
        @JvmStatic
        fun buildNotificationAndSendIt(
            context: Context,
            locationList: List<Location>,
            temperatureUnit: TemperatureUnit,
            dayTime: Boolean,
            tempIcon: Boolean,
            canBeCleared: Boolean
        ) {
            val weather = locationList[0].weather ?: return
            val provider = ResourcesProviderFactory.getNewInstance()
            LanguageUtils.setLanguage(
                context,
                SettingsManager.getInstance(context).language.locale
            )
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
                            if (SettingsManager.getInstance(context).isNotificationFeelsLike) {
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
                    locationList[0],
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
                    RemoteViews(context.packageName, R.layout.notification_multi_city),
                    provider,
                    locationList,
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
                                provider, weather.current.weatherCode, dayTime
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
                    if (SettingsManager.getInstance(context).isNotificationFeelsLike) {
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
            provider: ResourceProvider,
            locationList: List<Location>,
            temperatureUnit: TemperatureUnit,
            dayTime: Boolean
        ): RemoteViews {
            if (locationList[0].weather == null) {
                return views
            }
            var result = buildBaseView(
                context, views, provider, locationList[0], temperatureUnit, dayTime
            )
            result.setViewVisibility(R.id.notification_multi_city_1, View.GONE)
            if (locationList.size > 1 && locationList[1].weather != null) {
                val location = locationList[1]
                val weather = location.weather!!
                val cityDayTime = location.isDaylight
                result.setViewVisibility(R.id.notification_multi_city_1, View.VISIBLE)
                result.setImageViewUri(
                    R.id.notification_multi_city_icon_1,
                    ResourceHelper.getWidgetNotificationIconUri(
                        provider,
                        if (cityDayTime) weather.dailyForecast[0].day().weatherCode
                        else weather.dailyForecast[0].night().weatherCode,
                        cityDayTime, false, NotificationTextColor.GREY
                    )
                )
                result.setTextViewText(
                    R.id.notification_multi_city_text_1,
                    getCityTitle(context, location, temperatureUnit)
                )
            }
            result.setViewVisibility(R.id.notification_multi_city_2, View.GONE)
            if (locationList.size > 2 && locationList[2].weather != null) {
                val location = locationList[2]
                val weather = location.weather!!
                val cityDayTime = location.isDaylight
                result.setViewVisibility(R.id.notification_multi_city_2, View.VISIBLE)
                result.setImageViewUri(
                    R.id.notification_multi_city_icon_2,
                    ResourceHelper.getWidgetNotificationIconUri(
                        provider,
                        if (cityDayTime) weather.dailyForecast[0].day().weatherCode
                        else weather.dailyForecast[0].night().weatherCode,
                        cityDayTime, false, NotificationTextColor.GREY
                    )
                )
                result.setTextViewText(
                    R.id.notification_multi_city_text_2,
                    getCityTitle(context, location, temperatureUnit)
                )
            }
            result.setViewVisibility(R.id.notification_multi_city_3, View.GONE)
            if (locationList.size > 3 && locationList[3].weather != null) {
                val location = locationList[3]
                val weather = location.weather!!
                val cityDayTime = location.isDaylight
                result.setViewVisibility(R.id.notification_multi_city_3, View.VISIBLE)
                result.setImageViewUri(
                    R.id.notification_multi_city_icon_3,
                    ResourceHelper.getWidgetNotificationIconUri(
                        provider,
                        if (cityDayTime) weather.dailyForecast[0].day().weatherCode
                        else weather.dailyForecast[0].night().weatherCode,
                        cityDayTime, false, NotificationTextColor.GREY
                    )
                )
                result.setTextViewText(
                    R.id.notification_multi_city_text_3,
                    getCityTitle(context, location, temperatureUnit)
                )
            }
            return result
        }

        private fun getCityTitle(context: Context, location: Location, unit: TemperatureUnit): String {
            val builder = StringBuilder(
                if (location.isCurrentPosition) {
                    context.getString(R.string.current_location)
                } else {
                    location.getCityName(context)
                }
            )
            location.weather?.let { weather ->
                builder.append(", ").append(
                    Temperature.getTrendTemperature(
                        context,
                        weather.dailyForecast[0].night().temperature.temperature,
                        weather.dailyForecast[0].day().temperature.temperature,
                        unit
                    )
                )
            }
            return builder.toString()
        }

        @JvmStatic
        fun isEnable(context: Context): Boolean {
            return SettingsManager.getInstance(context).isNotificationEnabled
        }
    }
}
