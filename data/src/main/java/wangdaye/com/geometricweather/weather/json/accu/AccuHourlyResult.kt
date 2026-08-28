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
    @JvmField val Temperature: TemperatureBean? = null,
    @JvmField val RealFeelTemperature: RealFeelTemperatureBean? = null,
    @JvmField val RealFeelTemperatureShade: RealFeelTemperatureShadeBean? = null,
    @JvmField val WetBulbTemperature: WetBulbTemperatureBean? = null,
    @JvmField val PrecipitationProbability: Int = 0,
    @JvmField val ThunderstormProbability: Int = 0,
    @JvmField val RainProbability: Int = 0,
    @JvmField val SnowProbability: Int = 0,
    @JvmField val IceProbability: Int = 0,
    @JvmField val Wind: WindBean? = null,
    @JvmField val WindGust: WindGustBean? = null,
    @JvmField val UVIndex: Int = 0,
    @JvmField val UVIndexText: String? = null,
    @JvmField val TotalLiquid: TotalLiquidBean? = null,
    @JvmField val Rain: RainBean? = null,
    @JvmField val Snow: SnowBean? = null,
    @JvmField val Ice: IceBean? = null,
    @JvmField val MobileLink: String? = null,
    @JvmField val Link: String? = null
) {
    @Serializable
    class WindBean(
        @JvmField val Speed: SpeedBean? = null,
        @JvmField val Direction: DirectionBean? = null
    ) {
        @Serializable
        class SpeedBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class DirectionBean(
            @JvmField val Degrees: Int = 0,
            @JvmField val Localized: String? = null,
            @JvmField val English: String? = null
        )
    }

    @Serializable
    class WindGustBean(
        @JvmField val Speed: SpeedBean? = null,
        @JvmField val Direction: DirectionBean? = null
    ) {
        @Serializable
        class SpeedBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class DirectionBean(
            @JvmField val Degrees: Int = 0,
            @JvmField val Localized: String? = null,
            @JvmField val English: String? = null
        )
    }

    @Serializable
    class TemperatureBean(
        @JvmField val Value: Double = 0.0,
        @JvmField val Unit: String? = null,
        @JvmField val UnitType: Int = 0
    )

    @Serializable
    class RealFeelTemperatureBean(
        @JvmField val Value: Double = 0.0,
        @JvmField val Unit: String? = null,
        @JvmField val UnitType: Int = 0
    )

    @Serializable
    class RealFeelTemperatureShadeBean(
        @JvmField val Value: Double = 0.0,
        @JvmField val Unit: String? = null,
        @JvmField val UnitType: Int = 0
    )

    @Serializable
    class WetBulbTemperatureBean(
        @JvmField val Value: Double = 0.0,
        @JvmField val Unit: String? = null,
        @JvmField val UnitType: Int = 0
    )

    @Serializable
    class TotalLiquidBean(
        @JvmField val Value: Double = 0.0,
        @JvmField val Unit: String? = null,
        @JvmField val UnitType: Int = 0
    )

    @Serializable
    class RainBean(
        @JvmField val Value: Double = 0.0,
        @JvmField val Unit: String? = null,
        @JvmField val UnitType: Int = 0
    )

    @Serializable
    class SnowBean(
        @JvmField val Value: Double = 0.0,
        @JvmField val Unit: String? = null,
        @JvmField val UnitType: Int = 0
    )

    @Serializable
    class IceBean(
        @JvmField val Value: Double = 0.0,
        @JvmField val Unit: String? = null,
        @JvmField val UnitType: Int = 0
    )
}
