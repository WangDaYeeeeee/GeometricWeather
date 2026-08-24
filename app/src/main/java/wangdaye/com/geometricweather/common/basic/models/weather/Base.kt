package wangdaye.com.geometricweather.common.basic.models.weather

import android.annotation.SuppressLint
import android.content.Context
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date

class Base(
    val cityId: String,
    val timeStamp: Long,
    val publishDate: Date,
    val publishTime: Long,
    val updateDate: Date,
    val updateTime: Long
) : Serializable {

    companion object {

        @JvmStatic
        @SuppressLint("SimpleDateFormat")
        fun getTime(context: Context, date: Date): String {
            return getTime(date, DisplayUtils.is12Hour(context))
        }

        @SuppressLint("SimpleDateFormat")
        private fun getTime(date: Date, twelveHour: Boolean): String {
            return if (twelveHour) {
                SimpleDateFormat("h:mm aa").format(date)
            } else {
                SimpleDateFormat("HH:mm").format(date)
            }
        }
    }
}
