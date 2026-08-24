package wangdaye.com.geometricweather.db.generators

import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.common.basic.models.weather.Hourly
import wangdaye.com.geometricweather.common.basic.models.weather.Precipitation
import wangdaye.com.geometricweather.common.basic.models.weather.PrecipitationProbability
import wangdaye.com.geometricweather.common.basic.models.weather.Temperature
import wangdaye.com.geometricweather.common.basic.models.weather.UV
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode
import wangdaye.com.geometricweather.common.basic.models.weather.Wind
import wangdaye.com.geometricweather.common.basic.models.weather.WindDegree
import wangdaye.com.geometricweather.db.converters.WeatherSourceConverter
import wangdaye.com.geometricweather.db.entities.HourlyEntity
import java.util.Date

object HourlyEntityGenerator {

    @JvmStatic
    fun generate(cityId: String, source: WeatherSource, hourly: Hourly): HourlyEntity {
        val entity = HourlyEntity()
        entity.cityId = cityId
        entity.weatherSource = WeatherSourceConverter().convertToDatabaseValue(source)
        entity.date = hourly.date
        entity.time = hourly.time
        entity.daylight = hourly.isDaylight
        entity.weatherCode = hourly.weatherCode
        entity.weatherText = hourly.weatherText
        entity.temperature = hourly.temperature.temperature
        entity.realFeelTemperature = hourly.temperature.realFeelTemperature
        entity.realFeelShaderTemperature = hourly.temperature.realFeelShaderTemperature
        entity.apparentTemperature = hourly.temperature.apparentTemperature
        entity.windChillTemperature = hourly.temperature.windChillTemperature
        entity.wetBulbTemperature = hourly.temperature.wetBulbTemperature
        entity.degreeDayTemperature = hourly.temperature.degreeDayTemperature
        entity.totalPrecipitation = hourly.precipitation.total
        entity.thunderstormPrecipitation = hourly.precipitation.thunderstorm
        entity.rainPrecipitation = hourly.precipitation.rain
        entity.snowPrecipitation = hourly.precipitation.snow
        entity.icePrecipitation = hourly.precipitation.ice
        entity.totalPrecipitationProbability = hourly.precipitationProbability.total
        entity.thunderstormPrecipitationProbability = hourly.precipitationProbability.thunderstorm
        entity.rainPrecipitationProbability = hourly.precipitationProbability.rain
        entity.snowPrecipitationProbability = hourly.precipitationProbability.snow
        entity.icePrecipitationProbability = hourly.precipitationProbability.ice
        entity.windDirection = hourly.wind.direction
        entity.windDegree = hourly.wind.degree
        entity.windSpeed = hourly.wind.speed
        entity.windLevel = hourly.wind.level
        entity.uvIndex = hourly.uv.index
        entity.uvLevel = hourly.uv.level
        entity.uvDescription = hourly.uv.description
        return entity
    }

    @JvmStatic
    fun generateEntityList(
        cityId: String,
        source: WeatherSource,
        hourlyList: List<Hourly>
    ): List<HourlyEntity> {
        val entityList = ArrayList<HourlyEntity>(hourlyList.size)
        for (hourly in hourlyList) {
            entityList.add(generate(cityId, source, hourly))
        }
        return entityList
    }

    @JvmStatic
    fun generate(entity: HourlyEntity): Hourly {
        return Hourly(
            entity.date ?: Date(0),
            entity.time,
            entity.daylight,
            entity.weatherText ?: "",
            entity.weatherCode ?: WeatherCode.CLEAR,
            Temperature(
                entity.temperature,
                entity.realFeelTemperature,
                entity.realFeelShaderTemperature,
                entity.apparentTemperature,
                entity.windChillTemperature,
                entity.wetBulbTemperature,
                entity.degreeDayTemperature
            ),
            Precipitation(
                entity.totalPrecipitation,
                entity.thunderstormPrecipitation,
                entity.rainPrecipitation,
                entity.snowPrecipitation,
                entity.icePrecipitation
            ),
            PrecipitationProbability(
                entity.totalPrecipitationProbability,
                entity.thunderstormPrecipitationProbability,
                entity.rainPrecipitationProbability,
                entity.snowPrecipitationProbability,
                entity.icePrecipitationProbability
            ),
            Wind(
                entity.windDirection ?: "",
                entity.windDegree ?: WindDegree(-1f, true),
                entity.windSpeed,
                entity.windLevel ?: ""
            ),
            UV(entity.uvIndex, entity.uvLevel, entity.uvDescription)
        )
    }

    @JvmStatic
    fun generateModuleList(entityList: List<HourlyEntity>): List<Hourly> {
        val dailyList = ArrayList<Hourly>(entityList.size)
        for (entity in entityList) {
            dailyList.add(generate(entity))
        }
        return dailyList
    }
}
