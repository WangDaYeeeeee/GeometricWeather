@file:kotlinx.serialization.UseSerializers(wangdaye.com.geometricweather.common.json.GsonCompatibleDateSerializer::class)
package wangdaye.com.geometricweather.weather.json.mf

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class MfForecastV2Result(
    @JvmField val geometry: Geometry? = null,
    @JvmField val properties: ForecastProperties? = null,
    @JvmField val type: String? = null,
    @JvmField @SerialName("update_time") val updateTime: Date? = null
) {
    @Serializable
    class Geometry(
        @JvmField val coordinates: List<Float>? = null,
        @JvmField val type: String? = null
    )

    @Serializable
    class ForecastProperties(
        @JvmField val altitude: Int? = null,
        @JvmField @SerialName("bulletin_cote") val bulletinCote: Int? = null,
        @JvmField val country: String? = null,
        @JvmField @SerialName("daily_forecast") val dailyForecast: List<ForecastV2>? = null,
        @JvmField val forecast: List<HourForecast>? = null,
        @JvmField @SerialName("french_department") val frenchDepartment: String? = null,
        @JvmField val insee: String? = null,
        @JvmField val name: String? = null,
        @JvmField @SerialName("probability_forecast") val probabilityForecast: List<ProbabilityForecastV2>? = null,
        @JvmField @SerialName("rain_product_available") val rainProductAvailable: Int? = null,
        @JvmField val timezone: String? = null
    ) {
        @Serializable
        class ForecastV2(
            @JvmField @SerialName("daily_weather_description") val dailyWeatherDescription: String? = null,
            @JvmField @SerialName("daily_weather_icon") val dailyWeatherIcon: String? = null,
            @JvmField @SerialName("relative_humidity_max") val relativeHumidityMax: Int? = null,
            @JvmField @SerialName("relative_humidity_min") val relativeHumidityMin: Int? = null,
            @JvmField @SerialName("sunrise_time") val sunriseTime: Date? = null,
            @JvmField @SerialName("sunset_time") val sunsetTime: Date? = null,
            @JvmField @SerialName("T_max") val tMax: Float? = null,
            @JvmField @SerialName("T_min") val tMin: Float? = null,
            @JvmField @SerialName("T_sea") val tSea: Float? = null,
            @JvmField val time: Date? = null,
            @JvmField @SerialName("total_precipitation_24h") val totalPrecipitation24h: Float? = null,
            @JvmField @SerialName("uv_index") val uvIndex: Int? = null
        )

        @Serializable
        class HourForecast(
            @JvmField @SerialName("weather_confidence_index") val confidence: Int? = null,
            @JvmField val iso0: Int? = null,
            @JvmField @SerialName("moment_day") val momentDay: String? = null,
            @JvmField @SerialName("P_sea") val pSea: Float? = null,
            @JvmField @SerialName("rain_12h") val rain12h: Float? = null,
            @JvmField @SerialName("rain_1h") val rain1h: Float? = null,
            @JvmField @SerialName("rain_24h") val rain24h: Float? = null,
            @JvmField @SerialName("rain_3h") val rain3h: Float? = null,
            @JvmField @SerialName("rain_6h") val rain6h: Float? = null,
            @JvmField @SerialName("relative_humidity") val relativeHumidity: Int? = null,
            @JvmField @SerialName("snow_12h") val snow12h: Float? = null,
            @JvmField @SerialName("snow_1h") val snow1h: Float? = null,
            @JvmField @SerialName("snow_24h") val snow24h: Float? = null,
            @JvmField @SerialName("snow_3h") val snow3h: Float? = null,
            @JvmField @SerialName("snow_6h") val snow6h: Float? = null,
            @JvmField @SerialName("T") val t: Float? = null,
            @JvmField @SerialName("T_windchill") val tWindchill: Float? = null,
            @JvmField val time: Date? = null,
            @JvmField @SerialName("total_cloud_cover") val totalCloudCover: Int? = null,
            @JvmField @SerialName("weather_description") val weatherDescription: String? = null,
            @JvmField @SerialName("weather_icon") val weatherIcon: String? = null,
            @JvmField @SerialName("wind_direction") val windDirection: Int? = null,
            @JvmField @SerialName("wind_icon") val windIcon: String? = null,
            @JvmField @SerialName("wind_speed") val windSpeed: Int? = null,
            @JvmField @SerialName("wind_speed_gust") val windSpeedGust: Int? = null
        )

        @Serializable
        class ProbabilityForecastV2(
            @JvmField @SerialName("freezing_hazard") val freezingHazard: Int? = null,
            @JvmField @SerialName("rain_hazard_3h") val rainHazard3h: Int? = null,
            @JvmField @SerialName("rain_hazard_6h") val rainHazard6h: Int? = null,
            @JvmField @SerialName("snow_hazard_3h") val snowHazard3h: Int? = null,
            @JvmField @SerialName("snow_hazard_6h") val snowHazard6h: Int? = null,
            @JvmField @SerialName("storm_hazard") val stormHazard: Int? = null,
            @JvmField val time: Date? = null
        )
    }
}
