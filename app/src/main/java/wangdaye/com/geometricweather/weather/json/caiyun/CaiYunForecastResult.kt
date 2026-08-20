@file:kotlinx.serialization.UseSerializers(wangdaye.com.geometricweather.common.json.GsonCompatibleDateSerializer::class)
package wangdaye.com.geometricweather.weather.json.caiyun

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class CaiYunForecastResult(
    @JvmField val precipitation: PrecipitationBean? = null,
    @JvmField @SerialName("new") val newX: String? = null,
    @JvmField val status: Int = 0
) {
    @Serializable
    class PrecipitationBean(
        @JvmField val headDescription: String? = null,
        @JvmField val headIconType: String? = null,
        @JvmField val isRainOrSnow: Int = 0,
        @JvmField val pubTime: Date? = null,
        @JvmField val weather: String? = null,
        @JvmField val description: String? = null,
        @JvmField val shortDescription: String? = null,
        @JvmField val isModify: Boolean = false,
        @JvmField val isShow: Boolean = false,
        @JvmField val status: Int = 0,
        @JvmField val value: List<Double>? = null
    )
}
