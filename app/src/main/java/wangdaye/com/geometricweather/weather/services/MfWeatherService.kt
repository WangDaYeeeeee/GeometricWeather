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
import wangdaye.com.geometricweather.weather.apis.AtmoAuraIqaApi
import wangdaye.com.geometricweather.weather.apis.MfWeatherApi
import wangdaye.com.geometricweather.weather.converters.MfResultConverter
import wangdaye.com.geometricweather.weather.json.mf.MfWarningsResult
import javax.inject.Inject

class MfWeatherService @Inject constructor(
    private val mfApi: MfWeatherApi,
    private val atmoAuraApi: AtmoAuraIqaApi
) : WeatherService() {

    private val requestScope = CancellableCoroutineScope()

    override fun requestWeather(
        context: Context,
        location: Location,
        callback: RequestWeatherCallback
    ) {
        val languageCode = SettingsManager.getInstance(context).language.code
        val token = SettingsManager.getInstance(context).providerMfWsftKey

        requestScope.scope.launch {
            try {
                val wrapper = withContext(Dispatchers.IO) {
                    coroutineScope {
                        val current = async {
                            mfApi.getCurrent(
                                location.latitude.toDouble(),
                                location.longitude.toDouble(),
                                languageCode,
                                token
                            )
                        }
                        val forecast = async {
                            mfApi.getForecast(
                                location.latitude.toDouble(),
                                location.longitude.toDouble(),
                                languageCode,
                                token
                            )
                        }
                        val ephemeris = async {
                            mfApi.getEphemeris(
                                location.latitude.toDouble(),
                                location.longitude.toDouble(),
                                "en",
                                token
                            )
                        }
                        val rain = async {
                            mfApi.getRain(
                                location.latitude.toDouble(),
                                location.longitude.toDouble(),
                                languageCode,
                                token
                            )
                        }
                        val warnings = async {
                            resumeWithDefault(MfWarningsResult()) {
                                mfApi.getWarnings(location.province, null, token)
                            }
                        }
                        val aqiAtmoAura = async {
                            if (isAtmoAuraProvince(location.province)) {
                                resumeOrNull {
                                    atmoAuraApi.getQAFull(
                                        SettingsManager.getInstance(context).providerIqaAtmoAuraKey,
                                        location.latitude.toString(),
                                        location.longitude.toString()
                                    )
                                }
                            } else {
                                null
                            }
                        }
                        MfResultConverter.convert(
                            context,
                            location,
                            current.await(),
                            forecast.await(),
                            ephemeris.await(),
                            rain.await(),
                            warnings.await(),
                            aqiAtmoAura.await()
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
                mfApi.getWeatherLocation(
                    query,
                    48.86,
                    2.34,
                    SettingsManager.getInstance(context).providerMfWsftKey
                )
            }
        } catch (_: Exception) {
            null
        }

        val locationList = ArrayList<Location>()
        if (!resultList.isNullOrEmpty()) {
            for (r in resultList) {
                if (r.postCode != null) {
                    locationList.add(MfResultConverter.convert(null, r))
                }
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
                val mfForecastV2Result = withContext(Dispatchers.IO) {
                    mfApi.getForecastV2(
                        location.latitude.toDouble(),
                        location.longitude.toDouble(),
                        languageCode,
                        SettingsManager.getInstance(context).providerMfWsftKey
                    )
                }
                val locationList = ArrayList<Location>()
                if (mfForecastV2Result.properties.insee != null) {
                    locationList.add(MfResultConverter.convert(null, mfForecastV2Result))
                }
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
        requestScope.scope.launch {
            try {
                val mfLocationResults = withContext(Dispatchers.IO) {
                    mfApi.getWeatherLocation(
                        query,
                        48.86,
                        2.34,
                        SettingsManager.getInstance(context).providerMfWsftKey
                    )
                }
                if (mfLocationResults.isNotEmpty()) {
                    val locationList = ArrayList<Location>()
                    for (r in mfLocationResults) {
                        if (r.postCode != null) {
                            locationList.add(MfResultConverter.convert(null, r))
                        }
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

    private fun isAtmoAuraProvince(province: String?): Boolean {
        return province == "Auvergne-Rhône-Alpes"
            || province == "01"
            || province == "03"
            || province == "07"
            || province == "15"
            || province == "26"
            || province == "38"
            || province == "42"
            || province == "43"
            || province == "63"
            || province == "69"
            || province == "73"
            || province == "74"
    }
}
