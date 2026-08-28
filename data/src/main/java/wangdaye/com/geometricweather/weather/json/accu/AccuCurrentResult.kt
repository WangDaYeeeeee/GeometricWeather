@file:kotlinx.serialization.UseSerializers(wangdaye.com.geometricweather.common.json.GsonCompatibleDateSerializer::class)
package wangdaye.com.geometricweather.weather.json.accu

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class AccuCurrentResult(
    @JvmField val LocalObservationDateTime: Date? = null,
    @JvmField val EpochTime: Long = 0,
    @JvmField val WeatherText: String? = null,
    @JvmField val WeatherIcon: Int = 0,
    @JvmField val LocalSource: LocalSourceBean? = null,
    @JvmField val IsDayTime: Boolean = false,
    @JvmField val Temperature: TemperatureBean? = null,
    @JvmField val RealFeelTemperature: RealFeelTemperatureBean? = null,
    @JvmField val RealFeelTemperatureShade: RealFeelTemperatureShadeBean? = null,
    @JvmField val RelativeHumidity: Int = 0,
    @JvmField val DewPoint: DewPointBean? = null,
    @JvmField val Wind: WindBean? = null,
    @JvmField val WindGust: WindGustBean? = null,
    @JvmField val UVIndex: Int = 0,
    @JvmField val UVIndexText: String? = null,
    @JvmField val Visibility: VisibilityBean? = null,
    @JvmField val ObstructionsToVisibility: String? = null,
    @JvmField val CloudCover: Int = 0,
    @JvmField val Ceiling: CeilingBean? = null,
    @JvmField val Pressure: PressureBean? = null,
    @JvmField val PressureTendency: PressureTendencyBean? = null,
    @JvmField val Past24HourTemperatureDeparture: Past24HourTemperatureDepartureBean? = null,
    @JvmField val ApparentTemperature: ApparentTemperatureBean? = null,
    @JvmField val WindChillTemperature: WindChillTemperatureBean? = null,
    @JvmField val WetBulbTemperature: WetBulbTemperatureBean? = null,
    @JvmField val Precip1hr: Precip1hrBean? = null,
    @JvmField val PrecipitationSummary: PrecipitationSummaryBean? = null,
    @JvmField val TemperatureSummary: TemperatureSummaryBean? = null,
    @JvmField val MobileLink: String? = null,
    @JvmField val Link: String? = null
) {
    @Serializable
    class LocalSourceBean(
        @JvmField val Id: Int = 0,
        @JvmField val Name: String? = null,
        @JvmField val WeatherCode: String? = null
    )

    @Serializable
    class TemperatureBean(
        @JvmField val Metric: MetricBean? = null,
        @JvmField val Imperial: ImperialBean? = null
    ) {
        @Serializable
        class MetricBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class ImperialBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class RealFeelTemperatureBean(
        @JvmField val Metric: MetricBean? = null,
        @JvmField val Imperial: ImperialBean? = null
    ) {
        @Serializable
        class MetricBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class ImperialBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class RealFeelTemperatureShadeBean(
        @JvmField val Metric: MetricBean? = null,
        @JvmField val Imperial: ImperialBean? = null
    ) {
        @Serializable
        class MetricBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class ImperialBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class DewPointBean(
        @JvmField val Metric: MetricBean? = null,
        @JvmField val Imperial: ImperialBean? = null
    ) {
        @Serializable
        class MetricBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class ImperialBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class WindBean(
        @JvmField val Direction: DirectionBean? = null,
        @JvmField val Speed: SpeedBean? = null
    ) {
        @Serializable
        class DirectionBean(
            @JvmField val Degrees: Int = 0,
            @JvmField val Localized: String? = null,
            @JvmField val English: String? = null
        )

        @Serializable
        class SpeedBean(
            @JvmField val Metric: MetricBean? = null,
            @JvmField val Imperial: ImperialBean? = null
        ) {
            @Serializable
            class MetricBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class ImperialBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }
    }

    @Serializable
    class WindGustBean(
        @JvmField val Speed: SpeedBean? = null
    ) {
        @Serializable
        class SpeedBean(
            @JvmField val Metric: MetricBean? = null,
            @JvmField val Imperial: ImperialBean? = null
        ) {
            @Serializable
            class MetricBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class ImperialBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }
    }

    @Serializable
    class VisibilityBean(
        @JvmField val Metric: MetricBean? = null,
        @JvmField val Imperial: ImperialBean? = null
    ) {
        @Serializable
        class MetricBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class ImperialBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class CeilingBean(
        @JvmField val Metric: MetricBean? = null,
        @JvmField val Imperial: ImperialBean? = null
    ) {
        @Serializable
        class MetricBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class ImperialBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class PressureBean(
        @JvmField val Metric: MetricBean? = null,
        @JvmField val Imperial: ImperialBean? = null
    ) {
        @Serializable
        class MetricBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class ImperialBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class PressureTendencyBean(
        @JvmField val LocalizedText: String? = null,
        @JvmField val Code: String? = null
    )

    @Serializable
    class Past24HourTemperatureDepartureBean(
        @JvmField val Metric: MetricBean? = null,
        @JvmField val Imperial: ImperialBean? = null
    ) {
        @Serializable
        class MetricBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class ImperialBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class ApparentTemperatureBean(
        @JvmField val Metric: MetricBean? = null,
        @JvmField val Imperial: ImperialBean? = null
    ) {
        @Serializable
        class MetricBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class ImperialBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class WindChillTemperatureBean(
        @JvmField val Metric: MetricBean? = null,
        @JvmField val Imperial: ImperialBean? = null
    ) {
        @Serializable
        class MetricBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class ImperialBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class WetBulbTemperatureBean(
        @JvmField val Metric: MetricBean? = null,
        @JvmField val Imperial: ImperialBean? = null
    ) {
        @Serializable
        class MetricBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class ImperialBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class Precip1hrBean(
        @JvmField val Metric: MetricBean? = null,
        @JvmField val Imperial: ImperialBean? = null
    ) {
        @Serializable
        class MetricBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class ImperialBean(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class PrecipitationSummaryBean(
        @JvmField val Precipitation: PrecipitationBean? = null,
        @JvmField val PastHour: PastHourBean? = null,
        @JvmField val Past3Hours: Past3HoursBean? = null,
        @JvmField val Past6Hours: Past6HoursBean? = null,
        @JvmField val Past9Hours: Past9HoursBean? = null,
        @JvmField val Past12Hours: Past12HoursBean? = null,
        @JvmField val Past18Hours: Past18HoursBean? = null,
        @JvmField val Past24Hours: Past24HoursBean? = null
    ) {
        @Serializable
        class PrecipitationBean(
            @JvmField val Metric: MetricBean? = null,
            @JvmField val Imperial: ImperialBean? = null
        ) {
            @Serializable
            class MetricBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class ImperialBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class PastHourBean(
            @JvmField val Metric: MetricBean? = null,
            @JvmField val Imperial: ImperialBean? = null
        ) {
            @Serializable
            class MetricBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class ImperialBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class Past3HoursBean(
            @JvmField val Metric: MetricBean? = null,
            @JvmField val Imperial: ImperialBean? = null
        ) {
            @Serializable
            class MetricBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class ImperialBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class Past6HoursBean(
            @JvmField val Metric: MetricBean? = null,
            @JvmField val Imperial: ImperialBean? = null
        ) {
            @Serializable
            class MetricBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class ImperialBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class Past9HoursBean(
            @JvmField val Metric: MetricBean? = null,
            @JvmField val Imperial: ImperialBean? = null
        ) {
            @Serializable
            class MetricBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class ImperialBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class Past12HoursBean(
            @JvmField val Metric: MetricBean? = null,
            @JvmField val Imperial: ImperialBean? = null
        ) {
            @Serializable
            class MetricBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class ImperialBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class Past18HoursBean(
            @JvmField val Metric: MetricBean? = null,
            @JvmField val Imperial: ImperialBean? = null
        ) {
            @Serializable
            class MetricBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class ImperialBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class Past24HoursBean(
            @JvmField val Metric: MetricBean? = null,
            @JvmField val Imperial: ImperialBean? = null
        ) {
            @Serializable
            class MetricBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class ImperialBean(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }
    }

    @Serializable
    class TemperatureSummaryBean(
        @JvmField val Past6HourRange: Past6HourRangeBean? = null,
        @JvmField val Past12HourRange: Past12HourRangeBean? = null,
        @JvmField val Past24HourRange: Past24HourRangeBean? = null
    ) {
        @Serializable
        class Past6HourRangeBean(
            @JvmField val Minimum: MinimumBean? = null,
            @JvmField val Maximum: MaximumBean? = null
        ) {
            @Serializable
            class MinimumBean(
                @JvmField val Metric: MetricBean? = null,
                @JvmField val Imperial: ImperialBean? = null
            ) {
                @Serializable
                class MetricBean(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )

                @Serializable
                class ImperialBean(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )
            }

            @Serializable
            class MaximumBean(
                @JvmField val Metric: MetricBean? = null,
                @JvmField val Imperial: ImperialBean? = null
            ) {
                @Serializable
                class MetricBean(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )

                @Serializable
                class ImperialBean(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )
            }
        }

        @Serializable
        class Past12HourRangeBean(
            @JvmField val Minimum: MinimumBean? = null,
            @JvmField val Maximum: MaximumBean? = null
        ) {
            @Serializable
            class MinimumBean(
                @JvmField val Metric: MetricBean? = null,
                @JvmField val Imperial: ImperialBean? = null
            ) {
                @Serializable
                class MetricBean(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )

                @Serializable
                class ImperialBean(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )
            }

            @Serializable
            class MaximumBean(
                @JvmField val Metric: MetricBean? = null,
                @JvmField val Imperial: ImperialBean? = null
            ) {
                @Serializable
                class MetricBean(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )

                @Serializable
                class ImperialBean(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )
            }
        }

        @Serializable
        class Past24HourRangeBean(
            @JvmField val Minimum: MinimumBean? = null,
            @JvmField val Maximum: MaximumBean? = null
        ) {
            @Serializable
            class MinimumBean(
                @JvmField val Metric: MetricBean? = null,
                @JvmField val Imperial: ImperialBean? = null
            ) {
                @Serializable
                class MetricBean(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )

                @Serializable
                class ImperialBean(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )
            }

            @Serializable
            class MaximumBean(
                @JvmField val Metric: MetricBean? = null,
                @JvmField val Imperial: ImperialBean? = null
            ) {
                @Serializable
                class MetricBean(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )

                @Serializable
                class ImperialBean(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )
            }
        }
    }
}
