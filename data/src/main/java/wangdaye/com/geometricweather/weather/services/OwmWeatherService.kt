package wangdaye.com.geometricweather.weather.services

import android.content.Context
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.utils.CancellableCoroutineScope
import wangdaye.com.geometricweather.weather.WeatherProviderSettings
import wangdaye.com.geometricweather.weather.apis.OwmApi
import wangdaye.com.geometricweather.weather.converters.OwmResultConverter
import javax.inject.Inject

class OwmWeatherService @Inject constructor(
    private val api: OwmApi
) : WeatherService() {

    private val requestScope = CancellableCoroutineScope()

    override fun requestWeather(
        context: Context,
        location: Location,
        callback: RequestWeatherCallback
    ) {
        val languageCode = WeatherProviderSettings.getInstance(context).languageCode
        val key = WeatherProviderSettings.getInstance(context).providerOwmKey

        requestScope.scope.launch {
            try {
                val wrapper = withContext(Dispatchers.IO) {
                    coroutineScope {
                        val oneCall = async {
                            api.getOneCall(
                                key,
                                location.latitude.toDouble(),
                                location.longitude.toDouble(),
                                "metric",
                                languageCode
                            )
                        }
                        val airPollutionCurrent = async {
                            resumeOrNull {
                                api.getAirPollutionCurrent(
                                    key,
                                    location.latitude.toDouble(),
                                    location.longitude.toDouble()
                                )
                            }
                        }
                        val airPollutionForecast = async {
                            resumeOrNull {
                                api.getAirPollutionForecast(
                                    key,
                                    location.latitude.toDouble(),
                                    location.longitude.toDouble()
                                )
                            }
                        }
                        OwmResultConverter.convert(
                            context,
                            location,
                            oneCall.await(),
                            airPollutionCurrent.await(),
                            airPollutionForecast.await()
                        )
                    }
                }
                if (wrapper.result != null) {
                    callback.requestWeatherSuccess(Location.copy(location, wrapper.result))
                } else {
                    callback.requestWeatherFailed(location)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                callback.requestWeatherFailed(location)
            }
        }
    }

    override fun requestLocation(context: Context, query: String): List<Location> {
        val resultList = try {
            runBlocking {
                api.getWeatherLocation(
                    WeatherProviderSettings.getInstance(context).providerOwmKey,
                    query
                )
            }
        } catch (_: Exception) {
            null
        }

        val zipCode = if (query.matches("[a-zA-Z0-9]*".toRegex())) query else null
        val locationList = ArrayList<Location>()
        if (!resultList.isNullOrEmpty()) {
            for (r in resultList) {
                locationList.add(OwmResultConverter.convert(null, r, zipCode))
            }
        }
        return locationList
    }

    override fun requestLocation(
        context: Context,
        location: Location,
        callback: RequestLocationCallback
    ) {
        requestScope.scope.launch {
            try {
                val owmLocationResultList = withContext(Dispatchers.IO) {
                    api.getWeatherLocationByGeoPosition(
                        WeatherProviderSettings.getInstance(context).providerOwmKey,
                        location.latitude.toDouble(),
                        location.longitude.toDouble(),
                    )
                }
                if (owmLocationResultList.isNotEmpty()) {
                    val locationList = ArrayList<Location>()
                    locationList.add(
                        OwmResultConverter.convert(location, owmLocationResultList[0], null)
                    )
                    callback.requestLocationSuccess(
                        location.latitude.toString() + "," + location.longitude,
                        locationList
                    )
                } else {
                    callback.requestLocationFailed(
                        location.latitude.toString() + "," + location.longitude
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                callback.requestLocationFailed(
                    location.latitude.toString() + "," + location.longitude
                )
            }
        }
    }

    fun requestLocation(
        context: Context,
        query: String,
        callback: RequestLocationCallback
    ) {
        val zipCode = if (query.matches("[a-zA-Z0-9]".toRegex())) query else null
        requestScope.scope.launch {
            try {
                val owmLocationResults = withContext(Dispatchers.IO) {
                    api.getWeatherLocation(
                        WeatherProviderSettings.getInstance(context).providerOwmKey,
                        query
                    )
                }
                if (owmLocationResults.isNotEmpty()) {
                    val locationList = ArrayList<Location>()
                    for (r in owmLocationResults) {
                        locationList.add(OwmResultConverter.convert(null, r, zipCode))
                    }
                    callback.requestLocationSuccess(query, locationList)
                } else {
                    callback.requestLocationFailed(query)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                callback.requestLocationFailed(query)
            }
        }
    }

    override fun cancel() {
        requestScope.cancelChildren()
    }
}
