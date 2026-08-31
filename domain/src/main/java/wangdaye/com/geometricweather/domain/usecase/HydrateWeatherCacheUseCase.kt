package wangdaye.com.geometricweather.domain.usecase

import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.domain.repository.LocationWeatherStore

/**
 * Attaches cached weather to every location except [ignoredFormattedId], which is left
 * as-is (already hydrated on the main thread).
 */
class HydrateWeatherCacheUseCase(
    private val store: LocationWeatherStore
) {
    fun execute(oldList: List<Location>, ignoredFormattedId: String): List<Location> {
        return oldList.map {
            if (it.formattedId == ignoredFormattedId) {
                it
            } else {
                Location.copy(
                    src = it,
                    weather = store.readWeather(it)
                )
            }
        }
    }
}
