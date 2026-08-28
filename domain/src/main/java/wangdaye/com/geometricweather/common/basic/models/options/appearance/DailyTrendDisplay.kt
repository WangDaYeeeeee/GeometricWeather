package wangdaye.com.geometricweather.common.basic.models.options.appearance

import android.content.Context
import androidx.annotation.StringRes
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.basic.models.options._basic.BaseEnum

enum class DailyTrendDisplay(
    override val id: String,
    @StringRes val nameId: Int
): BaseEnum {

    TAG_TEMPERATURE("temperature", R.string.temperature), 
    TAG_AIR_QUALITY("air_quality", R.string.air_quality),
    TAG_WIND("wind", R.string.wind), 
    TAG_UV_INDEX("uv_index", R.string.uv_index), 
    TAG_PRECIPITATION("precipitation", R.string.precipitation);

    companion object {

        @JvmStatic
        fun toDailyTrendDisplayList(
            value: String
        ) = if (value.isEmpty()) {
            emptyList()
        } else try {
            value.split("&").mapNotNull { card ->
                when (card) {
                    "temperature" -> TAG_TEMPERATURE
                    "air_quality" -> TAG_AIR_QUALITY
                    "wind" -> TAG_WIND
                    "uv_index" -> TAG_UV_INDEX
                    "precipitation" -> TAG_PRECIPITATION
                    else -> null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }

        @JvmStatic
        fun toValue(list: List<DailyTrendDisplay>): String =
            list.joinToString("&") { it.id }

        @JvmStatic
        fun getSummary(context: Context, list: List<DailyTrendDisplay>): String =
            list.joinToString(", ") { it.getName(context) }
    }

    override val valueArrayId = 0
    override val nameArrayId = 0

    override fun getName(context: Context) = context.getString(nameId)
}
