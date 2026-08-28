package wangdaye.com.geometricweather.weather

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import wangdaye.com.geometricweather.weather.converters.CommonConverter

class WeatherProviderSettingsTest {

    @Test
    fun customKeyWinsOverDefault() {
        assertEquals("custom", WeatherProviderSettings.resolveProviderKey("custom", "default"))
    }

    @Test
    fun emptyCustomFallsBackToDefault() {
        assertEquals("default", WeatherProviderSettings.resolveProviderKey("", "default"))
    }

    @Test
    fun moonPhaseAngleMatchesKnownNames() {
        assertEquals(Integer.valueOf(90), CommonConverter.getMoonPhaseAngle("first quarter"))
        assertEquals(Integer.valueOf(180), CommonConverter.getMoonPhaseAngle("full moon"))
        assertNull(CommonConverter.getMoonPhaseAngle(null))
        assertNull(CommonConverter.getMoonPhaseAngle(""))
    }
}
