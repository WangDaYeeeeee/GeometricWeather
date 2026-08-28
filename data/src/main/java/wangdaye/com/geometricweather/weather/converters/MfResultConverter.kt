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
import wangdaye.com.geometricweather.weather.json.atmoaura.AtmoAuraQAResult
import wangdaye.com.geometricweather.weather.json.mf.MfCurrentResult
import wangdaye.com.geometricweather.weather.json.mf.MfEphemerisResult
import wangdaye.com.geometricweather.weather.json.mf.MfForecastResult
import wangdaye.com.geometricweather.weather.json.mf.MfForecastV2Result
import wangdaye.com.geometricweather.weather.json.mf.MfLocationResult
import wangdaye.com.geometricweather.weather.json.mf.MfRainResult
import wangdaye.com.geometricweather.weather.json.mf.MfWarningsResult
import wangdaye.com.geometricweather.weather.services.WeatherService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

object MfResultConverter {

    @JvmStatic
    fun convert(location: Location?, result: MfForecastV2Result): Location {
        val isChina = !TextUtils.isEmpty(result.properties!!.country) && (
            result.properties!!.country!!.startsWith("CN")
                || result.properties!!.country!!.startsWith("cn")
                || result.properties!!.country!!.startsWith("HK")
                || result.properties!!.country!!.startsWith("hk")
                || result.properties!!.country!!.startsWith("TW")
                || result.properties!!.country!!.startsWith("tw")
            )
        return if (location != null
            && !TextUtils.isEmpty(location.province)
            && !TextUtils.isEmpty(location.city)
            && !TextUtils.isEmpty(location.district)
        ) {
            Location(
                result.properties!!.insee!!,
                result.geometry!!.coordinates!![1],
                result.geometry!!.coordinates!![0],
                TimeZone.getTimeZone(result.properties!!.timezone),
                result.properties!!.country!!,
                location.province,
                location.city,
                location.district,
                null,
                WeatherSource.MF,
                false,
                false,
                isChina
            )
        } else {
            Location(
                result.properties!!.insee!!,
                result.geometry!!.coordinates!![1],
                result.geometry!!.coordinates!![0],
                TimeZone.getTimeZone(result.properties!!.timezone),
                result.properties!!.country!!,
                result.properties!!.frenchDepartment ?: "",
                result.properties!!.name ?: "",
                "",
                null,
                WeatherSource.MF,
                false,
                false,
                isChina
            )
        }
    }

    @JvmStatic
    fun convert(location: Location?, result: MfLocationResult): Location {
        val isChina = !TextUtils.isEmpty(result.country) && (
            result.country == "CN"
                || result.country == "cn"
                || result.country == "HK"
                || result.country == "hk"
                || result.country == "TW"
                || result.country == "tw"
            )
        return if (location != null
            && !TextUtils.isEmpty(location.province)
            && !TextUtils.isEmpty(location.city)
            && !TextUtils.isEmpty(location.district)
        ) {
            Location(
                result.postCode!!,
                result.lat.toFloat(),
                result.lon.toFloat(),
                TimeZone.getTimeZone("Europe/Paris"),
                result.country ?: "",
                location.province,
                location.city,
                location.district,
                null,
                WeatherSource.MF,
                false,
                false,
                isChina
            )
        } else {
            Location(
                result.postCode!!,
                result.lat.toFloat(),
                result.lon.toFloat(),
                TimeZone.getTimeZone("Europe/Paris"),
                result.country ?: "",
                result.admin2 ?: "",
                result.name + if (result.postCode == null) "" else " (${result.postCode})",
                "",
                null,
                WeatherSource.MF,
                false,
                false,
                isChina
            )
        }
    }

    @JvmStatic
    fun convert(
        context: Context,
        location: Location,
        currentResult: MfCurrentResult,
        forecastResult: MfForecastResult,
        ephemerisResult: MfEphemerisResult,
        rainResult: MfRainResult,
        warningsResult: MfWarningsResult,
        aqiAtmoAuraResult: AtmoAuraQAResult?
    ): WeatherService.WeatherResultWrapper {
        return try {
            val hourly = getHourlyList(context, forecastResult.forecasts!!, forecastResult.probabilityForecast!!)
            val weather = Weather(
                Base(
                    location.cityId,
                    System.currentTimeMillis(),
                    Date(forecastResult.updatedOn * 1000),
                    forecastResult.updatedOn * 1000,
                    Date(),
                    System.currentTimeMillis()
                ),
                Current(
                    currentResult.observation!!.weather!!.desc!!,
                    getWeatherCode(currentResult.observation!!.weather!!.icon),
                    Temperature(
                        toInt(currentResult.observation!!.temperature!!.toDouble()),
                        null, null, null, null, null, null
                    ),
                    Precipitation(null, null, null, null, null),
                    PrecipitationProbability(null, null, null, null, null),
                    Wind(
                        currentResult.observation!!.wind!!.icon!!,
                        WindDegree(
                            currentResult.observation!!.wind!!.direction!!.toFloat(),
                            currentResult.observation!!.wind!!.direction == -1
                        ),
                        currentResult.observation!!.wind!!.speed!! * 3.6f,
                        CommonConverter.getWindLevel(
                            context,
                            (currentResult.observation!!.wind!!.speed!! * 3.6f).toDouble()
                        )
                    ),
                    UV(null, null, null),
                    getAirQuality(Date(), aqiAtmoAuraResult),
                    null, null, null, null, null, null, null, null
                ),
                null,
                getDailyList(context, forecastResult, hourly, ephemerisResult, aqiAtmoAuraResult),
                hourly,
                getMinutelyList(
                    forecastResult.dailyForecasts!![0].sun!!.rise!!,
                    forecastResult.dailyForecasts!![0].sun!!.set!!,
                    rainResult
                ),
                getWarningsList(warningsResult)
            )
            WeatherService.WeatherResultWrapper(weather)
        } catch (_: Exception) {
            WeatherService.WeatherResultWrapper(null)
        }
    }

    private fun getAirQuality(requestedDate: Date, aqiAtmoAuraResult: AtmoAuraQAResult?): AirQuality {
        if (aqiAtmoAuraResult == null) {
            return AirQuality(null, null, null, null, null, null, null, null)
        }
        val fmt = SimpleDateFormat("yyyyMMdd")
        val indexs = aqiAtmoAuraResult.indexs!!
        return if (fmt.format(requestedDate) == fmt.format(indexs.yesterday!!.date!!)) {
            AirQuality(
                indexs.yesterday!!.aggregatedIndex!!.quali,
                Math.round(indexs.yesterday!!.aggregatedIndex!!.`val`).toInt(),
                null, indexs.yesterday!!.pm10!!.`val`.toFloat(),
                null, indexs.yesterday!!.no2!!.`val`.toFloat(),
                indexs.yesterday!!.o3!!.`val`.toFloat(), null
            )
        } else if (fmt.format(requestedDate) == fmt.format(indexs.today!!.date!!)) {
            AirQuality(
                indexs.today!!.aggregatedIndex!!.quali,
                Math.round(indexs.today!!.aggregatedIndex!!.`val`).toInt(),
                null, indexs.today!!.pm10!!.`val`.toFloat(),
                null, indexs.today!!.no2!!.`val`.toFloat(),
                indexs.today!!.o3!!.`val`.toFloat(), null
            )
        } else if (fmt.format(requestedDate) == fmt.format(indexs.tomorrow!!.date!!)) {
            AirQuality(
                indexs.tomorrow!!.aggregatedIndex!!.quali,
                Math.round(indexs.tomorrow!!.aggregatedIndex!!.`val`).toInt(),
                null, indexs.tomorrow!!.pm10!!.`val`.toFloat(),
                null, indexs.tomorrow!!.no2!!.`val`.toFloat(),
                indexs.tomorrow!!.o3!!.`val`.toFloat(), null
            )
        } else if (indexs.inTwoDays != null && fmt.format(requestedDate) == fmt.format(indexs.inTwoDays!!.date!!)) {
            AirQuality(
                indexs.inTwoDays!!.aggregatedIndex!!.quali,
                Math.round(indexs.inTwoDays!!.aggregatedIndex!!.`val`).toInt(),
                null, indexs.inTwoDays!!.pm10!!.`val`.toFloat(),
                null, indexs.inTwoDays!!.no2!!.`val`.toFloat(),
                indexs.inTwoDays!!.o3!!.`val`.toFloat(), null
            )
        } else {
            AirQuality(null, null, null, null, null, null, null, null)
        }
    }

    private fun getHalfDay(
        context: Context,
        isDaytime: Boolean,
        hourly: List<Hourly>,
        hourlyForecast: List<MfForecastResult.Forecast>,
        dailyForecast: MfForecastResult.DailyForecast
    ): HalfDay {
        var temp: Int? = if (isDaytime) toInt(dailyForecast.temperature!!.max!!.toDouble())
        else toInt(dailyForecast.temperature!!.min!!.toDouble())
        var tempWindChill: Int? = null

        var precipitationTotal = 0.0f
        var precipitationRain = 0.0f
        var precipitationSnow = 0.0f

        var probPrecipitationTotal = 0.0f
        var probPrecipitationRain = 0.0f
        var probPrecipitationSnow = 0.0f
        var probPrecipitationIce = 0.0f

        for (hour in hourly) {
            if ((isDaytime && hour.time / 1000 >= dailyForecast.dt + 6 * 3600 && hour.time / 1000 < dailyForecast.dt + 18 * 3600)
                || (!isDaytime && hour.time / 1000 >= dailyForecast.dt + 18 * 3600 && hour.time / 1000 < dailyForecast.dt + 30 * 3600)
            ) {
                if (isDaytime) {
                    if (temp == null || hour.temperature.temperature > temp) {
                        temp = hour.temperature.temperature
                    }
                    if (tempWindChill == null || hour.temperature.windChillTemperature!! > tempWindChill) {
                        tempWindChill = hour.temperature.windChillTemperature
                    }
                }
                if (!isDaytime) {
                    if (temp == null || hour.temperature.temperature < temp) {
                        temp = hour.temperature.temperature
                    }
                    if (tempWindChill == null || hour.temperature.windChillTemperature!! < tempWindChill) {
                        tempWindChill = hour.temperature.windChillTemperature
                    }
                }
                precipitationTotal += hour.precipitation.total!!
                precipitationRain += hour.precipitation.rain!!
                precipitationSnow += hour.precipitation.snow!!
                if (hour.precipitationProbability.total != null && hour.precipitationProbability.total!! > probPrecipitationTotal) {
                    probPrecipitationTotal = hour.precipitationProbability.total!!
                }
                if (hour.precipitationProbability.rain != null && hour.precipitationProbability.rain!! > probPrecipitationRain) {
                    probPrecipitationRain = hour.precipitationProbability.rain!!
                }
                if (hour.precipitationProbability.snow != null && hour.precipitationProbability.snow!! > probPrecipitationSnow) {
                    probPrecipitationSnow = hour.precipitationProbability.snow!!
                }
                if (hour.precipitationProbability.ice != null && hour.precipitationProbability.ice!! > probPrecipitationIce) {
                    probPrecipitationIce = hour.precipitationProbability.ice!!
                }
            }
        }

        var cloudCover: Int? = null
        var windDirection = "Pas d’info"
        var windDegree = WindDegree(0f, false)
        var windSpeed: Float? = null
        var windLevel = "Pas d’info"

        for (hourForecast in hourlyForecast) {
            if ((isDaytime && hourForecast.dt >= dailyForecast.dt + 6 * 3600 && hourForecast.dt < dailyForecast.dt + 18 * 3600)
                || (!isDaytime && hourForecast.dt >= dailyForecast.dt + 18 * 3600 && hourForecast.dt < dailyForecast.dt + 30 * 3600)
            ) {
                if (cloudCover == null || hourForecast.clouds!! > cloudCover) {
                    cloudCover = hourForecast.clouds
                }
                if (windSpeed == null || hourForecast.wind!!.speed!! * 3.6f > windSpeed) {
                    windDirection = hourForecast.wind!!.icon!!
                    windDegree = WindDegree(
                        if (hourForecast.wind!!.direction == "Variable") 0.0f
                        else java.lang.Float.parseFloat(hourForecast.wind!!.direction!!),
                        hourForecast.wind!!.direction == "Variable"
                    )
                    windSpeed = hourForecast.wind!!.speed!! * 3.6f
                    windLevel = CommonConverter.getWindLevel(context, (hourForecast.wind!!.speed!! * 3.6f).toDouble())
                }
            }
        }

        return HalfDay(
            dailyForecast.weather12H?.desc ?: "",
            dailyForecast.weather12H?.desc ?: "",
            if (dailyForecast.weather12H == null) WeatherCode.CLEAR else getWeatherCode(dailyForecast.weather12H!!.icon),
            Temperature(temp!!, null, null, null, tempWindChill, null, null),
            Precipitation(precipitationTotal, null, precipitationRain, precipitationSnow, null),
            PrecipitationProbability(
                probPrecipitationTotal, null, probPrecipitationRain, probPrecipitationSnow, probPrecipitationIce
            ),
            PrecipitationDuration(null, null, null, null, null),
            Wind(windDirection, windDegree, windSpeed, windLevel),
            cloudCover
        )
    }

    private fun getDailyList(
        context: Context,
        forecastsResult: MfForecastResult,
        hourly: List<Hourly>,
        ephemerisResult: MfEphemerisResult,
        aqiAtmoAuraResult: AtmoAuraQAResult?
    ): List<Daily> {
        val dailyList = ArrayList<Daily>(forecastsResult.dailyForecasts!!.size)
        for (dailyForecast in forecastsResult.dailyForecasts!!) {
            if (dailyForecast.temperature!!.min != null && dailyForecast.temperature!!.max != null) {
                dailyList.add(
                    Daily(
                        Date(dailyForecast.dt * 1000),
                        dailyForecast.dt * 1000,
                        getHalfDay(context, true, hourly, forecastsResult.forecasts!!, dailyForecast),
                        getHalfDay(context, false, hourly, forecastsResult.forecasts!!, dailyForecast),
                        Astro(Date(dailyForecast.sun!!.rise!! * 1000), Date(dailyForecast.sun!!.set!! * 1000)),
                        Astro(null, null),
                        MoonPhase(
                            CommonConverter.getMoonPhaseAngle(
                                ephemerisResult.properties!!.ephemeris!!.moonPhaseDescription
                            ),
                            ephemerisResult.properties!!.ephemeris!!.moonPhaseDescription
                        ),
                        getAirQuality(Date(dailyForecast.dt * 1000), aqiAtmoAuraResult),
                        Pollen(null, null, null, null, null, null, null, null, null, null, null, null),
                        UV(dailyForecast.uv, null, null),
                        getHoursOfDay(Date(dailyForecast.sun!!.rise!! * 1000), Date(dailyForecast.sun!!.set!! * 1000))
                    )
                )
            }
        }
        return dailyList
    }

    private fun getRainCumul(rain: MfForecastResult.Forecast.Rain): Float? {
        return rain.cumul1H ?: rain.cumul3H ?: rain.cumul6H ?: rain.cumul12H ?: rain.cumul24H
    }

    private fun getSnowCumul(snow: MfForecastResult.Forecast.Snow): Float? {
        return snow.cumul1H ?: snow.cumul3H ?: snow.cumul6H ?: snow.cumul12H ?: snow.cumul24H
    }

    private fun getHourlyPrecipitation(hourlyForecast: MfForecastResult.Forecast): Precipitation {
        val rainCumul = getRainCumul(hourlyForecast.rain!!)
        val snowCumul = getSnowCumul(hourlyForecast.snow!!)
        val totalCumul = if (rainCumul == null) {
            snowCumul
        } else if (snowCumul == null) {
            rainCumul
        } else {
            snowCumul + rainCumul
        }
        return Precipitation(totalCumul, null, rainCumul, snowCumul, null)
    }

    private fun getHourlyPrecipitationProbability(
        probabilityForecastResult: List<MfForecastResult.ProbabilityForecast>,
        dt: Long
    ): PrecipitationProbability {
        var rainProbability: Float? = null
        var snowProbability: Float? = null
        var iceProbability: Float? = null
        for (probabilityForecast in probabilityForecastResult) {
            if (probabilityForecast.dt == dt || probabilityForecast.dt + 3600 == dt || probabilityForecast.dt + 3600 * 2 == dt) {
                rainProbability = if (probabilityForecast.rain!!.proba3H != null) {
                    probabilityForecast.rain!!.proba3H!! * 1f
                } else if (probabilityForecast.rain!!.proba6H != null) {
                    probabilityForecast.rain!!.proba6H!! * 1f
                } else {
                    rainProbability
                }
                snowProbability = if (probabilityForecast.snow!!.proba3H != null) {
                    probabilityForecast.snow!!.proba3H!! * 1f
                } else if (probabilityForecast.snow!!.proba6H != null) {
                    probabilityForecast.snow!!.proba6H!! * 1f
                } else {
                    snowProbability
                }
                iceProbability = probabilityForecast.freezing!! * 1f
            }
            if (probabilityForecast.dt + 3600 * 3 == dt || probabilityForecast.dt + 3600 * 4 == dt || probabilityForecast.dt + 3600 * 5 == dt) {
                if (probabilityForecast.rain!!.proba6H != null) {
                    rainProbability = probabilityForecast.rain!!.proba6H!! * 1f
                }
                if (probabilityForecast.snow!!.proba6H != null) {
                    snowProbability = probabilityForecast.snow!!.proba6H!! * 1f
                }
                iceProbability = probabilityForecast.freezing!! * 1f
            }
        }
        val allProbabilities = listOf(
            rainProbability ?: 0f,
            snowProbability ?: 0f,
            iceProbability ?: 0f
        )
        return PrecipitationProbability(
            allProbabilities.maxOrNull() ?: 0f,
            null,
            rainProbability,
            snowProbability,
            iceProbability
        )
    }

    private fun getHourlyList(
        context: Context,
        hourlyForecastResult: List<MfForecastResult.Forecast>,
        probabilityForecastResult: List<MfForecastResult.ProbabilityForecast>
    ): List<Hourly> {
        val hourlyList = ArrayList<Hourly>(hourlyForecastResult.size)
        for (hourlyForecast in hourlyForecastResult) {
            hourlyList.add(
                Hourly(
                    Date(hourlyForecast.dt * 1000),
                    hourlyForecast.dt * 1000,
                    !hourlyForecast.weather!!.icon!!.endsWith("n"),
                    hourlyForecast.weather!!.desc!!,
                    getWeatherCode(hourlyForecast.weather!!.icon),
                    Temperature(
                        toInt(hourlyForecast.temperature!!.value!!.toDouble()),
                        null, null, null,
                        toInt(hourlyForecast.temperature!!.windChill!!.toDouble()),
                        null, null
                    ),
                    getHourlyPrecipitation(hourlyForecast),
                    getHourlyPrecipitationProbability(probabilityForecastResult, hourlyForecast.dt),
                    Wind(
                        hourlyForecast.wind!!.icon!!,
                        WindDegree(
                            if (hourlyForecast.wind!!.direction == "Variable") 0.0f
                            else java.lang.Float.parseFloat(hourlyForecast.wind!!.direction!!),
                            hourlyForecast.wind!!.direction == "Variable"
                        ),
                        hourlyForecast.wind!!.speed!! * 3.6f,
                        CommonConverter.getWindLevel(
                            context,
                            (hourlyForecast.wind!!.speed!! * 3.6f).toDouble()
                        )
                    ),
                    UV(null, null, null)
                )
            )
        }
        return hourlyList
    }

    private fun getMinutelyList(sunrise: Long, sunset: Long, rainResult: MfRainResult?): List<Minutely> {
        if (rainResult == null) {
            return emptyList()
        }
        val minutelyList = ArrayList<Minutely>(rainResult.rainForecasts!!.size)
        val minuteZero = rainResult.rainForecasts!![0].date / 60
        for (rainForecast in rainResult.rainForecasts!!) {
            minutelyList.add(
                Minutely(
                    Date(rainForecast.date * 1000),
                    rainForecast.date * 1000,
                    CommonConverter.isDaylight(
                        Date(sunrise * 1000),
                        Date(sunset * 1000),
                        Date(rainForecast.date * 1000)
                    ),
                    rainForecast.desc!!,
                    if (rainForecast.rain > 1) WeatherCode.RAIN else getWeatherCode(null),
                    toInt((rainForecast.date / 60 - minuteZero).toDouble()),
                    null as Int?,
                    null
                )
            )
        }
        return minutelyList
    }

    private fun getWarningsList(warningsResult: MfWarningsResult): List<Alert> {
        val alertList = ArrayList<Alert>(warningsResult.phenomenonsItems?.size ?: 0)
        if (warningsResult.phenomenonsItems != null) {
            for (phemononItem in warningsResult.phenomenonsItems!!) {
                if (phemononItem.phenomenoMaxColorId > 1) {
                    alertList.add(
                        Alert(
                            phemononItem.phenomenonId.toLong(),
                            Date(warningsResult.updateTime * 1000),
                            warningsResult.updateTime * 1000,
                            getWarningType(phemononItem.phenomenonId) + " — " + getWarningText(phemononItem.phenomenoMaxColorId),
                            "",
                            getWarningType(phemononItem.phenomenonId),
                            phemononItem.phenomenoMaxColorId,
                            getWarningColor(phemononItem.phenomenoMaxColorId)
                        )
                    )
                }
            }
            Alert.deduplication(alertList)
        }
        return alertList
    }

    private fun toInt(value: Double): Int {
        return (value + 0.5).toInt()
    }

    private fun getWarningType(phemononId: Int): String {
        return when (phemononId) {
            1 -> "Vent"
            2 -> "Pluie-Inondation"
            3 -> "Orages"
            4 -> "Crues"
            5 -> "Neige-Verglas"
            6 -> "Canicule"
            7 -> "Grand Froid"
            8 -> "Avalanches"
            9 -> "Vagues-Submersion"
            else -> "Divers"
        }
    }

    private fun getWarningText(colorId: Int): String {
        return when (colorId) {
            4 -> "Vigilance absolue"
            3 -> "Soyez très vigilant"
            2 -> "Soyez attentif"
            else -> "Pas de vigilance particulière"
        }
    }

    private fun getWarningColor(colorId: Int): Int {
        return when (colorId) {
            4 -> Color.rgb(204, 0, 0)
            3 -> Color.rgb(255, 184, 43)
            2 -> Color.rgb(255, 246, 0)
            else -> Color.rgb(49, 170, 53)
        }
    }

    private fun getWeatherCode(icon: String?): WeatherCode {
        if (icon == null) {
            return WeatherCode.CLEAR
        }
        return if (icon == "p1" || icon == "p1j" || icon == "p1n"
            || icon == "p1bis" || icon == "p1bisj" || icon == "p1bisn"
        ) {
            WeatherCode.CLEAR
        } else if (icon == "p2" || icon == "p2j" || icon == "p2n"
            || icon == "p2bis" || icon == "p2bisj" || icon == "p2bisn"
        ) {
            WeatherCode.PARTLY_CLOUDY
        } else if (icon == "p3" || icon == "p3j" || icon == "p3n"
            || icon == "p3bis" || icon == "p3bisj" || icon == "p3bisn"
        ) {
            WeatherCode.CLOUDY
        } else if (icon == "p4" || icon == "p4j" || icon == "p4n"
            || icon == "p5" || icon == "p5j" || icon == "p5n"
            || icon == "p5bis" || icon == "p5bisj" || icon == "p5bisn"
        ) {
            WeatherCode.HAZE
        } else if (icon == "p6" || icon == "p6j" || icon == "p6n"
            || icon == "p6bis" || icon == "p6bisj" || icon == "p6bisn"
            || icon == "p6ter" || icon == "p6terj" || icon == "p6tern"
            || icon == "p7" || icon == "p7j" || icon == "p7n"
            || icon == "p7bis" || icon == "p7bisj" || icon == "p7bisn"
            || icon == "p8" || icon == "p8j" || icon == "p8n"
            || icon == "p8bis" || icon == "p8bisj" || icon == "p8bisn"
        ) {
            WeatherCode.FOG
        } else if (icon == "p9" || icon == "p9j" || icon == "p9n"
            || icon.startsWith("p10") || icon.startsWith("p11") || icon.startsWith("p12")
            || icon.startsWith("p13") || icon.startsWith("p14")
        ) {
            WeatherCode.RAIN
        } else if (icon.startsWith("p16") || icon.startsWith("p24") || icon.startsWith("p25")) {
            WeatherCode.THUNDERSTORM
        } else if (icon.startsWith("p17") || icon.startsWith("p18")) {
            WeatherCode.SLEET
        } else if (icon.startsWith("p19") || icon.startsWith("p20")) {
            WeatherCode.HAIL
        } else if (icon.startsWith("p21") || icon.startsWith("p22") || icon.startsWith("p23")) {
            WeatherCode.SNOW
        } else if (icon.startsWith("p26") || icon.startsWith("p27") || icon.startsWith("p28")
            || icon.startsWith("p29")
        ) {
            WeatherCode.THUNDER
        } else {
            WeatherCode.CLEAR
        }
    }

    private fun getHoursOfDay(sunrise: Date, sunset: Date): Float {
        return ((sunset.time - sunrise.time) / 1000 / 60 / 60.0).toFloat()
    }
}
