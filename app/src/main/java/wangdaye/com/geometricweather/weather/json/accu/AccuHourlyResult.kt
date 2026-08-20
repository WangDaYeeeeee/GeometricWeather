@file:kotlinx.serialization.UseSerializers(wangdaye.com.geometricweather.common.json.GsonCompatibleDateSerializer::class)
package wangdaye.com.geometricweather.weather.json.accu

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class AccuHourlyResult(
    @JvmField val DateTime: Date? = null,
    @JvmField val EpochDateTime: Long = 0,
    @JvmField val WeatherIcon: Int = 0,
    @JvmField val IconPhrase: String? = null,
    @JvmField val IsDaylight: Boolean = false,
    @JvmField val Temperature: Temperature? = null,
    @JvmField val RealFeelTemperature: RealFeelTemperature? = null,
    @JvmField val RealFeelTemperatureShade: RealFeelTemperatureShade? = null,
    @JvmField val WetBulbTemperature: WetBulbTemperature? = null,
    @JvmField val PrecipitationProbability: Int = 0,
    @JvmField val ThunderstormProbability: Int = 0,
    @JvmField val RainProbability: Int = 0,
    @JvmField val SnowProbability: Int = 0,
    @JvmField val IceProbability: Int = 0,
    @JvmField val Wind: Wind? = null,
    @JvmField val WindGust: WindGust? = null,
    @JvmField val UVIndex: Int = 0,
    @JvmField val UVIndexText: String? = null,
    @JvmField val TotalLiquid: TotalLiquid? = null,
    @JvmField val Rain: Rain? = null,
    @JvmField val Snow: Snow? = null,
    @JvmField val Ice: Ice? = null,
    @JvmField val MobileLink: String? = null,
    @JvmField val Link: String? = null
) {
    @Serializable
    class Wind(
        @JvmField val Speed: Speed? = null,
        @JvmField val Direction: Direction? = null
    ) {
        @Serializable
        class Speed(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class Direction(
            @JvmField val Degrees: Int = 0,
            @JvmField val Localized: String? = null,
            @JvmField val English: String? = null
        )
    }

    @Serializable
    class WindGust(
        @JvmField val Speed: Speed? = null,
        @JvmField val Direction: Direction? = null
    ) {
        @Serializable
        class Speed(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class Direction(
            @JvmField val Degrees: Int = 0,
            @JvmField val Localized: String? = null,
            @JvmField val English: String? = null
        )
    }

    @Serializable
    class Temperature(
        @JvmField val Value: Double = 0.0,
        @JvmField val Unit: String? = null,
        @JvmField val UnitType: Int = 0
    )

    @Serializable
    class RealFeelTemperature(
        @JvmField val Value: Double = 0.0,
        @JvmField val Unit: String? = null,
        @JvmField val UnitType: Int = 0
    )

    @Serializable
    class RealFeelTemperatureShade(
        @JvmField val Value: Double = 0.0,
        @JvmField val Unit: String? = null,
        @JvmField val UnitType: Int = 0
    )

    @Serializable
    class WetBulbTemperature(
        @JvmField val Value: Double = 0.0,
        @JvmField val Unit: String? = null,
        @JvmField val UnitType: Int = 0
    )

    @Serializable
    class TotalLiquid(
        @JvmField val Value: Double = 0.0,
        @JvmField val Unit: String? = null,
        @JvmField val UnitType: Int = 0
    )

    @Serializable
    class Rain(
        @JvmField val Value: Double = 0.0,
        @JvmField val Unit: String? = null,
        @JvmField val UnitType: Int = 0
    )

    @Serializable
    class Snow(
        @JvmField val Value: Double = 0.0,
        @JvmField val Unit: String? = null,
        @JvmField val UnitType: Int = 0
    )

    @Serializable
    class Ice(
        @JvmField val Value: Double = 0.0,
        @JvmField val Unit: String? = null,
        @JvmField val UnitType: Int = 0
    )
}
