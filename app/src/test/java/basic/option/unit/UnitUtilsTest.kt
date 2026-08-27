package basic.option.unit

import android.annotation.SuppressLint
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class UnitUtilsTest {

    @Test
    fun formatFloat() {
        Assert.assertEquals(formatFloat(14.34234f), "14.34")
        Assert.assertEquals(formatFloat(14.34834f), "14.35")
        Assert.assertEquals(formatFloat(14.34834f, 3), "14.348")
        Assert.assertEquals(formatFloat(14.34864f, 3), "14.349")
    }

    @Test
    fun formatInt() {
        Assert.assertEquals(formatInt(14), "14")
        Assert.assertEquals(formatInt(16), "16")
    }

    companion object {
        fun formatFloat(value: Float): String = formatFloat(value, 2)

        fun formatFloat(value: Float, decimalNumber: Int): String {
            return String.format("%." + decimalNumber + "f", value)
        }

        @SuppressLint("DefaultLocale")
        fun formatInt(value: Int): String = String.format("%d", value)
    }
}
