package wangdaye.com.geometricweather.common.basic.models.weather

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherCodeTest {

    @Test
    fun getInstanceMapsKnownTokens() {
        assertEquals(WeatherCode.RAIN, WeatherCode.getInstance("rain"))
        assertEquals(WeatherCode.THUNDERSTORM, WeatherCode.getInstance("thunderstorm"))
        assertEquals(WeatherCode.CLEAR, WeatherCode.getInstance("unknown"))
    }

    @Test
    fun precipitationFlags() {
        assertTrue(WeatherCode.SLEET.isPrecipitation)
        assertTrue(WeatherCode.SLEET.isRain)
        assertTrue(WeatherCode.SLEET.isSnow)
    }
}
