package wangdaye.com.geometricweather.weather

import android.content.Context
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.common.utils.CancellableCoroutineScope
import wangdaye.com.geometricweather.common.utils.NetworkUtils
import wangdaye.com.geometricweather.common.utils.helpers.AsyncHelper
import wangdaye.com.geometricweather.db.DatabaseHelper
import wangdaye.com.geometricweather.weather.services.WeatherService
import javax.inject.Inject

class WeatherHelper @Inject constructor(
    private val serviceSet: WeatherServiceSet
) {

    private val requestScope = CancellableCoroutineScope()

    interface OnRequestWeatherListener {
        fun requestWeatherSuccess(requestLocation: Location)
        fun requestWeatherFailed(requestLocation: Location)
    }

    interface OnRequestLocationListener {
        fun requestLocationSuccess(query: String, locationList: List<Location>)
        fun requestLocationFailed(query: String)
    }

    fun requestWeather(c: Context, location: Location, l: OnRequestWeatherListener) {
        val service = serviceSet.get(location.weatherSource)
        if (!NetworkUtils.isAvailable(c)) {
            l.requestWeatherFailed(location)
            return
        }

        service.requestWeather(c, location.copy(), object : WeatherService.RequestWeatherCallback {
            override fun requestWeatherSuccess(requestLocation: Location) {
                val weather = requestLocation.weather
                if (weather != null) {
                    DatabaseHelper.getInstance(c).writeWeather(requestLocation, weather)
                    if (weather.yesterday == null) {
                        weather.yesterday = DatabaseHelper.getInstance(c).readHistory(requestLocation, weather)
                    }
                    l.requestWeatherSuccess(requestLocation)
                } else {
                    requestWeatherFailed(requestLocation)
                }
            }

            override fun requestWeatherFailed(requestLocation: Location) {
                l.requestWeatherFailed(
                    Location.copy(
                        requestLocation,
                        DatabaseHelper.getInstance(c).readWeather(requestLocation)
                    )
                )
            }
        })
    }

    fun requestLocation(
        context: Context,
        query: String,
        enabledSources: List<WeatherSource>?,
        l: OnRequestLocationListener
    ) {
        if (enabledSources.isNullOrEmpty()) {
            AsyncHelper.delayRunOnUI({ l.requestLocationFailed(query) }, 0)
            return
        }

        val services = Array(enabledSources.size) { i ->
            serviceSet.get(enabledSources[i])
        }

        requestScope.scope.launch {
            try {
                val locationList = withContext(Dispatchers.IO) {
                    coroutineScope {
                        services.map { service ->
                            async { service.requestLocation(context, query) }
                        }.awaitAll().flatten()
                    }
                }
                if (locationList.isNotEmpty()) {
                    l.requestLocationSuccess(query, locationList)
                } else {
                    l.requestLocationFailed(query)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                l.requestLocationFailed(query)
            }
        }
    }

    fun cancel() {
        for (s in serviceSet.getAll()) {
            s.cancel()
        }
        requestScope.cancelChildren()
    }
}
