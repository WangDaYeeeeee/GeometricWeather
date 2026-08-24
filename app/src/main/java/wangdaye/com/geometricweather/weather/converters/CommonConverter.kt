package wangdaye.com.geometricweather.weather.converters

import android.content.Context
import android.text.TextUtils
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.weather.AirQuality
import wangdaye.com.geometricweather.common.basic.models.weather.Wind
import java.util.Calendar
import java.util.Date
import java.util.Locale

object CommonConverter {

    @JvmStatic
    fun getWindLevel(c: Context, speed: Double): String {
        return if (speed <= Wind.WIND_SPEED_0) {
            c.getString(R.string.wind_0)
        } else if (speed <= Wind.WIND_SPEED_1) {
            c.getString(R.string.wind_1)
        } else if (speed <= Wind.WIND_SPEED_2) {
            c.getString(R.string.wind_2)
        } else if (speed <= Wind.WIND_SPEED_3) {
            c.getString(R.string.wind_3)
        } else if (speed <= Wind.WIND_SPEED_4) {
            c.getString(R.string.wind_4)
        } else if (speed <= Wind.WIND_SPEED_5) {
            c.getString(R.string.wind_5)
        } else if (speed <= Wind.WIND_SPEED_6) {
            c.getString(R.string.wind_6)
        } else if (speed <= Wind.WIND_SPEED_7) {
            c.getString(R.string.wind_7)
        } else if (speed <= Wind.WIND_SPEED_8) {
            c.getString(R.string.wind_8)
        } else if (speed <= Wind.WIND_SPEED_9) {
            c.getString(R.string.wind_9)
        } else if (speed <= Wind.WIND_SPEED_10) {
            c.getString(R.string.wind_10)
        } else if (speed <= Wind.WIND_SPEED_11) {
            c.getString(R.string.wind_11)
        } else {
            c.getString(R.string.wind_12)
        }
    }

    @JvmStatic
    fun getAqiQuality(c: Context, index: Int?): String? {
        if (index == null || index < 0) {
            return null
        }
        return if (index <= AirQuality.AQI_INDEX_1) {
            c.getString(R.string.aqi_1)
        } else if (index <= AirQuality.AQI_INDEX_2) {
            c.getString(R.string.aqi_2)
        } else if (index <= AirQuality.AQI_INDEX_3) {
            c.getString(R.string.aqi_3)
        } else if (index <= AirQuality.AQI_INDEX_4) {
            c.getString(R.string.aqi_4)
        } else if (index <= AirQuality.AQI_INDEX_5) {
            c.getString(R.string.aqi_5)
        } else {
            c.getString(R.string.aqi_6)
        }
    }

    @JvmStatic
    fun getMoonPhaseAngle(phase: String?): Int? {
        if (TextUtils.isEmpty(phase)) {
            return null
        }
        return when (phase!!.lowercase(Locale.getDefault())) {
            "waxingcrescent", "waxing crescent" -> 45
            "first", "firstquarter", "first quarter" -> 90
            "waxinggibbous", "waxing gibbous" -> 135
            "full", "fullmoon", "full moon" -> 180
            "waninggibbous", "waning gibbous" -> 225
            "third", "thirdquarter", "third quarter",
            "last", "lastquarter", "last quarter" -> 270
            "waningcrescent", "waning crescent" -> 315
            else -> 360
        }
    }

    @JvmStatic
    fun isDaylight(sunrise: Date, sunset: Date, current: Date): Boolean {
        val calendar = Calendar.getInstance()

        calendar.time = sunrise
        val sunriseTime = calendar[Calendar.HOUR_OF_DAY] * 60 + calendar[Calendar.MINUTE]

        calendar.time = sunset
        val sunsetTime = calendar[Calendar.HOUR_OF_DAY] * 60 + calendar[Calendar.MINUTE]

        calendar.time = current
        val currentTime = calendar[Calendar.HOUR_OF_DAY] * 60 + calendar[Calendar.MINUTE]

        return sunriseTime < currentTime && currentTime < sunsetTime
    }
}
