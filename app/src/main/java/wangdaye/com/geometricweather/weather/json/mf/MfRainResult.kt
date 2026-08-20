package wangdaye.com.geometricweather.weather.json.mf

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MfRainResult(
    @JvmField val position: Position? = null,
    @JvmField val quality: Int = 0,
    @JvmField @SerialName("forecast") val rainForecasts: List<RainForecast>? = null,
    @JvmField @SerialName("updated_on") val updatedOn: Long = 0
) {
    @Serializable
    class Position(
        @JvmField val alti: Int? = null,
        @JvmField val country: String? = null,
        @JvmField val dept: String? = null,
        @JvmField @SerialName("rain_product_available") val hasRain: Int = 0,
        @JvmField @SerialName("bulletin_cote") val hasSeaBulletin: Int = 0,
        @JvmField val insee: String? = null,
        @JvmField val lat: Double = 0.0,
        @JvmField val lon: Double = 0.0,
        @JvmField val name: String? = null,
        @JvmField val timezone: String? = null
    )

    @Serializable
    class RainForecast(
        @JvmField @SerialName("dt") val date: Long = 0,
        @JvmField val desc: String? = null,
        @JvmField val rain: Int = 0
    )
}
