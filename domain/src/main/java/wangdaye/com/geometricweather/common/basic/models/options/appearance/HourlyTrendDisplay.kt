package wangdaye.com.geometricweather.common.basic.models.options.appearance

import android.content.Context
import androidx.annotation.StringRes
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.basic.models.options._basic.BaseEnum

enum class HourlyTrendDisplay(
    override val id: String,
    @StringRes val nameId: Int
): BaseEnum {

    TAG_TEMPERATURE("temperature", R.string.temperature),
    TAG_WIND("wind", R.string.wind),
    TAG_UV_INDEX("uv_index", R.string.uv_index),
    TAG_PRECIPITATION("precipitation", R.string.precipitation);

    companion object {

        @JvmStatic
        fun toHourlyTrendDisplayList(
            value: String
        ) = if (value.isEmpty()) {
            emptyList()
        } else try {
            value.split("&").mapNotNull { card ->
                when (card) {
                    "temperature" -> TAG_TEMPERATURE
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
        fun toValue(list: List<HourlyTrendDisplay>): String =
            list.joinToString("&") { it.id }

        @JvmStatic
        fun getSummary(context: Context, list: List<HourlyTrendDisplay>): String =
            list.joinToString(", ") { it.getName(context) }
    }

    override val valueArrayId = 0
    override val nameArrayId = 0

    override fun getName(context: Context) = context.getString(nameId)
}
