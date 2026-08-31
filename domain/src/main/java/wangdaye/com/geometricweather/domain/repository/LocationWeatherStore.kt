package wangdaye.com.geometricweather.domain.repository

import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.weather.History
import wangdaye.com.geometricweather.common.basic.models.weather.Weather

/**
 * Persistence for the user's location list and per-location weather cache.
 * Implementations live in `:data` (Room / [wangdaye.com.geometricweather.db.DatabaseHelper]).
 */
interface LocationWeatherStore {

    fun readLocationList(): MutableList<Location>

    fun readLocation(formattedId: String): Location?

    fun writeLocation(location: Location)

    fun writeLocationList(list: List<Location>)

    fun deleteLocation(location: Location)

    fun readWeather(location: Location): Weather?

    fun writeWeather(location: Location, weather: Weather)

    fun deleteWeather(location: Location)

    fun readHistory(location: Location, weather: Weather): History?
}
