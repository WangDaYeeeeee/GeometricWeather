package wangdaye.com.geometricweather.domain.usecase

import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.domain.repository.LocationWeatherStore

/**
 * Cache side-effects of a weather network request: persist a successful payload and
 * attach yesterday history when missing; on failure attach the last cached weather.
 */
class CacheRequestedWeatherUseCase(
    private val store: LocationWeatherStore
) {
    fun persistSuccess(requestLocation: Location): Boolean {
        val weather = requestLocation.weather ?: return false
        store.writeWeather(requestLocation, weather)
        if (weather.yesterday == null) {
            weather.yesterday = store.readHistory(requestLocation, weather)
        }
        return true
    }

    fun attachCachedWeather(requestLocation: Location): Location {
        return Location.copy(
            requestLocation,
            store.readWeather(requestLocation)
        )
    }
}
