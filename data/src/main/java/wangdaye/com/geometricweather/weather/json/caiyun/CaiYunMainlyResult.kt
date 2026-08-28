@file:kotlinx.serialization.UseSerializers(wangdaye.com.geometricweather.common.json.GsonCompatibleDateSerializer::class)
package wangdaye.com.geometricweather.weather.json.caiyun

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class CaiYunMainlyResult(
    @JvmField val current: CurrentBean? = null,
    @JvmField val forecastDaily: ForecastDailyBean? = null,
    @JvmField val forecastHourly: ForecastHourlyBean? = null,
    @JvmField val indices: IndicesBeanX? = null,
    @JvmField val yesterday: YesterdayBean? = null,
    @JvmField val url: UrlBean? = null,
    @JvmField val brandInfo: BrandInfoBeanXX? = null,
    @JvmField val updateTime: Long = 0,
    @JvmField val aqi: AqiBeanXX? = null,
    @JvmField val alerts: List<AlertsBean>? = null
) {
    @Serializable
    class CurrentBean(
        @JvmField val feelsLike: FeelsLikeBean? = null,
        @JvmField val humidity: HumidityBean? = null,
        @JvmField val pressure: PressureBean? = null,
        @JvmField val pubTime: Date? = null,
        @JvmField val temperature: TemperatureBean? = null,
        @JvmField val uvIndex: String? = null,
        @JvmField val visibility: VisibilityBean? = null,
        @JvmField val weather: String? = null,
        @JvmField val wind: WindBean? = null
    ) {
        @Serializable
        class FeelsLikeBean(
            @JvmField val unit: String? = null,
            @JvmField val value: String? = null
        )

        @Serializable
        class HumidityBean(
            @JvmField val unit: String? = null,
            @JvmField val value: String? = null
        )

        @Serializable
        class PressureBean(
            @JvmField val unit: String? = null,
            @JvmField val value: String? = null
        )

        @Serializable
        class TemperatureBean(
            @JvmField val unit: String? = null,
            @JvmField val value: String? = null
        )

        @Serializable
        class VisibilityBean(
            @JvmField val unit: String? = null,
            @JvmField val value: String? = null
        )

        @Serializable
        class WindBean(
            @JvmField val direction: DirectionBean? = null,
            @JvmField val speed: SpeedBean? = null
        ) {
            @Serializable
            class DirectionBean(
                @JvmField val unit: String? = null,
                @JvmField val value: String? = null
            )

            @Serializable
            class SpeedBean(
                @JvmField val unit: String? = null,
                @JvmField val value: String? = null
            )
        }
    }

    @Serializable
    class ForecastDailyBean(
        @JvmField val aqi: AqiBean? = null,
        @JvmField val precipitationProbability: PrecipitationProbabilityBean? = null,
        @JvmField val pubTime: String? = null,
        @JvmField val status: Int = 0,
        @JvmField val sunRiseSet: SunRiseSetBean? = null,
        @JvmField val temperature: TemperatureBeanX? = null,
        @JvmField val weather: WeatherBean? = null,
        @JvmField val wind: WindBeanX? = null
    ) {
        @Serializable
        class AqiBean(
            @JvmField val brandInfo: BrandInfoBean? = null,
            @JvmField val pubTime: String? = null,
            @JvmField val status: Int = 0,
            @JvmField val value: List<Int>? = null
        ) {
            @Serializable
            class BrandInfoBean(
                @JvmField val brands: List<BrandsBean>? = null
            ) {
                @Serializable
                class BrandsBean(
                    @JvmField val brandId: String? = null,
                    @JvmField val logo: String? = null,
                    @JvmField val names: NamesBean? = null,
                    @JvmField val url: String? = null
                ) {
                    @Serializable
                    class NamesBean(
                        @JvmField val zh_TW: String? = null,
                        @JvmField val en_US: String? = null,
                        @JvmField val zh_CN: String? = null
                    )
                }
            }
        }

        @Serializable
        class PrecipitationProbabilityBean(
            @JvmField val status: Int = 0,
            @JvmField val value: List<String>? = null
        )

        @Serializable
        class SunRiseSetBean(
            @JvmField val status: Int = 0,
            @JvmField val value: List<ValueBean>? = null
        ) {
            @Serializable
            class ValueBean(
                @JvmField val from: Date? = null,
                @JvmField val to: Date? = null
            )
        }

        @Serializable
        class TemperatureBeanX(
            @JvmField val status: Int = 0,
            @JvmField val unit: String? = null,
            @JvmField val value: List<ValueBeanX>? = null
        ) {
            @Serializable
            class ValueBeanX(
                @JvmField val from: String? = null,
                @JvmField val to: String? = null
            )
        }

        @Serializable
        class WeatherBean(
            @JvmField val status: Int = 0,
            @JvmField val value: List<ValueBeanXX>? = null
        ) {
            @Serializable
            class ValueBeanXX(
                @JvmField val from: String? = null,
                @JvmField val to: String? = null
            )
        }

        @Serializable
        class WindBeanX(
            @JvmField val direction: DirectionBeanX? = null,
            @JvmField val speed: SpeedBeanX? = null
        ) {
            @Serializable
            class DirectionBeanX(
                @JvmField val status: Int = 0,
                @JvmField val unit: String? = null,
                @JvmField val value: List<ValueBeanXXX>? = null
            ) {
                @Serializable
                class ValueBeanXXX(
                    @JvmField val from: String? = null,
                    @JvmField val to: String? = null
                )
            }

            @Serializable
            class SpeedBeanX(
                @JvmField val status: Int = 0,
                @JvmField val unit: String? = null,
                @JvmField val value: List<ValueBeanXXXX>? = null
            ) {
                @Serializable
                class ValueBeanXXXX(
                    @JvmField val from: String? = null,
                    @JvmField val to: String? = null
                )
            }
        }
    }

    @Serializable
    class ForecastHourlyBean(
        @JvmField val aqi: AqiBeanX? = null,
        @JvmField val desc: String? = null,
        @JvmField val status: Int = 0,
        @JvmField val temperature: TemperatureBeanXX? = null,
        @JvmField val weather: WeatherBeanX? = null,
        @JvmField val wind: WindBeanXX? = null
    ) {
        @Serializable
        class AqiBeanX(
            @JvmField val brandInfo: BrandInfoBeanX? = null,
            @JvmField val pubTime: String? = null,
            @JvmField val status: Int = 0,
            @JvmField val value: List<Int>? = null
        ) {
            @Serializable
            class BrandInfoBeanX(
                @JvmField val brands: List<BrandsBeanX>? = null
            ) {
                @Serializable
                class BrandsBeanX(
                    @JvmField val brandId: String? = null,
                    @JvmField val logo: String? = null,
                    @JvmField val names: NamesBeanX? = null,
                    @JvmField val url: String? = null
                ) {
                    @Serializable
                    class NamesBeanX(
                        @JvmField val zh_TW: String? = null,
                        @JvmField val en_US: String? = null,
                        @JvmField val zh_CN: String? = null
                    )
                }
            }
        }

        @Serializable
        class TemperatureBeanXX(
            @JvmField val pubTime: String? = null,
            @JvmField val status: Int = 0,
            @JvmField val unit: String? = null,
            @JvmField val value: List<Int>? = null
        )

        @Serializable
        class WeatherBeanX(
            @JvmField val pubTime: String? = null,
            @JvmField val status: Int = 0,
            @JvmField val value: List<Int>? = null
        )

        @Serializable
        class WindBeanXX(
            @JvmField val status: Int = 0,
            @JvmField val value: List<ValueBeanXXXXX>? = null
        ) {
            @Serializable
            class ValueBeanXXXXX(
                @JvmField val datetime: String? = null,
                @JvmField val direction: String? = null,
                @JvmField val speed: String? = null
            )
        }
    }

    @Serializable
    class IndicesBeanX(
        @JvmField val pubTime: String? = null,
        @JvmField val status: Int = 0,
        @JvmField val indices: List<IndicesBean>? = null
    ) {
        @Serializable
        class IndicesBean(
            @JvmField val type: String? = null,
            @JvmField val value: String? = null
        )
    }

    @Serializable
    class YesterdayBean(
        @JvmField val aqi: String? = null,
        @JvmField val date: String? = null,
        @JvmField val status: Int = 0,
        @JvmField val sunRise: String? = null,
        @JvmField val sunSet: String? = null,
        @JvmField val tempMax: String? = null,
        @JvmField val tempMin: String? = null,
        @JvmField val weatherEnd: String? = null,
        @JvmField val weatherStart: String? = null,
        @JvmField val windDircEnd: String? = null,
        @JvmField val windDircStart: String? = null,
        @JvmField val windSpeedEnd: String? = null,
        @JvmField val windSpeedStart: String? = null
    )

    @Serializable
    class UrlBean(
        @JvmField val weathercn: String? = null,
        @JvmField val caiyun: String? = null
    )

    @Serializable
    class BrandInfoBeanXX(
        @JvmField val brands: List<BrandsBeanXX>? = null
    ) {
        @Serializable
        class BrandsBeanXX(
            @JvmField val brandId: String? = null,
            @JvmField val logo: String? = null,
            @JvmField val names: NamesBeanXX? = null,
            @JvmField val url: String? = null
        ) {
            @Serializable
            class NamesBeanXX(
                @JvmField val zh_TW: String? = null,
                @JvmField val en_US: String? = null,
                @JvmField val zh_CN: String? = null
            )
        }
    }

    @Serializable
    class AqiBeanXX(
        @JvmField val pm10Desc: String? = null,
        @JvmField val o3: String? = null,
        @JvmField val src: String? = null,
        @JvmField val pubTime: String? = null,
        @JvmField val pm10: String? = null,
        @JvmField val suggest: String? = null,
        @JvmField val co: String? = null,
        @JvmField val o3Desc: String? = null,
        @JvmField val no2: String? = null,
        @JvmField val so2Desc: String? = null,
        @JvmField val coDesc: String? = null,
        @JvmField val pm25: String? = null,
        @JvmField val so2: String? = null,
        @JvmField val aqi: String? = null,
        @JvmField val pm25Desc: String? = null,
        @JvmField val no2Desc: String? = null,
        @JvmField val brandInfo: BrandInfoBeanXXX? = null,
        @JvmField val primary: String? = null,
        @JvmField val status: Int = 0
    ) {
        @Serializable
        class BrandInfoBeanXXX(
            @JvmField val brands: List<BrandsBeanXXX>? = null
        ) {
            @Serializable
            class BrandsBeanXXX(
                @JvmField val names: NamesBeanXXX? = null,
                @JvmField val brandId: String? = null,
                @JvmField val logo: String? = null,
                @JvmField val url: String? = null
            ) {
                @Serializable
                class NamesBeanXXX(
                    @JvmField val zh_TW: String? = null,
                    @JvmField val en_US: String? = null,
                    @JvmField val zh_CN: String? = null
                )
            }
        }
    }

    @Serializable
    class AlertsBean(
        @JvmField val locationKey: String? = null,
        @JvmField val images: ImagesBean? = null,
        @JvmField val level: String? = null,
        @JvmField val pubTime: Date? = null,
        @JvmField val alertId: String? = null,
        @JvmField val detail: String? = null,
        @JvmField val title: String? = null,
        @JvmField val type: String? = null,
        @JvmField val defense: List<DefenseBean>? = null
    ) {
        @Serializable
        class ImagesBean(
            @JvmField val icon: String? = null,
            @JvmField val notice: String? = null
        )

        @Serializable
        class DefenseBean(
            @JvmField val defenseText: String? = null,
            @JvmField val defenseIcon: String? = null
        )
    }
}
