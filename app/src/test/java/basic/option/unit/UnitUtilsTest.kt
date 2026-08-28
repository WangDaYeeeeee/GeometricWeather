package basic.option.unit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import wangdaye.com.geometricweather.common.basic.models.options._basic.Utils

class UnitUtilsTest {

    @Test
    fun formatFloat() {
        assertEquals("14.34", Utils.formatFloat(14.34234f))
        assertEquals("14.35", Utils.formatFloat(14.34834f))
        assertEquals("14.348", Utils.formatFloat(14.34834f, 3))
        assertEquals("14.349", Utils.formatFloat(14.34864f, 3))
    }

    @Test
    fun formatInt() {
        assertEquals("14", Utils.formatInt(14))
        assertEquals("16", Utils.formatInt(16))
    }
}
