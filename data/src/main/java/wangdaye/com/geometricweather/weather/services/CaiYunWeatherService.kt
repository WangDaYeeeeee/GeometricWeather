package wangdaye.com.geometricweather.weather.services

import android.content.Context
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.utils.CancellableCoroutineScope
import wangdaye.com.geometricweather.common.utils.LanguageUtils
import wangdaye.com.geometricweather.db.DatabaseHelper
import wangdaye.com.geometricweather.weather.apis.CaiYunApi
import wangdaye.com.geometricweather.weather.converters.CaiyunResultConverter
import javax.inject.Inject

class CaiYunWeatherService @Inject constructor(
    private val api: CaiYunApi
) : WeatherService() {

    private val requestScope = CancellableCoroutineScope()

    override fun requestWeather(
        context: Context,
        location: Location,
        callback: RequestWeatherCallback
    ) {
        requestScope.scope.launch {
            try {
                val wrapper = withContext(Dispatchers.IO) {
                    coroutineScope {
                        val mainly = async {
                            api.getMainlyWeather(
                                location.latitude.toString(),
                                location.longitude.toString(),
                                location.isCurrentPosition,
                                "weathercn%3A" + location.cityId,
                                15,
                                "weather20151024",
                                "zUFJoAR2ZVrDy1vF3D07",
                                "V10.0.1.0.OAACNFH",
                                "10010002",
                                false,
                                false,
                                "gemini",
                                "",
                                "zh_cn"
                            )
                        }
                        val forecast = async {
                            api.getForecastWeather(
                                location.latitude.toString(),
                                location.longitude.toString(),
                                "zh_cn",
                                false,
                                "weather20151024",
                                "weathercn%3A" + location.cityId,
                                "zUFJoAR2ZVrDy1vF3D07"
                            )
                        }
                        CaiyunResultConverter.convert(
                            context,
                            location,
                            mainly.await(),
                            forecast.await()
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
        if (!LanguageUtils.isChinese(query)) {
            return ArrayList()
        }

        DatabaseHelper.getInstance(context).ensureChineseCityList(context)

        val locationList = ArrayList<Location>()
        val cityList = DatabaseHelper.getInstance(context).readChineseCityList(query)
        for (c in cityList) {
            locationList.add(c.toLocation())
        }
        return locationList
    }

    override fun requestLocation(
        context: Context,
        location: Location,
        callback: RequestLocationCallback
    ) {
        val hasGeocodeInformation = location.hasGeocodeInformation()
        requestScope.scope.launch {
            try {
                val locations = withContext(Dispatchers.IO) {
                    DatabaseHelper.getInstance(context).ensureChineseCityList(context)
                    val locationList = ArrayList<Location>()

                    if (hasGeocodeInformation) {
                        val chineseCity = DatabaseHelper.getInstance(context).readChineseCity(
                            formatLocationString(convertChinese(location.province)),
                            formatLocationString(convertChinese(location.city)),
                            formatLocationString(convertChinese(location.district))
                        )
                        if (chineseCity != null) {
                            locationList.add(chineseCity.toLocation())
                        }
                    }
                    if (locationList.isNotEmpty()) {
                        return@withContext locationList
                    }

                    val chineseCity = DatabaseHelper.getInstance(context).readChineseCity(
                        location.latitude,
                        location.longitude
                    )
                    if (chineseCity != null) {
                        locationList.add(chineseCity.toLocation())
                    }
                    locationList
                }
                if (locations.isNotEmpty()) {
                    callback.requestLocationSuccess(location.formattedId, locations)
                } else {
                    callback.requestLocationFailed(location.formattedId)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                callback.requestLocationFailed(location.formattedId)
            }
        }
    }

    override fun cancel() {
        requestScope.cancelChildren()
    }
}
