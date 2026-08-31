package wangdaye.com.geometricweather.domain.usecase

import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.domain.repository.LocationWeatherStore

/**
 * Loads the persisted location list and hydrates weather cache for the entry matching
 * [formattedId]. If no row matches, index 0 is hydrated (same as the previous
 * MainActivityRepository.initLocations behavior).
 */
class LoadLocationsWithWeatherUseCase(
    private val store: LocationWeatherStore
) {
    fun execute(formattedId: String): List<Location> {
        val list = store.readLocationList()

        var index = 0
        for (i in list.indices) {
            if (list[i].formattedId == formattedId) {
                index = i
                break
            }
        }

        list[index] = Location.copy(
            src = list[index],
            weather = store.readWeather(list[index])
        )
        return list
    }
}
