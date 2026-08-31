package wangdaye.com.geometricweather.domain.weather

import android.content.Context
import wangdaye.com.geometricweather.common.basic.models.Location

/**
 * Fetches weather for a location and updates the weather cache on success.
 * Implemented in `:data` by [wangdaye.com.geometricweather.weather.WeatherHelper].
 */
interface WeatherRequester {

    interface Listener {
        fun requestWeatherSuccess(requestLocation: Location)
        fun requestWeatherFailed(requestLocation: Location)
    }

    fun requestWeather(context: Context, location: Location, listener: Listener)

    fun cancel()
}
