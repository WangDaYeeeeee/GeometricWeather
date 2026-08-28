package wangdaye.com.geometricweather.weather

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

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
