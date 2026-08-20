@file:kotlinx.serialization.UseSerializers(wangdaye.com.geometricweather.common.json.GsonCompatibleDateSerializer::class)
package wangdaye.com.geometricweather.weather.json.accu

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class AccuAlertResult(
    @JvmField val CountryCode: String? = null,
    @JvmField val AlertID: Int = 0,
    @JvmField val Description: Description? = null,
    @JvmField val Category: String? = null,
    @JvmField val Priority: Int = 0,
    @JvmField val Type: String? = null,
    @JvmField val TypeID: String? = null,
    @JvmField val Level: String? = null,
    @JvmField val Color: Color? = null,
    @JvmField val Source: String? = null,
    @JvmField val SourceId: Int = 0,
    @JvmField val MobileLink: String? = null,
    @JvmField val Link: String? = null,
    @JvmField val Area: List<Area>? = null
) {
    @Serializable
    class Description(
        @JvmField val Localized: String? = null,
        @JvmField val English: String? = null
    )

    @Serializable
    class Color(
        @JvmField val Name: String? = null,
        @JvmField val Red: Int = 0,
        @JvmField val Green: Int = 0,
        @JvmField val Blue: Int = 0,
        @JvmField val Hex: String? = null
    )

    @Serializable
    class Area(
        @JvmField val Name: String? = null,
        @JvmField val StartTime: Date? = null,
        @JvmField val EpochStartTime: Long = 0,
        @JvmField val EndTime: Date? = null,
        @JvmField val EpochEndTime: Long = 0,
        @JvmField val LastAction: LastAction? = null,
        @JvmField val Text: String? = null,
        @JvmField val LanguageCode: String? = null,
        @JvmField val Summary: String? = null
    ) {
        @Serializable
        class LastAction(
            @JvmField val Localized: String? = null,
            @JvmField val English: String? = null
        )
    }
}
