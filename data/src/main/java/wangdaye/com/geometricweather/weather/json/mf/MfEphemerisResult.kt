@file:kotlinx.serialization.UseSerializers(wangdaye.com.geometricweather.common.json.GsonCompatibleDateSerializer::class)
package wangdaye.com.geometricweather.weather.json.mf

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class MfEphemerisResult(
    @JvmField val type: String? = null,
    @JvmField val geometry: Geometry? = null,
    @JvmField val properties: Properties? = null
) {
    @Serializable
    class Geometry(
        @JvmField val type: String? = null
    )

    @Serializable
    class Properties(
        @JvmField val ephemeris: Ephemeris? = null
    ) {
        @Serializable
        class Ephemeris(
            @JvmField @SerialName("sunrise_time") val sunriseTime: Date? = null,
            @JvmField @SerialName("sunset_time") val sunsetTime: Date? = null,
            @JvmField @SerialName("moonrise_time") val moonriseTime: Date? = null,
            @JvmField @SerialName("moonset_time") val moonsetTime: Date? = null,
            @JvmField @SerialName("moon_phase") val moonPhase: String? = null,
            @JvmField @SerialName("moon_phase_description") val moonPhaseDescription: String? = null,
            @JvmField val saint: String? = null
        )
    }
}
