package wangdaye.com.geometricweather.weather

import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherProviderSettingsTest {

    @Test
    fun customKeyWinsOverDefault() {
        assertEquals("custom", WeatherProviderSettings.resolveProviderKey("custom", "default"))
    }

    @Test
    fun emptyCustomFallsBackToDefault() {
        assertEquals("default", WeatherProviderSettings.resolveProviderKey("", "default"))
    }
}
