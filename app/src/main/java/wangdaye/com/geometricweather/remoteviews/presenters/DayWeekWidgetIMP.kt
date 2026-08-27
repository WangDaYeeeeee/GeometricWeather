package wangdaye.com.geometricweather.remoteviews.presenters

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.background.receiver.widget.WidgetDayWeekProvider
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.NotificationTextColor
import wangdaye.com.geometricweather.common.basic.models.options.unit.TemperatureUnit
import wangdaye.com.geometricweather.common.basic.models.weather.Base
import wangdaye.com.geometricweather.common.basic.models.weather.Temperature
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.common.utils.helpers.LunarHelper
import wangdaye.com.geometricweather.remoteviews.WidgetHelper
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getCalendarPendingIntent
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getCardBackgroundId
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getCustomSubtitle
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getDailyForecastPendingIntent
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getWeatherPendingIntent
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getWidgetConfig
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.isWeekIconDaytime
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.WidgetColor
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider
import java.util.Date

class DayWeekWidgetIMP : AbstractRemoteViewsPresenter() {

    companion object {
        @JvmStatic
        fun updateWidgetView(context: Context, location: Location) {
            val config = getWidgetConfig(
                context,
                context.getString(R.string.sp_widget_day_week_setting)
            )
            val views = getRemoteViews(
                context, location,
                config.viewStyle, config.cardStyle, config.cardAlpha,
                config.textColor, config.textSize, config.hideSubtitle, config.subtitleData
            )
            AppWidgetManager.getInstance(context).updateAppWidget(
                ComponentName(context, WidgetDayWeekProvider::class.java),
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
            textSize: Int,
            hideSubtitle: Boolean,
            subtitleData: String?
        ): RemoteViews {
            val provider = ResourcesProviderFactory.getNewInstance()
            val dayTime = location.isDaylight
            val settings = SettingsManager.getInstance(context)
            val temperatureUnit = settings.temperatureUnit
            val weekIconMode = settings.widgetWeekIconMode
            val minimalIcon = settings.isWidgetMinimalIconEnabled
            val color = WidgetColor(context, cardStyle ?: "none", textColor ?: "light")
            val views = buildWidgetViewDayPart(
                context, provider, location, temperatureUnit, color, dayTime, textSize,
                minimalIcon, viewStyle, hideSubtitle, subtitleData
            )
            val weather = location.weather ?: return views
            views.setTextViewText(R.id.widget_day_week_week_1, WidgetHelper.getDailyWeek(context, weather, 0))
            views.setTextViewText(R.id.widget_day_week_week_2, WidgetHelper.getDailyWeek(context, weather, 1))
            views.setTextViewText(R.id.widget_day_week_week_3, WidgetHelper.getDailyWeek(context, weather, 2))
            views.setTextViewText(R.id.widget_day_week_week_4, WidgetHelper.getDailyWeek(context, weather, 3))
            views.setTextViewText(R.id.widget_day_week_week_5, WidgetHelper.getDailyWeek(context, weather, 4))
            views.setTextViewText(R.id.widget_day_week_temp_1, getTemp(context, weather, 0, temperatureUnit))
            views.setTextViewText(R.id.widget_day_week_temp_2, getTemp(context, weather, 1, temperatureUnit))
            views.setTextViewText(R.id.widget_day_week_temp_3, getTemp(context, weather, 2, temperatureUnit))
            views.setTextViewText(R.id.widget_day_week_temp_4, getTemp(context, weather, 3, temperatureUnit))
            views.setTextViewText(R.id.widget_day_week_temp_5, getTemp(context, weather, 4, temperatureUnit))
            val weekIconDaytime = isWeekIconDaytime(weekIconMode, dayTime)
            val iconColor = color.getMinimalIconColor()
            views.setImageViewUri(
                R.id.widget_day_week_icon_1,
                getIconDrawableUri(provider, weather, weekIconDaytime, minimalIcon, iconColor, 0)
            )
            views.setImageViewUri(
                R.id.widget_day_week_icon_2,
                getIconDrawableUri(provider, weather, weekIconDaytime, minimalIcon, iconColor, 1)
            )
            views.setImageViewUri(
                R.id.widget_day_week_icon_3,
                getIconDrawableUri(provider, weather, weekIconDaytime, minimalIcon, iconColor, 2)
            )
            views.setImageViewUri(
                R.id.widget_day_week_icon_4,
                getIconDrawableUri(provider, weather, weekIconDaytime, minimalIcon, iconColor, 3)
            )
            views.setImageViewUri(
                R.id.widget_day_week_icon_5,
                getIconDrawableUri(provider, weather, weekIconDaytime, minimalIcon, iconColor, 4)
            )
            if (color.textColor != Color.TRANSPARENT) {
                views.setTextColor(R.id.widget_day_week_week_1, color.textColor)
                views.setTextColor(R.id.widget_day_week_week_2, color.textColor)
                views.setTextColor(R.id.widget_day_week_week_3, color.textColor)
                views.setTextColor(R.id.widget_day_week_week_4, color.textColor)
                views.setTextColor(R.id.widget_day_week_week_5, color.textColor)
                views.setTextColor(R.id.widget_day_week_temp_1, color.textColor)
                views.setTextColor(R.id.widget_day_week_temp_2, color.textColor)
                views.setTextColor(R.id.widget_day_week_temp_3, color.textColor)
                views.setTextColor(R.id.widget_day_week_temp_4, color.textColor)
                views.setTextColor(R.id.widget_day_week_temp_5, color.textColor)
            }
            if (textSize != 100) {
                val contentSize = context.resources.getDimensionPixelSize(R.dimen.widget_content_text_size) *
                    textSize / 100f
                views.setTextViewTextSize(R.id.widget_day_week_week_1, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_day_week_week_2, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_day_week_week_3, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_day_week_week_4, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_day_week_week_5, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_day_week_temp_1, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_day_week_temp_2, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_day_week_temp_3, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_day_week_temp_4, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_day_week_temp_5, TypedValue.COMPLEX_UNIT_PX, contentSize)
            }
            if (color.showCard) {
                views.setImageViewResource(R.id.widget_day_week_card, getCardBackgroundId(color.cardColor))
                views.setInt(R.id.widget_day_week_card, "setImageAlpha", (cardAlpha / 100.0 * 255).toInt())
            }
            setOnClickPendingIntent(context, views, location, subtitleData)
            return views
        }

        private fun buildWidgetViewDayPart(
            context: Context,
            helper: ResourceProvider,
            location: Location,
            temperatureUnit: TemperatureUnit,
            color: WidgetColor,
            dayTime: Boolean,
            textSize: Int,
            minimalIcon: Boolean,
            viewStyle: String?,
            hideSubtitle: Boolean,
            subtitleData: String?
        ): RemoteViews {
            var views = RemoteViews(
                context.packageName,
                if (!color.showCard) R.layout.widget_day_week_symmetry else R.layout.widget_day_week_symmetry_card
            )
            when (viewStyle) {
                "rectangle" -> views = RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_day_week_rectangle else R.layout.widget_day_week_rectangle_card
                )
                "symmetry" -> views = RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_day_week_symmetry else R.layout.widget_day_week_symmetry_card
                )
                "tile" -> views = RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_day_week_tile else R.layout.widget_day_week_tile_card
                )
            }
            val weather = location.weather ?: return views
            views.setImageViewUri(
                R.id.widget_day_week_icon,
                ResourceHelper.getWidgetNotificationIconUri(
                    helper, weather.current.weatherCode, dayTime, minimalIcon, color.getMinimalIconColor()
                )
            )
            views.setTextViewText(
                R.id.widget_day_week_title,
                getTitleText(context, location, viewStyle, temperatureUnit)
            )
            views.setTextViewText(
                R.id.widget_day_week_subtitle,
                getSubtitleText(context, weather, viewStyle, temperatureUnit)
            )
            views.setTextViewText(
                R.id.widget_day_week_time,
                getTimeText(context, location, viewStyle, subtitleData, temperatureUnit)
            )
            if (color.textColor != Color.TRANSPARENT) {
                views.setTextColor(R.id.widget_day_week_title, color.textColor)
                views.setTextColor(R.id.widget_day_week_subtitle, color.textColor)
                views.setTextColor(R.id.widget_day_week_time, color.textColor)
            }
            if (textSize != 100) {
                val contentSize = context.resources.getDimensionPixelSize(R.dimen.widget_content_text_size) *
                    textSize / 100f
                val timeSize = context.resources.getDimensionPixelSize(R.dimen.widget_time_text_size) *
                    textSize / 100f
                views.setTextViewTextSize(R.id.widget_day_week_title, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_day_week_subtitle, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_day_week_time, TypedValue.COMPLEX_UNIT_PX, timeSize)
            }
            views.setViewVisibility(R.id.widget_day_week_time, if (hideSubtitle) View.GONE else View.VISIBLE)
            return views
        }

        @JvmStatic
        fun isEnable(context: Context): Boolean {
            val widgetIds = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, WidgetDayWeekProvider::class.java))
            return widgetIds != null && widgetIds.isNotEmpty()
        }

        private fun getTitleText(
            context: Context,
            location: Location,
            viewStyle: String?,
            unit: TemperatureUnit
        ): String? {
            val weather = location.weather ?: return null
            return when (viewStyle) {
                "rectangle" -> WidgetHelper.buildWidgetDayStyleText(context, weather, unit)[0]
                "symmetry" -> location.getCityName(context) + "\n" +
                    weather.current.temperature.getTemperature(context, unit)
                "tile" -> weather.current.weatherText + " " +
                    weather.current.temperature.getTemperature(context, unit)
                else -> ""
            }
        }

        private fun getSubtitleText(
            context: Context,
            weather: Weather,
            viewStyle: String?,
            unit: TemperatureUnit
        ): String? {
            return when (viewStyle) {
                "rectangle" -> WidgetHelper.buildWidgetDayStyleText(context, weather, unit)[1]
                "tile" -> Temperature.getTrendTemperature(
                    context,
                    weather.dailyForecast[0].night().temperature.temperature,
                    weather.dailyForecast[0].day().temperature.temperature,
                    unit
                )
                "symmetry" -> weather.current.weatherText + "\n" + Temperature.getTrendTemperature(
                    context,
                    weather.dailyForecast[0].night().temperature.temperature,
                    weather.dailyForecast[0].day().temperature.temperature,
                    unit
                )
                else -> ""
            }
        }

        private fun getTimeText(
            context: Context,
            location: Location,
            viewStyle: String?,
            subtitleData: String?,
            unit: TemperatureUnit
        ): String? {
            val weather = location.weather ?: return null
            when (subtitleData) {
                "time" -> when (viewStyle) {
                    "rectangle" -> return location.getCityName(context) + " " +
                        Base.getTime(context, weather.base.updateDate)
                    "symmetry" -> return WidgetHelper.getWeek(context) + " " +
                        Base.getTime(context, weather.base.updateDate)
                    "tile" -> return location.getCityName(context) + " " +
                        WidgetHelper.getWeek(context) + " " +
                        Base.getTime(context, weather.base.updateDate)
                }
                "aqi" -> if (weather.current.airQuality.aqiIndex != null &&
                    weather.current.airQuality.aqiText != null
                ) {
                    return weather.current.airQuality.aqiText +
                        " (" + weather.current.airQuality.aqiIndex + ")"
                }
                "wind" -> return weather.current.wind.direction + " " + weather.current.wind.level
                "lunar" -> when (viewStyle) {
                    "rectangle" -> return location.getCityName(context) + " " +
                        LunarHelper.getLunarDate(Date())
                    "symmetry" -> return WidgetHelper.getWeek(context) + " " +
                        LunarHelper.getLunarDate(Date())
                    "tile" -> return location.getCityName(context) + " " +
                        WidgetHelper.getWeek(context) + " " +
                        LunarHelper.getLunarDate(Date())
                }
                "sensible_time" -> return context.getString(R.string.feels_like) + " " +
                    weather.current.temperature.getShortRealFeeTemperature(context, unit)
            }
            return getCustomSubtitle(context, subtitleData, location, weather)
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
                if (dayTime) weather.dailyForecast[index].day().weatherCode
                else weather.dailyForecast[index].night().weatherCode,
                dayTime, minimalIcon, color
            )
        }

        private fun setOnClickPendingIntent(
            context: Context,
            views: RemoteViews,
            location: Location,
            subtitleData: String?
        ) {
            views.setOnClickPendingIntent(
                R.id.widget_day_week_weather,
                getWeatherPendingIntent(
                    context, location,
                    GeometricWeather.WIDGET_DAY_WEEK_PENDING_INTENT_CODE_WEATHER
                )
            )
            views.setOnClickPendingIntent(
                R.id.widget_day_week_icon_1,
                getDailyForecastPendingIntent(
                    context, location, 0,
                    GeometricWeather.WIDGET_DAY_WEEK_PENDING_INTENT_CODE_DAILY_FORECAST_1
                )
            )
            views.setOnClickPendingIntent(
                R.id.widget_day_week_icon_2,
                getDailyForecastPendingIntent(
                    context, location, 1,
                    GeometricWeather.WIDGET_DAY_WEEK_PENDING_INTENT_CODE_DAILY_FORECAST_2
                )
            )
            views.setOnClickPendingIntent(
                R.id.widget_day_week_icon_3,
                getDailyForecastPendingIntent(
                    context, location, 2,
                    GeometricWeather.WIDGET_DAY_WEEK_PENDING_INTENT_CODE_DAILY_FORECAST_3
                )
            )
            views.setOnClickPendingIntent(
                R.id.widget_day_week_icon_4,
                getDailyForecastPendingIntent(
                    context, location, 3,
                    GeometricWeather.WIDGET_DAY_WEEK_PENDING_INTENT_CODE_DAILY_FORECAST_4
                )
            )
            views.setOnClickPendingIntent(
                R.id.widget_day_week_icon_5,
                getDailyForecastPendingIntent(
                    context, location, 4,
                    GeometricWeather.WIDGET_DAY_WEEK_PENDING_INTENT_CODE_DAILY_FORECAST_5
                )
            )
            if (subtitleData == "lunar") {
                views.setOnClickPendingIntent(
                    R.id.widget_day_week_subtitle,
                    getCalendarPendingIntent(
                        context, GeometricWeather.WIDGET_DAY_WEEK_PENDING_INTENT_CODE_CALENDAR
                    )
                )
            }
        }
    }
}
