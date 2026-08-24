package wangdaye.com.geometricweather.common.basic.models.weather

import java.io.Serializable
import java.util.Date
import kotlin.math.pow

class Minutely(
    val date: Date,
    val time: Long,
    val isDaylight: Boolean,
    val weatherText: String,
    val weatherCode: WeatherCode,
    val minuteInterval: Int,
    val dbz: Int?,
    val cloudCover: Int?
) : Serializable {

    constructor(
        date: Date,
        time: Long,
        daylight: Boolean,
        weatherText: String,
        weatherCode: WeatherCode,
        minuteInterval: Int,
        precipitationIntensity: Double?,
        cloudCover: Int?
    ) : this(
        date,
        time,
        daylight,
        weatherText,
        weatherCode,
        minuteInterval,
        precipitationIntensityToDBZ(precipitationIntensity),
        cloudCover
    )

    val precipitationIntensity: Double?
        @JvmName("getPrecipitationIntensity")
        get() {
        if (dbz == null) {
            return null
        }
        if (dbz <= 5) {
            return 0.0
        }
        return (10.0.pow(dbz / 10.0) / 200.0).pow(5.0 / 8.0)
    }

    val isPrecipitation: Boolean
        get() = weatherCode == WeatherCode.RAIN
            || weatherCode == WeatherCode.SNOW
            || weatherCode == WeatherCode.SLEET
            || weatherCode == WeatherCode.HAIL
            || weatherCode == WeatherCode.THUNDERSTORM

    companion object {
        private fun precipitationIntensityToDBZ(intensity: Double?): Int? {
            if (intensity == null) {
                return null
            }
            return (10.0 * kotlin.math.log10(200.0 * intensity.pow(8.0 / 5.0))).toInt()
        }
    }
}
