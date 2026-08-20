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
    @JvmField val LocalSource: LocalSource? = null,
    @JvmField val IsDayTime: Boolean = false,
    @JvmField val Temperature: Temperature? = null,
    @JvmField val RealFeelTemperature: RealFeelTemperature? = null,
    @JvmField val RealFeelTemperatureShade: RealFeelTemperatureShade? = null,
    @JvmField val RelativeHumidity: Int = 0,
    @JvmField val DewPoint: DewPoint? = null,
    @JvmField val Wind: Wind? = null,
    @JvmField val WindGust: WindGust? = null,
    @JvmField val UVIndex: Int = 0,
    @JvmField val UVIndexText: String? = null,
    @JvmField val Visibility: Visibility? = null,
    @JvmField val ObstructionsToVisibility: String? = null,
    @JvmField val CloudCover: Int = 0,
    @JvmField val Ceiling: Ceiling? = null,
    @JvmField val Pressure: Pressure? = null,
    @JvmField val PressureTendency: PressureTendency? = null,
    @JvmField val Past24HourTemperatureDeparture: Past24HourTemperatureDeparture? = null,
    @JvmField val ApparentTemperature: ApparentTemperature? = null,
    @JvmField val WindChillTemperature: WindChillTemperature? = null,
    @JvmField val WetBulbTemperature: WetBulbTemperature? = null,
    @JvmField val Precip1hr: Precip1hr? = null,
    @JvmField val PrecipitationSummary: PrecipitationSummary? = null,
    @JvmField val TemperatureSummary: TemperatureSummary? = null,
    @JvmField val MobileLink: String? = null,
    @JvmField val Link: String? = null
) {
    @Serializable
    class LocalSource(
        @JvmField val Id: Int = 0,
        @JvmField val Name: String? = null,
        @JvmField val WeatherCode: String? = null
    )

    @Serializable
    class Temperature(
        @JvmField val Metric: Metric? = null,
        @JvmField val Imperial: Imperial? = null
    ) {
        @Serializable
        class Metric(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class Imperial(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class RealFeelTemperature(
        @JvmField val Metric: Metric? = null,
        @JvmField val Imperial: Imperial? = null
    ) {
        @Serializable
        class Metric(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class Imperial(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class RealFeelTemperatureShade(
        @JvmField val Metric: Metric? = null,
        @JvmField val Imperial: Imperial? = null
    ) {
        @Serializable
        class Metric(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class Imperial(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class DewPoint(
        @JvmField val Metric: Metric? = null,
        @JvmField val Imperial: Imperial? = null
    ) {
        @Serializable
        class Metric(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class Imperial(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class Wind(
        @JvmField val Direction: Direction? = null,
        @JvmField val Speed: Speed? = null
    ) {
        @Serializable
        class Direction(
            @JvmField val Degrees: Int = 0,
            @JvmField val Localized: String? = null,
            @JvmField val English: String? = null
        )

        @Serializable
        class Speed(
            @JvmField val Metric: Metric? = null,
            @JvmField val Imperial: Imperial? = null
        ) {
            @Serializable
            class Metric(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class Imperial(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }
    }

    @Serializable
    class WindGust(
        @JvmField val Speed: Speed? = null
    ) {
        @Serializable
        class Speed(
            @JvmField val Metric: Metric? = null,
            @JvmField val Imperial: Imperial? = null
        ) {
            @Serializable
            class Metric(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class Imperial(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }
    }

    @Serializable
    class Visibility(
        @JvmField val Metric: Metric? = null,
        @JvmField val Imperial: Imperial? = null
    ) {
        @Serializable
        class Metric(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class Imperial(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class Ceiling(
        @JvmField val Metric: Metric? = null,
        @JvmField val Imperial: Imperial? = null
    ) {
        @Serializable
        class Metric(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class Imperial(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class Pressure(
        @JvmField val Metric: Metric? = null,
        @JvmField val Imperial: Imperial? = null
    ) {
        @Serializable
        class Metric(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class Imperial(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class PressureTendency(
        @JvmField val LocalizedText: String? = null,
        @JvmField val Code: String? = null
    )

    @Serializable
    class Past24HourTemperatureDeparture(
        @JvmField val Metric: Metric? = null,
        @JvmField val Imperial: Imperial? = null
    ) {
        @Serializable
        class Metric(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class Imperial(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class ApparentTemperature(
        @JvmField val Metric: Metric? = null,
        @JvmField val Imperial: Imperial? = null
    ) {
        @Serializable
        class Metric(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class Imperial(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class WindChillTemperature(
        @JvmField val Metric: Metric? = null,
        @JvmField val Imperial: Imperial? = null
    ) {
        @Serializable
        class Metric(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class Imperial(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class WetBulbTemperature(
        @JvmField val Metric: Metric? = null,
        @JvmField val Imperial: Imperial? = null
    ) {
        @Serializable
        class Metric(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class Imperial(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class Precip1hr(
        @JvmField val Metric: Metric? = null,
        @JvmField val Imperial: Imperial? = null
    ) {
        @Serializable
        class Metric(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )

        @Serializable
        class Imperial(
            @JvmField val Value: Double = 0.0,
            @JvmField val Unit: String? = null,
            @JvmField val UnitType: Int = 0
        )
    }

    @Serializable
    class PrecipitationSummary(
        @JvmField val Precipitation: Precipitation? = null,
        @JvmField val PastHour: PastHour? = null,
        @JvmField val Past3Hours: Past3Hours? = null,
        @JvmField val Past6Hours: Past6Hours? = null,
        @JvmField val Past9Hours: Past9Hours? = null,
        @JvmField val Past12Hours: Past12Hours? = null,
        @JvmField val Past18Hours: Past18Hours? = null,
        @JvmField val Past24Hours: Past24Hours? = null
    ) {
        @Serializable
        class Precipitation(
            @JvmField val Metric: Metric? = null,
            @JvmField val Imperial: Imperial? = null
        ) {
            @Serializable
            class Metric(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class Imperial(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class PastHour(
            @JvmField val Metric: Metric? = null,
            @JvmField val Imperial: Imperial? = null
        ) {
            @Serializable
            class Metric(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class Imperial(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class Past3Hours(
            @JvmField val Metric: Metric? = null,
            @JvmField val Imperial: Imperial? = null
        ) {
            @Serializable
            class Metric(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class Imperial(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class Past6Hours(
            @JvmField val Metric: Metric? = null,
            @JvmField val Imperial: Imperial? = null
        ) {
            @Serializable
            class Metric(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class Imperial(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class Past9Hours(
            @JvmField val Metric: Metric? = null,
            @JvmField val Imperial: Imperial? = null
        ) {
            @Serializable
            class Metric(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class Imperial(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class Past12Hours(
            @JvmField val Metric: Metric? = null,
            @JvmField val Imperial: Imperial? = null
        ) {
            @Serializable
            class Metric(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class Imperial(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class Past18Hours(
            @JvmField val Metric: Metric? = null,
            @JvmField val Imperial: Imperial? = null
        ) {
            @Serializable
            class Metric(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class Imperial(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }

        @Serializable
        class Past24Hours(
            @JvmField val Metric: Metric? = null,
            @JvmField val Imperial: Imperial? = null
        ) {
            @Serializable
            class Metric(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )

            @Serializable
            class Imperial(
                @JvmField val Value: Double = 0.0,
                @JvmField val Unit: String? = null,
                @JvmField val UnitType: Int = 0
            )
        }
    }

    @Serializable
    class TemperatureSummary(
        @JvmField val Past6HourRange: Past6HourRange? = null,
        @JvmField val Past12HourRange: Past12HourRange? = null,
        @JvmField val Past24HourRange: Past24HourRange? = null
    ) {
        @Serializable
        class Past6HourRange(
            @JvmField val Minimum: Minimum? = null,
            @JvmField val Maximum: Maximum? = null
        ) {
            @Serializable
            class Minimum(
                @JvmField val Metric: Metric? = null,
                @JvmField val Imperial: Imperial? = null
            ) {
                @Serializable
                class Metric(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )

                @Serializable
                class Imperial(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )
            }

            @Serializable
            class Maximum(
                @JvmField val Metric: Metric? = null,
                @JvmField val Imperial: Imperial? = null
            ) {
                @Serializable
                class Metric(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )

                @Serializable
                class Imperial(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )
            }
        }

        @Serializable
        class Past12HourRange(
            @JvmField val Minimum: Minimum? = null,
            @JvmField val Maximum: Maximum? = null
        ) {
            @Serializable
            class Minimum(
                @JvmField val Metric: Metric? = null,
                @JvmField val Imperial: Imperial? = null
            ) {
                @Serializable
                class Metric(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )

                @Serializable
                class Imperial(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )
            }

            @Serializable
            class Maximum(
                @JvmField val Metric: Metric? = null,
                @JvmField val Imperial: Imperial? = null
            ) {
                @Serializable
                class Metric(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )

                @Serializable
                class Imperial(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )
            }
        }

        @Serializable
        class Past24HourRange(
            @JvmField val Minimum: Minimum? = null,
            @JvmField val Maximum: Maximum? = null
        ) {
            @Serializable
            class Minimum(
                @JvmField val Metric: Metric? = null,
                @JvmField val Imperial: Imperial? = null
            ) {
                @Serializable
                class Metric(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )

                @Serializable
                class Imperial(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )
            }

            @Serializable
            class Maximum(
                @JvmField val Metric: Metric? = null,
                @JvmField val Imperial: Imperial? = null
            ) {
                @Serializable
                class Metric(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )

                @Serializable
                class Imperial(
                    @JvmField val Value: Double = 0.0,
                    @JvmField val Unit: String? = null,
                    @JvmField val UnitType: Int = 0
                )
            }
        }
    }
}
