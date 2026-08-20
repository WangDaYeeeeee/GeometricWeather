package wangdaye.com.geometricweather.common.json

import kotlinx.serialization.builtins.ListSerializer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import wangdaye.com.geometricweather.common.basic.models.ChineseCity
import wangdaye.com.geometricweather.weather.json.accu.AccuHourlyResult
import wangdaye.com.geometricweather.weather.json.owm.OwmOneCallResult

class JsonMigrationTest {

    @Test
    fun ignoreUnknownKeysAndSerialName() {
        val json = """
            {
              "lat": 48.8,
              "lon": 2.3,
              "timezone": "Europe/Paris",
              "timezone_offset": 3600,
              "unexpected": true,
              "current": {
                "dt": 1,
                "feels_like": 12.5,
                "wind_speed": 3.2,
                "wind_deg": 180
              }
            }
        """.trimIndent()

        val result = AppJson.decodeFromString(OwmOneCallResult.serializer(), json)
        assertEquals(48.8, result.lat, 0.0)
        assertEquals(3600, result.timezoneOffset)
        assertEquals(12.5, result.current!!.feelsLike, 0.0)
        assertEquals(3.2f, result.current!!.windSpeed)
        assertNull(result.hourly)
    }

    @Test
    fun gsonCompatibleDatePrefix() {
        val json = """
            {
              "DateTime": "2016-12-22T10:00:00+08:00",
              "EpochDateTime": 1482372000,
              "IsDaylight": true
            }
        """.trimIndent()

        val result = AppJson.decodeFromString(AccuHourlyResult.serializer(), json)
        assertNotNull(result.DateTime)
        assertEquals(1482372000, result.EpochDateTime)
    }

    @Test
    fun chineseCityList() {
        val json = """
            [{"cityId":"101010100","province":"北京","city":"北京","district":"北京","latitude":"39.9","longitude":"116.4"}]
        """.trimIndent()
        val cities = AppJson.decodeFromString(ListSerializer(ChineseCity.serializer()), json)
        assertEquals(1, cities.size)
        assertEquals("101010100", cities[0].cityId)
        assertEquals("北京", cities[0].province)
    }
}
