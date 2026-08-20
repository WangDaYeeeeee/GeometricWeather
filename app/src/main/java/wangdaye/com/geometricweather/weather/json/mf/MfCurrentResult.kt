package wangdaye.com.geometricweather.weather.json.mf

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MfCurrentResult(
    @JvmField val position: Position? = null,
    @JvmField @SerialName("updated_on") val updatedOn: Long = 0,
    @JvmField val observation: Observation? = null
) {
    @Serializable
    class Position(
        @JvmField val lat: Double = 0.0,
        @JvmField val lon: Double = 0.0,
        @JvmField val timezone: String? = null
    )

    @Serializable
    class Observation(
        @JvmField @SerialName("T") val temperature: Float? = null,
        @JvmField val wind: Wind? = null,
        @JvmField val weather: Weather? = null
    ) {
        @Serializable
        class Wind(
            @JvmField val speed: Float? = null,
            @JvmField val gust: Float? = null,
            @JvmField val direction: Int? = null,
            @JvmField val icon: String? = null
        )

        @Serializable
        class Weather(
            @JvmField val desc: String? = null,
            @JvmField val icon: String? = null
        )
    }
}
