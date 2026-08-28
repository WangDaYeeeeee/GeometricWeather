package wangdaye.com.geometricweather.weather.json.mf

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
class MfForecastResult(
    @JvmField @SerialName("daily_forecast") val dailyForecasts: List<DailyForecast>? = null,
    @JvmField @SerialName("forecast") val forecasts: List<Forecast>? = null,
    @JvmField val position: Position? = null,
    @JvmField @SerialName("probability_forecast") val probabilityForecast: List<ProbabilityForecast>? = null,
    @JvmField @SerialName("updated_on") val updatedOn: Long = 0
) {
    @Serializable
    class DailyForecast(
        @JvmField val dt: Long = 0,
        @JvmField val humidity: Humidity? = null,
        @JvmField val precipitation: Precipitation? = null,
        @JvmField val sun: Sun? = null,
        @JvmField @SerialName("T") val temperature: DailyTemperature? = null,
        @JvmField val uv: Int = 0,
        @JvmField val weather12H: Weather? = null
    ) {
        @Serializable
        class Humidity(
            @JvmField val max: Int? = null,
            @JvmField val min: Int? = null
        )

        @Serializable
        class Precipitation(
            @JvmField @SerialName("24h") val cumul24H: Float? = null
        )

        @Serializable
        class Sun(
            @JvmField val rise: Long? = null,
            @JvmField val set: Long? = null
        )

        @Serializable
        class DailyTemperature(
            @JvmField val max: Float? = null,
            @JvmField val min: Float? = null
        )

        @Serializable
        class Weather(
            @JvmField val desc: String? = null,
            @JvmField val icon: String? = null
        )
    }

    @Serializable
    class Forecast(
        @JvmField val clouds: Int? = null,
        @JvmField val dt: Long = 0,
        @JvmField val humidity: Int? = null,
        @JvmField val iso0: Int? = null,
        @JvmField val precipitation: Precipitation? = null,
        @JvmField val rain: Rain? = null,
        @JvmField @SerialName("rain snow limit") val rainSnowLimitRaw: JsonElement? = null,
        @JvmField @SerialName("sea_level") val seaLevel: Double = 0.0,
        @JvmField val snow: Snow? = null,
        @JvmField @SerialName("T") val temperature: Temperature? = null,
        @JvmField val weather: Weather? = null,
        @JvmField val wind: Wind? = null
    ) {
        @Serializable
        class Precipitation(
            @JvmField @SerialName("24h") val cumul24H: Float? = null
        )

        @Serializable
        class Rain(
            @JvmField @SerialName("12h") val cumul12H: Float? = null,
            @JvmField @SerialName("1h") val cumul1H: Float? = null,
            @JvmField @SerialName("24h") val cumul24H: Float? = null,
            @JvmField @SerialName("3h") val cumul3H: Float? = null,
            @JvmField @SerialName("6h") val cumul6H: Float? = null
        )

        @Serializable
        class Snow(
            @JvmField @SerialName("12h") val cumul12H: Float? = null,
            @JvmField @SerialName("1h") val cumul1H: Float? = null,
            @JvmField @SerialName("24h") val cumul24H: Float? = null,
            @JvmField @SerialName("3h") val cumul3H: Float? = null,
            @JvmField @SerialName("6h") val cumul6H: Float? = null
        )

        @Serializable
        class Temperature(
            @JvmField val value: Float? = null,
            @JvmField @SerialName("windchill") val windChill: Float? = null
        )

        @Serializable
        class Weather(
            @JvmField val desc: String? = null,
            @JvmField val icon: String? = null
        )

        @Serializable
        class Wind(
            @JvmField val direction: String? = null,
            @JvmField val gust: Int? = null,
            @JvmField val icon: String? = null,
            @JvmField val speed: Int? = null
        )
    }

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
    class ProbabilityForecast(
        @JvmField val dt: Long = 0,
        @JvmField val freezing: Int? = null,
        @JvmField val rain: ProbabilityRain? = null,
        @JvmField val snow: ProbabilitySnow? = null
    ) {
        @Serializable
        class ProbabilityRain(
            @JvmField @SerialName("3h") val proba3H: Int? = null,
            @JvmField @SerialName("6h") val proba6H: Int? = null
        )

        @Serializable
        class ProbabilitySnow(
            @JvmField @SerialName("3h") val proba3H: Int? = null,
            @JvmField @SerialName("6h") val proba6H: Int? = null
        )
    }
}
