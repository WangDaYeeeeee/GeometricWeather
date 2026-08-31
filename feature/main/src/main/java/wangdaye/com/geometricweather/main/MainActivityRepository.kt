package wangdaye.com.geometricweather.main

import android.content.Context
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.utils.helpers.AsyncHelper
import wangdaye.com.geometricweather.domain.repository.LocationWeatherStore
import wangdaye.com.geometricweather.domain.usecase.DeleteLocationUseCase
import wangdaye.com.geometricweather.domain.usecase.HydrateWeatherCacheUseCase
import wangdaye.com.geometricweather.domain.usecase.LoadLocationsWithWeatherUseCase
import wangdaye.com.geometricweather.domain.weather.WeatherRequester
import wangdaye.com.geometricweather.location.LocationHelper
import java.util.concurrent.Executors
import javax.inject.Inject

class MainActivityRepository @Inject constructor(
    private val locationHelper: LocationHelper,
    private val weatherRequester: WeatherRequester,
    private val locationWeatherStore: LocationWeatherStore,
    private val loadLocationsWithWeather: LoadLocationsWithWeatherUseCase,
    private val hydrateWeatherCache: HydrateWeatherCacheUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase
) : MainWeatherRepository {
    private val singleThreadExecutor = Executors.newSingleThreadExecutor()

    override fun destroy() {
        cancelWeatherRequest()
    }

    override fun initLocations(context: Context, formattedId: String): List<Location> {
        return loadLocationsWithWeather.execute(formattedId)
    }

    override fun getWeatherCacheForLocations(
        context: Context,
        oldList: List<Location>,
        ignoredFormattedId: String,
        callback: AsyncHelper.Callback<List<Location>>
    ) {
        AsyncHelper.runOnExecutor({ emitter ->
            emitter.send(
                hydrateWeatherCache.execute(oldList, ignoredFormattedId),
                true
            )
        }, callback, singleThreadExecutor)
    }

    override fun writeLocationList(context: Context, locationList: List<Location>) {
        AsyncHelper.runOnExecutor({
            locationWeatherStore.writeLocationList(locationList)
        }, singleThreadExecutor)
    }

    override fun deleteLocation(context: Context, location: Location) {
        AsyncHelper.runOnExecutor({
            deleteLocationUseCase.execute(location)
        }, singleThreadExecutor)
    }

    override fun getWeather(
        context: Context,
        location: Location,
        locate: Boolean,
        callback: MainWeatherRepository.WeatherRequestCallback,
    ) {
        if (locate) {
            ensureValidLocationInformation(context, location, callback)
        } else {
            getWeatherWithValidLocationInformation(context, location, null, callback)
        }
    }

    private fun ensureValidLocationInformation(
        context: Context,
        location: Location,
        callback: MainWeatherRepository.WeatherRequestCallback,
    ) = locationHelper.requestLocation(
        context,
        location,
        false,
        object : LocationHelper.OnRequestLocationListener {

            override fun requestLocationSuccess(requestLocation: Location) {
                if (requestLocation.formattedId != location.formattedId) {
                    return
                }
                getWeatherWithValidLocationInformation(
                    context,
                    requestLocation,
                    false,
                    callback
                )
            }

            override fun requestLocationFailed(requestLocation: Location) {
                if (requestLocation.formattedId != location.formattedId) {
                    return
                }
                getWeatherWithValidLocationInformation(
                    context,
                    requestLocation,
                    true,
                    callback
                )
            }
        }
    )

    private fun getWeatherWithValidLocationInformation(
        context: Context,
        location: Location,
        locationFailed: Boolean?,
        callback: MainWeatherRepository.WeatherRequestCallback,
    ) = weatherRequester.requestWeather(
        context,
        location,
        object : WeatherRequester.Listener {
            override fun requestWeatherSuccess(requestLocation: Location) {
                if (requestLocation.formattedId != location.formattedId) {
                    return
                }
                callback.onCompleted(
                    requestLocation,
                    locationFailed = locationFailed,
                    weatherRequestFailed = false
                )
            }

            override fun requestWeatherFailed(requestLocation: Location) {
                if (requestLocation.formattedId != location.formattedId) {
                    return
                }
                callback.onCompleted(
                    requestLocation,
                    locationFailed = locationFailed,
                    weatherRequestFailed = true
                )
            }
        }
    )

    override fun getLocatePermissionList(context: Context) = locationHelper
        .getPermissions(context)
        .toList()

    override fun cancelWeatherRequest() {
        locationHelper.cancel()
        weatherRequester.cancel()
    }
}
