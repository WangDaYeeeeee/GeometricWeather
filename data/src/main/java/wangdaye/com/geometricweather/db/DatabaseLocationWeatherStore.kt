package wangdaye.com.geometricweather.db

import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.weather.History
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.domain.repository.LocationWeatherStore
import javax.inject.Inject

class DatabaseLocationWeatherStore @Inject constructor(
    private val databaseHelper: DatabaseHelper
) : LocationWeatherStore {

    override fun readLocationList(): MutableList<Location> = databaseHelper.readLocationList()

    override fun readLocation(formattedId: String): Location? = databaseHelper.readLocation(formattedId)

    override fun writeLocation(location: Location) {
        databaseHelper.writeLocation(location)
    }

    override fun writeLocationList(list: List<Location>) {
        databaseHelper.writeLocationList(list)
    }

    override fun deleteLocation(location: Location) {
        databaseHelper.deleteLocation(location)
    }

    override fun readWeather(location: Location): Weather? = databaseHelper.readWeather(location)

    override fun writeWeather(location: Location, weather: Weather) {
        databaseHelper.writeWeather(location, weather)
    }

    override fun deleteWeather(location: Location) {
        databaseHelper.deleteWeather(location)
    }

    override fun readHistory(location: Location, weather: Weather): History? {
        return databaseHelper.readHistory(location, weather)
    }
}
