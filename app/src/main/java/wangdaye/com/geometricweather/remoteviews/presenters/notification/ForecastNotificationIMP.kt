package wangdaye.com.geometricweather.remoteviews.presenters.notification

import android.Manifest
import android.app.Notification
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
import wangdaye.com.geometricweather.common.utils.LanguageUtils
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.drawableToBitmap
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getWeatherPendingIntent
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.ThemeManager
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory
import wangdaye.com.geometricweather.theme.weatherView.WeatherViewController

class ForecastNotificationIMP : AbstractRemoteViewsPresenter() {

    companion object {
        @JvmStatic
        fun buildForecastAndSendIt(context: Context, location: Location, today: Boolean) {
            val weather = location.weather ?: return
            val provider = ResourcesProviderFactory.getNewInstance()
            LanguageUtils.setLanguage(
                context,
                SettingsManager.getInstance(context).language.locale
            )
            val manager = NotificationManagerCompat.from(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    GeometricWeather.NOTIFICATION_CHANNEL_ID_FORECAST,
                    GeometricWeather.getNotificationChannelName(
                        context, GeometricWeather.NOTIFICATION_CHANNEL_ID_FORECAST
                    ),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                channel.importance = NotificationManager.IMPORTANCE_HIGH
                channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                manager.createNotificationChannel(channel)
            }
            val builder = NotificationCompat.Builder(
                context, GeometricWeather.NOTIFICATION_CHANNEL_ID_FORECAST
            )
            builder.priority = NotificationCompat.PRIORITY_MAX
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            val daytime: Boolean
            val weatherCode = if (today) {
                daytime = location.isDaylight
                if (daytime) {
                    weather.dailyForecast[0].day().weatherCode
                } else {
                    weather.dailyForecast[0].night().weatherCode
                }
            } else {
                daytime = true
                weather.dailyForecast[1].day().weatherCode
            }
            builder.setSmallIcon(ResourceHelper.getDefaultMinimalXmlIconId(weatherCode, daytime))
            builder.setLargeIcon(
                drawableToBitmap(ResourceHelper.getWeatherIcon(provider, weatherCode, daytime))
            )
            if (today) {
                builder.setSubText(context.getString(R.string.today))
            } else {
                builder.setSubText(context.getString(R.string.tomorrow))
            }
            val temperatureUnit = SettingsManager.getInstance(context).temperatureUnit
            if (today) {
                builder.setContentTitle(
                    context.getString(R.string.daytime) + " " +
                        weather.dailyForecast[0].day().weatherText + " " +
                        weather.dailyForecast[0].day().temperature.getTemperature(context, temperatureUnit)
                ).setContentText(
                    context.getString(R.string.nighttime) + " " +
                        weather.dailyForecast[0].night().weatherText + " " +
                        weather.dailyForecast[0].night().temperature.getTemperature(context, temperatureUnit)
                )
            } else {
                builder.setContentTitle(
                    context.getString(R.string.daytime) + " " +
                        weather.dailyForecast[1].day().weatherText + " " +
                        weather.dailyForecast[1].day().temperature.getTemperature(context, temperatureUnit)
                ).setContentText(
                    context.getString(R.string.nighttime) + " " +
                        weather.dailyForecast[1].night().weatherText + " " +
                        weather.dailyForecast[1].night().temperature.getTemperature(context, temperatureUnit)
                )
            }
            builder.color = ThemeManager.getInstance(context).weatherThemeDelegate.getThemeColors(
                context, WeatherViewController.getWeatherKind(weather), daytime
            )[0]
            builder.setContentIntent(
                getWeatherPendingIntent(
                    context,
                    null,
                    if (today) {
                        GeometricWeather.NOTIFICATION_ID_TODAY_FORECAST
                    } else {
                        GeometricWeather.NOTIFICATION_ID_TOMORROW_FORECAST
                    }
                )
            )
            builder.setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
            builder.setAutoCancel(true)
            builder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            val notification = builder.build()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                manager.notify(
                    if (today) {
                        GeometricWeather.NOTIFICATION_ID_TODAY_FORECAST
                    } else {
                        GeometricWeather.NOTIFICATION_ID_TOMORROW_FORECAST
                    },
                    notification
                )
            }
        }

        @JvmStatic
        fun isEnable(context: Context, today: Boolean): Boolean {
            return if (today) {
                SettingsManager.getInstance(context).isTodayForecastEnabled
            } else {
                SettingsManager.getInstance(context).isTomorrowForecastEnabled
            }
        }
    }
}
