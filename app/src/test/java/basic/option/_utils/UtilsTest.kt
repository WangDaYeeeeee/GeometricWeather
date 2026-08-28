package basic.option._utils

import android.content.res.Resources
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.options._basic.Utils

class UtilsTest {

    @Test
    fun getNameByValue() {
        val res = mockk<Resources>()
        every { res.getStringArray(R.array.dark_modes) } returns arrayOf(
            "Automatic", "Follow system", "Always light", "Always dark"
        )
        every { res.getStringArray(R.array.dark_mode_values) } returns arrayOf(
            "auto", "system", "light", "dark"
        )
        assertEquals(
            "Automatic",
            Utils.getNameByValue(res, "auto", R.array.dark_modes, R.array.dark_mode_values)
        )
    }
}
