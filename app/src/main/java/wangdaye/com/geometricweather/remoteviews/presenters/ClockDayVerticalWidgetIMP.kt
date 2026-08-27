package wangdaye.com.geometricweather.remoteviews.presenters

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.background.receiver.widget.WidgetClockDayVerticalProvider
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.unit.TemperatureUnit
import wangdaye.com.geometricweather.common.basic.models.weather.Base
import wangdaye.com.geometricweather.common.basic.models.weather.Temperature
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import wangdaye.com.geometricweather.common.utils.helpers.LunarHelper
import wangdaye.com.geometricweather.remoteviews.WidgetHelper
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getAlarmPendingIntent
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getCalendarPendingIntent
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getCardBackgroundId
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getCustomSubtitle
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getWeatherPendingIntent
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getWidgetConfig
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.WidgetColor
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory
import java.util.Date

class ClockDayVerticalWidgetIMP : AbstractRemoteViewsPresenter() {

    companion object {
        @JvmStatic
        fun updateWidgetView(context: Context, location: Location) {
            val config = getWidgetConfig(
                context,
                context.getString(R.string.sp_widget_clock_day_vertical_setting)
            )
            val views = getRemoteViews(
                context, location,
                config.viewStyle, config.cardStyle, config.cardAlpha, config.textColor, config.textSize,
                config.hideSubtitle, config.subtitleData, config.clockFont
            )
            AppWidgetManager.getInstance(context).updateAppWidget(
                ComponentName(context, WidgetClockDayVerticalProvider::class.java),
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
            subtitleData: String?,
            clockFont: String?
        ): RemoteViews {
            val dayTime = location.isDaylight
            val settings = SettingsManager.getInstance(context)
            val temperatureUnit = settings.temperatureUnit
            val minimalIcon = settings.isWidgetMinimalIconEnabled
            val color = WidgetColor(context, cardStyle ?: "none", textColor ?: "light")
            val views = buildWidgetViewDayPart(
                context, location, temperatureUnit, color, dayTime, textSize,
                minimalIcon, clockFont, viewStyle, hideSubtitle, subtitleData
            )
            if (color.showCard) {
                views.setImageViewResource(R.id.widget_clock_day_card, getCardBackgroundId(color.cardColor))
                views.setInt(R.id.widget_clock_day_card, "setImageAlpha", (cardAlpha / 100.0 * 255).toInt())
            }
            setOnClickPendingIntent(context, views, location, subtitleData)
            return views
        }

        private fun buildWidgetViewDayPart(
            context: Context,
            location: Location,
            temperatureUnit: TemperatureUnit,
            color: WidgetColor,
            dayTime: Boolean,
            textSize: Int,
            minimalIcon: Boolean,
            clockFont: String?,
            viewStyle: String?,
            hideSubtitle: Boolean,
            subtitleData: String?
        ): RemoteViews {
            val weather = location.weather
            var views = RemoteViews(
                context.packageName,
                if (!color.showCard) R.layout.widget_clock_day_symmetry else R.layout.widget_clock_day_symmetry_card
            )
            when (viewStyle) {
                "rectangle" -> views = RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_clock_day_rectangle else R.layout.widget_clock_day_rectangle_card
                )
                "symmetry" -> views = RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_clock_day_symmetry else R.layout.widget_clock_day_symmetry_card
                )
                "tile" -> views = RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_clock_day_tile else R.layout.widget_clock_day_tile_card
                )
                "mini" -> views = RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_clock_day_mini else R.layout.widget_clock_day_mini_card
                )
                "vertical" -> views = RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_clock_day_vertical else R.layout.widget_clock_day_vertical_card
                )
                "temp" -> views = RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_clock_day_temp else R.layout.widget_clock_day_temp_card
                )
            }
            if (weather == null) {
                return views
            }
            val provider = ResourcesProviderFactory.getNewInstance()
            views.setImageViewUri(
                R.id.widget_clock_day_icon,
                ResourceHelper.getWidgetNotificationIconUri(
                    provider, weather.current.weatherCode, dayTime, minimalIcon, color.getMinimalIconColor()
                )
            )
            views.setTextViewText(
                R.id.widget_clock_day_title,
                getTitleText(context, location, viewStyle, temperatureUnit)
            )
            views.setTextViewText(
                R.id.widget_clock_day_subtitle,
                getSubtitleText(context, weather, viewStyle, temperatureUnit)
            )
            views.setTextViewText(
                R.id.widget_clock_day_time,
                getTimeText(context, location, viewStyle, subtitleData, temperatureUnit)
            )
            if (color.textColor != Color.TRANSPARENT) {
                views.setTextColor(R.id.widget_clock_day_clock_light, color.textColor)
                views.setTextColor(R.id.widget_clock_day_clock_normal, color.textColor)
                views.setTextColor(R.id.widget_clock_day_clock_black, color.textColor)
                views.setTextColor(R.id.widget_clock_day_clock_aa_light, color.textColor)
                views.setTextColor(R.id.widget_clock_day_clock_aa_normal, color.textColor)
                views.setTextColor(R.id.widget_clock_day_clock_aa_black, color.textColor)
                views.setTextColor(R.id.widget_clock_day_clock_1_light, color.textColor)
                views.setTextColor(R.id.widget_clock_day_clock_1_normal, color.textColor)
                views.setTextColor(R.id.widget_clock_day_clock_1_black, color.textColor)
                views.setTextColor(R.id.widget_clock_day_clock_2_light, color.textColor)
                views.setTextColor(R.id.widget_clock_day_clock_2_normal, color.textColor)
                views.setTextColor(R.id.widget_clock_day_clock_2_black, color.textColor)
                views.setTextColor(R.id.widget_clock_day_date, color.textColor)
                views.setTextColor(R.id.widget_clock_day_title, color.textColor)
                views.setTextColor(R.id.widget_clock_day_subtitle, color.textColor)
                views.setTextColor(R.id.widget_clock_day_time, color.textColor)
            }
            if (textSize != 100) {
                val clockSize = context.resources.getDimensionPixelSize(
                    R.dimen.widget_current_weather_icon_size
                ) * textSize / 100f
                val clockAASize = context.resources.getDimensionPixelSize(R.dimen.widget_aa_text_size) *
                    textSize / 100f
                val verticalClockSize = DisplayUtils.spToPx(context, 64) * textSize / 100f
                views.setTextViewTextSize(R.id.widget_clock_day_clock_light, TypedValue.COMPLEX_UNIT_PX, clockSize)
                views.setTextViewTextSize(R.id.widget_clock_day_clock_normal, TypedValue.COMPLEX_UNIT_PX, clockSize)
                views.setTextViewTextSize(R.id.widget_clock_day_clock_black, TypedValue.COMPLEX_UNIT_PX, clockSize)
                views.setTextViewTextSize(R.id.widget_clock_day_clock_aa_light, TypedValue.COMPLEX_UNIT_PX, clockAASize)
                views.setTextViewTextSize(R.id.widget_clock_day_clock_aa_normal, TypedValue.COMPLEX_UNIT_PX, clockAASize)
                views.setTextViewTextSize(R.id.widget_clock_day_clock_aa_black, TypedValue.COMPLEX_UNIT_PX, clockAASize)
                views.setTextViewTextSize(R.id.widget_clock_day_clock_1_light, TypedValue.COMPLEX_UNIT_PX, verticalClockSize)
                views.setTextViewTextSize(R.id.widget_clock_day_clock_1_normal, TypedValue.COMPLEX_UNIT_PX, verticalClockSize)
                views.setTextViewTextSize(R.id.widget_clock_day_clock_1_black, TypedValue.COMPLEX_UNIT_PX, verticalClockSize)
                views.setTextViewTextSize(R.id.widget_clock_day_clock_2_light, TypedValue.COMPLEX_UNIT_PX, verticalClockSize)
                views.setTextViewTextSize(R.id.widget_clock_day_clock_2_normal, TypedValue.COMPLEX_UNIT_PX, verticalClockSize)
                views.setTextViewTextSize(R.id.widget_clock_day_clock_2_black, TypedValue.COMPLEX_UNIT_PX, verticalClockSize)
                views.setTextViewTextSize(
                    R.id.widget_clock_day_date, TypedValue.COMPLEX_UNIT_PX,
                    getTitleSize(context, viewStyle) * textSize / 100f
                )
                views.setTextViewTextSize(
                    R.id.widget_clock_day_title, TypedValue.COMPLEX_UNIT_PX,
                    getTitleSize(context, viewStyle) * textSize / 100f
                )
                views.setTextViewTextSize(
                    R.id.widget_clock_day_subtitle, TypedValue.COMPLEX_UNIT_PX,
                    getSubtitleSize(context, viewStyle) * textSize / 100f
                )
                views.setTextViewTextSize(
                    R.id.widget_clock_day_time, TypedValue.COMPLEX_UNIT_PX,
                    getTimeSize(context, viewStyle) * textSize / 100f
                )
            }
            views.setViewVisibility(R.id.widget_clock_day_time, if (hideSubtitle) View.GONE else View.VISIBLE)
            when (clockFont ?: "light") {
                "light" -> {
                    views.setViewVisibility(R.id.widget_clock_day_clock_lightContainer, View.VISIBLE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_normalContainer, View.GONE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_blackContainer, View.GONE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_analogContainer_auto, View.GONE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_analogContainer_light, View.GONE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_analogContainer_dark, View.GONE)
                }
                "normal" -> {
                    views.setViewVisibility(R.id.widget_clock_day_clock_lightContainer, View.GONE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_normalContainer, View.VISIBLE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_blackContainer, View.GONE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_analogContainer_auto, View.GONE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_analogContainer_light, View.GONE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_analogContainer_dark, View.GONE)
                }
                "black" -> {
                    views.setViewVisibility(R.id.widget_clock_day_clock_lightContainer, View.GONE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_normalContainer, View.GONE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_blackContainer, View.VISIBLE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_analogContainer_auto, View.GONE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_analogContainer_light, View.GONE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_analogContainer_dark, View.GONE)
                }
                "analog" -> {
                    views.setViewVisibility(R.id.widget_clock_day_clock_lightContainer, View.GONE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_normalContainer, View.GONE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_blackContainer, View.GONE)
                    views.setViewVisibility(
                        R.id.widget_clock_day_clock_analogContainer_auto,
                        if (color.showCard && color.cardColor == WidgetColor.ColorType.AUTO) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    )
                    views.setViewVisibility(
                        R.id.widget_clock_day_clock_analogContainer_light,
                        if (color.showCard && color.cardColor == WidgetColor.ColorType.AUTO) {
                            View.GONE
                        } else if (color.darkText) {
                            View.GONE
                        } else {
                            View.VISIBLE
                        }
                    )
                    views.setViewVisibility(
                        R.id.widget_clock_day_clock_analogContainer_dark,
                        if (color.showCard && color.cardColor == WidgetColor.ColorType.AUTO) {
                            View.GONE
                        } else if (color.darkText) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    )
                }
            }
            return views
        }

        @JvmStatic
        fun isEnable(context: Context): Boolean {
            val widgetIds = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, WidgetClockDayVerticalProvider::class.java))
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
                "vertical", "tile" -> weather.current.weatherText + " " +
                    weather.current.temperature.getTemperature(context, unit)
                "mini" -> weather.current.weatherText
                "temp" -> weather.current.temperature.getShortTemperature(context, unit)
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
                "symmetry" -> weather.current.weatherText + "\n" + Temperature.getTrendTemperature(
                    context,
                    weather.dailyForecast[0].night().temperature.temperature,
                    weather.dailyForecast[0].day().temperature.temperature,
                    unit
                )
                "tile", "temp" -> Temperature.getTrendTemperature(
                    context,
                    weather.dailyForecast[0].night().temperature.temperature,
                    weather.dailyForecast[0].day().temperature.temperature,
                    unit
                )
                "mini" -> weather.current.temperature.getTemperature(context, unit)
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
                    "tile", "vertical" -> return location.getCityName(context) +
                        " " + WidgetHelper.getWeek(context) +
                        " " + Base.getTime(context, weather.base.updateDate)
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
                    "tile", "vertical" -> return location.getCityName(context) +
                        " " + WidgetHelper.getWeek(context) +
                        " " + LunarHelper.getLunarDate(Date())
                }
                "sensible_time" -> return context.getString(R.string.feels_like) + " " +
                    weather.current.temperature.getRealFeelTemperature(context, unit)
            }
            return getCustomSubtitle(context, subtitleData, location, weather)
        }

        private fun getTitleSize(context: Context, viewStyle: String?): Float {
            return when (viewStyle) {
                "rectangle", "symmetry", "tile", "mini", "vertical" ->
                    context.resources.getDimensionPixelSize(R.dimen.widget_content_text_size).toFloat()
                "temp" ->
                    context.resources.getDimensionPixelSize(R.dimen.widget_title_text_size).toFloat()
                else -> 0f
            }
        }

        private fun getSubtitleSize(context: Context, viewStyle: String?): Float {
            return when (viewStyle) {
                "rectangle", "symmetry", "tile", "mini" ->
                    context.resources.getDimensionPixelSize(R.dimen.widget_content_text_size).toFloat()
                "temp" ->
                    context.resources.getDimensionPixelSize(R.dimen.widget_subtitle_text_size).toFloat()
                else -> 0f
            }
        }

        private fun getTimeSize(context: Context, viewStyle: String?): Float {
            return when (viewStyle) {
                "rectangle", "symmetry", "tile", "vertical", "mini" ->
                    context.resources.getDimensionPixelSize(R.dimen.widget_time_text_size).toFloat()
                else -> 0f
            }
        }

        private fun setOnClickPendingIntent(
            context: Context,
            views: RemoteViews,
            location: Location,
            subtitleData: String?
        ) {
            views.setOnClickPendingIntent(
                R.id.widget_clock_day_weather,
                getWeatherPendingIntent(
                    context, location,
                    GeometricWeather.WIDGET_CLOCK_DAY_VERTICAL_PENDING_INTENT_CODE_WEATHER
                )
            )
            views.setOnClickPendingIntent(
                R.id.widget_clock_day_clock_light,
                getAlarmPendingIntent(
                    context, GeometricWeather.WIDGET_CLOCK_DAY_VERTICAL_PENDING_INTENT_CODE_CLOCK_LIGHT
                )
            )
            views.setOnClickPendingIntent(
                R.id.widget_clock_day_clock_normal,
                getAlarmPendingIntent(
                    context, GeometricWeather.WIDGET_CLOCK_DAY_VERTICAL_PENDING_INTENT_CODE_CLOCK_NORMAL
                )
            )
            views.setOnClickPendingIntent(
                R.id.widget_clock_day_clock_black,
                getAlarmPendingIntent(
                    context, GeometricWeather.WIDGET_CLOCK_DAY_VERTICAL_PENDING_INTENT_CODE_CLOCK_BLACK
                )
            )
            views.setOnClickPendingIntent(
                R.id.widget_clock_day_clock_1_light,
                getAlarmPendingIntent(
                    context, GeometricWeather.WIDGET_CLOCK_DAY_VERTICAL_PENDING_INTENT_CODE_CLOCK_1_LIGHT
                )
            )
            views.setOnClickPendingIntent(
                R.id.widget_clock_day_clock_1_normal,
                getAlarmPendingIntent(
                    context, GeometricWeather.WIDGET_CLOCK_DAY_VERTICAL_PENDING_INTENT_CODE_CLOCK_1_NORMAL
                )
            )
            views.setOnClickPendingIntent(
                R.id.widget_clock_day_clock_1_black,
                getAlarmPendingIntent(
                    context, GeometricWeather.WIDGET_CLOCK_DAY_VERTICAL_PENDING_INTENT_CODE_CLOCK_1_BLACK
                )
            )
            views.setOnClickPendingIntent(
                R.id.widget_clock_day_clock_2_light,
                getAlarmPendingIntent(
                    context, GeometricWeather.WIDGET_CLOCK_DAY_VERTICAL_PENDING_INTENT_CODE_CLOCK_2_LIGHT
                )
            )
            views.setOnClickPendingIntent(
                R.id.widget_clock_day_clock_2_normal,
                getAlarmPendingIntent(
                    context, GeometricWeather.WIDGET_CLOCK_DAY_VERTICAL_PENDING_INTENT_CODE_CLOCK_2_NORMAL
                )
            )
            views.setOnClickPendingIntent(
                R.id.widget_clock_day_clock_2_black,
                getAlarmPendingIntent(
                    context, GeometricWeather.WIDGET_CLOCK_DAY_VERTICAL_PENDING_INTENT_CODE_CLOCK_2_BLACK
                )
            )
            if (subtitleData == "lunar") {
                views.setOnClickPendingIntent(
                    R.id.widget_clock_day_time,
                    getCalendarPendingIntent(
                        context, GeometricWeather.WIDGET_DAY_PENDING_INTENT_CODE_CALENDAR
                    )
                )
            }
        }
    }
}
