package wangdaye.com.geometricweather.db.generators

import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.weather.AirQuality
import wangdaye.com.geometricweather.common.basic.models.weather.Base
import wangdaye.com.geometricweather.common.basic.models.weather.Current
import wangdaye.com.geometricweather.common.basic.models.weather.Precipitation
import wangdaye.com.geometricweather.common.basic.models.weather.PrecipitationProbability
import wangdaye.com.geometricweather.common.basic.models.weather.Temperature
import wangdaye.com.geometricweather.common.basic.models.weather.UV
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode
import wangdaye.com.geometricweather.common.basic.models.weather.Wind
import wangdaye.com.geometricweather.common.basic.models.weather.WindDegree
import wangdaye.com.geometricweather.db.converters.WeatherSourceConverter
import wangdaye.com.geometricweather.db.entities.AlertEntity
import wangdaye.com.geometricweather.db.entities.DailyEntity
import wangdaye.com.geometricweather.db.entities.HistoryEntity
import wangdaye.com.geometricweather.db.entities.HourlyEntity
import wangdaye.com.geometricweather.db.entities.MinutelyEntity
import wangdaye.com.geometricweather.db.entities.WeatherEntity
import java.util.Date

object WeatherEntityGenerator {

    @JvmStatic
    fun generate(location: Location, weather: Weather): WeatherEntity {
        val entity = WeatherEntity()
        entity.cityId = weather.base.cityId
        entity.weatherSource = WeatherSourceConverter().convertToDatabaseValue(location.weatherSource)
        entity.timeStamp = weather.base.timeStamp
        entity.publishDate = weather.base.publishDate
        entity.publishTime = weather.base.publishTime
        entity.updateDate = weather.base.updateDate
        entity.updateTime = weather.base.updateTime
        entity.weatherText = weather.current.weatherText
        entity.weatherCode = weather.current.weatherCode
        entity.temperature = weather.current.temperature.temperature
        entity.realFeelTemperature = weather.current.temperature.realFeelTemperature
        entity.realFeelShaderTemperature = weather.current.temperature.realFeelShaderTemperature
        entity.apparentTemperature = weather.current.temperature.apparentTemperature
        entity.windChillTemperature = weather.current.temperature.windChillTemperature
        entity.wetBulbTemperature = weather.current.temperature.wetBulbTemperature
        entity.degreeDayTemperature = weather.current.temperature.degreeDayTemperature
        entity.totalPrecipitation = weather.current.precipitation.total
        entity.thunderstormPrecipitation = weather.current.precipitation.thunderstorm
        entity.rainPrecipitation = weather.current.precipitation.rain
        entity.snowPrecipitation = weather.current.precipitation.snow
        entity.icePrecipitation = weather.current.precipitation.ice
        entity.totalPrecipitationProbability = weather.current.precipitationProbability.total
        entity.thunderstormPrecipitationProbability = weather.current.precipitationProbability.thunderstorm
        entity.rainPrecipitationProbability = weather.current.precipitationProbability.rain
        entity.snowPrecipitationProbability = weather.current.precipitationProbability.snow
        entity.icePrecipitationProbability = weather.current.precipitationProbability.ice
        entity.windDirection = weather.current.wind.direction
        entity.windDegree = weather.current.wind.degree
        entity.windSpeed = weather.current.wind.speed
        entity.windLevel = weather.current.wind.level
        entity.uvIndex = weather.current.uv.index
        entity.uvLevel = weather.current.uv.level
        entity.uvDescription = weather.current.uv.description
        entity.aqiText = weather.current.airQuality.aqiText
        entity.aqiIndex = weather.current.airQuality.aqiIndex
        entity.pm25 = weather.current.airQuality.pm25
        entity.pm10 = weather.current.airQuality.pm10
        entity.so2 = weather.current.airQuality.so2
        entity.no2 = weather.current.airQuality.no2
        entity.o3 = weather.current.airQuality.o3
        entity.co = weather.current.airQuality.co
        entity.relativeHumidity = weather.current.relativeHumidity
        entity.pressure = weather.current.pressure
        entity.visibility = weather.current.visibility
        entity.dewPoint = weather.current.dewPoint
        entity.cloudCover = weather.current.cloudCover
        entity.ceiling = weather.current.ceiling
        entity.dailyForecast = weather.current.dailyForecast
        entity.hourlyForecast = weather.current.hourlyForecast
        return entity
    }

    @JvmStatic
    fun generate(
        weatherEntity: WeatherEntity?,
        historyEntity: HistoryEntity?,
        dailyEntityList: List<DailyEntity>?,
        hourlyEntityList: List<HourlyEntity>?,
        minutelyEntityList: List<MinutelyEntity>?,
        alertEntityList: List<AlertEntity>?
    ): Weather? {
        if (weatherEntity == null) {
            return null
        }
        return Weather(
            Base(
                weatherEntity.cityId ?: "",
                weatherEntity.timeStamp,
                weatherEntity.publishDate ?: Date(0),
                weatherEntity.publishTime,
                weatherEntity.updateDate ?: Date(0),
                weatherEntity.updateTime
            ),
            Current(
                weatherEntity.weatherText ?: "",
                weatherEntity.weatherCode ?: WeatherCode.CLEAR,
                Temperature(
                    weatherEntity.temperature,
                    weatherEntity.realFeelTemperature,
                    weatherEntity.realFeelShaderTemperature,
                    weatherEntity.apparentTemperature,
                    weatherEntity.windChillTemperature,
                    weatherEntity.wetBulbTemperature,
                    weatherEntity.degreeDayTemperature
                ),
                Precipitation(
                    weatherEntity.totalPrecipitation,
                    weatherEntity.thunderstormPrecipitation,
                    weatherEntity.rainPrecipitation,
                    weatherEntity.snowPrecipitation,
                    weatherEntity.icePrecipitation
                ),
                PrecipitationProbability(
                    weatherEntity.totalPrecipitationProbability,
                    weatherEntity.thunderstormPrecipitationProbability,
                    weatherEntity.rainPrecipitationProbability,
                    weatherEntity.snowPrecipitationProbability,
                    weatherEntity.icePrecipitationProbability
                ),
                Wind(
                    weatherEntity.windDirection ?: "",
                    weatherEntity.windDegree ?: WindDegree(-1f, true),
                    weatherEntity.windSpeed,
                    weatherEntity.windLevel ?: ""
                ),
                UV(weatherEntity.uvIndex, weatherEntity.uvLevel, weatherEntity.uvDescription),
                AirQuality(
                    weatherEntity.aqiText,
                    weatherEntity.aqiIndex,
                    weatherEntity.pm25,
                    weatherEntity.pm10,
                    weatherEntity.so2,
                    weatherEntity.no2,
                    weatherEntity.o3,
                    weatherEntity.co
                ),
                weatherEntity.relativeHumidity,
                weatherEntity.pressure,
                weatherEntity.visibility,
                weatherEntity.dewPoint,
                weatherEntity.cloudCover,
                weatherEntity.ceiling,
                weatherEntity.dailyForecast,
                weatherEntity.hourlyForecast
            ),
            HistoryEntityGenerator.generate(historyEntity),
            DailyEntityGenerator.generate(dailyEntityList ?: emptyList()),
            HourlyEntityGenerator.generateModuleList(hourlyEntityList ?: emptyList()),
            MinutelyEntityGenerator.generate(minutelyEntityList ?: emptyList()),
            AlertEntityGenerator.generate(alertEntityList ?: emptyList())
        )
    }
}
