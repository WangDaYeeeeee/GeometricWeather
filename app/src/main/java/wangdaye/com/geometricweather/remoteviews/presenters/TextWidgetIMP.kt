package wangdaye.com.geometricweather.remoteviews.presenters

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.background.receiver.widget.WidgetTextProvider
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getCalendarPendingIntent
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getWeatherPendingIntent
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getWidgetConfig
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.isLightWallpaper

class TextWidgetIMP : AbstractRemoteViewsPresenter() {

    companion object {
        @JvmStatic
        fun updateWidgetView(context: Context, location: Location) {
            val config = getWidgetConfig(
                context,
                context.getString(R.string.sp_widget_text_setting)
            )
            val views = getRemoteViews(
                context, location, config.textColor, config.textSize, config.alignEnd
            )
            AppWidgetManager.getInstance(context).updateAppWidget(
                ComponentName(context, WidgetTextProvider::class.java),
                views
            )
        }

        @JvmStatic
        fun getRemoteViews(
            context: Context,
            location: Location,
            textColor: String?,
            textSize: Int,
            alignEnd: Boolean
        ): RemoteViews {
            val settings = SettingsManager.getInstance(context)
            val temperatureUnit = settings.temperatureUnit

            val views = RemoteViews(
                context.packageName,
                if (alignEnd) R.layout.widget_text_end else R.layout.widget_text
            )
            val weather = location.weather ?: return views

            val darkText = textColor == "dark" ||
                (textColor == "auto" && isLightWallpaper(context))

            val textColorInt = if (darkText) {
                ContextCompat.getColor(context, R.color.colorTextDark)
            } else {
                ContextCompat.getColor(context, R.color.colorTextLight)
            }

            views.setTextViewText(
                R.id.widget_text_weather,
                weather.current.weatherText
            )
            views.setTextViewText(
                R.id.widget_text_temperature,
                weather.current.temperature.getShortTemperature(context, temperatureUnit)
            )

            views.setTextColor(R.id.widget_text_date, textColorInt)
            views.setTextColor(R.id.widget_text_weather, textColorInt)
            views.setTextColor(R.id.widget_text_temperature, textColorInt)

            if (textSize != 100) {
                val contentSize = context.resources.getDimensionPixelSize(R.dimen.widget_content_text_size) *
                    textSize / 100f
                val temperatureSize = DisplayUtils.spToPx(context, 48) * textSize / 100f

                views.setTextViewTextSize(R.id.widget_text_date, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_text_weather, TypedValue.COMPLEX_UNIT_PX, contentSize)
                views.setTextViewTextSize(R.id.widget_text_temperature, TypedValue.COMPLEX_UNIT_PX, temperatureSize)
            }

            setOnClickPendingIntent(context, views, location)
            return views
        }

        @JvmStatic
        fun isEnable(context: Context): Boolean {
            val widgetIds = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, WidgetTextProvider::class.java))
            return widgetIds != null && widgetIds.isNotEmpty()
        }

        private fun setOnClickPendingIntent(context: Context, views: RemoteViews, location: Location) {
            views.setOnClickPendingIntent(
                R.id.widget_text_container,
                getWeatherPendingIntent(
                    context,
                    location,
                    GeometricWeather.WIDGET_TEXT_PENDING_INTENT_CODE_WEATHER
                )
            )
            views.setOnClickPendingIntent(
                R.id.widget_text_date,
                getCalendarPendingIntent(
                    context,
                    GeometricWeather.WIDGET_TEXT_PENDING_INTENT_CODE_CALENDAR
                )
            )
        }
    }
}
