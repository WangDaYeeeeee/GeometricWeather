package wangdaye.com.geometricweather.common.basic.models.weather

import android.content.Context
import wangdaye.com.geometricweather.common.basic.models.options.unit.TemperatureUnit
import wangdaye.com.geometricweather.settings.SettingsManager
import java.io.Serializable

/**
 * Temperature.
 * default unit : [TemperatureUnit.C]
 */
class Temperature(
    val temperature: Int,
    val realFeelTemperature: Int?,
    val realFeelShaderTemperature: Int?,
    val apparentTemperature: Int?,
    val windChillTemperature: Int?,
    val wetBulbTemperature: Int?,
    val degreeDayTemperature: Int?
) : Serializable {

    fun getTemperature(context: Context, unit: TemperatureUnit): String? {
        return getTemperature(context, temperature, unit)
    }

    fun getShortTemperature(context: Context, unit: TemperatureUnit): String? {
        return getShortTemperature(context, temperature, unit)
    }

    fun getRealFeelTemperature(context: Context, unit: TemperatureUnit): String? {
        return getTemperature(context, realFeelTemperature, unit)
    }

    fun getShortRealFeeTemperature(context: Context, unit: TemperatureUnit): String? {
        return getShortTemperature(context, realFeelTemperature, unit)
    }

    val isValid: Boolean
        get() = realFeelTemperature != null
            || realFeelShaderTemperature != null
            || apparentTemperature != null
            || windChillTemperature != null
            || wetBulbTemperature != null
            || degreeDayTemperature != null

    companion object {

        @JvmStatic
        fun getTemperature(
            context: Context,
            temperature: Int?,
            unit: TemperatureUnit
        ): String? {
            if (temperature == null) {
                return null
            }
            return unit.getValueText(context, temperature)
        }

        @JvmStatic
        fun getShortTemperature(
            context: Context,
            temperature: Int?,
            unit: TemperatureUnit
        ): String? {
            if (temperature == null) {
                return null
            }
            return unit.getShortValueText(context, temperature)
        }

        @JvmStatic
        fun getTrendTemperature(
            context: Context,
            night: Int?,
            day: Int?,
            unit: TemperatureUnit
        ): String? {
            return getTrendTemperature(
                context,
                night,
                day,
                unit,
                SettingsManager.getInstance(context).isExchangeDayNightTempEnabled
            )
        }

        @JvmStatic
        fun getTrendTemperature(
            context: Context,
            night: Int?,
            day: Int?,
            unit: TemperatureUnit,
            switchDayNight: Boolean
        ): String? {
            if (night == null || day == null) {
                return null
            }
            return if (switchDayNight) {
                getShortTemperature(context, day, unit) + "/" + getShortTemperature(context, night, unit)
            } else {
                getShortTemperature(context, night, unit) + "/" + getShortTemperature(context, day, unit)
            }
        }
    }
}
