package wangdaye.com.geometricweather.domain.usecase

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.common.basic.models.weather.AirQuality
import wangdaye.com.geometricweather.common.basic.models.weather.Base
import wangdaye.com.geometricweather.common.basic.models.weather.Current
import wangdaye.com.geometricweather.common.basic.models.weather.History
import wangdaye.com.geometricweather.common.basic.models.weather.Precipitation
import wangdaye.com.geometricweather.common.basic.models.weather.PrecipitationProbability
import wangdaye.com.geometricweather.common.basic.models.weather.Temperature
import wangdaye.com.geometricweather.common.basic.models.weather.UV
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode
import wangdaye.com.geometricweather.common.basic.models.weather.Wind
import wangdaye.com.geometricweather.common.basic.models.weather.WindDegree
import wangdaye.com.geometricweather.domain.repository.LocationWeatherStore
import java.util.Date

class LocationUseCasesTest {

    @Test
    fun loadLocationsHydratesMatchingIdOnly() {
        val local = Location.buildLocal()
        val beijing = Location.buildDefaultLocation(WeatherSource.ACCU)
        val store = FakeLocationWeatherStore(
            locations = mutableListOf(local, beijing),
            weatherById = mapOf(
                local.formattedId to weather(1),
                beijing.formattedId to weather(2)
            )
        )

        val result = LoadLocationsWithWeatherUseCase(store).execute(beijing.formattedId)

        assertEquals(2, result.size)
        assertNull(result[0].weather)
        assertEquals(2L, result[1].weather!!.base.timeStamp)
        assertEquals(listOf(beijing.formattedId), store.readWeatherCalls)
    }

    @Test
    fun loadLocationsFallsBackToIndexZeroWhenIdMissing() {
        val local = Location.buildLocal()
        val beijing = Location.buildDefaultLocation(WeatherSource.ACCU)
        val store = FakeLocationWeatherStore(
            locations = mutableListOf(local, beijing),
            weatherById = mapOf(local.formattedId to weather(9))
        )

        val result = LoadLocationsWithWeatherUseCase(store).execute("missing")

        assertEquals(9L, result[0].weather!!.base.timeStamp)
        assertEquals(listOf(local.formattedId), store.readWeatherCalls)
    }

    @Test
    fun hydrateSkipsIgnoredId() {
        val local = Location.buildLocal()
        val beijing = Location.buildDefaultLocation(WeatherSource.ACCU)
        val store = FakeLocationWeatherStore(
            weatherById = mapOf(beijing.formattedId to weather(3))
        )

        val result = HydrateWeatherCacheUseCase(store).execute(
            listOf(local, beijing),
            ignoredFormattedId = local.formattedId
        )

        assertSame(local, result[0])
        assertEquals(3L, result[1].weather!!.base.timeStamp)
        assertEquals(listOf(beijing.formattedId), store.readWeatherCalls)
    }

    @Test
    fun loadAllHydratesEveryRow() {
        val local = Location.buildLocal()
        val beijing = Location.buildDefaultLocation(WeatherSource.ACCU)
        val store = FakeLocationWeatherStore(
            locations = mutableListOf(local, beijing),
            weatherById = mapOf(
                local.formattedId to weather(1),
                beijing.formattedId to weather(2)
            )
        )

        val result = LoadAllLocationsWithWeatherUseCase(store).execute()

        assertEquals(1L, result[0].weather!!.base.timeStamp)
        assertEquals(2L, result[1].weather!!.base.timeStamp)
        assertEquals(2, store.readWeatherCalls.size)
    }

    @Test
    fun deleteRemovesLocationThenWeather() {
        val location = Location.buildLocal()
        val store = FakeLocationWeatherStore()

        DeleteLocationUseCase(store).execute(location)

        assertEquals(listOf("location", "weather"), store.deleteOrder)
    }

    @Test
    fun persistSuccessWritesWeatherAndFillsYesterday() {
        val location = Location.buildDefaultLocation(WeatherSource.ACCU)
            .copy(weather = weather(5, yesterday = null))
        val history = History(Date(1), 1L, 10, 0)
        val store = FakeLocationWeatherStore(history = history)

        val ok = CacheRequestedWeatherUseCase(store).persistSuccess(location)

        assertTrue(ok)
        assertEquals(1, store.writtenWeather.size)
        assertSame(history, location.weather!!.yesterday)
    }

    @Test
    fun persistSuccessReturnsFalseWhenWeatherMissing() {
        val store = FakeLocationWeatherStore()
        val ok = CacheRequestedWeatherUseCase(store).persistSuccess(Location.buildLocal())
        assertTrue(!ok)
        assertTrue(store.writtenWeather.isEmpty())
    }

    @Test
    fun attachCachedWeatherCopiesStorePayload() {
        val location = Location.buildDefaultLocation(WeatherSource.ACCU)
        val cached = weather(7)
        val store = FakeLocationWeatherStore(
            weatherById = mapOf(location.formattedId to cached)
        )

        val result = CacheRequestedWeatherUseCase(store).attachCachedWeather(location)

        assertEquals(7L, result.weather!!.base.timeStamp)
    }

    private fun weather(stamp: Long, yesterday: History? = null): Weather {
        val now = Date(stamp)
        return Weather(
            base = Base("id", stamp, now, stamp, now, stamp),
            current = Current(
                weatherText = "clear",
                weatherCode = WeatherCode.CLEAR,
                temperature = Temperature(20, null, null, null, null, null, null),
                precipitation = Precipitation(null, null, null, null, null),
                precipitationProbability = PrecipitationProbability(null, null, null, null, null),
                wind = Wind("", WindDegree(0f, true), null, ""),
                uv = UV(null, null, null),
                airQuality = AirQuality(null, null, null, null, null, null, null, null),
                relativeHumidity = null,
                pressure = null,
                visibility = null,
                dewPoint = null,
                cloudCover = null,
                ceiling = null,
                dailyForecast = null,
                hourlyForecast = null
            ),
            yesterday = yesterday,
            dailyForecast = emptyList(),
            hourlyForecast = emptyList(),
            minutelyForecast = emptyList(),
            alertList = emptyList()
        )
    }

    private class FakeLocationWeatherStore(
        private val locations: MutableList<Location> = mutableListOf(),
        private val weatherById: Map<String, Weather?> = emptyMap(),
        private val history: History? = null
    ) : LocationWeatherStore {
        val readWeatherCalls = mutableListOf<String>()
        val writtenWeather = mutableListOf<Pair<Location, Weather>>()
        val deleteOrder = mutableListOf<String>()

        override fun readLocationList() = locations

        override fun readLocation(formattedId: String) =
            locations.firstOrNull { it.formattedId == formattedId }

        override fun writeLocation(location: Location) {}

        override fun writeLocationList(list: List<Location>) {}

        override fun deleteLocation(location: Location) {
            deleteOrder.add("location")
        }

        override fun readWeather(location: Location): Weather? {
            readWeatherCalls.add(location.formattedId)
            return weatherById[location.formattedId]
        }

        override fun writeWeather(location: Location, weather: Weather) {
            writtenWeather.add(location to weather)
        }

        override fun deleteWeather(location: Location) {
            deleteOrder.add("weather")
        }

        override fun readHistory(location: Location, weather: Weather) = history
    }
}
