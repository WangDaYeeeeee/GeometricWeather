package wangdaye.com.geometricweather.weather.json.china

import java.util.*

import kotlinx.serialization.Serializable
import wangdaye.com.geometricweather.common.serializer.DateSerializer

@Serializable
data class ChinaCurrent(
    @Serializable(DateSerializer::class) val pubTime: Date,
    val feelsLike: ChinaUnitValue?,
    val humidity: ChinaUnitValue?,
    val pressure: ChinaUnitValue?,
    val temperature: ChinaUnitValue?,
    val uvIndex: String?,
    val visibility: ChinaUnitValue?,
    val weather: String?,
    val wind: ChinaCurrentWind?
)