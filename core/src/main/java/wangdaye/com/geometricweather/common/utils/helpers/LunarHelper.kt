package wangdaye.com.geometricweather.common.utils.helpers

import android.util.Log
import com.xhinliang.lunarcalendar.LunarCalendar
import java.util.Calendar
import java.util.Date

/**
 * Lunar helper.
 */
object LunarHelper {

    @JvmStatic
    fun getLunarDate(date: Date?): String {
        date ?: return ""
        val calendar = Calendar.getInstance()
        calendar.time = date
        return getLunarDate(calendar)
    }

    private fun getLunarDate(calendar: Calendar): String {
        return getLunarDate(
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH] + 1,
            calendar[Calendar.DAY_OF_MONTH]
        )
    }

    private fun getLunarDate(year: Int, month: Int, day: Int): String {
        return try {
            val lunarCalendar = LunarCalendar.obtainCalendar(year, month, day)
            lunarCalendar.fullLunarStr.split("年".toRegex()).toTypedArray()[1]
                .replace("廿十", "二十")
                .replace("卅十", "三十")
        } catch (e: Exception) {
            Log.w("LunarHelper", "Failed to format lunar date", e)
            ""
        }
    }
}
