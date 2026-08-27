package wangdaye.com.geometricweather.common.basic.models.weather

import wangdaye.com.geometricweather.common.basic.models.options.unit.DistanceUnit
import wangdaye.com.geometricweather.common.basic.models.options.unit.RelativeHumidityUnit
import wangdaye.com.geometricweather.common.basic.models.options.unit.TemperatureUnit
import java.io.Serializable

/**
 * Current.
 *
 * default unit
 * [relativeHumidity] : [RelativeHumidityUnit.PERCENT]
 * dewPoint : [TemperatureUnit.C]
 * [visibility] : [DistanceUnit.KM]
 * [ceiling] : [DistanceUnit.KM]
 */
class Current(
    val weatherText: String,
    val weatherCode: WeatherCode,
    val temperature: Temperature,
    val precipitation: Precipitation,
    val precipitationProbability: PrecipitationProbability,
    val wind: Wind,
    @get:JvmName("getUV") val uv: UV,
    val airQuality: AirQuality,
    val relativeHumidity: Float?,
    val pressure: Float?,
    val visibility: Float?,
    val dewPoint: Int?,
    val cloudCover: Int?,
    val ceiling: Float?,
    val dailyForecast: String?,
    val hourlyForecast: String?
) : Serializable
