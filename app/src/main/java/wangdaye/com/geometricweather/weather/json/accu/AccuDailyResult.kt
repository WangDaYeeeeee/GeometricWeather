@file:kotlinx.serialization.UseSerializers(wangdaye.com.geometricweather.common.json.GsonCompatibleDateSerializer::class)
package wangdaye.com.geometricweather.weather.json.accu

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class AccuDailyResult(
    @JvmField val Headline: HeadlineBean? = null,
    @JvmField val DailyForecasts: List<DailyForecastsBean>? = null
) {
    @Serializable
    class HeadlineBean(
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
    class DailyForecastsBean(
        @JvmField val Date: Date? = null,
        @JvmField val EpochDate: Long = 0,
        @JvmField val Sun: SunBean? = null,
        @JvmField val Moon: MoonBean? = null,
        @JvmField val Temperature: TemperatureBean? = null,
        @JvmField val RealFeelTemperature: RealFeelTemperatureBean? = null,
        @JvmField val RealFeelTemperatureShade: RealFeelTemperatureShadeBean? = null,
        @JvmField val HoursOfSun: Double = 0.0,
        @JvmField val DegreeDaySummary: DegreeDaySummaryBean? = null,
        @JvmField val Day: DayBean? = null,
        @JvmField val Night: NightBean? = null,
        @JvmField val MobileLink: String? = null,
        @JvmField val Link: String? = null,
        @JvmField val AirAndPollen: List<AirAndPollenBean>? = null,
        @JvmField val Sources: List<String>? = null
    ) {
        @Serializable
        class SunBean(
            @JvmField val Rise: Date? = null,
            @JvmField val EpochRise: Long = 0,
            @JvmField val Set: Date? = null,
            @JvmField val EpochSet: Long = 0
        )

        @Serializable
        class MoonBean(
            @JvmField val Rise: Date? = null,
            @JvmField val EpochRise: Long = 0,
            @JvmField val Set: Date? = null,
            @JvmField val EpochSet: Long = 0,
            @JvmField val Phase: String? = null,
            @JvmField val Age: Int = 0
        )

        @Serializable
        class TemperatureBean(
            @JvmField val Minimum: MinimumBean? = null,
            @JvmField val Maximum: MaximumBean? = null
        ) {
            @Serializable
            class MinimumBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class MaximumBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class RealFeelTemperatureBean(
            @JvmField val Minimum: MinimumBean? = null,
            @JvmField val Maximum: MaximumBean? = null
        ) {
            @Serializable
            class MinimumBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class MaximumBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class RealFeelTemperatureShadeBean(
            @JvmField val Minimum: MinimumBean? = null,
            @JvmField val Maximum: MaximumBean? = null
        ) {
            @Serializable
            class MinimumBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class MaximumBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class DegreeDaySummaryBean(
            @JvmField val Heating: HeatingBean? = null,
            @JvmField val Cooling: CoolingBean? = null
        ) {
            @Serializable
            class HeatingBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class CoolingBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class DayBean(
            @JvmField val Icon: Int = 0,
            @JvmField val IconPhrase: String? = null,
            @JvmField val LocalSource: LocalSourceBean? = null,
            @JvmField val ShortPhrase: String? = null,
            @JvmField val LongPhrase: String? = null,
            @JvmField val PrecipitationProbability: Int = 0,
            @JvmField val ThunderstormProbability: Int = 0,
            @JvmField val RainProbability: Int = 0,
            @JvmField val SnowProbability: Int = 0,
            @JvmField val IceProbability: Int = 0,
            @JvmField val Wind: WindBean? = null,
            @JvmField val WindGust: WindGustBean? = null,
            @JvmField val TotalLiquid: TotalLiquidBean? = null,
            @JvmField val Rain: RainBean? = null,
            @JvmField val Snow: SnowBean? = null,
            @JvmField val Ice: IceBean? = null,
            @JvmField val HoursOfPrecipitation: Double = 0.0,
            @JvmField val HoursOfRain: Double = 0.0,
            @JvmField val HoursOfSnow: Double = 0.0,
            @JvmField val HoursOfIce: Double = 0.0,
            @JvmField val CloudCover: Int = 0
        ) {
            @Serializable
            class LocalSourceBean(
                @JvmField val Id: Int = 0,
                @JvmField val Name: String? = null,
                @JvmField val WeatherCode: String? = null
            )

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

        @Serializable
        class NightBean(
            @JvmField val Icon: Int = 0,
            @JvmField val IconPhrase: String? = null,
            @JvmField val LocalSource: LocalSourceBean? = null,
            @JvmField val ShortPhrase: String? = null,
            @JvmField val LongPhrase: String? = null,
            @JvmField val PrecipitationProbability: Int = 0,
            @JvmField val ThunderstormProbability: Int = 0,
            @JvmField val RainProbability: Int = 0,
            @JvmField val SnowProbability: Int = 0,
            @JvmField val IceProbability: Int = 0,
            @JvmField val Wind: WindBean? = null,
            @JvmField val WindGust: WindGustBean? = null,
            @JvmField val TotalLiquid: TotalLiquidBean? = null,
            @JvmField val Rain: RainBean? = null,
            @JvmField val Snow: SnowBean? = null,
            @JvmField val Ice: IceBean? = null,
            @JvmField val HoursOfPrecipitation: Double = 0.0,
            @JvmField val HoursOfRain: Double = 0.0,
            @JvmField val HoursOfSnow: Double = 0.0,
            @JvmField val HoursOfIce: Double = 0.0,
            @JvmField val CloudCover: Int = 0
        ) {
            @Serializable
            class LocalSourceBean(
                @JvmField val Id: Int = 0,
                @JvmField val Name: String? = null,
                @JvmField val WeatherCode: String? = null
            )

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

        @Serializable
        class AirAndPollenBean(
            @JvmField val Name: String? = null,
            @JvmField val Value: Int = 0,
            @JvmField val Category: String? = null,
            @JvmField val CategoryValue: Int = 0,
            @JvmField val Type: String? = null
        )
    }
}
