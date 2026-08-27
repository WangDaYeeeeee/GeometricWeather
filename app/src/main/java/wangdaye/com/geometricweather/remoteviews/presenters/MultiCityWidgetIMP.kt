package wangdaye.com.geometricweather.remoteviews.presenters

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.background.receiver.widget.WidgetMultiCityProvider
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.weather.Temperature
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getCardBackgroundId
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getWeatherPendingIntent
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getWidgetConfig
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.WidgetColor
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory

class MultiCityWidgetIMP : AbstractRemoteViewsPresenter() {

    companion object {
        @JvmStatic
        fun updateWidgetView(context: Context, locationList: List<Location>) {
            val config = getWidgetConfig(context, context.getString(R.string.sp_widget_multi_city))
            val views = getRemoteViews(
                context, locationList,
                config.cardStyle, config.cardAlpha, config.textColor, config.textSize
            )
            AppWidgetManager.getInstance(context).updateAppWidget(
                ComponentName(context, WidgetMultiCityProvider::class.java),
                views
            )
        }

        @JvmStatic
        fun getRemoteViews(
            context: Context,
            locationList: List<Location>,
            cardStyle: String?,
            cardAlpha: Int,
            textColor: String?,
            textSize: Int
        ): RemoteViews {
            var location = locationList[0]
            var weather = location.weather
            var dayTime = location.isDaylight
            val provider = ResourcesProviderFactory.getNewInstance()
            val settings = SettingsManager.getInstance(context)
            val temperatureUnit = settings.temperatureUnit
            val minimalIcon = settings.isWidgetMinimalIconEnabled
            val color = WidgetColor(context, cardStyle ?: "none", textColor ?: "light")
            val views = RemoteViews(
                context.packageName,
                if (!color.showCard) {
                    R.layout.widget_multi_city_horizontal
                } else {
                    R.layout.widget_multi_city_horizontal_card
                }
            )
            views.setViewVisibility(R.id.widget_multi_city_horizontal_weather_1, View.VISIBLE)
            if (weather != null) {
                views.setTextViewText(R.id.widget_multi_city_horizontal_title_1, location.getCityName(context))
                views.setImageViewUri(
                    R.id.widget_multi_city_horizontal_icon_1,
                    ResourceHelper.getWidgetNotificationIconUri(
                        provider,
                        if (dayTime) weather.dailyForecast[0].day().weatherCode
                        else weather.dailyForecast[0].night().weatherCode,
                        dayTime, minimalIcon, color.getMinimalIconColor()
                    )
                )
                views.setTextViewText(
                    R.id.widget_multi_city_horizontal_content_1,
                    Temperature.getTrendTemperature(
                        context,
                        weather.dailyForecast[0].night().temperature.temperature,
                        weather.dailyForecast[0].day().temperature.temperature,
                        temperatureUnit
                    )
                )
            }
            setOnClickPendingIntent(context, views, location, R.id.widget_multi_city_horizontal_weather_1, 0)
            if (locationList.size >= 2) {
                location = locationList[1]
                weather = location.weather
                dayTime = location.isDaylight
                views.setViewVisibility(R.id.widget_multi_city_horizontal_weather_2, View.VISIBLE)
                if (weather != null) {
                    views.setTextViewText(R.id.widget_multi_city_horizontal_title_2, location.getCityName(context))
                    views.setImageViewUri(
                        R.id.widget_multi_city_horizontal_icon_2,
                        ResourceHelper.getWidgetNotificationIconUri(
                            provider,
                            if (dayTime) weather.dailyForecast[0].day().weatherCode
                            else weather.dailyForecast[0].night().weatherCode,
                            dayTime, minimalIcon, color.getMinimalIconColor()
                        )
                    )
                    views.setTextViewText(
                        R.id.widget_multi_city_horizontal_content_2,
                        Temperature.getTrendTemperature(
                            context,
                            weather.dailyForecast[0].night().temperature.temperature,
                            weather.dailyForecast[0].day().temperature.temperature,
                            temperatureUnit
                        )
                    )
                }
                setOnClickPendingIntent(context, views, location, R.id.widget_multi_city_horizontal_weather_2, 1)
            } else {
                views.setViewVisibility(R.id.widget_multi_city_horizontal_weather_2, View.GONE)
            }
            if (locationList.size >= 3) {
                location = locationList[2]
                weather = location.weather
                dayTime = location.isDaylight
                views.setViewVisibility(R.id.widget_multi_city_horizontal_weather_3, View.VISIBLE)
                if (weather != null) {
                    views.setTextViewText(R.id.widget_multi_city_horizontal_title_3, location.getCityName(context))
                    views.setImageViewUri(
                        R.id.widget_multi_city_horizontal_icon_3,
                        ResourceHelper.getWidgetNotificationIconUri(
                            provider,
                            if (dayTime) weather.dailyForecast[0].day().weatherCode
                            else weather.dailyForecast[0].night().weatherCode,
                            dayTime, minimalIcon, color.getMinimalIconColor()
                        )
                    )
                    views.setTextViewText(
                        R.id.widget_multi_city_horizontal_content_3,
                        Temperature.getTrendTemperature(
                            context,
                            weather.dailyForecast[0].night().temperature.temperature,
                            weather.dailyForecast[0].day().temperature.temperature,
                            temperatureUnit
                        )
                    )
                }
                setOnClickPendingIntent(context, views, location, R.id.widget_multi_city_horizontal_weather_3, 2)
            } else {
                views.setViewVisibility(R.id.widget_multi_city_horizontal_weather_3, View.GONE)
            }
            if (color.textColor != Color.TRANSPARENT) {
                views.setTextColor(R.id.widget_multi_city_horizontal_title_1, color.textColor)
                views.setTextColor(R.id.widget_multi_city_horizontal_title_2, color.textColor)
                views.setTextColor(R.id.widget_multi_city_horizontal_title_3, color.textColor)
                views.setTextColor(R.id.widget_multi_city_horizontal_content_1, color.textColor)
                views.setTextColor(R.id.widget_multi_city_horizontal_content_2, color.textColor)
                views.setTextColor(R.id.widget_multi_city_horizontal_content_3, color.textColor)
            }
            if (textSize != 100) {
                val titleSize = context.resources.getDimensionPixelSize(R.dimen.widget_title_text_size) *
                    textSize / 100f
                val contentSize = context.resources.getDimensionPixelSize(R.dimen.widget_content_text_size) *
                    textSize / 100f
                views.setTextViewTextSize(R.id.widget_multi_city_horizontal_title_1, TypedValue.COMPLEX_UNIT_PX, titleSize)
                views.setTextViewTextSize(R.id.widget_multi_city_horizontal_title_2, TypedValue.COMPLEX_UNIT_PX, titleSize)
                views.setTextViewTextSize(R.id.widget_multi_city_horizontal_title_3, TypedValue.COMPLEX_UNIT_PX, titleSize)
                views.setTextViewTextSize(R.id.widget_multi_city_horizontal_content_1, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_multi_city_horizontal_content_2, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_multi_city_horizontal_content_3, TypedValue.COMPLEX_UNIT_PX, contentSize)
            }
            if (color.showCard) {
                views.setImageViewResource(
                    R.id.widget_multi_city_horizontal_card,
                    getCardBackgroundId(color.cardColor)
                )
                views.setInt(
                    R.id.widget_multi_city_horizontal_card,
                    "setImageAlpha",
                    (cardAlpha / 100.0 * 255).toInt()
                )
            }
            return views
        }

        @JvmStatic
        fun isEnable(context: Context): Boolean {
            val widgetIds = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, WidgetMultiCityProvider::class.java))
            return widgetIds != null && widgetIds.isNotEmpty()
        }

        private fun setOnClickPendingIntent(
            context: Context,
            views: RemoteViews,
            location: Location,
            @IdRes resId: Int,
            @IntRange(from = 0, to = 2) index: Int
        ) {
            views.setOnClickPendingIntent(
                resId,
                getWeatherPendingIntent(
                    context,
                    location,
                    GeometricWeather.WIDGET_MULTI_CITY_PENDING_INTENT_CODE_WEATHER_1 + 2 * index
                )
            )
        }
    }
}
