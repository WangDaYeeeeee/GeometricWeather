package wangdaye.com.geometricweather.domain.weather

import android.content.Context
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource

/**
 * Searches geocoded locations for a query across enabled weather sources.
 * Implemented in `:data` by [wangdaye.com.geometricweather.weather.WeatherHelper].
 */
interface LocationSearcher {

    interface Listener {
        fun requestLocationSuccess(query: String, locationList: List<Location>)
        fun requestLocationFailed(query: String)
    }

    fun requestLocation(
        context: Context,
        query: String,
        enabledSources: List<WeatherSource>?,
        listener: Listener
    )

    fun cancel()
}
