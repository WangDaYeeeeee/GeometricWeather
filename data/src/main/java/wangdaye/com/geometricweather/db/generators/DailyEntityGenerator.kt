package wangdaye.com.geometricweather.db.generators

import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.common.basic.models.weather.AirQuality
import wangdaye.com.geometricweather.common.basic.models.weather.Astro
import wangdaye.com.geometricweather.common.basic.models.weather.Daily
import wangdaye.com.geometricweather.common.basic.models.weather.HalfDay
import wangdaye.com.geometricweather.common.basic.models.weather.MoonPhase
import wangdaye.com.geometricweather.common.basic.models.weather.Pollen
import wangdaye.com.geometricweather.common.basic.models.weather.Precipitation
import wangdaye.com.geometricweather.common.basic.models.weather.PrecipitationDuration
import wangdaye.com.geometricweather.common.basic.models.weather.PrecipitationProbability
import wangdaye.com.geometricweather.common.basic.models.weather.Temperature
import wangdaye.com.geometricweather.common.basic.models.weather.UV
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode
import wangdaye.com.geometricweather.common.basic.models.weather.Wind
import wangdaye.com.geometricweather.common.basic.models.weather.WindDegree
import wangdaye.com.geometricweather.db.converters.WeatherSourceConverter
import wangdaye.com.geometricweather.db.entities.DailyEntity
import java.util.Date

object DailyEntityGenerator {

    @JvmStatic
    fun generate(cityId: String, source: WeatherSource, daily: Daily): DailyEntity {
        val entity = DailyEntity()
        entity.cityId = cityId
        entity.weatherSource = WeatherSourceConverter().convertToDatabaseValue(source)
        entity.date = daily.date
        entity.time = daily.time

        entity.daytimeWeatherText = daily.day().weatherText
        entity.daytimeWeatherPhase = daily.day().weatherPhase
        entity.daytimeWeatherCode = daily.day().weatherCode
        entity.daytimeTemperature = daily.day().temperature.temperature
        entity.daytimeRealFeelTemperature = daily.day().temperature.realFeelTemperature
        entity.daytimeRealFeelShaderTemperature = daily.day().temperature.realFeelShaderTemperature
        entity.daytimeApparentTemperature = daily.day().temperature.apparentTemperature
        entity.daytimeWindChillTemperature = daily.day().temperature.windChillTemperature
        entity.daytimeWetBulbTemperature = daily.day().temperature.wetBulbTemperature
        entity.daytimeDegreeDayTemperature = daily.day().temperature.degreeDayTemperature
        entity.daytimeTotalPrecipitation = daily.day().precipitation.total
        entity.daytimeThunderstormPrecipitation = daily.day().precipitation.thunderstorm
        entity.daytimeRainPrecipitation = daily.day().precipitation.rain
        entity.daytimeSnowPrecipitation = daily.day().precipitation.snow
        entity.daytimeIcePrecipitation = daily.day().precipitation.ice
        entity.daytimeTotalPrecipitationProbability = daily.day().precipitationProbability.total
        entity.daytimeThunderstormPrecipitationProbability = daily.day().precipitationProbability.thunderstorm
        entity.daytimeRainPrecipitationProbability = daily.day().precipitationProbability.rain
        entity.daytimeSnowPrecipitationProbability = daily.day().precipitationProbability.snow
        entity.daytimeIcePrecipitationProbability = daily.day().precipitationProbability.ice
        entity.daytimeTotalPrecipitationDuration = daily.day().precipitationDuration.total
        entity.daytimeThunderstormPrecipitationDuration = daily.day().precipitationDuration.thunderstorm
        entity.daytimeRainPrecipitationDuration = daily.day().precipitationDuration.rain
        entity.daytimeSnowPrecipitationDuration = daily.day().precipitationDuration.snow
        entity.daytimeIcePrecipitationDuration = daily.day().precipitationDuration.ice
        entity.daytimeWindDirection = daily.day().wind.direction
        entity.daytimeWindDegree = daily.day().wind.degree
        entity.daytimeWindSpeed = daily.day().wind.speed
        entity.daytimeWindLevel = daily.day().wind.level
        entity.daytimeCloudCover = daily.day().cloudCover

        entity.nighttimeWeatherText = daily.night().weatherText
        entity.nighttimeWeatherPhase = daily.night().weatherPhase
        entity.nighttimeWeatherCode = daily.night().weatherCode
        entity.nighttimeTemperature = daily.night().temperature.temperature
        entity.nighttimeRealFeelTemperature = daily.night().temperature.realFeelTemperature
        entity.nighttimeRealFeelShaderTemperature = daily.night().temperature.realFeelShaderTemperature
        entity.nighttimeApparentTemperature = daily.night().temperature.apparentTemperature
        entity.nighttimeWindChillTemperature = daily.night().temperature.windChillTemperature
        entity.nighttimeWetBulbTemperature = daily.night().temperature.wetBulbTemperature
        entity.nighttimeDegreeDayTemperature = daily.night().temperature.degreeDayTemperature
        entity.nighttimeTotalPrecipitation = daily.night().precipitation.total
        entity.nighttimeThunderstormPrecipitation = daily.night().precipitation.thunderstorm
        entity.nighttimeRainPrecipitation = daily.night().precipitation.rain
        entity.nighttimeSnowPrecipitation = daily.night().precipitation.snow
        entity.nighttimeIcePrecipitation = daily.night().precipitation.ice
        entity.nighttimeTotalPrecipitationProbability = daily.night().precipitationProbability.total
        entity.nighttimeThunderstormPrecipitationProbability = daily.night().precipitationProbability.thunderstorm
        entity.nighttimeRainPrecipitationProbability = daily.night().precipitationProbability.rain
        entity.nighttimeSnowPrecipitationProbability = daily.night().precipitationProbability.snow
        entity.nighttimeIcePrecipitationProbability = daily.night().precipitationProbability.ice
        entity.nighttimeTotalPrecipitationDuration = daily.night().precipitationDuration.total
        entity.nighttimeThunderstormPrecipitationDuration = daily.night().precipitationDuration.thunderstorm
        entity.nighttimeRainPrecipitationDuration = daily.night().precipitationDuration.rain
        entity.nighttimeSnowPrecipitationDuration = daily.night().precipitationDuration.snow
        entity.nighttimeIcePrecipitationDuration = daily.night().precipitationDuration.ice
        entity.nighttimeWindDirection = daily.night().wind.direction
        entity.nighttimeWindDegree = daily.night().wind.degree
        entity.nighttimeWindSpeed = daily.night().wind.speed
        entity.nighttimeWindLevel = daily.night().wind.level
        entity.nighttimeCloudCover = daily.night().cloudCover

        entity.sunRiseDate = daily.sun().riseDate
        entity.sunSetDate = daily.sun().setDate
        entity.moonRiseDate = daily.moon().riseDate
        entity.moonSetDate = daily.moon().setDate
        entity.moonPhaseAngle = daily.moonPhase.angle
        entity.moonPhaseDescription = daily.moonPhase.description
        entity.aqiText = daily.airQuality.aqiText
        entity.aqiIndex = daily.airQuality.aqiIndex
        entity.pm25 = daily.airQuality.pm25
        entity.pm10 = daily.airQuality.pm10
        entity.so2 = daily.airQuality.so2
        entity.no2 = daily.airQuality.no2
        entity.o3 = daily.airQuality.o3
        entity.co = daily.airQuality.co
        entity.grassIndex = daily.pollen.grassIndex
        entity.grassLevel = daily.pollen.grassLevel
        entity.grassDescription = daily.pollen.grassDescription
        entity.moldIndex = daily.pollen.moldIndex
        entity.moldLevel = daily.pollen.moldLevel
        entity.moldDescription = daily.pollen.moldDescription
        entity.ragweedIndex = daily.pollen.ragweedIndex
        entity.ragweedLevel = daily.pollen.ragweedLevel
        entity.ragweedDescription = daily.pollen.ragweedDescription
        entity.treeIndex = daily.pollen.treeIndex
        entity.treeLevel = daily.pollen.treeLevel
        entity.treeDescription = daily.pollen.treeDescription
        entity.uvIndex = daily.uv.index
        entity.uvLevel = daily.uv.level
        entity.uvDescription = daily.uv.description
        entity.hoursOfSun = daily.hoursOfSun
        return entity
    }

    @JvmStatic
    fun generate(cityId: String, source: WeatherSource, dailyList: List<Daily>): List<DailyEntity> {
        val entityList = ArrayList<DailyEntity>(dailyList.size)
        for (daily in dailyList) {
            entityList.add(generate(cityId, source, daily))
        }
        return entityList
    }

    @JvmStatic
    fun generate(entity: DailyEntity): Daily {
        return Daily(
            entity.date ?: Date(0),
            entity.time,
            halfDay(
                entity.daytimeWeatherText,
                entity.daytimeWeatherPhase,
                entity.daytimeWeatherCode,
                entity.daytimeTemperature,
                entity.daytimeRealFeelTemperature,
                entity.daytimeRealFeelShaderTemperature,
                entity.daytimeApparentTemperature,
                entity.daytimeWindChillTemperature,
                entity.daytimeWetBulbTemperature,
                entity.daytimeDegreeDayTemperature,
                entity.daytimeTotalPrecipitation,
                entity.daytimeThunderstormPrecipitation,
                entity.daytimeRainPrecipitation,
                entity.daytimeSnowPrecipitation,
                entity.daytimeIcePrecipitation,
                entity.daytimeTotalPrecipitationProbability,
                entity.daytimeThunderstormPrecipitationProbability,
                entity.daytimeRainPrecipitationProbability,
                entity.daytimeSnowPrecipitationProbability,
                entity.daytimeIcePrecipitationProbability,
                entity.daytimeTotalPrecipitationDuration,
                entity.daytimeThunderstormPrecipitationDuration,
                entity.daytimeRainPrecipitationDuration,
                entity.daytimeSnowPrecipitationDuration,
                entity.daytimeIcePrecipitationDuration,
                entity.daytimeWindDirection,
                entity.daytimeWindDegree,
                entity.daytimeWindSpeed,
                entity.daytimeWindLevel,
                entity.daytimeCloudCover
            ),
            halfDay(
                entity.nighttimeWeatherText,
                entity.nighttimeWeatherPhase,
                entity.nighttimeWeatherCode,
                entity.nighttimeTemperature,
                entity.nighttimeRealFeelTemperature,
                entity.nighttimeRealFeelShaderTemperature,
                entity.nighttimeApparentTemperature,
                entity.nighttimeWindChillTemperature,
                entity.nighttimeWetBulbTemperature,
                entity.nighttimeDegreeDayTemperature,
                entity.nighttimeTotalPrecipitation,
                entity.nighttimeThunderstormPrecipitation,
                entity.nighttimeRainPrecipitation,
                entity.nighttimeSnowPrecipitation,
                entity.nighttimeIcePrecipitation,
                entity.nighttimeTotalPrecipitationProbability,
                entity.nighttimeThunderstormPrecipitationProbability,
                entity.nighttimeRainPrecipitationProbability,
                entity.nighttimeSnowPrecipitationProbability,
                entity.nighttimeIcePrecipitationProbability,
                entity.nighttimeTotalPrecipitationDuration,
                entity.nighttimeThunderstormPrecipitationDuration,
                entity.nighttimeRainPrecipitationDuration,
                entity.nighttimeSnowPrecipitationDuration,
                entity.nighttimeIcePrecipitationDuration,
                entity.nighttimeWindDirection,
                entity.nighttimeWindDegree,
                entity.nighttimeWindSpeed,
                entity.nighttimeWindLevel,
                entity.nighttimeCloudCover
            ),
            Astro(entity.sunRiseDate, entity.sunSetDate),
            Astro(entity.moonRiseDate, entity.moonSetDate),
            MoonPhase(entity.moonPhaseAngle, entity.moonPhaseDescription),
            AirQuality(
                entity.aqiText,
                entity.aqiIndex,
                entity.pm25,
                entity.pm10,
                entity.so2,
                entity.no2,
                entity.o3,
                entity.co
            ),
            Pollen(
                entity.grassIndex, entity.grassLevel, entity.grassDescription,
                entity.moldIndex, entity.moldLevel, entity.moldDescription,
                entity.ragweedIndex, entity.ragweedLevel, entity.ragweedDescription,
                entity.treeIndex, entity.treeLevel, entity.treeDescription
            ),
            UV(entity.uvIndex, entity.uvLevel, entity.uvDescription),
            entity.hoursOfSun
        )
    }

    @JvmStatic
    fun generate(entityList: List<DailyEntity>): List<Daily> {
        val dailyList = ArrayList<Daily>(entityList.size)
        for (entity in entityList) {
            dailyList.add(generate(entity))
        }
        return dailyList
    }

    private fun halfDay(
        weatherText: String?,
        weatherPhase: String?,
        weatherCode: WeatherCode?,
        temperature: Int,
        realFeel: Int?,
        realFeelShader: Int?,
        apparent: Int?,
        windChill: Int?,
        wetBulb: Int?,
        degreeDay: Int?,
        totalPrecip: Float?,
        thunderPrecip: Float?,
        rainPrecip: Float?,
        snowPrecip: Float?,
        icePrecip: Float?,
        totalProb: Float?,
        thunderProb: Float?,
        rainProb: Float?,
        snowProb: Float?,
        iceProb: Float?,
        totalDur: Float?,
        thunderDur: Float?,
        rainDur: Float?,
        snowDur: Float?,
        iceDur: Float?,
        windDirection: String?,
        windDegree: WindDegree?,
        windSpeed: Float?,
        windLevel: String?,
        cloudCover: Int?
    ): HalfDay {
        return HalfDay(
            weatherText ?: "",
            weatherPhase ?: "",
            weatherCode ?: WeatherCode.CLEAR,
            Temperature(temperature, realFeel, realFeelShader, apparent, windChill, wetBulb, degreeDay),
            Precipitation(totalPrecip, thunderPrecip, rainPrecip, snowPrecip, icePrecip),
            PrecipitationProbability(totalProb, thunderProb, rainProb, snowProb, iceProb),
            PrecipitationDuration(totalDur, thunderDur, rainDur, snowDur, iceDur),
            Wind(
                windDirection ?: "",
                windDegree ?: WindDegree(-1f, true),
                windSpeed,
                windLevel ?: ""
            ),
            cloudCover
        )
    }
}
