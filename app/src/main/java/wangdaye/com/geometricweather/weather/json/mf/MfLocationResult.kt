package wangdaye.com.geometricweather.weather.json.mf

import kotlinx.serialization.Serializable

@Serializable
class MfLocationResult(
    @JvmField val insee: String? = null,
    @JvmField val name: String? = null,
    @JvmField val lat: Double = 0.0,
    @JvmField val lon: Double = 0.0,
    @JvmField val country: String? = null,
    @JvmField val admin: String? = null,
    @JvmField val admin2: String? = null,
    @JvmField val postCode: String? = null
)
