package wangdaye.com.geometricweather.weather.converters

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.common.basic.models.options.unit.PrecipitationUnit
import wangdaye.com.geometricweather.common.basic.models.weather.AirQuality
import wangdaye.com.geometricweather.common.basic.models.weather.Alert
import wangdaye.com.geometricweather.common.basic.models.weather.Astro
import wangdaye.com.geometricweather.common.basic.models.weather.Base
import wangdaye.com.geometricweather.common.basic.models.weather.Current
import wangdaye.com.geometricweather.common.basic.models.weather.Daily
import wangdaye.com.geometricweather.common.basic.models.weather.HalfDay
import wangdaye.com.geometricweather.common.basic.models.weather.History
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
import wangdaye.com.geometricweather.weather.WeatherProviderSettings
import wangdaye.com.geometricweather.weather.json.accu.AccuAlertResult
import wangdaye.com.geometricweather.weather.json.accu.AccuAqiResult
import wangdaye.com.geometricweather.weather.json.accu.AccuCurrentResult
import wangdaye.com.geometricweather.weather.json.accu.AccuDailyResult
import wangdaye.com.geometricweather.weather.json.accu.AccuHourlyResult
import wangdaye.com.geometricweather.weather.json.accu.AccuLocationResult
import wangdaye.com.geometricweather.weather.json.accu.AccuMinuteResult
import wangdaye.com.geometricweather.weather.services.WeatherService
import java.util.Date
import java.util.TimeZone
import java.util.regex.Pattern

object AccuResultConverter {

    @JvmStatic
    fun convert(location: Location?, result: AccuLocationResult, zipCode: String?): Location {
        val zipSuffix = if (zipCode == null) "" else " ($zipCode)"
        val isChina = !TextUtils.isEmpty(result.Country!!.ID) && (
            result.Country!!.ID == "CN"
                || result.Country!!.ID == "cn"
                || result.Country!!.ID == "HK"
                || result.Country!!.ID == "hk"
                || result.Country!!.ID == "TW"
                || result.Country!!.ID == "tw"
            )
        return if (location != null
            && !TextUtils.isEmpty(location.province)
            && !TextUtils.isEmpty(location.city)
            && !TextUtils.isEmpty(location.district)
        ) {
            Location(
                result.Key!!,
                result.GeoPosition!!.Latitude.toFloat(),
                result.GeoPosition!!.Longitude.toFloat(),
                TimeZone.getTimeZone(result.TimeZone!!.Name),
                result.Country!!.LocalizedName!!,
                location.province,
                location.city,
                location.district + zipSuffix,
                null,
                WeatherSource.ACCU,
                false,
                false,
                isChina
            )
        } else {
            Location(
                result.Key!!,
                result.GeoPosition!!.Latitude.toFloat(),
                result.GeoPosition!!.Longitude.toFloat(),
                TimeZone.getTimeZone(result.TimeZone!!.Name),
                result.Country!!.LocalizedName!!,
                result.AdministrativeArea?.LocalizedName ?: "",
                result.LocalizedName + zipSuffix,
                "",
                null,
                WeatherSource.ACCU,
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
        currentResult: AccuCurrentResult,
        dailyResult: AccuDailyResult,
        hourlyResultList: List<AccuHourlyResult>,
        minuteResult: AccuMinuteResult?,
        aqiResult: AccuAqiResult?,
        alertResultList: List<AccuAlertResult>
    ): WeatherService.WeatherResultWrapper {
        return try {
            val weather = Weather(
                Base(
                    location.cityId,
                    System.currentTimeMillis(),
                    Date(currentResult.EpochTime * 1000),
                    currentResult.EpochTime * 1000,
                    Date(),
                    System.currentTimeMillis()
                ),
                Current(
                    currentResult.WeatherText!!,
                    getWeatherCode(currentResult.WeatherIcon),
                    Temperature(
                        toInt(currentResult.Temperature!!.Metric!!.Value),
                        toInt(currentResult.RealFeelTemperature!!.Metric!!.Value),
                        toInt(currentResult.RealFeelTemperatureShade!!.Metric!!.Value),
                        toInt(currentResult.ApparentTemperature!!.Metric!!.Value),
                        toInt(currentResult.WindChillTemperature!!.Metric!!.Value),
                        toInt(currentResult.WetBulbTemperature!!.Metric!!.Value),
                        null
                    ),
                    Precipitation(
                        currentResult.Precip1hr!!.Metric!!.Value.toFloat(),
                        null,
                        null,
                        null,
                        null
                    ),
                    PrecipitationProbability(null, null, null, null, null),
                    Wind(
                        currentResult.Wind!!.Direction!!.Localized!!,
                        WindDegree(currentResult.Wind!!.Direction!!.Degrees.toFloat(), false),
                        currentResult.WindGust!!.Speed!!.Metric!!.Value.toFloat(),
                        CommonConverter.getWindLevel(context, currentResult.WindGust!!.Speed!!.Metric!!.Value)
                    ),
                    UV(currentResult.UVIndex, currentResult.UVIndexText, null),
                    if (aqiResult == null) AirQuality(
                        null, null, null, null,
                        null, null, null, null
                    ) else AirQuality(
                        CommonConverter.getAqiQuality(context, aqiResult.Index),
                        aqiResult.Index,
                        aqiResult.ParticulateMatter2_5,
                        aqiResult.ParticulateMatter10,
                        aqiResult.SulfurDioxide,
                        aqiResult.NitrogenDioxide,
                        aqiResult.Ozone,
                        aqiResult.CarbonMonoxide
                    ),
                    currentResult.RelativeHumidity.toFloat(),
                    currentResult.Pressure!!.Metric!!.Value.toFloat(),
                    currentResult.Visibility!!.Metric!!.Value.toFloat(),
                    toInt(currentResult.DewPoint!!.Metric!!.Value),
                    currentResult.CloudCover,
                    (currentResult.Ceiling!!.Metric!!.Value / 1000.0).toFloat(),
                    convertUnit(context, dailyResult.Headline!!.Text),
                    convertUnit(context, minuteResult?.Summary?.LongPhrase)
                ),
                History(
                    Date((currentResult.EpochTime - 24 * 60 * 60) * 1000),
                    (currentResult.EpochTime - 24 * 60 * 60) * 1000,
                    toInt(currentResult.TemperatureSummary!!.Past24HourRange!!.Maximum!!.Metric!!.Value),
                    toInt(currentResult.TemperatureSummary!!.Past24HourRange!!.Minimum!!.Metric!!.Value)
                ),
                getDailyList(context, dailyResult),
                getHourlyList(context, hourlyResultList),
                getMinutelyList(
                    Date(dailyResult.DailyForecasts!![0].Sun!!.EpochRise * 1000),
                    Date(dailyResult.DailyForecasts!![0].Sun!!.EpochSet * 1000),
                    minuteResult
                ),
                getAlertList(alertResultList)
            )
            WeatherService.WeatherResultWrapper(weather)
        } catch (_: Exception) {
            WeatherService.WeatherResultWrapper(null)
        }
    }

    private fun getDailyList(context: Context, dailyResult: AccuDailyResult): List<Daily> {
        val dailyList = ArrayList<Daily>(dailyResult.DailyForecasts!!.size)
        for (forecasts in dailyResult.DailyForecasts!!) {
            dailyList.add(
                Daily(
                    forecasts.Date!!,
                    forecasts.EpochDate * 1000,
                    HalfDay(
                        convertUnit(context, forecasts.Day!!.LongPhrase)!!,
                        forecasts.Day!!.ShortPhrase!!,
                        getWeatherCode(forecasts.Day!!.Icon),
                        Temperature(
                            toInt(forecasts.Temperature!!.Maximum!!.Value),
                            toInt(forecasts.RealFeelTemperature!!.Maximum!!.Value),
                            toInt(forecasts.RealFeelTemperatureShade!!.Maximum!!.Value),
                            null,
                            null,
                            null,
                            toInt(forecasts.DegreeDaySummary!!.Heating!!.Value)
                        ),
                        Precipitation(
                            forecasts.Day!!.TotalLiquid!!.Value.toFloat(),
                            null,
                            forecasts.Day!!.Rain!!.Value.toFloat(),
                            (forecasts.Day!!.Snow!!.Value * 10).toFloat(),
                            forecasts.Day!!.Ice!!.Value.toFloat()
                        ),
                        PrecipitationProbability(
                            forecasts.Day!!.PrecipitationProbability.toFloat(),
                            forecasts.Day!!.ThunderstormProbability.toFloat(),
                            forecasts.Day!!.RainProbability.toFloat(),
                            forecasts.Day!!.SnowProbability.toFloat(),
                            forecasts.Day!!.IceProbability.toFloat()
                        ),
                        PrecipitationDuration(
                            forecasts.Day!!.HoursOfPrecipitation.toFloat(),
                            null,
                            forecasts.Day!!.HoursOfRain.toFloat(),
                            forecasts.Day!!.HoursOfSnow.toFloat(),
                            forecasts.Day!!.HoursOfIce.toFloat()
                        ),
                        Wind(
                            forecasts.Day!!.Wind!!.Direction!!.Localized!!,
                            WindDegree(forecasts.Day!!.Wind!!.Direction!!.Degrees.toFloat(), false),
                            forecasts.Day!!.WindGust!!.Speed!!.Value.toFloat(),
                            CommonConverter.getWindLevel(context, forecasts.Day!!.WindGust!!.Speed!!.Value)
                        ),
                        forecasts.Day!!.CloudCover
                    ),
                    HalfDay(
                        convertUnit(context, forecasts.Night!!.LongPhrase)!!,
                        forecasts.Night!!.ShortPhrase!!,
                        getWeatherCode(forecasts.Night!!.Icon),
                        Temperature(
                            toInt(forecasts.Temperature!!.Minimum!!.Value),
                            toInt(forecasts.RealFeelTemperature!!.Minimum!!.Value),
                            toInt(forecasts.RealFeelTemperatureShade!!.Minimum!!.Value),
                            null,
                            null,
                            null,
                            toInt(forecasts.DegreeDaySummary!!.Cooling!!.Value)
                        ),
                        Precipitation(
                            forecasts.Night!!.TotalLiquid!!.Value.toFloat(),
                            null,
                            forecasts.Night!!.Rain!!.Value.toFloat(),
                            (forecasts.Day!!.Snow!!.Value * 10).toFloat(),
                            forecasts.Night!!.Ice!!.Value.toFloat()
                        ),
                        PrecipitationProbability(
                            forecasts.Night!!.PrecipitationProbability.toFloat(),
                            forecasts.Night!!.ThunderstormProbability.toFloat(),
                            forecasts.Night!!.RainProbability.toFloat(),
                            forecasts.Night!!.SnowProbability.toFloat(),
                            forecasts.Night!!.IceProbability.toFloat()
                        ),
                        PrecipitationDuration(
                            forecasts.Night!!.HoursOfPrecipitation.toFloat(),
                            null,
                            forecasts.Night!!.HoursOfRain.toFloat(),
                            forecasts.Night!!.HoursOfSnow.toFloat(),
                            forecasts.Night!!.HoursOfIce.toFloat()
                        ),
                        Wind(
                            forecasts.Night!!.Wind!!.Direction!!.Localized!!,
                            WindDegree(forecasts.Night!!.Wind!!.Direction!!.Degrees.toFloat(), false),
                            forecasts.Night!!.WindGust!!.Speed!!.Value.toFloat(),
                            CommonConverter.getWindLevel(context, forecasts.Night!!.WindGust!!.Speed!!.Value)
                        ),
                        forecasts.Night!!.CloudCover
                    ),
                    Astro(
                        Date(forecasts.Sun!!.EpochRise * 1000),
                        Date(forecasts.Sun!!.EpochSet * 1000)
                    ),
                    Astro(
                        Date(forecasts.Moon!!.EpochRise * 1000),
                        Date(forecasts.Moon!!.EpochSet * 1000)
                    ),
                    MoonPhase(
                        CommonConverter.getMoonPhaseAngle(forecasts.Moon!!.Phase),
                        forecasts.Moon!!.Phase
                    ),
                    getDailyAirQuality(context, forecasts.AirAndPollen!!),
                    getDailyPollen(forecasts.AirAndPollen!!),
                    getDailyUV(forecasts.AirAndPollen!!),
                    forecasts.HoursOfSun.toFloat()
                )
            )
        }
        return dailyList
    }

    private fun getDailyAirQuality(
        context: Context,
        list: List<AccuDailyResult.DailyForecastsBean.AirAndPollenBean>
    ): AirQuality {
        val aqi = getAirAndPollen(list, "AirQuality")
        var index: Int? = aqi?.Value
        if (index != null && index == 0) {
            index = null
        }
        return AirQuality(
            CommonConverter.getAqiQuality(context, index),
            index,
            null, null, null, null, null, null
        )
    }

    private fun getDailyPollen(
        list: List<AccuDailyResult.DailyForecastsBean.AirAndPollenBean>
    ): Pollen {
        val grass = getAirAndPollen(list, "Grass")
        val mold = getAirAndPollen(list, "Mold")
        val ragweed = getAirAndPollen(list, "Ragweed")
        val tree = getAirAndPollen(list, "Tree")
        return Pollen(
            grass?.Value, grass?.CategoryValue, grass?.Category,
            mold?.Value, mold?.CategoryValue, mold?.Category,
            ragweed?.Value, ragweed?.CategoryValue, ragweed?.Category,
            tree?.Value, tree?.CategoryValue, tree?.Category
        )
    }

    private fun getDailyUV(
        list: List<AccuDailyResult.DailyForecastsBean.AirAndPollenBean>
    ): UV {
        val uv = getAirAndPollen(list, "UVIndex")
        return UV(uv?.Value, uv?.Category, null)
    }

    private fun getAirAndPollen(
        list: List<AccuDailyResult.DailyForecastsBean.AirAndPollenBean>,
        name: String
    ): AccuDailyResult.DailyForecastsBean.AirAndPollenBean? {
        for (item in list) {
            if (item.Name == name) {
                return item
            }
        }
        return null
    }

    private fun getHourlyList(context: Context, resultList: List<AccuHourlyResult>): List<Hourly> {
        val hourlyList = ArrayList<Hourly>(resultList.size)
        for (result in resultList) {
            hourlyList.add(
                Hourly(
                    result.DateTime!!,
                    result.EpochDateTime * 1000,
                    result.IsDaylight,
                    result.IconPhrase!!,
                    getWeatherCode(result.WeatherIcon),
                    Temperature(
                        toInt(result.Temperature!!.Value),
                        toInt(result.RealFeelTemperature!!.Value),
                        toInt(result.RealFeelTemperatureShade!!.Value),
                        null,
                        null,
                        toInt(result.WetBulbTemperature!!.Value),
                        null
                    ),
                    Precipitation(
                        result.TotalLiquid!!.Value.toFloat(),
                        null,
                        result.Rain!!.Value.toFloat(),
                        (result.Snow!!.Value * 10).toFloat(),
                        result.Ice!!.Value.toFloat()
                    ),
                    PrecipitationProbability(
                        result.PrecipitationProbability.toFloat(),
                        result.ThunderstormProbability.toFloat(),
                        result.RainProbability.toFloat(),
                        result.SnowProbability.toFloat(),
                        result.IceProbability.toFloat()
                    ),
                    Wind(
                        result.Wind!!.Direction!!.Localized!!,
                        WindDegree(result.Wind!!.Direction!!.Degrees.toFloat(), false),
                        result.WindGust!!.Speed!!.Value.toFloat(),
                        CommonConverter.getWindLevel(context, result.WindGust!!.Speed!!.Value)
                    ),
                    UV(result.UVIndex, null, result.UVIndexText)
                )
            )
        }
        return hourlyList
    }

    private fun getMinutelyList(
        sunrise: Date,
        sunset: Date,
        minuteResult: AccuMinuteResult?
    ): List<Minutely> {
        if (minuteResult == null) {
            return ArrayList()
        }
        val minutelyList = ArrayList<Minutely>(minuteResult.Intervals!!.size)
        for (interval in minuteResult.Intervals!!) {
            minutelyList.add(
                Minutely(
                    interval.StartDateTime!!,
                    interval.StartEpochDateTime,
                    CommonConverter.isDaylight(sunrise, sunset, interval.StartDateTime!!),
                    interval.ShortPhrase!!,
                    getWeatherCode(interval.IconCode),
                    interval.Minute,
                    toInt(interval.Dbz),
                    interval.CloudCover
                )
            )
        }
        return minutelyList
    }

    private fun getAlertList(resultList: List<AccuAlertResult>): List<Alert> {
        val alertList = ArrayList<Alert>(resultList.size)
        for (result in resultList) {
            alertList.add(
                Alert(
                    result.AlertID.toLong(),
                    result.Area!![0].StartTime!!,
                    result.Area!![0].EpochStartTime * 1000,
                    result.Description!!.Localized!!,
                    result.Area!![0].Text!!,
                    result.TypeID!!,
                    result.Priority,
                    Color.rgb(result.Color!!.Red, result.Color!!.Green, result.Color!!.Blue)
                )
            )
        }
        Alert.deduplication(alertList)
        Alert.descByTime(alertList)
        return alertList
    }

    private fun toInt(value: Double): Int {
        return (value + 0.5).toInt()
    }

    private fun getWeatherCode(icon: Int): WeatherCode {
        return if (icon == 1 || icon == 2 || icon == 30 || icon == 33 || icon == 34) {
            WeatherCode.CLEAR
        } else if (icon == 3 || icon == 4 || icon == 6 || icon == 35 || icon == 36 || icon == 38) {
            WeatherCode.PARTLY_CLOUDY
        } else if (icon == 5 || icon == 37) {
            WeatherCode.HAZE
        } else if (icon == 7 || icon == 8) {
            WeatherCode.CLOUDY
        } else if (icon == 11) {
            WeatherCode.FOG
        } else if (icon == 12 || icon == 13 || icon == 14 || icon == 18 || icon == 39 || icon == 40) {
            WeatherCode.RAIN
        } else if (icon == 15 || icon == 16 || icon == 17 || icon == 41 || icon == 42) {
            WeatherCode.THUNDERSTORM
        } else if (icon == 19 || icon == 20 || icon == 21 || icon == 22 || icon == 23 || icon == 24
            || icon == 31 || icon == 43 || icon == 44
        ) {
            WeatherCode.SNOW
        } else if (icon == 25) {
            WeatherCode.HAIL
        } else if (icon == 26 || icon == 29) {
            WeatherCode.SLEET
        } else if (icon == 32) {
            WeatherCode.WIND
        } else {
            WeatherCode.CLOUDY
        }
    }

    private fun convertUnit(context: Context, str: String?): String? {
        if (TextUtils.isEmpty(str)) {
            return str
        }
        val precipitationUnit = WeatherProviderSettings.getInstance(context).precipitationUnit
        var result = convertUnit(context, str!!, PrecipitationUnit.CM, precipitationUnit)
        result = convertUnit(context, result, PrecipitationUnit.MM, precipitationUnit)
        return result
    }

    private fun convertUnit(
        context: Context,
        str: String,
        targetUnit: PrecipitationUnit,
        resultUnit: PrecipitationUnit
    ): String {
        var text = str
        return try {
            val numberPattern = "\\d+-\\d+(\\s+)?"
            val matcher = Pattern.compile(numberPattern + targetUnit).matcher(text)
            val targetList = ArrayList<String>()
            val resultList = ArrayList<String>()
            while (matcher.find()) {
                val target = text.substring(matcher.start(), matcher.end())
                targetList.add(target)
                val targetSplitResults = target.replace(" ", "").split(
                    targetUnit.getName(context).toRegex()
                ).toTypedArray()
                val numberTexts = targetSplitResults[0].split("-").toTypedArray()
                for (i in numberTexts.indices) {
                    var number = numberTexts[i].toFloat()
                    number = targetUnit.getValueInDefaultUnit(number)
                    numberTexts[i] = resultUnit.getValueWithoutUnit(number).toString()
                }
                resultList.add(arrayToString(numberTexts) + " " + resultUnit.getName(context))
            }
            for (i in targetList.indices) {
                text = text.replace(targetList[i], resultList[i])
            }
            text
        } catch (_: Exception) {
            text
        }
    }

    private fun arrayToString(array: Array<String>): String {
        val builder = StringBuilder()
        for (i in array.indices) {
            builder.append(array[i])
            if (i < array.size - 1) {
                builder.append("-")
            }
        }
        return builder.toString()
    }
}
