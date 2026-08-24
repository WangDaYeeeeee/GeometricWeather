package wangdaye.com.geometricweather.db.generators

import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.common.basic.models.weather.Alert
import wangdaye.com.geometricweather.db.converters.WeatherSourceConverter
import wangdaye.com.geometricweather.db.entities.AlertEntity

object AlertEntityGenerator {

    @JvmStatic
    fun generate(cityId: String, source: WeatherSource, alert: Alert): AlertEntity {
        val entity = AlertEntity()
        entity.cityId = cityId
        entity.weatherSource = WeatherSourceConverter().convertToDatabaseValue(source)
        entity.alertId = alert.alertId
        entity.date = alert.date
        entity.time = alert.time
        entity.description = alert.description
        entity.content = alert.content
        entity.type = alert.type
        entity.priority = alert.priority
        entity.color = alert.color
        return entity
    }

    @JvmStatic
    fun generate(cityId: String, source: WeatherSource, alertList: List<Alert>): List<AlertEntity> {
        val entityList = ArrayList<AlertEntity>(alertList.size)
        for (alert in alertList) {
            entityList.add(generate(cityId, source, alert))
        }
        return entityList
    }

    @JvmStatic
    fun generate(entity: AlertEntity): Alert {
        return Alert(
            entity.alertId,
            entity.date!!,
            entity.time,
            entity.description ?: "",
            entity.content ?: "",
            entity.type ?: "",
            entity.priority,
            entity.color
        )
    }

    @JvmStatic
    fun generate(entityList: List<AlertEntity>): List<Alert> {
        val dailyList = ArrayList<Alert>(entityList.size)
        for (entity in entityList) {
            dailyList.add(generate(entity))
        }
        return dailyList
    }
}
