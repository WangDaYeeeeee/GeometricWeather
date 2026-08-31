package wangdaye.com.geometricweather.domain.usecase

import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.domain.repository.LocationWeatherStore

/** Reads the location list and hydrates weather cache for every row. */
class LoadAllLocationsWithWeatherUseCase(
    private val store: LocationWeatherStore
) {
    fun execute(): List<Location> {
        return store.readLocationList().map {
            it.copy(weather = store.readWeather(it))
        }
    }
}
