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
import wangdaye.com.geometricweather.background.receiver.widget.WidgetDayProvider
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.unit.TemperatureUnit
import wangdaye.com.geometricweather.common.basic.models.weather.Base
import wangdaye.com.geometricweather.common.basic.models.weather.Temperature
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.common.utils.helpers.LunarHelper
import wangdaye.com.geometricweather.remoteviews.WidgetHelper
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
import kotlin.math.abs

class DayWidgetIMP : AbstractRemoteViewsPresenter() {

    companion object {
        @JvmStatic
        fun updateWidgetView(context: Context, location: Location) {
            val config = getWidgetConfig(
                context,
                context.getString(R.string.sp_widget_day_setting)
            )
            val views = getRemoteViews(
                context, location,
                config.viewStyle, config.cardStyle, config.cardAlpha,
                config.textColor, config.textSize, config.hideSubtitle, config.subtitleData
            )
            AppWidgetManager.getInstance(context).updateAppWidget(
                ComponentName(context, WidgetDayProvider::class.java),
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
            val dayTime = location.isDaylight
            val settings = SettingsManager.getInstance(context)
            val temperatureUnit = settings.temperatureUnit
            val minimalIcon = settings.isWidgetMinimalIconEnabled
            val color = if (viewStyle == "pixel" || viewStyle == "nano"
                || viewStyle == "oreo" || viewStyle == "oreo_google_sans"
                || viewStyle == "temp"
            ) {
                WidgetColor(context, "none", textColor ?: "light")
            } else {
                WidgetColor(context, cardStyle ?: "none", textColor ?: "light")
            }
            val views = buildWidgetView(
                context, location, temperatureUnit,
                color,
                dayTime, minimalIcon,
                viewStyle, textSize,
                hideSubtitle, subtitleData
            )
            val weather = location.weather ?: return views
            if (color.showCard) {
                views.setImageViewResource(
                    R.id.widget_day_card,
                    getCardBackgroundId(color.cardColor)
                )
                views.setInt(
                    R.id.widget_day_card,
                    "setImageAlpha",
                    (cardAlpha / 100.0 * 255).toInt()
                )
            }
            setOnClickPendingIntent(context, views, location, viewStyle, subtitleData)
            return views
        }

        private fun buildWidgetView(
            context: Context,
            location: Location,
            temperatureUnit: TemperatureUnit,
            color: WidgetColor,
            dayTime: Boolean,
            minimalIcon: Boolean,
            viewStyle: String?,
            textSize: Int,
            hideSubtitle: Boolean,
            subtitleData: String?
        ): RemoteViews {
            var views = RemoteViews(
                context.packageName,
                if (!color.showCard) R.layout.widget_day_symmetry else R.layout.widget_day_symmetry_card
            )
            when (viewStyle) {
                "rectangle" -> views = RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_day_rectangle else R.layout.widget_day_rectangle_card
                )
                "symmetry" -> views = RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_day_symmetry else R.layout.widget_day_symmetry_card
                )
                "tile" -> views = RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_day_tile else R.layout.widget_day_tile_card
                )
                "mini" -> views = RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_day_mini else R.layout.widget_day_mini_card
                )
                "nano" -> views = RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_day_nano else R.layout.widget_day_nano_card
                )
                "pixel" -> views = RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_day_pixel else R.layout.widget_day_pixel_card
                )
                "vertical" -> views = RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_day_vertical else R.layout.widget_day_vertical_card
                )
                "oreo" -> views = RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_day_oreo else R.layout.widget_day_oreo_card
                )
                "oreo_google_sans" -> views = RemoteViews(
                    context.packageName,
                    if (!color.showCard) {
                        R.layout.widget_day_oreo_google_sans
                    } else {
                        R.layout.widget_day_oreo_google_sans_card
                    }
                )
                "temp" -> views = RemoteViews(
                    context.packageName,
                    if (!color.showCard) R.layout.widget_day_temp else R.layout.widget_day_temp_card
                )
            }
            val weather = location.weather ?: return views
            val provider = ResourcesProviderFactory.getNewInstance()
            views.setImageViewUri(
                R.id.widget_day_icon,
                ResourceHelper.getWidgetNotificationIconUri(
                    provider,
                    weather.current.weatherCode,
                    dayTime,
                    minimalIcon,
                    color.getMinimalIconColor()
                )
            )
            if (viewStyle != "oreo" && viewStyle != "oreo_google_sans") {
                views.setTextViewText(
                    R.id.widget_day_title,
                    getTitleText(context, location, viewStyle, temperatureUnit)
                )
            }
            if (viewStyle == "vertical") {
                val negative = temperatureUnit.getValueWithoutUnit(
                    weather.current.temperature.temperature
                ) < 0
                views.setViewVisibility(
                    R.id.widget_day_sign,
                    if (negative) View.VISIBLE else View.GONE
                )
            }
            views.setTextViewText(
                R.id.widget_day_subtitle,
                getSubtitleText(context, weather, viewStyle, temperatureUnit)
            )
            if (viewStyle != "pixel") {
                views.setTextViewText(
                    R.id.widget_day_time,
                    getTimeText(context, location, weather, viewStyle, subtitleData, temperatureUnit)
                )
            }
            if (color.textColor != Color.TRANSPARENT) {
                views.setTextColor(R.id.widget_day_title, color.textColor)
                views.setTextColor(R.id.widget_day_sign, color.textColor)
                views.setTextColor(R.id.widget_day_symbol, color.textColor)
                views.setTextColor(R.id.widget_day_subtitle, color.textColor)
                views.setTextColor(R.id.widget_day_time, color.textColor)
            }
            if (textSize != 100) {
                val signSymbolSize = context.resources.getDimensionPixelSize(
                    R.dimen.widget_current_weather_icon_size
                ) * textSize / 100f
                views.setTextViewTextSize(
                    R.id.widget_day_title, TypedValue.COMPLEX_UNIT_PX,
                    getTitleSize(context, viewStyle) * textSize / 100f
                )
                views.setTextViewTextSize(R.id.widget_day_sign, TypedValue.COMPLEX_UNIT_PX, signSymbolSize)
                views.setTextViewTextSize(R.id.widget_day_symbol, TypedValue.COMPLEX_UNIT_PX, signSymbolSize)
                views.setTextViewTextSize(
                    R.id.widget_day_subtitle, TypedValue.COMPLEX_UNIT_PX,
                    getSubtitleSize(context, viewStyle) * textSize / 100f
                )
                views.setTextViewTextSize(
                    R.id.widget_day_time, TypedValue.COMPLEX_UNIT_PX,
                    getTimeSize(context, viewStyle) * textSize / 100f
                )
            }
            views.setViewVisibility(R.id.widget_day_time, if (hideSubtitle) View.GONE else View.VISIBLE)
            return views
        }

        @JvmStatic
        fun isEnable(context: Context): Boolean {
            val widgetIds = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, WidgetDayProvider::class.java))
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
                "tile", "mini" -> weather.current.weatherText + " " +
                    weather.current.temperature.getTemperature(context, unit)
                "nano", "pixel" -> weather.current.temperature.getTemperature(context, unit)
                "temp" -> weather.current.temperature.getShortTemperature(context, unit)
                "vertical" -> abs(
                    unit.getValueWithoutUnit(weather.current.temperature.temperature)
                ).toString()
                else -> ""
            }
        }

        private fun getSubtitleText(
            context: Context,
            weather: Weather,
            viewStyle: String?,
            unit: TemperatureUnit
        ): String {
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
                "vertical" -> weather.current.weatherText + " " + Temperature.getTrendTemperature(
                    context,
                    weather.dailyForecast[0].night().temperature.temperature,
                    weather.dailyForecast[0].day().temperature.temperature,
                    unit
                )
                "oreo" -> weather.current.temperature.getTemperature(context, unit) ?: ""
                "oreo_google_sans" -> unit.getLongValueText(
                    context, weather.current.temperature.temperature
                )
                else -> ""
            }
        }

        private fun getTimeText(
            context: Context,
            location: Location,
            weather: Weather,
            viewStyle: String?,
            subtitleData: String?,
            unit: TemperatureUnit
        ): String {
            when (subtitleData) {
                "time" -> when (viewStyle) {
                    "rectangle" -> return location.getCityName(context) + " " +
                        Base.getTime(context, weather.base.updateDate)
                    "symmetry" -> return WidgetHelper.getWeek(context) + " " +
                        Base.getTime(context, weather.base.updateDate)
                    "tile", "mini", "vertical" -> return location.getCityName(context) +
                        " " + WidgetHelper.getWeek(context) +
                        " " + Base.getTime(context, weather.base.updateDate)
                }
                "aqi" -> if (weather.current.airQuality.aqiIndex != null
                    && weather.current.airQuality.aqiText != null
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
                    "tile", "mini", "vertical" -> return location.getCityName(context) +
                        " " + WidgetHelper.getWeek(context) +
                        " " + LunarHelper.getLunarDate(Date())
                }
                "sensible_time" -> return context.getString(R.string.feels_like) + " " +
                    weather.current.temperature.getShortRealFeeTemperature(context, unit)
            }
            return getCustomSubtitle(context, subtitleData, location, weather)
        }

        private fun getTitleSize(context: Context, viewStyle: String?): Float {
            return when (viewStyle) {
                "rectangle", "symmetry", "tile" ->
                    context.resources.getDimensionPixelSize(R.dimen.widget_content_text_size).toFloat()
                "mini", "nano" ->
                    context.resources.getDimensionPixelSize(R.dimen.widget_subtitle_text_size).toFloat()
                "pixel" ->
                    context.resources.getDimensionPixelSize(R.dimen.widget_design_title_text_size).toFloat()
                "vertical" ->
                    context.resources.getDimensionPixelSize(R.dimen.widget_current_weather_icon_size).toFloat()
                "oreo", "oreo_google_sans", "temp" ->
                    context.resources.getDimensionPixelSize(R.dimen.widget_large_title_text_size).toFloat()
                else -> 0f
            }
        }

        private fun getSubtitleSize(context: Context, viewStyle: String?): Float {
            return when (viewStyle) {
                "rectangle", "symmetry", "tile", "vertical" ->
                    context.resources.getDimensionPixelSize(R.dimen.widget_content_text_size).toFloat()
                "oreo", "oreo_google_sans" ->
                    context.resources.getDimensionPixelSize(R.dimen.widget_large_title_text_size).toFloat()
                else -> 0f
            }
        }

        private fun getTimeSize(context: Context, viewStyle: String?): Float {
            return when (viewStyle) {
                "rectangle", "symmetry", "tile", "vertical", "mini" ->
                    context.resources.getDimensionPixelSize(R.dimen.widget_time_text_size).toFloat()
                "pixel" ->
                    context.resources.getDimensionPixelSize(R.dimen.widget_subtitle_text_size).toFloat()
                else -> 0f
            }
        }

        private fun setOnClickPendingIntent(
            context: Context,
            views: RemoteViews,
            location: Location,
            viewStyle: String?,
            subtitleData: String?
        ) {
            views.setOnClickPendingIntent(
                R.id.widget_day_weather,
                getWeatherPendingIntent(
                    context,
                    location,
                    GeometricWeather.WIDGET_DAY_PENDING_INTENT_CODE_WEATHER
                )
            )
            if (viewStyle == "oreo" || viewStyle == "oreo_google_sans") {
                views.setOnClickPendingIntent(
                    R.id.widget_day_title,
                    getCalendarPendingIntent(
                        context,
                        GeometricWeather.WIDGET_DAY_PENDING_INTENT_CODE_CALENDAR
                    )
                )
            }
            if (viewStyle == "pixel" || subtitleData == "lunar") {
                views.setOnClickPendingIntent(
                    R.id.widget_day_time,
                    getCalendarPendingIntent(
                        context,
                        GeometricWeather.WIDGET_DAY_PENDING_INTENT_CODE_CALENDAR
                    )
                )
            }
        }
    }
}
