package wangdaye.com.geometricweather.weather.json.mf

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MfHistoryResult(
    @JvmField val position: Position? = null,
    @JvmField val history: List<History>? = null
) {
    @Serializable
    class Position(
        @JvmField val lat: Double = 0.0,
        @JvmField val lon: Double = 0.0,
        @JvmField val alti: Int? = null,
        @JvmField val name: String? = null,
        @JvmField val country: String? = null,
        @JvmField val dept: String? = null,
        @JvmField val timezone: String? = null
    )

    @Serializable
    class History(
        @JvmField val dt: Long = 0,
        @JvmField @SerialName("T") val temperature: Temperature? = null,
        @JvmField val humidity: Int = 0,
        @JvmField @SerialName("sea_level") val seaLevel: Double = 0.0,
        @JvmField val visibility: Double = 0.0,
        @JvmField val wind: Wind? = null,
        @JvmField val precipitation: Precipitation? = null,
        @JvmField val snow: Snow? = null,
        @JvmField val clouds: Int? = null,
        @JvmField val weather: Weather? = null
    ) {
        @Serializable
        class Temperature(
            @JvmField val value: Float? = null,
            @JvmField @SerialName("windchill") val windChill: Float? = null
        )

        @Serializable
        class Wind(
            @JvmField val speed: Double = 0.0,
            @JvmField val gust: Double = 0.0,
            @JvmField val direction: Int? = null,
            @JvmField val icon: String? = null
        )

        @Serializable
        class Precipitation(
            @JvmField @SerialName("1h") val qty1H: Double = 0.0,
            @JvmField @SerialName("3h") val qty3H: Double = 0.0,
            @JvmField @SerialName("6h") val qty6H: Double = 0.0,
            @JvmField @SerialName("12h") val qty12H: Double = 0.0,
            @JvmField @SerialName("24h") val qty24H: Double = 0.0
        )

        @Serializable
        class Snow(
            @JvmField val depth: Int? = null,
            @JvmField val fresh: Int? = null
        )

        @Serializable
        class Weather(
            @JvmField val desc: String? = null,
            @JvmField val icon: String? = null
        )
    }
}
