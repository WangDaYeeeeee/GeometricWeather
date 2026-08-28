package wangdaye.com.geometricweather.remoteviews.presenters.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.unit.TemperatureUnit
import wangdaye.com.geometricweather.common.basic.models.weather.Base
import wangdaye.com.geometricweather.common.utils.LanguageUtils
import wangdaye.com.geometricweather.common.utils.helpers.LunarHelper
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.drawableToBitmap
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getWeatherPendingIntent
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.ThemeManager
import wangdaye.com.geometricweather.theme.weatherThemeDelegate
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory
import wangdaye.com.geometricweather.theme.weatherView.WeatherViewController
import java.util.Date

internal class NativeNormalNotificationIMP : AbstractRemoteViewsPresenter() {

    companion object {
        @JvmStatic
        fun buildNotificationAndSendIt(
            context: Context,
            location: Location,
            temperatureUnit: TemperatureUnit,
            daytime: Boolean,
            tempIcon: Boolean,
            canBeCleared: Boolean
        ) {
            val weather = location.weather ?: return
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
                                weather.current.temperature.realFeelTemperature
                                    ?: weather.current.temperature.temperature
                            } else {
                                weather.current.temperature.temperature
                            }
                        )
                    )
                } else {
                    ResourceHelper.getDefaultMinimalXmlIconId(
                        weather.current.weatherCode,
                        daytime
                    )
                }
            )
            builder.setLargeIcon(
                drawableToBitmap(
                    ResourceHelper.getWidgetNotificationIcon(
                        provider, weather.current.weatherCode,
                        daytime, false, false
                    )
                )
            )
            val subtitle = StringBuilder()
            subtitle.append(location.getCityName(context))
            if (SettingsManager.getInstance(context).language.isChinese) {
                subtitle.append(", ").append(LunarHelper.getLunarDate(Date()))
            } else {
                subtitle.append(", ")
                    .append(context.getString(R.string.refresh_at))
                    .append(" ")
                    .append(Base.getTime(context, weather.base.updateDate))
            }
            builder.setSubText(subtitle.toString())
            val content = StringBuilder()
            if (!tempIcon) {
                content.append(
                    if (SettingsManager.getInstance(context).isNotificationFeelsLike) {
                        weather.current.temperature.getRealFeelTemperature(context, temperatureUnit)
                    } else {
                        weather.current.temperature.getTemperature(context, temperatureUnit)
                    }
                ).append(" ")
            }
            content.append(weather.current.weatherText)
            builder.setContentTitle(content.toString())
            val contentText = StringBuilder()
            if (weather.current.airQuality.isValid) {
                contentText.append(context.getString(R.string.air_quality))
                    .append(" - ")
                    .append(weather.current.airQuality.aqiText)
            } else {
                contentText.append(context.getString(R.string.wind))
                    .append(" - ")
                    .append(weather.current.wind.level)
            }
            builder.setContentText(contentText.toString())
            builder.color = ThemeManager.getInstance(context).weatherThemeDelegate.getThemeColors(
                context, WeatherViewController.getWeatherKind(weather), daytime
            )[0]
            builder.setOngoing(!canBeCleared)
            builder.setOnlyAlertOnce(true)
            builder.setContentIntent(
                getWeatherPendingIntent(context, null, GeometricWeather.NOTIFICATION_ID_NORMALLY)
            )
            val notification = builder.build()
            if (!tempIcon && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    notification.javaClass
                        .getMethod("setSmallIcon", Icon::class.java)
                        .invoke(
                            notification,
                            ResourceHelper.getMinimalIcon(
                                provider, weather.current.weatherCode, daytime
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
    }
}
