package wangdaye.com.geometricweather.remoteviews

import android.content.Context
import android.text.TextPaint
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.unit.TemperatureUnit
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.remoteviews.presenters.ClockDayDetailsWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.ClockDayHorizontalWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.ClockDayVerticalWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.ClockDayWeekWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.DailyTrendWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.DayWeekWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.DayWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.HourlyTrendWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.MaterialYouCurrentWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.MaterialYouForecastWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.MultiCityWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.TextWidgetIMP
import wangdaye.com.geometricweather.remoteviews.presenters.WeekWidgetIMP
import java.util.Calendar
import java.util.Date

object WidgetHelper {

    @JvmStatic
    fun updateWidgetIfNecessary(context: Context, location: Location) {
        if (DayWidgetIMP.isEnable(context)) {
            DayWidgetIMP.updateWidgetView(context, location)
        }
        if (WeekWidgetIMP.isEnable(context)) {
            WeekWidgetIMP.updateWidgetView(context, location)
        }
        if (DayWeekWidgetIMP.isEnable(context)) {
            DayWeekWidgetIMP.updateWidgetView(context, location)
        }
        if (ClockDayHorizontalWidgetIMP.isEnable(context)) {
            ClockDayHorizontalWidgetIMP.updateWidgetView(context, location)
        }
        if (ClockDayVerticalWidgetIMP.isEnable(context)) {
            ClockDayVerticalWidgetIMP.updateWidgetView(context, location)
        }
        if (ClockDayWeekWidgetIMP.isEnable(context)) {
            ClockDayWeekWidgetIMP.updateWidgetView(context, location)
        }
        if (ClockDayDetailsWidgetIMP.isEnable(context)) {
            ClockDayDetailsWidgetIMP.updateWidgetView(context, location)
        }
        if (TextWidgetIMP.isEnable(context)) {
            TextWidgetIMP.updateWidgetView(context, location)
        }
        if (DailyTrendWidgetIMP.isEnable(context)) {
            DailyTrendWidgetIMP.updateWidgetView(context, location)
        }
        if (HourlyTrendWidgetIMP.isEnable(context)) {
            HourlyTrendWidgetIMP.updateWidgetView(context, location)
        }
        if (MaterialYouForecastWidgetIMP.isEnable(context)) {
            MaterialYouForecastWidgetIMP.updateWidgetView(context, location)
        }
        if (MaterialYouCurrentWidgetIMP.isEnable(context)) {
            MaterialYouCurrentWidgetIMP.updateWidgetView(context, location)
        }
    }

    @JvmStatic
    fun updateWidgetIfNecessary(context: Context, locationList: List<Location>) {
        val valid = Location.excludeInvalidResidentLocation(context, locationList)
        if (MultiCityWidgetIMP.isEnable(context)) {
            MultiCityWidgetIMP.updateWidgetView(context, valid)
        }
    }

    @JvmStatic
    fun buildWidgetDayStyleText(
        context: Context,
        weather: Weather,
        unit: TemperatureUnit
    ): Array<String> {
        val texts = arrayOf(
            weather.current.weatherText,
            weather.current.temperature.getTemperature(context, unit).orEmpty(),
            weather.dailyForecast[0].day().temperature.getShortTemperature(context, unit).orEmpty(),
            weather.dailyForecast[0].night().temperature.getShortTemperature(context, unit).orEmpty()
        )

        val paint = TextPaint()
        val widths = FloatArray(4)
        for (i in widths.indices) {
            widths[i] = paint.measureText(texts[i])
        }

        var maxiWidth = widths[0]
        for (w in widths) {
            if (w > maxiWidth) {
                maxiWidth = w
            }
        }

        while (true) {
            val flags = booleanArrayOf(false, false, false, false)
            for (i in 0 until 2) {
                if (widths[i] < maxiWidth) {
                    texts[i] = texts[i] + " "
                    widths[i] = paint.measureText(texts[i])
                } else {
                    flags[i] = true
                }
            }
            for (i in 2 until 4) {
                if (widths[i] < maxiWidth) {
                    texts[i] = " " + texts[i]
                    widths[i] = paint.measureText(texts[i])
                } else {
                    flags[i] = true
                }
            }
            var n = 0
            for (flag in flags) {
                if (flag) n++
            }
            if (n == 4) {
                break
            }
        }

        return arrayOf(
            texts[0] + "\n" + texts[1],
            texts[2] + "\n" + texts[3]
        )
    }

    @JvmStatic
    fun getWeek(context: Context): String {
        val c = Calendar.getInstance()
        return when (c.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> context.getString(R.string.week_7)
            Calendar.MONDAY -> context.getString(R.string.week_1)
            Calendar.TUESDAY -> context.getString(R.string.week_2)
            Calendar.WEDNESDAY -> context.getString(R.string.week_3)
            Calendar.THURSDAY -> context.getString(R.string.week_4)
            Calendar.FRIDAY -> context.getString(R.string.week_5)
            Calendar.SATURDAY -> context.getString(R.string.week_6)
            else -> ""
        }
    }

    @JvmStatic
    fun getDailyWeek(context: Context, weather: Weather, index: Int): String {
        if (index > 1) {
            return weather.dailyForecast[index].getWeek(context)
        }

        val today = Calendar.getInstance()
        today.time = Date()
        val publish = Calendar.getInstance()
        publish.time = weather.dailyForecast[0].date

        val firstDay: String
        val secondDay: String
        if (today.get(Calendar.YEAR) == publish.get(Calendar.YEAR)
            && today.get(Calendar.DAY_OF_YEAR) == publish.get(Calendar.DAY_OF_YEAR)
        ) {
            firstDay = context.getString(R.string.today)
            secondDay = weather.dailyForecast[1].getWeek(context)
        } else if (today.get(Calendar.YEAR) == publish.get(Calendar.YEAR)
            && today.get(Calendar.DAY_OF_YEAR) == publish.get(Calendar.DAY_OF_YEAR) + 1
        ) {
            firstDay = context.getString(R.string.yesterday)
            secondDay = context.getString(R.string.today)
        } else {
            firstDay = weather.dailyForecast[0].getWeek(context)
            secondDay = weather.dailyForecast[1].getWeek(context)
        }
        return if (index == 0) firstDay else secondDay
    }

    @JvmStatic
    fun getNonNullValue(value: Float?, defaultValue: Float): Float {
        return value ?: defaultValue
    }

    @JvmStatic
    fun getNonNullValue(value: Int?, defaultValue: Int): Int {
        return value ?: defaultValue
    }
}
