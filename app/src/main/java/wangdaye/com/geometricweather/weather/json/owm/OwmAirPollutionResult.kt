package wangdaye.com.geometricweather.weather.json.owm

import kotlinx.serialization.Serializable

@Serializable
class OwmAirPollutionResult(
    @JvmField val list: List<AirPollution>? = null
) {
    @Serializable
    class AirPollution(
        @JvmField val dt: Long = 0,
        @JvmField val main: Main? = null,
        @JvmField val components: Components? = null
    ) {
        @Serializable
        class Main(
            @JvmField val aqi: Int = 0
        )

        @Serializable
        class Components(
            @JvmField val co: Double = 0.0,
            @JvmField val no: Double = 0.0,
            @JvmField val no2: Double = 0.0,
            @JvmField val o3: Double = 0.0,
            @JvmField val so2: Double = 0.0,
            @JvmField val pm2_5: Double = 0.0,
            @JvmField val pm10: Double = 0.0,
            @JvmField val nh3: Double = 0.0
        )
    }
}
