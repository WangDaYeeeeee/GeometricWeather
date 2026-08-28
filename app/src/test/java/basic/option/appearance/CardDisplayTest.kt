package basic.option.appearance

import android.content.Context
import android.text.TextUtils
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import wangdaye.com.geometricweather.common.basic.models.options.appearance.CardDisplay

class CardDisplayTest {

    @BeforeEach
    fun mockTextUtils() {
        mockkStatic(TextUtils::class)
        every { TextUtils.isEmpty(any()) } answers {
            val value = invocation.args[0] as CharSequence?
            value.isNullOrEmpty()
        }
    }

    @AfterEach
    fun unmockTextUtils() {
        unmockkStatic(TextUtils::class)
    }

    @Test
    fun toCardDisplayList() {
        val value = "daily_overview&hourly_overview&air_quality&allergen&life_details&sunrise_sunset"
        val list = CardDisplay.toCardDisplayList(value)
        assertEquals(CardDisplay.CARD_DAILY_OVERVIEW, list[0])
        assertEquals(CardDisplay.CARD_HOURLY_OVERVIEW, list[1])
        assertEquals(CardDisplay.CARD_AIR_QUALITY, list[2])
        assertEquals(CardDisplay.CARD_ALLERGEN, list[3])
        assertEquals(CardDisplay.CARD_LIFE_DETAILS, list[4])
        assertEquals(CardDisplay.CARD_SUNRISE_SUNSET, list[5])
    }

    @Test
    fun emptyValueYieldsEmptyList() {
        assertTrue(CardDisplay.toCardDisplayList("").isEmpty())
    }

    @Test
    fun toValue() {
        val list = listOf(
            CardDisplay.CARD_DAILY_OVERVIEW,
            CardDisplay.CARD_HOURLY_OVERVIEW,
            CardDisplay.CARD_AIR_QUALITY,
            CardDisplay.CARD_ALLERGEN,
            CardDisplay.CARD_LIFE_DETAILS,
            CardDisplay.CARD_SUNRISE_SUNSET
        )
        val value = "daily_overview&hourly_overview&air_quality&allergen&life_details&sunrise_sunset"
        assertEquals(value, CardDisplay.toValue(list))
    }

    @Test
    fun getSummary() {
        val context = mockk<Context>()
        every { context.getString(any()) } returns "Name"
        val list = listOf(
            CardDisplay.CARD_DAILY_OVERVIEW,
            CardDisplay.CARD_HOURLY_OVERVIEW,
            CardDisplay.CARD_AIR_QUALITY,
            CardDisplay.CARD_ALLERGEN,
            CardDisplay.CARD_LIFE_DETAILS,
            CardDisplay.CARD_SUNRISE_SUNSET
        )
        assertEquals("Name, Name, Name, Name, Name, Name", CardDisplay.getSummary(context, list))
    }
}
