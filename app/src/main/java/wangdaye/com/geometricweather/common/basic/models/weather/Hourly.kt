package wangdaye.com.geometricweather.common.basic.models.weather

import android.annotation.SuppressLint
import android.content.Context
import android.text.BidiFormatter
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class Hourly(
    val date: Date,
    val time: Long,
    val isDaylight: Boolean,
    val weatherText: String,
    val weatherCode: WeatherCode,
    val temperature: Temperature,
    val precipitation: Precipitation,
    val precipitationProbability: PrecipitationProbability,
    val wind: Wind,
    @get:JvmName("getUV") val uv: UV
) : Serializable {

    fun getHourIn24Format(): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar[Calendar.HOUR_OF_DAY]
    }

    fun getHour(context: Context): String {
        return getHour(context, DisplayUtils.is12Hour(context), DisplayUtils.isRtl(context))
    }

    @SuppressLint("DefaultLocale")
    private fun getHour(context: Context, twelveHour: Boolean, rtl: Boolean): String {
        val calendar = Calendar.getInstance()
        calendar.time = date

        val hour = if (twelveHour) {
            var h = calendar[Calendar.HOUR]
            if (h == 0) {
                h = 12
            }
            h
        } else {
            calendar[Calendar.HOUR_OF_DAY]
        }

        return if (rtl) {
            BidiFormatter.getInstance().unicodeWrap(String.format("%d", hour)) +
                context.getString(R.string.of_clock)
        } else {
            hour.toString() + context.getString(R.string.of_clock)
        }
    }

    fun getLongDate(context: Context): String {
        return getDate(context.getString(R.string.date_format_long))
    }

    fun getShortDate(context: Context): String {
        return getDate(context.getString(R.string.date_format_short))
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(format: String): String {
        return SimpleDateFormat(format).format(date)
    }

    fun getWeek(context: Context): String {
        val calendar = Calendar.getInstance()
        calendar.time = date

        val day = calendar[Calendar.DAY_OF_WEEK]
        return if (day == 1) {
            context.getString(R.string.week_7)
        } else if (day == 2) {
            context.getString(R.string.week_1)
        } else if (day == 3) {
            context.getString(R.string.week_2)
        } else if (day == 4) {
            context.getString(R.string.week_3)
        } else if (day == 5) {
            context.getString(R.string.week_4)
        } else if (day == 6) {
            context.getString(R.string.week_5)
        } else {
            context.getString(R.string.week_6)
        }
    }

    fun isToday(timeZone: TimeZone): Boolean {
        val millis = System.currentTimeMillis()

        val current = Calendar.getInstance()
        current.add(
            Calendar.MILLISECOND,
            timeZone.getOffset(millis) - TimeZone.getDefault().getOffset(millis)
        )

        val thisDay = Calendar.getInstance()
        thisDay.time = date

        return current[Calendar.YEAR] == thisDay[Calendar.YEAR]
            && current[Calendar.DAY_OF_YEAR] == thisDay[Calendar.DAY_OF_YEAR]
    }
}
