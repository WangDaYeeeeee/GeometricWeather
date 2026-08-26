package basic.option.appearance

import android.content.Context
import android.text.TextUtils
import org.hamcrest.core.Is.`is`
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.doReturn
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import wangdaye.com.geometricweather.common.basic.models.options.appearance.CardDisplay

@RunWith(PowerMockRunner::class)
@PrepareForTest(TextUtils::class)
class CardDisplayTest {

    @Test
    fun toCardDisplayList() {
        val value = "daily_overview&hourly_overview&air_quality&allergen&life_details&sunrise_sunset"
        val list = CardDisplay.toCardDisplayList(value)
        Assert.assertEquals(list[0], CardDisplay.CARD_DAILY_OVERVIEW)
        Assert.assertEquals(list[1], CardDisplay.CARD_HOURLY_OVERVIEW)
        Assert.assertEquals(list[2], CardDisplay.CARD_AIR_QUALITY)
        Assert.assertEquals(list[3], CardDisplay.CARD_ALLERGEN)
        Assert.assertEquals(list[4], CardDisplay.CARD_LIFE_DETAILS)
        Assert.assertEquals(list[5], CardDisplay.CARD_SUNRISE_SUNSET)
    }

    @Test
    fun toValue() {
        val list = ArrayList<CardDisplay>()
        list.add(CardDisplay.CARD_DAILY_OVERVIEW)
        list.add(CardDisplay.CARD_HOURLY_OVERVIEW)
        list.add(CardDisplay.CARD_AIR_QUALITY)
        list.add(CardDisplay.CARD_ALLERGEN)
        list.add(CardDisplay.CARD_LIFE_DETAILS)
        list.add(CardDisplay.CARD_SUNRISE_SUNSET)
        val value = "daily_overview&hourly_overview&air_quality&allergen&life_details&sunrise_sunset"
        Assert.assertEquals(CardDisplay.toValue(list), value)
    }

    @Test
    fun getSummary() {
        val context = PowerMockito.mock(Context::class.java)
        doReturn("Name").`when`(context).getString(anyInt())
        val list = ArrayList<CardDisplay>()
        list.add(CardDisplay.CARD_DAILY_OVERVIEW)
        list.add(CardDisplay.CARD_HOURLY_OVERVIEW)
        list.add(CardDisplay.CARD_AIR_QUALITY)
        list.add(CardDisplay.CARD_ALLERGEN)
        list.add(CardDisplay.CARD_LIFE_DETAILS)
        list.add(CardDisplay.CARD_SUNRISE_SUNSET)
        val value = "Name, Name, Name, Name, Name, Name"
        Assert.assertThat(CardDisplay.getSummary(context, list), `is`(value))
    }

    companion object {
        @JvmStatic
        @BeforeClass
        @Throws(Exception::class)
        fun setup() {
            PowerMockito.mockStatic(TextUtils::class.java)
            PowerMockito.`when`(TextUtils::class.java, "isEmpty", anyString()).thenReturn(false)
        }
    }
}
