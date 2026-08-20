package wangdaye.com.geometricweather.weather.json.owm

import kotlinx.serialization.Serializable

@Serializable
class OwmLocationResult(
    @JvmField val name: String? = null,
    @JvmField val lat: Double = 0.0,
    @JvmField val lon: Double = 0.0,
    @JvmField val country: String? = null
)
