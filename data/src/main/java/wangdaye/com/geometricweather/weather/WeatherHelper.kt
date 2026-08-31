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
import wangdaye.com.geometricweather.domain.usecase.CacheRequestedWeatherUseCase
import wangdaye.com.geometricweather.domain.weather.LocationSearcher
import wangdaye.com.geometricweather.domain.weather.WeatherRequester
import wangdaye.com.geometricweather.weather.services.WeatherService
import javax.inject.Inject

class WeatherHelper @Inject constructor(
    private val serviceSet: WeatherServiceSet,
    private val cacheRequestedWeather: CacheRequestedWeatherUseCase
) : WeatherRequester, LocationSearcher {

    private val requestScope = CancellableCoroutineScope()

    interface OnRequestWeatherListener : WeatherRequester.Listener

    interface OnRequestLocationListener : LocationSearcher.Listener

    override fun requestWeather(c: Context, location: Location, l: WeatherRequester.Listener) {
        val service = serviceSet.get(location.weatherSource)
        if (!NetworkUtils.isAvailable(c)) {
            l.requestWeatherFailed(location)
            return
        }

        service.requestWeather(c, location.copy(), object : WeatherService.RequestWeatherCallback {
            override fun requestWeatherSuccess(requestLocation: Location) {
                if (cacheRequestedWeather.persistSuccess(requestLocation)) {
                    l.requestWeatherSuccess(requestLocation)
                } else {
                    requestWeatherFailed(requestLocation)
                }
            }

            override fun requestWeatherFailed(requestLocation: Location) {
                l.requestWeatherFailed(
                    cacheRequestedWeather.attachCachedWeather(requestLocation)
                )
            }
        })
    }

    override fun requestLocation(
        context: Context,
        query: String,
        enabledSources: List<WeatherSource>?,
        l: LocationSearcher.Listener
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

    override fun cancel() {
        for (s in serviceSet.getAll()) {
            s.cancel()
        }
        requestScope.cancelChildren()
    }
}
