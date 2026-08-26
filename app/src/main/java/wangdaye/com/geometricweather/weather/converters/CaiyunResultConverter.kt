package wangdaye.com.geometricweather.weather.converters

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import androidx.annotation.ColorInt
import wangdaye.com.geometricweather.common.basic.models.Location
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
import wangdaye.com.geometricweather.weather.json.caiyun.CaiYunForecastResult
import wangdaye.com.geometricweather.weather.json.caiyun.CaiYunMainlyResult
import wangdaye.com.geometricweather.weather.services.WeatherService
import java.util.Calendar
import java.util.Date

object CaiyunResultConverter {

    @JvmStatic
    fun convert(
        context: Context,
        location: Location,
        mainlyResult: CaiYunMainlyResult,
        forecastResult: CaiYunForecastResult
    ): WeatherService.WeatherResultWrapper {
        return try {
            val weather = Weather(
                Base(
                    location.cityId,
                    System.currentTimeMillis(),
                    mainlyResult.current!!.pubTime!!,
                    mainlyResult.current!!.pubTime!!.time,
                    Date(System.currentTimeMillis()),
                    System.currentTimeMillis()
                ),
                Current(
                    getWeatherText(mainlyResult.current!!.weather),
                    getWeatherCode(mainlyResult.current!!.weather),
                    Temperature(
                        Integer.parseInt(mainlyResult.current!!.temperature!!.value!!),
                        Integer.parseInt(mainlyResult.current!!.feelsLike!!.value!!),
                        null, null, null, null, null
                    ),
                    Precipitation(null, null, null, null, null),
                    PrecipitationProbability(null, null, null, null, null),
                    Wind(
                        getWindDirection(java.lang.Float.parseFloat(mainlyResult.current!!.wind!!.direction!!.value!!)),
                        WindDegree(
                            java.lang.Float.parseFloat(mainlyResult.current!!.wind!!.direction!!.value!!),
                            false
                        ),
                        java.lang.Float.parseFloat(mainlyResult.current!!.wind!!.speed!!.value!!),
                        CommonConverter.getWindLevel(
                            context,
                            java.lang.Float.parseFloat(mainlyResult.current!!.wind!!.speed!!.value!!).toDouble()
                        )
                    ),
                    UV(
                        Integer.parseInt(mainlyResult.current!!.uvIndex!!),
                        getUVDescription(mainlyResult.current!!.uvIndex),
                        null
                    ),
                    getAirQuality(context, mainlyResult),
                    if (!TextUtils.isEmpty(mainlyResult.current!!.humidity!!.value))
                        java.lang.Float.parseFloat(mainlyResult.current!!.humidity!!.value!!)
                    else null,
                    if (!TextUtils.isEmpty(mainlyResult.current!!.pressure!!.value))
                        java.lang.Float.parseFloat(mainlyResult.current!!.pressure!!.value!!)
                    else null,
                    if (!TextUtils.isEmpty(mainlyResult.current!!.visibility!!.value))
                        java.lang.Float.parseFloat(mainlyResult.current!!.visibility!!.value!!)
                    else null,
                    null, null, null, null,
                    forecastResult.precipitation!!.description
                ),
                getYesterday(mainlyResult),
                getDailyList(context, mainlyResult.current!!.pubTime!!, mainlyResult.forecastDaily!!),
                getHourlyList(
                    context,
                    mainlyResult.current!!.pubTime!!,
                    mainlyResult.forecastDaily!!.sunRiseSet!!.value!![0].from!!,
                    mainlyResult.forecastDaily!!.sunRiseSet!!.value!![0].to!!,
                    mainlyResult.forecastHourly!!
                ),
                getMinutelyList(
                    mainlyResult.forecastDaily!!.sunRiseSet!!.value!![0].from!!,
                    mainlyResult.forecastDaily!!.sunRiseSet!!.value!![0].to!!,
                    getWeatherText(mainlyResult.current!!.weather),
                    getWeatherCode(mainlyResult.current!!.weather),
                    forecastResult
                ),
                getAlertList(mainlyResult)
            )
            WeatherService.WeatherResultWrapper(weather)
        } catch (e: Exception) {
            e.printStackTrace()
            WeatherService.WeatherResultWrapper(null)
        }
    }

    private fun getAirQuality(context: Context, result: CaiYunMainlyResult): AirQuality {
        val quality = try {
            CommonConverter.getAqiQuality(context, Integer.parseInt(result.aqi!!.aqi!!))
        } catch (_: Exception) {
            null
        }
        val index = try {
            java.lang.Double.parseDouble(result.aqi!!.aqi!!).toInt()
        } catch (_: Exception) {
            null
        }
        val pm25 = try {
            java.lang.Float.parseFloat(result.aqi!!.pm25!!)
        } catch (_: Exception) {
            null
        }
        val pm10 = try {
            java.lang.Float.parseFloat(result.aqi!!.pm10!!)
        } catch (_: Exception) {
            null
        }
        val so2 = try {
            java.lang.Float.parseFloat(result.aqi!!.so2!!)
        } catch (_: Exception) {
            null
        }
        val no2 = try {
            java.lang.Float.parseFloat(result.aqi!!.no2!!)
        } catch (_: Exception) {
            null
        }
        val o3 = try {
            java.lang.Float.parseFloat(result.aqi!!.o3!!)
        } catch (_: Exception) {
            null
        }
        val co = try {
            java.lang.Float.parseFloat(result.aqi!!.co!!)
        } catch (_: Exception) {
            null
        }
        return AirQuality(quality, index, pm25, pm10, so2, no2, o3, co)
    }

    private fun getYesterday(result: CaiYunMainlyResult): History? {
        return try {
            History(
                Date(result.updateTime - 24 * 60 * 60 * 1000),
                result.updateTime - 24 * 60 * 60 * 1000,
                Integer.parseInt(result.yesterday!!.tempMax!!),
                Integer.parseInt(result.yesterday!!.tempMin!!)
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun getDailyList(
        context: Context,
        publishDate: Date,
        forecast: CaiYunMainlyResult.ForecastDailyBean
    ): List<Daily> {
        val dailyList = ArrayList<Daily>(forecast.weather!!.value!!.size)
        for (i in forecast.weather!!.value!!.indices) {
            val calendar = Calendar.getInstance()
            calendar.time = publishDate
            calendar.add(Calendar.DATE, i)
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
            dailyList.add(
                Daily(
                    calendar.time,
                    calendar.timeInMillis,
                    HalfDay(
                        getWeatherText(forecast.weather!!.value!![i].from),
                        getWeatherText(forecast.weather!!.value!![i].from),
                        getWeatherCode(forecast.weather!!.value!![i].from),
                        Temperature(
                            Integer.parseInt(forecast.temperature!!.value!![i].from!!),
                            null, null, null, null, null, null
                        ),
                        Precipitation(null, null, null, null, null),
                        PrecipitationProbability(
                            getPrecipitationProbability(forecast, i),
                            null, null, null, null
                        ),
                        PrecipitationDuration(null, null, null, null, null),
                        Wind(
                            getWindDirection(java.lang.Float.parseFloat(forecast.wind!!.direction!!.value!![i].from!!)),
                            WindDegree(
                                java.lang.Float.parseFloat(forecast.wind!!.direction!!.value!![i].from!!),
                                false
                            ),
                            java.lang.Float.parseFloat(forecast.wind!!.speed!!.value!![i].from!!),
                            CommonConverter.getWindLevel(
                                context,
                                java.lang.Float.parseFloat(forecast.wind!!.speed!!.value!![i].from!!).toDouble()
                            )
                        ),
                        null
                    ),
                    HalfDay(
                        getWeatherText(forecast.weather!!.value!![i].to),
                        getWeatherText(forecast.weather!!.value!![i].to),
                        getWeatherCode(forecast.weather!!.value!![i].to),
                        Temperature(
                            Integer.parseInt(forecast.temperature!!.value!![i].to!!),
                            null, null, null, null, null, null
                        ),
                        Precipitation(null, null, null, null, null),
                        PrecipitationProbability(
                            getPrecipitationProbability(forecast, i),
                            null, null, null, null
                        ),
                        PrecipitationDuration(null, null, null, null, null),
                        Wind(
                            getWindDirection(java.lang.Float.parseFloat(forecast.wind!!.direction!!.value!![i].to!!)),
                            WindDegree(
                                java.lang.Float.parseFloat(forecast.wind!!.direction!!.value!![i].to!!),
                                false
                            ),
                            java.lang.Float.parseFloat(forecast.wind!!.speed!!.value!![i].to!!),
                            CommonConverter.getWindLevel(
                                context,
                                java.lang.Float.parseFloat(forecast.wind!!.speed!!.value!![i].to!!).toDouble()
                            )
                        ),
                        null
                    ),
                    Astro(
                        forecast.sunRiseSet!!.value!![i].from,
                        forecast.sunRiseSet!!.value!![i].to
                    ),
                    Astro(null, null),
                    MoonPhase(null, null),
                    AirQuality(
                        if (forecast.aqi?.value != null && forecast.aqi!!.value!!.size > i)
                            CommonConverter.getAqiQuality(context, forecast.aqi!!.value!![i])
                        else null,
                        if (forecast.aqi?.value != null && forecast.aqi!!.value!!.size > i)
                            forecast.aqi!!.value!![i]
                        else null,
                        null, null, null, null, null, null
                    ),
                    Pollen(null, null, null, null, null, null, null, null, null, null, null, null),
                    UV(null, null, null),
                    (
                        (forecast.sunRiseSet!!.value!![i].to!!.time
                            - forecast.sunRiseSet!!.value!![i].from!!.time)
                            / 1000
                            / 60
                            / 60.0
                        ).toFloat()
                )
            )
        }
        return dailyList
    }

    private fun getPrecipitationProbability(
        forecast: CaiYunMainlyResult.ForecastDailyBean,
        index: Int
    ): Float? {
        return try {
            if (index < forecast.precipitationProbability!!.value!!.size) {
                java.lang.Float.parseFloat(forecast.precipitationProbability!!.value!![index])
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun getHourlyList(
        context: Context,
        publishDate: Date,
        sunrise: Date,
        sunset: Date,
        forecast: CaiYunMainlyResult.ForecastHourlyBean
    ): List<Hourly> {
        val hourlyList = ArrayList<Hourly>(forecast.weather!!.value!!.size)
        for (i in forecast.weather!!.value!!.indices) {
            val calendar = Calendar.getInstance()
            calendar.time = publishDate
            calendar.add(Calendar.HOUR_OF_DAY, i)
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
            val date = calendar.time
            hourlyList.add(
                Hourly(
                    date,
                    date.time,
                    CommonConverter.isDaylight(sunrise, sunset, date),
                    getWeatherText(forecast.weather!!.value!![i].toString()),
                    getWeatherCode(forecast.weather!!.value!![i].toString()),
                    Temperature(
                        forecast.temperature!!.value!![i],
                        null, null, null, null, null, null
                    ),
                    Precipitation(null, null, null, null, null),
                    PrecipitationProbability(null, null, null, null, null),
                    Wind(
                        getWindDirection(java.lang.Float.parseFloat(forecast.wind!!.value!![i].direction!!)),
                        WindDegree(
                            java.lang.Float.parseFloat(forecast.wind!!.value!![i].direction!!),
                            false
                        ),
                        java.lang.Float.parseFloat(forecast.wind!!.value!![i].speed!!),
                        CommonConverter.getWindLevel(
                            context,
                            java.lang.Float.parseFloat(forecast.wind!!.value!![i].speed!!).toDouble()
                        )
                    ),
                    UV(null, null, null)
                )
            )
        }
        return hourlyList
    }

    private fun getMinutelyList(
        sunrise: Date,
        sunset: Date,
        currentWeatherText: String,
        currentWeatherCode: WeatherCode,
        result: CaiYunForecastResult
    ): List<Minutely> {
        val current = result.precipitation!!.pubTime
        val minutelyList = ArrayList<Minutely>(result.precipitation!!.value!!.size)
        for (i in result.precipitation!!.value!!.indices) {
            val calendar = Calendar.getInstance()
            calendar.time = current!!
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
            minutelyList.add(
                Minutely(
                    calendar.time,
                    calendar.timeInMillis,
                    CommonConverter.isDaylight(sunrise, sunset, calendar.time),
                    getMinuteWeatherText(
                        result.precipitation!!.value!![i],
                        currentWeatherText,
                        currentWeatherCode
                    ),
                    getMinuteWeatherCode(
                        result.precipitation!!.value!![i],
                        currentWeatherCode
                    ),
                    1,
                    null as Int?,
                    null
                )
            )
        }
        return minutelyList
    }

    private fun getMinuteWeatherText(
        precipitation: Double,
        currentWeatherText: String,
        currentWeatherCode: WeatherCode
    ): String {
        return if (precipitation > 0) {
            if (isPrecipitation(currentWeatherCode)) currentWeatherText else "阴"
        } else {
            if (isPrecipitation(currentWeatherCode)) "阴" else currentWeatherText
        }
    }

    private fun getMinuteWeatherCode(
        precipitation: Double,
        currentWeatherCode: WeatherCode
    ): WeatherCode {
        return if (precipitation > 0) {
            if (isPrecipitation(currentWeatherCode)) currentWeatherCode else WeatherCode.CLOUDY
        } else {
            if (isPrecipitation(currentWeatherCode)) WeatherCode.CLOUDY else currentWeatherCode
        }
    }

    private fun isPrecipitation(code: WeatherCode): Boolean {
        return code == WeatherCode.RAIN
            || code == WeatherCode.SNOW
            || code == WeatherCode.HAIL
            || code == WeatherCode.SLEET
            || code == WeatherCode.THUNDERSTORM
    }

    private fun getAlertList(result: CaiYunMainlyResult): List<Alert> {
        val alertList = ArrayList<Alert>(result.alerts!!.size)
        for (a in result.alerts!!) {
            alertList.add(
                Alert(
                    a.pubTime!!.time,
                    a.pubTime!!,
                    a.pubTime!!.time,
                    a.title!!,
                    a.detail!!,
                    a.type!!,
                    getAlertPriority(a.level),
                    getAlertColor(a.level)
                )
            )
        }
        Alert.deduplication(alertList)
        Alert.descByTime(alertList)
        return alertList
    }

    private fun getWeatherText(icon: String?): String {
        if (TextUtils.isEmpty(icon)) {
            return "未知"
        }
        return when (icon) {
            "0", "00" -> "晴"
            "1", "01" -> "多云"
            "2", "02" -> "阴"
            "3", "03" -> "阵雨"
            "4", "04" -> "雷阵雨"
            "5", "05" -> "雷阵雨伴有冰雹"
            "6", "06" -> "雨夹雪"
            "7", "07" -> "小雨"
            "8", "08" -> "中雨"
            "9", "09" -> "大雨"
            "10" -> "暴雨"
            "11" -> "大暴雨"
            "12" -> "特大暴雨"
            "13" -> "阵雪"
            "14" -> "小雪"
            "15" -> "中雪"
            "16" -> "大雪"
            "17" -> "暴雪"
            "18" -> "雾"
            "19" -> "冻雨"
            "20" -> "沙尘暴"
            "21" -> "小到中雨"
            "22" -> "中到大雨"
            "23" -> "大到暴雨"
            "24" -> "暴雨到大暴雨"
            "25" -> "大暴雨到特大暴雨"
            "26" -> "小到中雪"
            "27" -> "中到大雪"
            "28" -> "大到暴雪"
            "29" -> "浮尘"
            "30" -> "扬沙"
            "31" -> "强沙尘暴"
            "53", "54", "55", "56" -> "霾"
            else -> "未知"
        }
    }

    private fun getWeatherCode(icon: String?): WeatherCode {
        if (TextUtils.isEmpty(icon)) {
            return WeatherCode.CLOUDY
        }
        return when (icon) {
            "0", "00" -> WeatherCode.CLEAR
            "1", "01" -> WeatherCode.PARTLY_CLOUDY
            "3", "7", "8", "9", "03", "07", "08", "09",
            "10", "11", "12", "21", "22", "23", "24", "25" -> WeatherCode.RAIN
            "4", "04" -> WeatherCode.THUNDERSTORM
            "5", "05" -> WeatherCode.HAIL
            "6", "06", "19" -> WeatherCode.SLEET
            "13", "14", "15", "16", "17", "26", "27", "28" -> WeatherCode.SNOW
            "18", "32", "49", "57" -> WeatherCode.FOG
            "20", "29", "30" -> WeatherCode.WIND
            "53", "54", "55", "56" -> WeatherCode.HAZE
            else -> WeatherCode.CLOUDY
        }
    }

    private fun getWindDirection(degree: Float): String {
        return if (degree < 0) {
            "无风向"
        } else if (22.5 < degree && degree <= 67.5) {
            "东北风"
        } else if (67.5 < degree && degree <= 112.5) {
            "东风"
        } else if (112.5 < degree && degree <= 157.5) {
            "东南风"
        } else if (157.5 < degree && degree <= 202.5) {
            "南风"
        } else if (202.5 < degree && degree <= 247.5) {
            "西南风"
        } else if (247.5 < degree && degree <= 292.5) {
            "西风"
        } else if (292.0 < degree && degree <= 337.5) {
            "西北风"
        } else {
            "北风"
        }
    }

    private fun getUVDescription(index: String?): String? {
        return try {
            val num = Integer.parseInt(index!!)
            when {
                num <= 2 -> "最弱"
                num <= 4 -> "弱"
                num <= 6 -> "中等"
                num <= 9 -> "强"
                else -> "很强"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @ColorInt
    private fun getAlertPriority(color: String?): Int {
        if (TextUtils.isEmpty(color)) {
            return 0
        }
        return when (color) {
            "蓝", "蓝色" -> 1
            "黄", "黄色" -> 2
            "橙", "橙色", "橘", "橘色", "橘黄", "橘黄色" -> 3
            "红", "红色" -> 4
            else -> 0
        }
    }

    @ColorInt
    private fun getAlertColor(color: String?): Int {
        if (TextUtils.isEmpty(color)) {
            return Color.TRANSPARENT
        }
        return when (color) {
            "蓝", "蓝色" -> Color.rgb(51, 100, 255)
            "黄", "黄色" -> Color.rgb(250, 237, 36)
            "橙", "橙色", "橘", "橘色", "橘黄", "橘黄色" -> Color.rgb(249, 138, 30)
            "红", "红色" -> Color.rgb(215, 48, 42)
            else -> Color.TRANSPARENT
        }
    }
}
