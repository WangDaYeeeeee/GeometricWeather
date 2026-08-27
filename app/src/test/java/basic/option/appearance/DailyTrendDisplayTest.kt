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
import wangdaye.com.geometricweather.common.basic.models.options.appearance.DailyTrendDisplay

@RunWith(PowerMockRunner::class)
@PrepareForTest(TextUtils::class)
class DailyTrendDisplayTest {

    @Test
    fun toDailyTrendDisplayList() {
        val value = "temperature&air_quality&wind&uv_index&precipitation"
        val list = DailyTrendDisplay.toDailyTrendDisplayList(value)
        Assert.assertEquals(list[0], DailyTrendDisplay.TAG_TEMPERATURE)
        Assert.assertEquals(list[1], DailyTrendDisplay.TAG_AIR_QUALITY)
        Assert.assertEquals(list[2], DailyTrendDisplay.TAG_WIND)
        Assert.assertEquals(list[3], DailyTrendDisplay.TAG_UV_INDEX)
        Assert.assertEquals(list[4], DailyTrendDisplay.TAG_PRECIPITATION)
    }

    @Test
    fun toValue() {
        val list = ArrayList<DailyTrendDisplay>()
        list.add(DailyTrendDisplay.TAG_TEMPERATURE)
        list.add(DailyTrendDisplay.TAG_AIR_QUALITY)
        list.add(DailyTrendDisplay.TAG_WIND)
        list.add(DailyTrendDisplay.TAG_UV_INDEX)
        list.add(DailyTrendDisplay.TAG_PRECIPITATION)
        val value = "temperature&air_quality&wind&uv_index&precipitation"
        Assert.assertEquals(DailyTrendDisplay.toValue(list), value)
    }

    @Test
    fun getSummary() {
        val context = PowerMockito.mock(Context::class.java)
        doReturn("Name").`when`(context).getString(anyInt())
        val list = ArrayList<DailyTrendDisplay>()
        list.add(DailyTrendDisplay.TAG_TEMPERATURE)
        list.add(DailyTrendDisplay.TAG_AIR_QUALITY)
        list.add(DailyTrendDisplay.TAG_WIND)
        list.add(DailyTrendDisplay.TAG_UV_INDEX)
        list.add(DailyTrendDisplay.TAG_PRECIPITATION)
        val value = "Name, Name, Name, Name, Name"
        Assert.assertThat(DailyTrendDisplay.getSummary(context, list), `is`(value))
    }

    companion object {
        @JvmStatic
        @BeforeClass
        @Throws(Exception::class)
        fun setup() {
            PowerMockito.mockStatic(TextUtils::class.java)
            PowerMockito.`when`(TextUtils.isEmpty(anyString())).thenReturn(false)
        }
    }
}
