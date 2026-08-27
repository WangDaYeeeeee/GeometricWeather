package wangdaye.com.geometricweather.common.basic.models.weather

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.basic.models.options.unit.PrecipitationUnit
import java.io.Serializable

/**
 * Precipitation.
 *
 * default unit : [PrecipitationUnit.MM]
 */
class Precipitation(
    val total: Float?,
    val thunderstorm: Float?,
    val rain: Float?,
    val snow: Float?,
    val ice: Float?
) : Serializable {

    val isValid: Boolean
        get() = total != null && total > 0

    @ColorInt
    fun getPrecipitationColor(context: Context): Int {
        return if (total == null) {
            ContextCompat.getColor(context, R.color.colorLevel_1)
        } else if (total <= PRECIPITATION_LIGHT) {
            ContextCompat.getColor(context, R.color.colorLevel_1)
        } else if (total <= PRECIPITATION_MIDDLE) {
            ContextCompat.getColor(context, R.color.colorLevel_2)
        } else if (total <= PRECIPITATION_HEAVY) {
            ContextCompat.getColor(context, R.color.colorLevel_3)
        } else if (total <= PRECIPITATION_RAINSTORM) {
            ContextCompat.getColor(context, R.color.colorLevel_4)
        } else {
            ContextCompat.getColor(context, R.color.colorLevel_5)
        }
    }

    companion object {
        const val PRECIPITATION_LIGHT = 10f
        const val PRECIPITATION_MIDDLE = 25f
        const val PRECIPITATION_HEAVY = 50f
        const val PRECIPITATION_RAINSTORM = 100f
    }
}
