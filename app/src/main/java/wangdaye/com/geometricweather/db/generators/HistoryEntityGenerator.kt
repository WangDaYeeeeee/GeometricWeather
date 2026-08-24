package wangdaye.com.geometricweather.db.generators

import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.common.basic.models.weather.History
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.db.converters.WeatherSourceConverter
import wangdaye.com.geometricweather.db.entities.HistoryEntity

object HistoryEntityGenerator {

    @JvmStatic
    fun generate(cityId: String, source: WeatherSource, history: History): HistoryEntity {
        val entity = HistoryEntity()
        entity.cityId = cityId
        entity.weatherSource = WeatherSourceConverter().convertToDatabaseValue(source)
        entity.date = history.date
        entity.time = history.time
        entity.daytimeTemperature = history.daytimeTemperature
        entity.nighttimeTemperature = history.nighttimeTemperature
        return entity
    }

    @JvmStatic
    fun generate(cityId: String, source: WeatherSource, weather: Weather): HistoryEntity {
        val entity = HistoryEntity()
        entity.cityId = cityId
        entity.weatherSource = WeatherSourceConverter().convertToDatabaseValue(source)
        entity.date = weather.base.publishDate
        entity.time = weather.base.publishTime
        entity.daytimeTemperature = weather.dailyForecast[0].day().temperature.temperature
        entity.nighttimeTemperature = weather.dailyForecast[0].night().temperature.temperature
        return entity
    }

    @JvmStatic
    fun generate(entity: HistoryEntity?): History? {
        if (entity == null) {
            return null
        }
        return History(
            entity.date!!,
            entity.time,
            entity.daytimeTemperature,
            entity.nighttimeTemperature
        )
    }
}
