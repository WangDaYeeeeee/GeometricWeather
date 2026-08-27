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
import wangdaye.com.geometricweather.background.receiver.widget.WidgetClockDayDetailsProvider
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.unit.RelativeHumidityUnit
import wangdaye.com.geometricweather.common.basic.models.weather.Temperature
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.common.utils.helpers.LunarHelper
import wangdaye.com.geometricweather.remoteviews.WidgetHelper
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getAlarmPendingIntent
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getCalendarPendingIntent
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getCardBackgroundId
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getWeatherPendingIntent
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getWidgetConfig
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.WidgetColor
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory
import java.util.Date

class ClockDayDetailsWidgetIMP : AbstractRemoteViewsPresenter() {

    companion object {
        @JvmStatic
        fun updateWidgetView(context: Context, location: Location) {
            val config = getWidgetConfig(
                context,
                context.getString(R.string.sp_widget_clock_day_details_setting)
            )
            val views = getRemoteViews(
                context, location,
                config.cardStyle, config.cardAlpha, config.textColor, config.textSize, config.clockFont,
                config.hideLunar
            )
            AppWidgetManager.getInstance(context).updateAppWidget(
                ComponentName(context, WidgetClockDayDetailsProvider::class.java),
                views
            )
        }

        @JvmStatic
        fun getRemoteViews(
            context: Context,
            location: Location,
            cardStyle: String?,
            cardAlpha: Int,
            textColor: String?,
            textSize: Int,
            clockFont: String?,
            hideLunar: Boolean
        ): RemoteViews {
            val provider = ResourcesProviderFactory.getNewInstance()
            val dayTime = location.isDaylight
            val settings = SettingsManager.getInstance(context)
            val temperatureUnit = settings.temperatureUnit
            val minimalIcon = settings.isWidgetMinimalIconEnabled
            val color = WidgetColor(context, cardStyle ?: "none", textColor ?: "light")
            val views = RemoteViews(
                context.packageName,
                if (!color.showCard) {
                    R.layout.widget_clock_day_details
                } else {
                    R.layout.widget_clock_day_details_card
                }
            )
            val weather = location.weather ?: return views
            views.setImageViewUri(
                R.id.widget_clock_day_icon,
                ResourceHelper.getWidgetNotificationIconUri(
                    provider,
                    weather.current.weatherCode,
                    dayTime,
                    minimalIcon,
                    color.getMinimalIconColor()
                )
            )
            views.setTextViewText(
                R.id.widget_clock_day_lunar,
                if (settings.language.isChinese && !hideLunar) {
                    " - " + LunarHelper.getLunarDate(Date())
                } else {
                    ""
                }
            )
            views.setTextViewText(
                R.id.widget_clock_day_subtitle,
                location.getCityName(context) + " " +
                    weather.current.temperature.getTemperature(context, temperatureUnit)
            )
            views.setTextViewText(
                R.id.widget_clock_day_todayTemp,
                context.getString(R.string.today) + " " + Temperature.getTrendTemperature(
                    context,
                    weather.dailyForecast[0].night().temperature.temperature,
                    weather.dailyForecast[0].day().temperature.temperature,
                    temperatureUnit
                )
            )
            views.setTextViewText(
                R.id.widget_clock_day_sensibleTemp,
                context.getString(R.string.feels_like) + " " +
                    weather.current.temperature.getRealFeelTemperature(context, temperatureUnit)
            )
            views.setTextViewText(
                R.id.widget_clock_day_aqiHumidity,
                getAQIHumidityTempText(context, weather)
            )
            views.setTextViewText(
                R.id.widget_clock_day_wind,
                weather.current.wind.shortWindDescription
            )
            if (color.textColor != Color.TRANSPARENT) {
                views.setTextColor(R.id.widget_clock_day_clock_light, color.textColor)
                views.setTextColor(R.id.widget_clock_day_clock_normal, color.textColor)
                views.setTextColor(R.id.widget_clock_day_clock_black, color.textColor)
                views.setTextColor(R.id.widget_clock_day_clock_aa_light, color.textColor)
                views.setTextColor(R.id.widget_clock_day_clock_aa_normal, color.textColor)
                views.setTextColor(R.id.widget_clock_day_clock_aa_black, color.textColor)
                views.setTextColor(R.id.widget_clock_day_title, color.textColor)
                views.setTextColor(R.id.widget_clock_day_lunar, color.textColor)
                views.setTextColor(R.id.widget_clock_day_subtitle, color.textColor)
                views.setTextColor(R.id.widget_clock_day_todayTemp, color.textColor)
                views.setTextColor(R.id.widget_clock_day_sensibleTemp, color.textColor)
                views.setTextColor(R.id.widget_clock_day_aqiHumidity, color.textColor)
                views.setTextColor(R.id.widget_clock_day_wind, color.textColor)
            }
            if (textSize != 100) {
                val clockSize = context.resources.getDimensionPixelSize(
                    R.dimen.widget_current_weather_icon_size
                ) * textSize / 100f
                val clockAASize = context.resources.getDimensionPixelSize(R.dimen.widget_aa_text_size) *
                    textSize / 100f
                val contentSize = context.resources.getDimensionPixelSize(R.dimen.widget_content_text_size) *
                    textSize / 100f
                views.setTextViewTextSize(R.id.widget_clock_day_clock_light, TypedValue.COMPLEX_UNIT_PX, clockSize)
                views.setTextViewTextSize(R.id.widget_clock_day_clock_normal, TypedValue.COMPLEX_UNIT_PX, clockSize)
                views.setTextViewTextSize(R.id.widget_clock_day_clock_black, TypedValue.COMPLEX_UNIT_PX, clockSize)
                views.setTextViewTextSize(R.id.widget_clock_day_clock_aa_light, TypedValue.COMPLEX_UNIT_PX, clockAASize)
                views.setTextViewTextSize(R.id.widget_clock_day_clock_aa_normal, TypedValue.COMPLEX_UNIT_PX, clockAASize)
                views.setTextViewTextSize(R.id.widget_clock_day_clock_aa_black, TypedValue.COMPLEX_UNIT_PX, clockAASize)
                views.setTextViewTextSize(R.id.widget_clock_day_title, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_clock_day_lunar, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_clock_day_subtitle, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_clock_day_todayTemp, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_clock_day_sensibleTemp, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_clock_day_aqiHumidity, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_clock_day_wind, TypedValue.COMPLEX_UNIT_PX, contentSize)
            }
            if (color.showCard) {
                views.setImageViewResource(R.id.widget_clock_day_card, getCardBackgroundId(color.cardColor))
                views.setInt(R.id.widget_clock_day_card, "setImageAlpha", (cardAlpha / 100.0 * 255).toInt())
            }
            when (clockFont ?: "light") {
                "light" -> {
                    views.setViewVisibility(R.id.widget_clock_day_clock_lightContainer, View.VISIBLE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_normalContainer, View.GONE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_blackContainer, View.GONE)
                }
                "normal" -> {
                    views.setViewVisibility(R.id.widget_clock_day_clock_lightContainer, View.GONE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_normalContainer, View.VISIBLE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_blackContainer, View.GONE)
                }
                "black" -> {
                    views.setViewVisibility(R.id.widget_clock_day_clock_lightContainer, View.GONE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_normalContainer, View.GONE)
                    views.setViewVisibility(R.id.widget_clock_day_clock_blackContainer, View.VISIBLE)
                }
            }
            setOnClickPendingIntent(context, views, location)
            return views
        }

        @JvmStatic
        fun isEnable(context: Context): Boolean {
            val widgetIds = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, WidgetClockDayDetailsProvider::class.java))
            return widgetIds != null && widgetIds.isNotEmpty()
        }

        private fun getAQIHumidityTempText(context: Context, weather: Weather): String {
            return if (weather.current.airQuality.aqiIndex != null &&
                weather.current.airQuality.aqiText != null
            ) {
                "AQI " + weather.current.airQuality.aqiIndex +
                    " (" + weather.current.airQuality.aqiText + ")"
            } else {
                context.getString(R.string.humidity) + " " +
                    RelativeHumidityUnit.PERCENT.getValueText(
                        context,
                        WidgetHelper.getNonNullValue(weather.current.relativeHumidity, 0f).toInt()
                    )
            }
        }

        private fun setOnClickPendingIntent(context: Context, views: RemoteViews, location: Location) {
            views.setOnClickPendingIntent(
                R.id.widget_clock_day_weather,
                getWeatherPendingIntent(
                    context, location,
                    GeometricWeather.WIDGET_CLOCK_DAY_DETAILS_PENDING_INTENT_CODE_WEATHER
                )
            )
            views.setOnClickPendingIntent(
                R.id.widget_clock_day_clock_light,
                getAlarmPendingIntent(
                    context, GeometricWeather.WIDGET_CLOCK_DAY_DETAILS_PENDING_INTENT_CODE_CLOCK_LIGHT
                )
            )
            views.setOnClickPendingIntent(
                R.id.widget_clock_day_clock_normal,
                getAlarmPendingIntent(
                    context, GeometricWeather.WIDGET_CLOCK_DAY_DETAILS_PENDING_INTENT_CODE_CLOCK_NORMAL
                )
            )
            views.setOnClickPendingIntent(
                R.id.widget_clock_day_clock_black,
                getAlarmPendingIntent(
                    context, GeometricWeather.WIDGET_CLOCK_DAY_DETAILS_PENDING_INTENT_CODE_CLOCK_BLACK
                )
            )
            views.setOnClickPendingIntent(
                R.id.widget_clock_day_title,
                getCalendarPendingIntent(
                    context, GeometricWeather.WIDGET_CLOCK_DAY_DETAILS_PENDING_INTENT_CODE_CALENDAR
                )
            )
        }
    }
}
