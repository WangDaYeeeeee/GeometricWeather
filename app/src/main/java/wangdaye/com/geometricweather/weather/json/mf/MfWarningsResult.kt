package wangdaye.com.geometricweather.weather.json.mf

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MfWarningsResult(
    @JvmField val advices: List<WarningAdvice>? = null,
    @JvmField val comments: WarningComments? = null,
    @JvmField val consequences: List<WarningConsequence>? = null,
    @JvmField @SerialName("domain_id") val domain: String? = null,
    @JvmField @SerialName("end_validity_time") val endValidityTime: Long = 0,
    @JvmField @SerialName("color_max") val maxColor: Int = 0,
    @JvmField @SerialName("max_count_items") val maxCountItems: List<WarningMaxCountItems>? = null,
    @JvmField @SerialName("phenomenons_items") val phenomenonsItems: List<PhenomenonMaxColor>? = null,
    @JvmField val text: WarningComments? = null,
    @JvmField @SerialName("text_avalanche") val textAvalanche: WarningComments? = null,
    @JvmField val timelaps: List<WarningTimelaps>? = null,
    @JvmField @SerialName("update_time") val updateTime: Long = 0
) {
    @Serializable
    class WarningAdvice(
        @JvmField @SerialName("phenomenon_max_color_id") val phenomenoMaxColorId: Int = 0,
        @JvmField @SerialName("phenomenon_id") val phenomenonId: Int = 0,
        @JvmField @SerialName("text_advice") val textAdvice: String? = null
    )

    @Serializable
    class WarningComments(
        @JvmField @SerialName("begin_time") val beginTime: Long = 0,
        @JvmField @SerialName("end_time") val endTime: Long = 0,
        @JvmField @SerialName("text_bloc_item") val textBlocItems: List<WarningTextBlocItem>? = null
    ) {
        @Serializable
        class WarningTextBlocItem(
            @JvmField val text: List<String>? = null,
            @JvmField @SerialName("text_html") val textHtml: List<String>? = null,
            @JvmField val title: String? = null,
            @JvmField @SerialName("title_html") val titleHtml: String? = null
        )
    }

    @Serializable
    class WarningConsequence(
        @JvmField @SerialName("phenomenon_max_color_id") val phenomenoMaxColorId: Int = 0,
        @JvmField @SerialName("phenomenon_id") val phenomenonId: Int = 0,
        @JvmField @SerialName("text_consequence") val textConsequence: String? = null
    )

    @Serializable
    class WarningMaxCountItems(
        @JvmField @SerialName("color_id") val colorId: Int = 0,
        @JvmField val count: Int = 0,
        @JvmField @SerialName("text_count") val textCount: String? = null
    )

    @Serializable
    class PhenomenonMaxColor(
        @JvmField @SerialName("phenomenon_max_color_id") val phenomenoMaxColorId: Int = 0,
        @JvmField @SerialName("phenomenon_id") val phenomenonId: Int = 0
    )

    @Serializable
    class WarningTimelaps(
        @JvmField @SerialName("phenomenon_id") val phenomenonId: Int = 0,
        @JvmField @SerialName("timelaps_items") val timelapsItems: List<WarningTimelapsItem>? = null
    ) {
        @Serializable
        class WarningTimelapsItem(
            @JvmField @SerialName("begin_time") val beginTime: Long = 0,
            @JvmField @SerialName("color_id") val colorId: Int = 0
        )
    }
}
