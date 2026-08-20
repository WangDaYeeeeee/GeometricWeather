@file:kotlinx.serialization.UseSerializers(wangdaye.com.geometricweather.common.json.GsonCompatibleDateSerializer::class)
package wangdaye.com.geometricweather.weather.json.accu

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class AccuDailyResult(
    @JvmField val Headline: Headline? = null,
    @JvmField val DailyForecasts: List<DailyForecasts>? = null
) {
    @Serializable
    class Headline(
        @JvmField val EffectiveDate: Date? = null,
        @JvmField val EffectiveEpochDate: Long = 0,
        @JvmField val Severity: Int = 0,
        @JvmField val Text: String? = null,
        @JvmField val Category: String? = null,
        @JvmField val EndDate: Date? = null,
        @JvmField val EndEpochDate: Long = 0,
        @JvmField val MobileLink: String? = null,
        @JvmField val Link: String? = null
    )

    @Serializable
    class DailyForecasts(
        @JvmField val Date: Date? = null,
        @JvmField val EpochDate: Long = 0,
        @JvmField val Sun: Sun? = null,
        @JvmField val Moon: Moon? = null,
        @JvmField val Temperature: Temperature? = null,
        @JvmField val RealFeelTemperature: RealFeelTemperature? = null,
        @JvmField val RealFeelTemperatureShade: RealFeelTemperatureShade? = null,
        @JvmField val HoursOfSun: Double = 0.0,
        @JvmField val DegreeDaySummary: DegreeDaySummary? = null,
        @JvmField val Day: Day? = null,
        @JvmField val Night: Night? = null,
        @JvmField val MobileLink: String? = null,
        @JvmField val Link: String? = null,
        @JvmField val AirAndPollen: List<AirAndPollen>? = null,
        @JvmField val Sources: List<String>? = null
    ) {
        @Serializable
        class Sun(
            @JvmField val Rise: Date? = null,
            @JvmField val EpochRise: Long = 0,
            @JvmField val Set: Date? = null,
            @JvmField val EpochSet: Long = 0
        )

        @Serializable
        class Moon(
            @JvmField val Rise: Date? = null,
            @JvmField val EpochRise: Long = 0,
            @JvmField val Set: Date? = null,
            @JvmField val EpochSet: Long = 0,
            @JvmField val Phase: String? = null,
            @JvmField val Age: Int = 0
        )

        @Serializable
        class Temperature(
            @JvmField val Minimum: Minimum? = null,
            @JvmField val Maximum: Maximum? = null
        ) {
            @Serializable
            class Minimum(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class Maximum(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class RealFeelTemperature(
            @JvmField val Minimum: Minimum? = null,
            @JvmField val Maximum: Maximum? = null
        ) {
            @Serializable
            class Minimum(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class Maximum(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class RealFeelTemperatureShade(
            @JvmField val Minimum: Minimum? = null,
            @JvmField val Maximum: Maximum? = null
        ) {
            @Serializable
            class Minimum(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class Maximum(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class DegreeDaySummary(
            @JvmField val Heating: Heating? = null,
            @JvmField val Cooling: Cooling? = null
        ) {
            @Serializable
            class Heating(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class Cooling(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class Day(
            @JvmField val Icon: Int = 0,
            @JvmField val IconPhrase: String? = null,
            @JvmField val LocalSource: LocalSource? = null,
            @JvmField val ShortPhrase: String? = null,
            @JvmField val LongPhrase: String? = null,
            @JvmField val PrecipitationProbability: Int = 0,
            @JvmField val ThunderstormProbability: Int = 0,
            @JvmField val RainProbability: Int = 0,
            @JvmField val SnowProbability: Int = 0,
            @JvmField val IceProbability: Int = 0,
            @JvmField val Wind: Wind? = null,
            @JvmField val WindGust: WindGust? = null,
            @JvmField val TotalLiquid: TotalLiquid? = null,
            @JvmField val Rain: Rain? = null,
            @JvmField val Snow: Snow? = null,
            @JvmField val Ice: Ice? = null,
            @JvmField val HoursOfPrecipitation: Double = 0.0,
            @JvmField val HoursOfRain: Double = 0.0,
            @JvmField val HoursOfSnow: Double = 0.0,
            @JvmField val HoursOfIce: Double = 0.0,
            @JvmField val CloudCover: Int = 0
        ) {
            @Serializable
            class LocalSource(
                @JvmField val Id: Int = 0,
                @JvmField val Name: String? = null,
                @JvmField val WeatherCode: String? = null
            )

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

        @Serializable
        class Night(
            @JvmField val Icon: Int = 0,
            @JvmField val IconPhrase: String? = null,
            @JvmField val LocalSource: LocalSource? = null,
            @JvmField val ShortPhrase: String? = null,
            @JvmField val LongPhrase: String? = null,
            @JvmField val PrecipitationProbability: Int = 0,
            @JvmField val ThunderstormProbability: Int = 0,
            @JvmField val RainProbability: Int = 0,
            @JvmField val SnowProbability: Int = 0,
            @JvmField val IceProbability: Int = 0,
            @JvmField val Wind: Wind? = null,
            @JvmField val WindGust: WindGust? = null,
            @JvmField val TotalLiquid: TotalLiquid? = null,
            @JvmField val Rain: Rain? = null,
            @JvmField val Snow: Snow? = null,
            @JvmField val Ice: Ice? = null,
            @JvmField val HoursOfPrecipitation: Double = 0.0,
            @JvmField val HoursOfRain: Double = 0.0,
            @JvmField val HoursOfSnow: Double = 0.0,
            @JvmField val HoursOfIce: Double = 0.0,
            @JvmField val CloudCover: Int = 0
        ) {
            @Serializable
            class LocalSource(
                @JvmField val Id: Int = 0,
                @JvmField val Name: String? = null,
                @JvmField val WeatherCode: String? = null
            )

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

        @Serializable
        class AirAndPollen(
            @JvmField val Name: String? = null,
            @JvmField val Value: Int = 0,
            @JvmField val Category: String? = null,
            @JvmField val CategoryValue: Int = 0,
            @JvmField val Type: String? = null
        )
    }
}
