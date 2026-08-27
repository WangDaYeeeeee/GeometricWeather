package wangdaye.com.geometricweather.common.basic.models.weather

import java.io.Serializable

class HalfDay(
    val weatherText: String,
    val weatherPhase: String,
    val weatherCode: WeatherCode,
    val temperature: Temperature,
    val precipitation: Precipitation,
    val precipitationProbability: PrecipitationProbability,
    val precipitationDuration: PrecipitationDuration,
    val wind: Wind,
    val cloudCover: Int?
) : Serializable
