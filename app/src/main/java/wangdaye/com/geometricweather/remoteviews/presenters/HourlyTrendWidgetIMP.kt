package wangdaye.com.geometricweather.remoteviews.presenters

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.background.receiver.widget.WidgetTrendHourlyProvider
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import wangdaye.com.geometricweather.common.utils.helpers.AsyncHelper
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getCardBackgroundId
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getWeatherPendingIntent
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.Companion.getWidgetConfig
import wangdaye.com.geometricweather.remoteviews.presenters.AbstractRemoteViewsPresenter.WidgetColor
import wangdaye.com.geometricweather.remoteviews.trend.TrendLinearLayout
import wangdaye.com.geometricweather.remoteviews.trend.WidgetItemView
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.ThemeManager
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory
import wangdaye.com.geometricweather.theme.weatherView.WeatherViewController

class HourlyTrendWidgetIMP : AbstractRemoteViewsPresenter() {

    companion object {
        @JvmStatic
        fun updateWidgetView(context: Context, location: Location) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                innerUpdateWidget(context, location)
                return
            }
            AsyncHelper.runOnIO { innerUpdateWidget(context, location) }
        }

        @WorkerThread
        private fun innerUpdateWidget(context: Context, location: Location) {
            val config = getWidgetConfig(
                context,
                context.getString(R.string.sp_widget_hourly_trend_setting)
            )
            if (config.cardStyle == "none") {
                config.cardStyle = "light"
            }
            AppWidgetManager.getInstance(context).updateAppWidget(
                ComponentName(context, WidgetTrendHourlyProvider::class.java),
                getRemoteViews(
                    context, location,
                    DisplayUtils.getTabletListAdaptiveWidth(
                        context,
                        context.resources.displayMetrics.widthPixels
                    ),
                    config.cardStyle, config.cardAlpha
                )
            )
        }

        @WorkerThread
        @SuppressLint("InflateParams", "WrongThread")
        private fun getDrawableView(context: Context, location: Location, lightTheme: Boolean): View? {
            val weather = location.weather ?: return null
            val provider = ResourcesProviderFactory.getNewInstance()
            val itemCount = 5
            val minimalIcon = SettingsManager.getInstance(context).isWidgetMinimalIconEnabled
            val temperatureUnit = SettingsManager.getInstance(context).temperatureUnit
            val temperatures = FloatArray(itemCount * 2 - 1)
            var i = 0
            while (i < temperatures.size) {
                temperatures[i] = weather.hourlyForecast[i / 2].temperature.temperature.toFloat()
                i += 2
            }
            i = 1
            while (i < temperatures.size) {
                temperatures[i] = (temperatures[i - 1] + temperatures[i + 1]) * 0.5f
                i += 2
            }
            var highestTemperature = if (weather.yesterday == null) {
                Int.MIN_VALUE
            } else {
                weather.yesterday!!.daytimeTemperature
            }
            var lowestTemperature = if (weather.yesterday == null) {
                Int.MAX_VALUE
            } else {
                weather.yesterday!!.nighttimeTemperature
            }
            for (index in 0 until itemCount) {
                if (weather.hourlyForecast[index].temperature.temperature > highestTemperature) {
                    highestTemperature = weather.hourlyForecast[index].temperature.temperature
                }
                if (weather.hourlyForecast[index].temperature.temperature < lowestTemperature) {
                    lowestTemperature = weather.hourlyForecast[index].temperature.temperature
                }
            }
            val drawableView = LayoutInflater.from(context)
                .inflate(R.layout.widget_trend_hourly, null, false)
            if (weather.yesterday != null) {
                val trendParent = drawableView.findViewById<TrendLinearLayout>(R.id.widget_trend_hourly)
                trendParent.setData(
                    intArrayOf(
                        weather.yesterday!!.daytimeTemperature,
                        weather.yesterday!!.nighttimeTemperature
                    ),
                    highestTemperature,
                    lowestTemperature,
                    temperatureUnit,
                    false
                )
                trendParent.setColor(lightTheme)
            }
            val items = arrayOf(
                drawableView.findViewById<WidgetItemView>(R.id.widget_trend_hourly_item_1),
                drawableView.findViewById(R.id.widget_trend_hourly_item_2),
                drawableView.findViewById(R.id.widget_trend_hourly_item_3),
                drawableView.findViewById(R.id.widget_trend_hourly_item_4),
                drawableView.findViewById(R.id.widget_trend_hourly_item_5)
            )
            val colors = ThemeManager.getInstance(context).weatherThemeDelegate.getThemeColors(
                context, WeatherViewController.getWeatherKind(weather), location.isDaylight
            )
            for (index in items.indices) {
                val hourly = weather.hourlyForecast[index]
                items[index].setTitleText(hourly.getHour(context))
                items[index].setSubtitleText(null)
                items[index].setTopIconDrawable(
                    ResourceHelper.getWidgetNotificationIcon(
                        provider, hourly.weatherCode, hourly.isDaylight, minimalIcon, lightTheme
                    )
                )
                items[index].trendItemView.setData(
                    buildTemperatureArrayForItem(temperatures, index),
                    null,
                    hourly.temperature.getShortTemperature(context, temperatureUnit),
                    null,
                    highestTemperature.toFloat(),
                    lowestTemperature.toFloat(),
                    null, null, null, null
                )
                items[index].trendItemView.setLineColors(
                    colors[1], colors[2],
                    if (lightTheme) {
                        ColorUtils.setAlphaComponent(Color.BLACK, (255 * 0.05).toInt())
                    } else {
                        ColorUtils.setAlphaComponent(Color.WHITE, (255 * 0.1).toInt())
                    }
                )
                items[index].trendItemView.setShadowColors(colors[1], colors[2], lightTheme)
                items[index].trendItemView.setTextColors(
                    if (lightTheme) ContextCompat.getColor(context, R.color.colorTextDark)
                    else ContextCompat.getColor(context, R.color.colorTextLight),
                    if (lightTheme) ContextCompat.getColor(context, R.color.colorTextDark2nd)
                    else ContextCompat.getColor(context, R.color.colorTextLight2nd),
                    if (lightTheme) ContextCompat.getColor(context, R.color.colorTextGrey2nd)
                    else ContextCompat.getColor(context, R.color.colorTextGrey)
                )
                items[index].trendItemView.setHistogramAlpha(if (lightTheme) 0.2f else 0.5f)
                items[index].setBottomIconDrawable(null)
                items[index].setColor(lightTheme)
            }
            return drawableView
        }

        @SuppressLint("WrongThread")
        @WorkerThread
        private fun getRemoteViews(
            context: Context,
            drawableView: View?,
            location: Location,
            width: Int,
            cardAlpha: Int,
            cardStyle: String?
        ): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_remote)
            if (drawableView == null) {
                return views
            }
            val items = arrayOf(
                drawableView.findViewById<WidgetItemView>(R.id.widget_trend_hourly_item_1),
                drawableView.findViewById(R.id.widget_trend_hourly_item_2),
                drawableView.findViewById(R.id.widget_trend_hourly_item_3),
                drawableView.findViewById(R.id.widget_trend_hourly_item_4),
                drawableView.findViewById(R.id.widget_trend_hourly_item_5)
            )
            for (item in items) {
                item.setSize(width / 5f)
            }
            drawableView.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            drawableView.layout(0, 0, drawableView.measuredWidth, drawableView.measuredHeight)
            val cache = Bitmap.createBitmap(
                drawableView.measuredWidth,
                drawableView.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(cache)
            drawableView.draw(canvas)
            views.setImageViewBitmap(R.id.widget_remote_drawable, cache)
            views.setViewVisibility(R.id.widget_remote_progress, View.GONE)
            val colorType = when (cardStyle) {
                "light" -> WidgetColor.ColorType.LIGHT
                "dark" -> WidgetColor.ColorType.DARK
                else -> WidgetColor.ColorType.AUTO
            }
            views.setImageViewResource(R.id.widget_remote_card, getCardBackgroundId(colorType))
            views.setInt(R.id.widget_remote_card, "setImageAlpha", (cardAlpha / 100.0 * 255).toInt())
            setOnClickPendingIntent(context, views, location)
            return views
        }

        @JvmStatic
        @WorkerThread
        fun getRemoteViews(
            context: Context,
            location: Location,
            width: Int,
            cardStyle: String?,
            cardAlpha: Int
        ): RemoteViews {
            val lightTheme = when (cardStyle) {
                "light" -> true
                "dark" -> false
                else -> location.isDaylight
            }
            return getRemoteViews(
                context,
                getDrawableView(context, location, lightTheme),
                location,
                width,
                cardAlpha,
                cardStyle
            )
        }

        @JvmStatic
        fun isEnable(context: Context): Boolean {
            val widgetIds = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, WidgetTrendHourlyProvider::class.java))
            return widgetIds != null && widgetIds.isNotEmpty()
        }

        private fun buildTemperatureArrayForItem(temps: FloatArray, index: Int): Array<Float?> {
            val a = arrayOfNulls<Float>(3)
            a[1] = temps[2 * index]
            a[0] = if (2 * index - 1 < 0) null else temps[2 * index - 1]
            a[2] = if (2 * index + 1 >= temps.size) null else temps[2 * index + 1]
            return a
        }

        private fun setOnClickPendingIntent(context: Context, views: RemoteViews, location: Location) {
            views.setOnClickPendingIntent(
                R.id.widget_remote_drawable,
                getWeatherPendingIntent(
                    context, location,
                    GeometricWeather.WIDGET_TREND_HOURLY_PENDING_INTENT_CODE_WEATHER
                )
            )
        }
    }
}
