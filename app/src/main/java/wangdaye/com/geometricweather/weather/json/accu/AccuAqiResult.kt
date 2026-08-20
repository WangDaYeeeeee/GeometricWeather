@file:kotlinx.serialization.UseSerializers(wangdaye.com.geometricweather.common.json.GsonCompatibleDateSerializer::class)
package wangdaye.com.geometricweather.weather.json.accu

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class AccuAqiResult(
    @JvmField val Date: Date? = null,
    @JvmField val EpochDate: Long = 0,
    @JvmField val Index: Int = 0,
    @JvmField val ParticulateMatter2_5: Float = 0f,
    @JvmField val ParticulateMatter10: Float = 0f,
    @JvmField val Ozone: Float = 0f,
    @JvmField val CarbonMonoxide: Float = 0f,
    @JvmField val NitrogenMonoxide: Float = 0f,
    @JvmField val NitrogenDioxide: Float = 0f,
    @JvmField val SulfurDioxide: Float = 0f,
    @JvmField val Lead: Float = 0f,
    @JvmField val Source: String? = null
)
