package wangdaye.com.geometricweather.common.basic.models.weather

import android.annotation.SuppressLint
import android.content.Context
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.utils.helpers.LunarHelper
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class Daily(
    val date: Date,
    val time: Long,
    day: HalfDay,
    night: HalfDay,
    sun: Astro,
    moon: Astro,
    val moonPhase: MoonPhase,
    val airQuality: AirQuality,
    val pollen: Pollen,
    @get:JvmName("getUV") val uv: UV,
    val hoursOfSun: Float
) : Serializable {

    private val halfDays = arrayOf(day, night)
    private val astros = arrayOf(sun, moon)

    fun day(): HalfDay = halfDays[0]

    fun night(): HalfDay = halfDays[1]

    fun sun(): Astro = astros[0]

    fun moon(): Astro = astros[1]

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

    val lunar: String
        get() = LunarHelper.getLunarDate(date)

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
