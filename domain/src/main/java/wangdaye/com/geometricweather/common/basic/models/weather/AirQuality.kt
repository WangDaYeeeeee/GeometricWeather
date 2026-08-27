package wangdaye.com.geometricweather.common.basic.models.weather

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.basic.models.options.unit.AirQualityCOUnit
import wangdaye.com.geometricweather.common.basic.models.options.unit.AirQualityUnit
import java.io.Serializable

/**
 * DailyAirQuality quality.
 *
 * default unit : [AirQualityUnit.MUGPCUM],
 *                [AirQualityCOUnit.MGPCUM]
 */
class AirQuality(
    val aqiText: String?,
    val aqiIndex: Int?,
    @get:JvmName("getPM25") val pm25: Float?,
    @get:JvmName("getPM10") val pm10: Float?,
    @get:JvmName("getSO2") val so2: Float?,
    @get:JvmName("getNO2") val no2: Float?,
    val o3: Float?,
    @get:JvmName("getCO") val co: Float?
) : Serializable {

    @ColorInt
    fun getAqiColor(context: Context): Int {
        return if (aqiIndex == null) {
            ContextCompat.getColor(context, R.color.colorLevel_1)
        } else if (aqiIndex <= AQI_INDEX_1) {
            ContextCompat.getColor(context, R.color.colorLevel_1)
        } else if (aqiIndex <= AQI_INDEX_2) {
            ContextCompat.getColor(context, R.color.colorLevel_2)
        } else if (aqiIndex <= AQI_INDEX_3) {
            ContextCompat.getColor(context, R.color.colorLevel_3)
        } else if (aqiIndex <= AQI_INDEX_4) {
            ContextCompat.getColor(context, R.color.colorLevel_4)
        } else if (aqiIndex <= AQI_INDEX_5) {
            ContextCompat.getColor(context, R.color.colorLevel_5)
        } else {
            ContextCompat.getColor(context, R.color.colorLevel_6)
        }
    }

    @ColorInt
    fun getPm25Color(context: Context): Int {
        return if (pm25 == null) {
            Color.TRANSPARENT
        } else if (pm25 <= 35) {
            ContextCompat.getColor(context, R.color.colorLevel_1)
        } else if (pm25 <= 75) {
            ContextCompat.getColor(context, R.color.colorLevel_2)
        } else if (pm25 <= 115) {
            ContextCompat.getColor(context, R.color.colorLevel_3)
        } else if (pm25 <= 150) {
            ContextCompat.getColor(context, R.color.colorLevel_4)
        } else if (pm25 <= 250) {
            ContextCompat.getColor(context, R.color.colorLevel_5)
        } else {
            ContextCompat.getColor(context, R.color.colorLevel_6)
        }
    }

    @ColorInt
    fun getPm10Color(context: Context): Int {
        return if (pm10 == null) {
            Color.TRANSPARENT
        } else if (pm10 <= 50) {
            ContextCompat.getColor(context, R.color.colorLevel_1)
        } else if (pm10 <= 150) {
            ContextCompat.getColor(context, R.color.colorLevel_2)
        } else if (pm10 <= 250) {
            ContextCompat.getColor(context, R.color.colorLevel_3)
        } else if (pm10 <= 350) {
            ContextCompat.getColor(context, R.color.colorLevel_4)
        } else if (pm10 <= 420) {
            ContextCompat.getColor(context, R.color.colorLevel_5)
        } else {
            ContextCompat.getColor(context, R.color.colorLevel_6)
        }
    }

    @ColorInt
    fun getSo2Color(context: Context): Int {
        return if (so2 == null) {
            Color.TRANSPARENT
        } else if (so2 <= 50) {
            ContextCompat.getColor(context, R.color.colorLevel_1)
        } else if (so2 <= 150) {
            ContextCompat.getColor(context, R.color.colorLevel_2)
        } else if (so2 <= 475) {
            ContextCompat.getColor(context, R.color.colorLevel_3)
        } else if (so2 <= 800) {
            ContextCompat.getColor(context, R.color.colorLevel_4)
        } else if (so2 <= 1600) {
            ContextCompat.getColor(context, R.color.colorLevel_5)
        } else {
            ContextCompat.getColor(context, R.color.colorLevel_6)
        }
    }

    @ColorInt
    fun getNo2Color(context: Context): Int {
        return if (no2 == null) {
            Color.TRANSPARENT
        } else if (no2 <= 40) {
            ContextCompat.getColor(context, R.color.colorLevel_1)
        } else if (no2 <= 80) {
            ContextCompat.getColor(context, R.color.colorLevel_2)
        } else if (no2 <= 180) {
            ContextCompat.getColor(context, R.color.colorLevel_3)
        } else if (no2 <= 280) {
            ContextCompat.getColor(context, R.color.colorLevel_4)
        } else if (no2 <= 565) {
            ContextCompat.getColor(context, R.color.colorLevel_5)
        } else {
            ContextCompat.getColor(context, R.color.colorLevel_6)
        }
    }

    @ColorInt
    fun getO3Color(context: Context): Int {
        return if (o3 == null) {
            Color.TRANSPARENT
        } else if (o3 <= 160) {
            ContextCompat.getColor(context, R.color.colorLevel_1)
        } else if (o3 <= 200) {
            ContextCompat.getColor(context, R.color.colorLevel_2)
        } else if (o3 <= 300) {
            ContextCompat.getColor(context, R.color.colorLevel_3)
        } else if (o3 <= 400) {
            ContextCompat.getColor(context, R.color.colorLevel_4)
        } else if (o3 <= 800) {
            ContextCompat.getColor(context, R.color.colorLevel_5)
        } else {
            ContextCompat.getColor(context, R.color.colorLevel_6)
        }
    }

    @ColorInt
    fun getCOColor(context: Context): Int {
        return if (co == null) {
            Color.TRANSPARENT
        } else if (co <= 5) {
            ContextCompat.getColor(context, R.color.colorLevel_1)
        } else if (co <= 10) {
            ContextCompat.getColor(context, R.color.colorLevel_2)
        } else if (co <= 35) {
            ContextCompat.getColor(context, R.color.colorLevel_3)
        } else if (co <= 60) {
            ContextCompat.getColor(context, R.color.colorLevel_4)
        } else if (co <= 90) {
            ContextCompat.getColor(context, R.color.colorLevel_5)
        } else {
            ContextCompat.getColor(context, R.color.colorLevel_6)
        }
    }

    val isValid: Boolean
        get() = aqiIndex != null
            || aqiText != null
            || pm25 != null
            || pm10 != null
            || so2 != null
            || no2 != null
            || o3 != null
            || co != null

    val isValidIndex: Boolean
        get() = aqiIndex != null && aqiIndex > 0

    companion object {
        const val AQI_INDEX_1 = 50
        const val AQI_INDEX_2 = 100
        const val AQI_INDEX_3 = 150
        const val AQI_INDEX_4 = 200
        const val AQI_INDEX_5 = 300
    }
}
