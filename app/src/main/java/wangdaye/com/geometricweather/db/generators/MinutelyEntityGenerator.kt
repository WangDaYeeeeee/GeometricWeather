package wangdaye.com.geometricweather.db.generators

import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.common.basic.models.weather.Minutely
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode
import wangdaye.com.geometricweather.db.converters.WeatherSourceConverter
import wangdaye.com.geometricweather.db.entities.MinutelyEntity
import java.util.Date

object MinutelyEntityGenerator {

    @JvmStatic
    fun generate(cityId: String, source: WeatherSource, minutely: Minutely): MinutelyEntity {
        val entity = MinutelyEntity()
        entity.cityId = cityId
        entity.weatherSource = WeatherSourceConverter().convertToDatabaseValue(source)
        entity.date = minutely.date
        entity.time = minutely.time
        entity.daylight = minutely.isDaylight
        entity.weatherCode = minutely.weatherCode
        entity.weatherText = minutely.weatherText
        entity.minuteInterval = minutely.minuteInterval
        entity.dbz = minutely.dbz
        entity.cloudCover = minutely.cloudCover
        return entity
    }

    @JvmStatic
    fun generate(
        cityId: String,
        source: WeatherSource,
        minutelyList: List<Minutely>
    ): List<MinutelyEntity> {
        val entityList = ArrayList<MinutelyEntity>(minutelyList.size)
        for (minutely in minutelyList) {
            entityList.add(generate(cityId, source, minutely))
        }
        return entityList
    }

    @JvmStatic
    fun generate(entity: MinutelyEntity): Minutely {
        return Minutely(
            entity.date ?: Date(0),
            entity.time,
            entity.daylight,
            entity.weatherText ?: "",
            entity.weatherCode ?: WeatherCode.CLEAR,
            entity.minuteInterval,
            entity.dbz,
            entity.cloudCover
        )
    }

    @JvmStatic
    fun generate(entityList: List<MinutelyEntity>): List<Minutely> {
        val dailyList = ArrayList<Minutely>(entityList.size)
        for (entity in entityList) {
            dailyList.add(generate(entity))
        }
        return dailyList
    }
}
