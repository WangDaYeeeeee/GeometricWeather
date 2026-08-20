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
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.weather.apis.AccuWeatherApi
import wangdaye.com.geometricweather.weather.converters.AccuResultConverter
import javax.inject.Inject

class AccuWeatherService @Inject constructor(
    private val api: AccuWeatherApi
) : WeatherService() {

    private val requestScope = CancellableCoroutineScope()

    override fun requestWeather(
        context: Context,
        location: Location,
        callback: RequestWeatherCallback
    ) {
        val languageCode = SettingsManager.getInstance(context).language.code
        val settings = SettingsManager.getInstance(context)

        requestScope.scope.launch {
            try {
                val wrapper = withContext(Dispatchers.IO) {
                    coroutineScope {
                        val realtime = async {
                            api.getCurrent(
                                location.cityId,
                                settings.providerAccuCurrentKey,
                                languageCode,
                                true
                            )
                        }
                        val daily = async {
                            api.getDaily(
                                location.cityId,
                                settings.providerAccuWeatherKey,
                                languageCode,
                                true,
                                true
                            )
                        }
                        val hourly = async {
                            api.getHourly(
                                location.cityId,
                                settings.providerAccuWeatherKey,
                                languageCode,
                                true,
                                true
                            )
                        }
                        val minute = async {
                            resumeOrNull {
                                api.getMinutely(
                                    settings.providerAccuWeatherKey,
                                    languageCode,
                                    true,
                                    location.latitude.toString() + "," + location.longitude
                                )
                            }
                        }
                        val alert = async {
                            api.getAlert(
                                location.cityId,
                                settings.providerAccuWeatherKey,
                                languageCode,
                                true
                            )
                        }
                        val aqi = async {
                            resumeOrNull {
                                api.getAirQuality(
                                    location.cityId,
                                    settings.providerAccuAqiKey
                                )
                            }
                        }
                        AccuResultConverter.convert(
                            context,
                            location,
                            realtime.await()[0],
                            daily.await(),
                            hourly.await(),
                            minute.await(),
                            aqi.await(),
                            alert.await()
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
        val languageCode = SettingsManager.getInstance(context).language.code
        val resultList = try {
            runBlocking {
                api.getWeatherLocation(
                    "Always",
                    SettingsManager.getInstance(context).providerAccuWeatherKey,
                    query,
                    languageCode
                )
            }
        } catch (_: Exception) {
            null
        }

        val zipCode = if (query.matches("[a-zA-Z0-9]*".toRegex())) query else null
        val locationList = ArrayList<Location>()
        if (!resultList.isNullOrEmpty()) {
            for (r in resultList) {
                locationList.add(AccuResultConverter.convert(null, r, zipCode))
            }
        }
        return locationList
    }

    override fun requestLocation(
        context: Context,
        location: Location,
        callback: RequestLocationCallback
    ) {
        val languageCode = SettingsManager.getInstance(context).language.code
        requestScope.scope.launch {
            try {
                val accuLocationResult = withContext(Dispatchers.IO) {
                    api.getWeatherLocationByGeoPosition(
                        "Always",
                        SettingsManager.getInstance(context).providerAccuWeatherKey,
                        location.latitude.toString() + "," + location.longitude,
                        languageCode
                    )
                }
                val locationList = ArrayList<Location>()
                locationList.add(AccuResultConverter.convert(location, accuLocationResult, null))
                callback.requestLocationSuccess(
                    location.latitude.toString() + "," + location.longitude,
                    locationList
                )
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
        val languageCode = SettingsManager.getInstance(context).language.code
        val zipCode = if (query.matches("[a-zA-Z0-9]".toRegex())) query else null
        requestScope.scope.launch {
            try {
                val accuLocationResults = withContext(Dispatchers.IO) {
                    api.getWeatherLocation(
                        "Always",
                        SettingsManager.getInstance(context).providerAccuWeatherKey,
                        query,
                        languageCode
                    )
                }
                if (accuLocationResults.isNotEmpty()) {
                    val locationList = ArrayList<Location>()
                    for (r in accuLocationResults) {
                        locationList.add(AccuResultConverter.convert(null, r, zipCode))
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
