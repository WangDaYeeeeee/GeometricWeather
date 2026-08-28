package basic.option.appearance

import android.content.Context
import io.mockk.any
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.robolectric.annotation.Config
import org.robolectric.junit.jupiter.RobolectricExtension
import wangdaye.com.geometricweather.common.basic.models.options.appearance.DailyTrendDisplay

@ExtendWith(RobolectricExtension::class)
@Config(sdk = [28])
class DailyTrendDisplayTest {

    @Test
    fun toDailyTrendDisplayList() {
        val value = "temperature&air_quality&wind&uv_index&precipitation"
        val list = DailyTrendDisplay.toDailyTrendDisplayList(value)
        assertEquals(DailyTrendDisplay.TAG_TEMPERATURE, list[0])
        assertEquals(DailyTrendDisplay.TAG_AIR_QUALITY, list[1])
        assertEquals(DailyTrendDisplay.TAG_WIND, list[2])
        assertEquals(DailyTrendDisplay.TAG_UV_INDEX, list[3])
        assertEquals(DailyTrendDisplay.TAG_PRECIPITATION, list[4])
    }

    @Test
    fun emptyValueYieldsEmptyList() {
        assertTrue(DailyTrendDisplay.toDailyTrendDisplayList("").isEmpty())
    }

    @Test
    fun toValue() {
        val list = listOf(
            DailyTrendDisplay.TAG_TEMPERATURE,
            DailyTrendDisplay.TAG_AIR_QUALITY,
            DailyTrendDisplay.TAG_WIND,
            DailyTrendDisplay.TAG_UV_INDEX,
            DailyTrendDisplay.TAG_PRECIPITATION
        )
        val value = "temperature&air_quality&wind&uv_index&precipitation"
        assertEquals(value, DailyTrendDisplay.toValue(list))
    }

    @Test
    fun getSummary() {
        val context = mockk<Context>()
        every { context.getString(any<Int>()) } returns "Name"
        val list = listOf(
            DailyTrendDisplay.TAG_TEMPERATURE,
            DailyTrendDisplay.TAG_AIR_QUALITY,
            DailyTrendDisplay.TAG_WIND,
            DailyTrendDisplay.TAG_UV_INDEX,
            DailyTrendDisplay.TAG_PRECIPITATION
        )
        assertEquals("Name, Name, Name, Name, Name", DailyTrendDisplay.getSummary(context, list))
    }
}
