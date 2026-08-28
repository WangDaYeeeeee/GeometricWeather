package wangdaye.com.geometricweather.weather.converters

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.common.basic.models.weather.AirQuality
import wangdaye.com.geometricweather.common.basic.models.weather.Alert
import wangdaye.com.geometricweather.common.basic.models.weather.Astro
import wangdaye.com.geometricweather.common.basic.models.weather.Base
import wangdaye.com.geometricweather.common.basic.models.weather.Current
import wangdaye.com.geometricweather.common.basic.models.weather.Daily
import wangdaye.com.geometricweather.common.basic.models.weather.HalfDay
import wangdaye.com.geometricweather.common.basic.models.weather.Hourly
import wangdaye.com.geometricweather.common.basic.models.weather.Minutely
import wangdaye.com.geometricweather.common.basic.models.weather.MoonPhase
import wangdaye.com.geometricweather.common.basic.models.weather.Pollen
import wangdaye.com.geometricweather.common.basic.models.weather.Precipitation
import wangdaye.com.geometricweather.common.basic.models.weather.PrecipitationDuration
import wangdaye.com.geometricweather.common.basic.models.weather.PrecipitationProbability
import wangdaye.com.geometricweather.common.basic.models.weather.Temperature
import wangdaye.com.geometricweather.common.basic.models.weather.UV
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode
import wangdaye.com.geometricweather.common.basic.models.weather.Wind
import wangdaye.com.geometricweather.common.basic.models.weather.WindDegree
import wangdaye.com.geometricweather.weather.json.owm.OwmAirPollutionResult
import wangdaye.com.geometricweather.weather.json.owm.OwmLocationResult
import wangdaye.com.geometricweather.weather.json.owm.OwmOneCallResult
import wangdaye.com.geometricweather.weather.services.WeatherService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

object OwmResultConverter {

    @JvmStatic
    fun convert(location: Location?, result: OwmLocationResult, zipCode: String?): Location {
        val isChina = !TextUtils.isEmpty(result.country) && (
            result.country == "CN"
                || result.country == "cn"
                || result.country == "HK"
                || result.country == "hk"
                || result.country == "TW"
                || result.country == "tw"
            )
        return Location(
            result.lat.toString() + ',' + result.lon.toString(),
            result.lat.toFloat(),
            result.lon.toFloat(),
            TimeZone.getTimeZone("UTC"),
            result.country ?: "",
            "",
            result.name ?: "",
            "",
            null,
            WeatherSource.OWM,
            false,
            false,
            isChina
        )
    }

    @JvmStatic
    fun convert(
        context: Context,
        location: Location,
        oneCallResult: OwmOneCallResult,
        airPollutionCurrentResult: OwmAirPollutionResult?,
        airPollutionForecastResult: OwmAirPollutionResult?
    ): WeatherService.WeatherResultWrapper {
        return try {
            val weather = Weather(
                Base(
                    location.cityId,
                    System.currentTimeMillis(),
                    Date(),
                    System.currentTimeMillis(),
                    Date(),
                    System.currentTimeMillis()
                ),
                Current(
                    oneCallResult.current!!.weather!![0].description!!,
                    getWeatherCode(oneCallResult.current!!.weather!![0].id),
                    Temperature(
                        toInt(oneCallResult.current!!.temp),
                        toInt(oneCallResult.current!!.feelsLike),
                        null, null, null, null, null
                    ),
                    Precipitation(
                        getTotalPrecipitation(
                            oneCallResult.current!!.rain?.cumul1h,
                            oneCallResult.current!!.snow?.cumul1h
                        ),
                        null,
                        oneCallResult.current!!.rain?.cumul1h,
                        oneCallResult.current!!.snow?.cumul1h,
                        null
                    ),
                    PrecipitationProbability(null, null, null, null, null),
                    Wind(
                        getWindDirection(oneCallResult.current!!.windDeg.toFloat()),
                        WindDegree(oneCallResult.current!!.windDeg.toFloat(), false),
                        oneCallResult.current!!.windSpeed!! * 3.6f,
                        CommonConverter.getWindLevel(context, (oneCallResult.current!!.windSpeed!! * 3.6f).toDouble())
                    ),
                    UV(toInt(oneCallResult.current!!.uvi), null, null),
                    if (airPollutionCurrentResult == null) AirQuality(
                        null, null, null, null,
                        null, null, null, null
                    ) else AirQuality(
                        CommonConverter.getAqiQuality(
                            context,
                            getAqiFromIndex(airPollutionCurrentResult.list!![0].main!!.aqi)
                        ),
                        getAqiFromIndex(airPollutionCurrentResult.list!![0].main!!.aqi),
                        airPollutionCurrentResult.list!![0].components!!.pm2_5.toFloat(),
                        airPollutionCurrentResult.list!![0].components!!.pm10.toFloat(),
                        airPollutionCurrentResult.list!![0].components!!.so2.toFloat(),
                        airPollutionCurrentResult.list!![0].components!!.no2.toFloat(),
                        airPollutionCurrentResult.list!![0].components!!.o3.toFloat(),
                        airPollutionCurrentResult.list!![0].components!!.co.toFloat()
                    ),
                    oneCallResult.current!!.humidity.toFloat(),
                    oneCallResult.current!!.pressure.toFloat(),
                    (oneCallResult.current!!.visibility / 1000).toFloat(),
                    toInt(oneCallResult.current!!.dewPoint),
                    oneCallResult.current!!.clouds,
                    null,
                    null,
                    null
                ),
                null,
                getDailyList(context, oneCallResult.daily!!, airPollutionForecastResult),
                getHourlyList(
                    context,
                    oneCallResult.current!!.sunrise,
                    oneCallResult.current!!.sunset,
                    oneCallResult.hourly!!
                ),
                getMinutelyList(
                    oneCallResult.current!!.sunrise,
                    oneCallResult.current!!.sunset,
                    oneCallResult.minutely
                ),
                getAlertList(oneCallResult.alerts)
            )
            WeatherService.WeatherResultWrapper(weather)
        } catch (_: Exception) {
            WeatherService.WeatherResultWrapper(null)
        }
    }

    private fun getDailyList(
        context: Context,
        dailyResult: List<OwmOneCallResult.Daily>,
        airPollutionForecastResult: OwmAirPollutionResult?
    ): List<Daily> {
        val dailyList = ArrayList<Daily>(dailyResult.size)
        for (forecasts in dailyResult) {
            dailyList.add(
                Daily(
                    Date(forecasts.dt * 1000),
                    forecasts.dt * 1000,
                    HalfDay(
                        forecasts.weather!![0].description!!,
                        forecasts.weather!![0].description!!,
                        getWeatherCode(forecasts.weather!![0].id),
                        Temperature(
                            toInt(forecasts.temp!!.day),
                            toInt(forecasts.feelsLike!!.day),
                            null, null, null, null, null
                        ),
                        Precipitation(
                            getTotalPrecipitation(
                                getPrecipitationForDaily(forecasts.rain),
                                getPrecipitationForDaily(forecasts.snow)
                            ),
                            null,
                            getPrecipitationForDaily(forecasts.rain),
                            getPrecipitationForDaily(forecasts.snow),
                            null
                        ),
                        PrecipitationProbability(forecasts.pop, null, null, null, null),
                        PrecipitationDuration(null, null, null, null, null),
                        Wind(
                            getWindDirection(forecasts.windDeg.toFloat()),
                            WindDegree(forecasts.windDeg.toFloat(), false),
                            forecasts.windSpeed!! * 3.6f,
                            CommonConverter.getWindLevel(context, (forecasts.windSpeed!! * 3.6f).toDouble())
                        ),
                        forecasts.clouds
                    ),
                    HalfDay(
                        forecasts.weather!![0].description!!,
                        forecasts.weather!![0].description!!,
                        getWeatherCode(forecasts.weather!![0].id),
                        Temperature(
                            toInt(forecasts.temp!!.night),
                            toInt(forecasts.feelsLike!!.night),
                            null, null, null, null, null
                        ),
                        Precipitation(
                            getTotalPrecipitation(
                                getPrecipitationForDaily(forecasts.rain),
                                getPrecipitationForDaily(forecasts.snow)
                            ),
                            null,
                            getPrecipitationForDaily(forecasts.rain),
                            getPrecipitationForDaily(forecasts.snow),
                            null
                        ),
                        PrecipitationProbability(forecasts.pop, null, null, null, null),
                        PrecipitationDuration(null, null, null, null, null),
                        Wind(
                            getWindDirection(forecasts.windDeg.toFloat()),
                            WindDegree(forecasts.windDeg.toFloat(), false),
                            forecasts.windSpeed!! * 3.6f,
                            CommonConverter.getWindLevel(context, (forecasts.windSpeed!! * 3.6f).toDouble())
                        ),
                        forecasts.clouds
                    ),
                    Astro(Date(forecasts.sunrise * 1000), Date(forecasts.sunset * 1000)),
                    Astro(null, null),
                    MoonPhase(null, null),
                    getAirQuality(context, Date(forecasts.dt * 1000), airPollutionForecastResult),
                    Pollen(null, null, null, null, null, null, null, null, null, null, null, null),
                    UV(toInt(forecasts.uvi), null, null),
                    0.0f
                )
            )
        }
        return dailyList
    }

    private fun getPrecipitationForDaily(precipitation: Float?): Float? {
        return if (precipitation != null) precipitation / 2 else null
    }

    private fun getTotalPrecipitation(rain: Float?, snow: Float?): Float? {
        if (rain == null) {
            return snow
        }
        if (snow == null) {
            return rain
        }
        return rain + snow
    }

    private fun getHourlyList(
        context: Context,
        sunrise: Long,
        sunset: Long,
        resultList: List<OwmOneCallResult.Hourly>
    ): List<Hourly> {
        val hourlyList = ArrayList<Hourly>(resultList.size)
        for (result in resultList) {
            hourlyList.add(
                Hourly(
                    Date(result.dt * 1000),
                    result.dt * 1000,
                    CommonConverter.isDaylight(
                        Date(sunrise * 1000),
                        Date(sunset * 1000),
                        Date(result.dt * 1000)
                    ),
                    result.weather!![0].main!!,
                    getWeatherCode(result.weather!![0].id),
                    Temperature(
                        toInt(result.temp),
                        toInt(result.feelsLike),
                        null, null, null, null, null
                    ),
                    Precipitation(
                        getTotalPrecipitation(result.rain?.cumul1h, result.snow?.cumul1h),
                        null,
                        result.rain?.cumul1h,
                        result.snow?.cumul1h,
                        null
                    ),
                    PrecipitationProbability(result.pop, null, null, null, null),
                    Wind(
                        getWindDirection(result.windDeg.toFloat()),
                        WindDegree(result.windDeg.toFloat(), false),
                        result.windSpeed!! * 3.6f,
                        CommonConverter.getWindLevel(context, (result.windSpeed!! * 3.6f).toDouble())
                    ),
                    UV(toInt(result.uvi), null, null)
                )
            )
        }
        return hourlyList
    }

    private fun getMinutelyList(
        sunrise: Long,
        sunset: Long,
        minuteResult: List<OwmOneCallResult.Minutely>?
    ): List<Minutely> {
        if (minuteResult == null) {
            return ArrayList()
        }
        return ArrayList(minuteResult.size)
    }

    private fun getAqiFromIndex(aqi: Int?): Int? {
        if (aqi == null || aqi <= 0) {
            return null
        }
        return if (aqi <= AirQuality.AQI_INDEX_1) {
            AirQuality.AQI_INDEX_1
        } else if (aqi <= AirQuality.AQI_INDEX_2) {
            AirQuality.AQI_INDEX_2
        } else if (aqi <= AirQuality.AQI_INDEX_3) {
            AirQuality.AQI_INDEX_3
        } else if (aqi <= AirQuality.AQI_INDEX_4) {
            AirQuality.AQI_INDEX_4
        } else if (aqi <= AirQuality.AQI_INDEX_5) {
            AirQuality.AQI_INDEX_5
        } else {
            400
        }
    }

    private fun getAirQuality(
        context: Context,
        requestedDate: Date,
        owmAirPollutionForecastResult: OwmAirPollutionResult?
    ): AirQuality {
        if (owmAirPollutionForecastResult != null) {
            val fmt = SimpleDateFormat("yyyyMMdd")
            for (airPollutionForecast in owmAirPollutionForecastResult.list!!) {
                if (fmt.format(requestedDate) == fmt.format(Date(airPollutionForecast.dt * 1000))) {
                    return AirQuality(
                        CommonConverter.getAqiQuality(
                            context,
                            getAqiFromIndex(airPollutionForecast.main!!.aqi)
                        ),
                        getAqiFromIndex(airPollutionForecast.main!!.aqi),
                        airPollutionForecast.components!!.pm2_5.toFloat(),
                        airPollutionForecast.components!!.pm10.toFloat(),
                        airPollutionForecast.components!!.so2.toFloat(),
                        airPollutionForecast.components!!.no2.toFloat(),
                        airPollutionForecast.components!!.o3.toFloat(),
                        airPollutionForecast.components!!.co.toFloat()
                    )
                }
            }
        }
        return AirQuality(null, null, null, null, null, null, null, null)
    }

    private fun getAlertList(resultList: List<OwmOneCallResult.Alert>?): List<Alert> {
        var i = 0
        if (resultList != null) {
            val alertList = ArrayList<Alert>(resultList.size)
            for (result in resultList) {
                alertList.add(
                    Alert(
                        i.toLong(),
                        Date(result.start * 1000),
                        result.start * 1000,
                        result.event!!,
                        result.description!!,
                        result.event!!,
                        1,
                        Color.rgb(255, 184, 43)
                    )
                )
                ++i
            }
            Alert.deduplication(alertList)
            Alert.descByTime(alertList)
            return alertList
        } else {
            return ArrayList()
        }
    }

    private fun toInt(value: Double): Int {
        return (value + 0.5).toInt()
    }

    private fun getWeatherCode(icon: Int): WeatherCode {
        return if (icon == 200 || icon == 201 || icon == 202) {
            WeatherCode.THUNDERSTORM
        } else if (icon == 210 || icon == 211 || icon == 212) {
            WeatherCode.THUNDER
        } else if (icon == 221 || icon == 230 || icon == 231 || icon == 232) {
            WeatherCode.THUNDERSTORM
        } else if (icon == 300 || icon == 301 || icon == 302
            || icon == 310 || icon == 311 || icon == 312
            || icon == 313 || icon == 314 || icon == 321
        ) {
            WeatherCode.RAIN
        } else if (icon == 500 || icon == 501 || icon == 502 || icon == 503 || icon == 504) {
            WeatherCode.RAIN
        } else if (icon == 511) {
            WeatherCode.SLEET
        } else if (icon == 600 || icon == 601 || icon == 602) {
            WeatherCode.SNOW
        } else if (icon == 611 || icon == 612 || icon == 613
            || icon == 614 || icon == 615 || icon == 616
        ) {
            WeatherCode.SLEET
        } else if (icon == 620 || icon == 621 || icon == 622) {
            WeatherCode.SNOW
        } else if (icon == 701 || icon == 711 || icon == 721 || icon == 731) {
            WeatherCode.HAZE
        } else if (icon == 741) {
            WeatherCode.FOG
        } else if (icon == 751 || icon == 761 || icon == 762) {
            WeatherCode.HAZE
        } else if (icon == 771 || icon == 781) {
            WeatherCode.WIND
        } else if (icon == 800) {
            WeatherCode.CLEAR
        } else if (icon == 801 || icon == 802) {
            WeatherCode.PARTLY_CLOUDY
        } else if (icon == 803 || icon == 804) {
            WeatherCode.CLOUDY
        } else {
            WeatherCode.CLOUDY
        }
    }

    private fun getWindDirection(degree: Float): String {
        return if (degree < 0) {
            "Variable"
        } else if (22.5 < degree && degree <= 67.5) {
            "NE"
        } else if (67.5 < degree && degree <= 112.5) {
            "E"
        } else if (112.5 < degree && degree <= 157.5) {
            "SE"
        } else if (157.5 < degree && degree <= 202.5) {
            "S"
        } else if (202.5 < degree && degree <= 247.5) {
            "SO"
        } else if (247.5 < degree && degree <= 292.5) {
            "O"
        } else if (292.0 < degree && degree <= 337.5) {
            "NO"
        } else {
            "N"
        }
    }
}
