package wangdaye.com.geometricweather.common.basic.models.weather

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.basic.models.options.unit.SpeedUnit
import java.io.Serializable

/**
 * DailyWind.
 *
 * default unit:
 * [speed] : [SpeedUnit.KPH]
 */
class Wind(
    val direction: String,
    val degree: WindDegree,
    val speed: Float?,
    val level: String
) : Serializable {

    @ColorInt
    fun getWindColor(context: Context): Int {
        return if (speed == null) {
            ContextCompat.getColor(context, R.color.colorLevel_1)
        } else if (speed <= WIND_SPEED_3) {
            ContextCompat.getColor(context, R.color.colorLevel_1)
        } else if (speed <= WIND_SPEED_5) {
            ContextCompat.getColor(context, R.color.colorLevel_2)
        } else if (speed <= WIND_SPEED_7) {
            ContextCompat.getColor(context, R.color.colorLevel_3)
        } else if (speed <= WIND_SPEED_9) {
            ContextCompat.getColor(context, R.color.colorLevel_4)
        } else if (speed <= WIND_SPEED_11) {
            ContextCompat.getColor(context, R.color.colorLevel_5)
        } else {
            ContextCompat.getColor(context, R.color.colorLevel_6)
        }
    }

    @get:JvmName("getShortWindDescription")
    val shortWindDescription: String
        get() = "$direction $level"

    fun getWindDescription(context: Context, unit: SpeedUnit): String {
        val builder = StringBuilder()
        builder.append(direction)
        if (speed != null) {
            builder.append(" ").append(unit.getValueText(context, speed))
        }
        builder.append(" ").append("(").append(level).append(")")
        if (!degree.isNoDirection) {
            builder.append(" ").append(degree.getWindArrow())
        }
        return builder.toString()
    }

    val isValidSpeed: Boolean
        get() = speed != null && speed > 0

    companion object {
        const val WIND_SPEED_0 = 2f
        const val WIND_SPEED_1 = 6f
        const val WIND_SPEED_2 = 12f
        const val WIND_SPEED_3 = 19f
        const val WIND_SPEED_4 = 30f
        const val WIND_SPEED_5 = 40f
        const val WIND_SPEED_6 = 51f
        const val WIND_SPEED_7 = 62f
        const val WIND_SPEED_8 = 75f
        const val WIND_SPEED_9 = 87f
        const val WIND_SPEED_10 = 103f
        const val WIND_SPEED_11 = 117f
    }
}
