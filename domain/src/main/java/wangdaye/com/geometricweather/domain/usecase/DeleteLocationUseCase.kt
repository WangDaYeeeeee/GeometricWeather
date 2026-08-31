package wangdaye.com.geometricweather.domain.usecase

import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.domain.repository.LocationWeatherStore

/** Removes a location row and its weather cache. */
class DeleteLocationUseCase(
    private val store: LocationWeatherStore
) {
    fun execute(location: Location) {
        store.deleteLocation(location)
        store.deleteWeather(location)
    }
}
