package wangdaye.com.geometricweather.common.basic.models.options.appearance

import android.content.Context
import androidx.annotation.StringRes
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.basic.models.options._basic.BaseEnum

enum class CardDisplay(
    override val id: String,
    @StringRes private val nameId: Int
): BaseEnum {

    CARD_DAILY_OVERVIEW("daily_overview", R.string.daily_overview),
    CARD_HOURLY_OVERVIEW("hourly_overview", R.string.hourly_overview),
    CARD_AIR_QUALITY("air_quality", R.string.air_quality),
    CARD_ALLERGEN("allergen", R.string.allergen),
    CARD_SUNRISE_SUNSET("sunrise_sunset", R.string.sunrise_sunset),
    CARD_LIFE_DETAILS("life_details", R.string.life_details);

    companion object {

        @JvmStatic
        fun toCardDisplayList(
            value: String
        ) = if (value.isEmpty()) {
            emptyList()
        } else try {
            value.split("&").mapNotNull { card ->
                when (card) {
                    "daily_overview" -> CARD_DAILY_OVERVIEW
                    "hourly_overview" -> CARD_HOURLY_OVERVIEW
                    "air_quality" -> CARD_AIR_QUALITY
                    "allergen" -> CARD_ALLERGEN
                    "life_details" -> CARD_LIFE_DETAILS
                    "sunrise_sunset" -> CARD_SUNRISE_SUNSET
                    else -> null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }

        @JvmStatic
        fun toValue(list: List<CardDisplay>): String =
            list.joinToString("&") { it.id }

        @JvmStatic
        fun getSummary(context: Context, list: List<CardDisplay>): String =
            list.joinToString(", ") { it.getName(context) }
    }

    override val valueArrayId = 0
    override val nameArrayId = 0

    override fun getName(context: Context) = context.getString(nameId)
}
