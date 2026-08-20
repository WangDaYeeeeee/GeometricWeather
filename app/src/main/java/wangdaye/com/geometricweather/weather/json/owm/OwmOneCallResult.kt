package wangdaye.com.geometricweather.weather.json.owm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class OwmOneCallResult(
    @JvmField val lat: Double = 0.0,
    @JvmField val lon: Double = 0.0,
    @JvmField val timezone: String? = null,
    @JvmField @SerialName("timezone_offset") val timezoneOffset: Int = 0,
    @JvmField val current: Current? = null,
    @JvmField val minutely: List<Minutely>? = null,
    @JvmField val hourly: List<Hourly>? = null,
    @JvmField val daily: List<Daily>? = null,
    @JvmField val alerts: List<Alert>? = null
) {
    @Serializable
    class Current(
        @JvmField val dt: Long = 0,
        @JvmField val sunrise: Long = 0,
        @JvmField val sunset: Long = 0,
        @JvmField val temp: Double = 0.0,
        @JvmField @SerialName("feels_like") val feelsLike: Double = 0.0,
        @JvmField val pressure: Int = 0,
        @JvmField val humidity: Int = 0,
        @JvmField @SerialName("dew_point") val dewPoint: Double = 0.0,
        @JvmField val uvi: Double = 0.0,
        @JvmField val clouds: Int = 0,
        @JvmField val visibility: Int = 0,
        @JvmField @SerialName("wind_speed") val windSpeed: Float? = null,
        @JvmField @SerialName("wind_deg") val windDeg: Int = 0,
        @JvmField val weather: List<Weather>? = null,
        @JvmField val rain: Precipitation? = null,
        @JvmField val snow: Precipitation? = null
    )

    @Serializable
    class Minutely(
        @JvmField val dt: Long = 0,
        @JvmField val precipitation: Float = 0f
    )

    @Serializable
    class Hourly(
        @JvmField val dt: Long = 0,
        @JvmField val temp: Double = 0.0,
        @JvmField @SerialName("feels_like") val feelsLike: Double = 0.0,
        @JvmField val pressure: Int = 0,
        @JvmField val humidity: Int = 0,
        @JvmField @SerialName("dew_point") val dewPoint: Double = 0.0,
        @JvmField val uvi: Double = 0.0,
        @JvmField val clouds: Int = 0,
        @JvmField val visibility: Int = 0,
        @JvmField @SerialName("wind_speed") val windSpeed: Float? = null,
        @JvmField @SerialName("wind_deg") val windDeg: Int = 0,
        @JvmField val weather: List<Weather>? = null,
        @JvmField val pop: Float? = null,
        @JvmField val rain: Precipitation? = null,
        @JvmField val snow: Precipitation? = null
    )

    @Serializable
    class Precipitation(
        @JvmField @SerialName("1h") val cumul1h: Float? = null
    )

    @Serializable
    class Daily(
        @JvmField val dt: Long = 0,
        @JvmField val sunrise: Long = 0,
        @JvmField val sunset: Long = 0,
        @JvmField val temp: Temp? = null,
        @JvmField @SerialName("feels_like") val feelsLike: FeelsLike? = null,
        @JvmField val pressure: Int = 0,
        @JvmField val humidity: Int = 0,
        @JvmField @SerialName("dew_point") val dewPoint: Double = 0.0,
        @JvmField @SerialName("wind_speed") val windSpeed: Float? = null,
        @JvmField @SerialName("wind_deg") val windDeg: Int = 0,
        @JvmField val weather: List<Weather>? = null,
        @JvmField val clouds: Int = 0,
        @JvmField val pop: Float? = null,
        @JvmField val rain: Float? = null,
        @JvmField val snow: Float? = null,
        @JvmField val uvi: Double = 0.0
    ) {
        @Serializable
        class Temp(
            @JvmField val day: Double = 0.0,
            @JvmField val min: Double = 0.0,
            @JvmField val max: Double = 0.0,
            @JvmField val night: Double = 0.0,
            @JvmField val eve: Double = 0.0,
            @JvmField val morn: Double = 0.0
        )

        @Serializable
        class FeelsLike(
            @JvmField val day: Double = 0.0,
            @JvmField val night: Double = 0.0,
            @JvmField val eve: Double = 0.0,
            @JvmField val morn: Double = 0.0
        )
    }

    @Serializable
    class Weather(
        @JvmField val id: Int = 0,
        @JvmField val main: String? = null,
        @JvmField val description: String? = null,
        @JvmField val icon: String? = null
    )

    @Serializable
    class Alert(
        @JvmField @SerialName("sender_name") val senderName: String? = null,
        @JvmField val event: String? = null,
        @JvmField val start: Long = 0,
        @JvmField val end: Long = 0,
        @JvmField val description: String? = null
    )
}
