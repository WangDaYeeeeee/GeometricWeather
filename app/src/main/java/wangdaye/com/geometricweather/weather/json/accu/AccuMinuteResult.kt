@file:kotlinx.serialization.UseSerializers(wangdaye.com.geometricweather.common.json.GsonCompatibleDateSerializer::class)
package wangdaye.com.geometricweather.weather.json.accu

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class AccuMinuteResult(
    @JvmField val Summary: SummaryBean? = null,
    @JvmField val MobileLink: String? = null,
    @JvmField val Link: String? = null,
    @JvmField val Summaries: List<SummariesBean>? = null,
    @JvmField val Intervals: List<IntervalsBean>? = null
) {
    @Serializable
    class SummaryBean(
        @JvmField val Phrase: String? = null,
        @JvmField val Phrase_60: String? = null,
        @JvmField val WidgetPhrase: String? = null,
        @JvmField val ShortPhrase: String? = null,
        @JvmField val BriefPhrase: String? = null,
        @JvmField val LongPhrase: String? = null,
        @JvmField val IconCode: Int = 0
    )

    @Serializable
    class SummariesBean(
        @JvmField val StartMinute: Int = 0,
        @JvmField val EndMinute: Int = 0,
        @JvmField val CountMinute: Int = 0,
        @JvmField val MinuteText: String? = null,
        @JvmField val MinutesText: String? = null,
        @JvmField val WidgetPhrase: String? = null,
        @JvmField val ShortPhrase: String? = null,
        @JvmField val BriefPhrase: String? = null,
        @JvmField val LongPhrase: String? = null,
        @JvmField val IconCode: Int = 0
    )

    @Serializable
    class IntervalsBean(
        @JvmField val StartDateTime: Date? = null,
        @JvmField val StartEpochDateTime: Long = 0,
        @JvmField val Minute: Int = 0,
        @JvmField val Dbz: Double = 0.0,
        @JvmField val ShortPhrase: String? = null,
        @JvmField val IconCode: Int = 0,
        @JvmField val CloudCover: Int = 0
    )
}
