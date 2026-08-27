package wangdaye.com.geometricweather.remoteviews.presenters

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.TypedValue
import android.widget.RemoteViews
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.background.receiver.widget.WidgetWeekProvider
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.NotificationTextColor
import wangdaye.com.geometricweather.common.basic.models.options.unit.TemperatureUnit
import wangdaye.com.geometricweather.common.basic.models.weather.Temperature
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.remoteviews.WidgetHelper
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getCardBackgroundId
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getDailyForecastPendingIntent
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getWeatherPendingIntent
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getWidgetConfig
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.isWeekIconDaytime
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.WidgetColor
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider

class WeekWidgetIMP : AbstractRemoteViewsPresenter() {

    companion object {
        @JvmStatic
        fun updateWidgetView(context: Context, location: Location) {
            val config = getWidgetConfig(
                context,
                context.getString(R.string.sp_widget_week_setting)
            )
            val views = getRemoteViews(
                context, location, config.viewStyle, config.cardStyle,
                config.cardAlpha, config.textColor, config.textSize
            )
            AppWidgetManager.getInstance(context).updateAppWidget(
                ComponentName(context, WidgetWeekProvider::class.java),
                views
            )
        }

        @JvmStatic
        fun getRemoteViews(
            context: Context,
            location: Location,
            viewStyle: String?,
            cardStyle: String?,
            cardAlpha: Int,
            textColor: String?,
            textSize: Int
        ): RemoteViews {
            val provider = ResourcesProviderFactory.getNewInstance()
            val dayTime = location.isDaylight
            val settings = SettingsManager.getInstance(context)
            val temperatureUnit = settings.temperatureUnit
            val weekIconMode = settings.widgetWeekIconMode
            val minimalIcon = settings.isWidgetMinimalIconEnabled
            val color = WidgetColor(context, cardStyle ?: "none", textColor ?: "light")
            val views = if (viewStyle == "3_days") {
                RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_week_3 else R.layout.widget_week_3_card
                )
            } else {
                RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_week else R.layout.widget_week_card
                )
            }
            val weather = location.weather ?: return views
            views.setTextViewText(R.id.widget_week_week_1, WidgetHelper.getDailyWeek(context, weather, 0))
            views.setTextViewText(R.id.widget_week_week_2, WidgetHelper.getDailyWeek(context, weather, 1))
            views.setTextViewText(R.id.widget_week_week_3, WidgetHelper.getDailyWeek(context, weather, 2))
            views.setTextViewText(R.id.widget_week_week_4, WidgetHelper.getDailyWeek(context, weather, 3))
            views.setTextViewText(R.id.widget_week_week_5, WidgetHelper.getDailyWeek(context, weather, 4))
            views.setTextViewText(
                R.id.widget_week_temp,
                weather.current.temperature.getShortTemperature(context, temperatureUnit)
            )
            views.setTextViewText(R.id.widget_week_temp_1, getTemp(context, weather, 0, temperatureUnit))
            views.setTextViewText(R.id.widget_week_temp_2, getTemp(context, weather, 1, temperatureUnit))
            views.setTextViewText(R.id.widget_week_temp_3, getTemp(context, weather, 2, temperatureUnit))
            views.setTextViewText(R.id.widget_week_temp_4, getTemp(context, weather, 3, temperatureUnit))
            views.setTextViewText(R.id.widget_week_temp_5, getTemp(context, weather, 4, temperatureUnit))
            views.setImageViewUri(
                R.id.widget_week_icon,
                ResourceHelper.getWidgetNotificationIconUri(
                    provider,
                    weather.current.weatherCode,
                    dayTime,
                    minimalIcon,
                    color.getMinimalIconColor()
                )
            )
            val weekIconDaytime = isWeekIconDaytime(weekIconMode, dayTime)
            views.setImageViewUri(
                R.id.widget_week_icon_1,
                getIconDrawableUri(provider, weather, weekIconDaytime, minimalIcon, color.getMinimalIconColor(), 0)
            )
            views.setImageViewUri(
                R.id.widget_week_icon_2,
                getIconDrawableUri(provider, weather, weekIconDaytime, minimalIcon, color.getMinimalIconColor(), 1)
            )
            views.setImageViewUri(
                R.id.widget_week_icon_3,
                getIconDrawableUri(provider, weather, weekIconDaytime, minimalIcon, color.getMinimalIconColor(), 2)
            )
            views.setImageViewUri(
                R.id.widget_week_icon_4,
                getIconDrawableUri(provider, weather, weekIconDaytime, minimalIcon, color.getMinimalIconColor(), 3)
            )
            views.setImageViewUri(
                R.id.widget_week_icon_5,
                getIconDrawableUri(provider, weather, weekIconDaytime, minimalIcon, color.getMinimalIconColor(), 4)
            )
            if (color.textColor != Color.TRANSPARENT) {
                views.setTextColor(R.id.widget_week_week_1, color.textColor)
                views.setTextColor(R.id.widget_week_week_2, color.textColor)
                views.setTextColor(R.id.widget_week_week_3, color.textColor)
                views.setTextColor(R.id.widget_week_week_4, color.textColor)
                views.setTextColor(R.id.widget_week_week_5, color.textColor)
                views.setTextColor(R.id.widget_week_temp, color.textColor)
                views.setTextColor(R.id.widget_week_temp_1, color.textColor)
                views.setTextColor(R.id.widget_week_temp_2, color.textColor)
                views.setTextColor(R.id.widget_week_temp_3, color.textColor)
                views.setTextColor(R.id.widget_week_temp_4, color.textColor)
                views.setTextColor(R.id.widget_week_temp_5, color.textColor)
            }
            if (textSize != 100) {
                val contentSize = context.resources.getDimensionPixelSize(R.dimen.widget_content_text_size) *
                    textSize / 100f
                views.setTextViewTextSize(R.id.widget_week_week_1, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_week_week_2, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_week_week_3, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_week_week_4, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_week_week_5, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_week_temp_1, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_week_temp_2, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_week_temp_3, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_week_temp_4, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_week_temp_5, TypedValue.COMPLEX_UNIT_PX, contentSize)
            }
            if (color.showCard) {
                views.setImageViewResource(R.id.widget_week_card, getCardBackgroundId(color.cardColor))
                views.setInt(R.id.widget_week_card, "setImageAlpha", (cardAlpha / 100.0 * 255).toInt())
            }
            setOnClickPendingIntent(context, views, location, viewStyle)
            return views
        }

        @JvmStatic
        fun isEnable(context: Context): Boolean {
            val widgetIds = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, WidgetWeekProvider::class.java))
            return widgetIds != null && widgetIds.isNotEmpty()
        }

        private fun getTemp(context: Context, weather: Weather, index: Int, unit: TemperatureUnit): String? {
            return Temperature.getTrendTemperature(
                context,
                weather.dailyForecast[index].night().temperature.temperature,
                weather.dailyForecast[index].day().temperature.temperature,
                unit
            )
        }

        private fun getIconDrawableUri(
            helper: ResourceProvider,
            weather: Weather,
            dayTime: Boolean,
            minimalIcon: Boolean,
            color: NotificationTextColor,
            index: Int
        ): Uri {
            return ResourceHelper.getWidgetNotificationIconUri(
                helper,
                if (dayTime) {
                    weather.dailyForecast[index].day().weatherCode
                } else {
                    weather.dailyForecast[index].night().weatherCode
                },
                dayTime, minimalIcon, color
            )
        }

        private fun setOnClickPendingIntent(
            context: Context,
            views: RemoteViews,
            location: Location,
            viewType: String?
        ) {
            views.setOnClickPendingIntent(
                R.id.widget_week_weather,
                getWeatherPendingIntent(
                    context,
                    location,
                    GeometricWeather.WIDGET_WEEK_PENDING_INTENT_CODE_WEATHER
                )
            )
            if (viewType == "3_days") {
                views.setOnClickPendingIntent(
                    R.id.widget_week_icon_1,
                    getDailyForecastPendingIntent(
                        context, location, 0,
                        GeometricWeather.WIDGET_WEEK_PENDING_INTENT_CODE_DAILY_FORECAST_1
                    )
                )
                views.setOnClickPendingIntent(
                    R.id.widget_week_icon_2,
                    getDailyForecastPendingIntent(
                        context, location, 1,
                        GeometricWeather.WIDGET_WEEK_PENDING_INTENT_CODE_DAILY_FORECAST_2
                    )
                )
                views.setOnClickPendingIntent(
                    R.id.widget_week_icon_3,
                    getDailyForecastPendingIntent(
                        context, location, 2,
                        GeometricWeather.WIDGET_WEEK_PENDING_INTENT_CODE_DAILY_FORECAST_3
                    )
                )
                views.setOnClickPendingIntent(
                    R.id.widget_week_icon_4,
                    getDailyForecastPendingIntent(
                        context, location, 3,
                        GeometricWeather.WIDGET_WEEK_PENDING_INTENT_CODE_DAILY_FORECAST_4
                    )
                )
                views.setOnClickPendingIntent(
                    R.id.widget_week_icon_5,
                    getDailyForecastPendingIntent(
                        context, location, 4,
                        GeometricWeather.WIDGET_WEEK_PENDING_INTENT_CODE_DAILY_FORECAST_5
                    )
                )
            }
        }
    }
}
