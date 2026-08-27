package basic.option._utils

import android.content.res.Resources
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.modules.junit4.PowerMockRunner
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.options._basic.Utils

@RunWith(PowerMockRunner::class)
class UtilsTest {

    @Test
    fun getNameByValue() {
        val res = PowerMockito.mock(Resources::class.java)
        PowerMockito.`when`(res.getStringArray(R.array.dark_modes)).thenReturn(
            arrayOf("Automatic", "Follow system", "Always light", "Always dark")
        )
        PowerMockito.`when`(res.getStringArray(R.array.dark_mode_values)).thenReturn(
            arrayOf("auto", "system", "light", "dark")
        )
        Assert.assertEquals(
            Utils.getNameByValue(res, "auto", R.array.dark_modes, R.array.dark_mode_values),
            "Automatic"
        )
    }
}
