package wangdaye.com.geometricweather.db.generators

import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.db.entities.LocationEntity
import java.util.TimeZone

object LocationEntityGenerator {

    @JvmStatic
    fun generate(location: Location): LocationEntity {
        val entity = LocationEntity()
        entity.formattedId = location.formattedId
        entity.cityId = location.cityId
        entity.latitude = location.latitude
        entity.longitude = location.longitude
        entity.timeZone = location.timeZone
        entity.country = location.country
        entity.province = location.province
        entity.city = location.city
        entity.district = location.district
        entity.weatherSource = location.weatherSource
        entity.currentPosition = location.isCurrentPosition
        entity.residentPosition = location.isResidentPosition
        entity.china = location.isChina
        return entity
    }

    @JvmStatic
    fun generateEntityList(locationList: List<Location>): List<LocationEntity> {
        val entityList = ArrayList<LocationEntity>(locationList.size)
        for (i in locationList.indices) {
            entityList.add(generate(locationList[i]))
        }
        return entityList
    }

    @JvmStatic
    fun generate(entity: LocationEntity): Location {
        return Location(
            GeneratorUtils.nonNull(entity.cityId),
            entity.latitude,
            entity.longitude,
            entity.timeZone ?: TimeZone.getDefault(),
            GeneratorUtils.nonNull(entity.country),
            GeneratorUtils.nonNull(entity.province),
            GeneratorUtils.nonNull(entity.city),
            GeneratorUtils.nonNull(entity.district),
            null,
            entity.weatherSource ?: wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource.ACCU,
            entity.currentPosition,
            entity.residentPosition,
            entity.china
        )
    }

    @JvmStatic
    fun generateModuleList(entityList: List<LocationEntity>): List<Location> {
        val locationList = ArrayList<Location>(entityList.size)
        for (entity in entityList) {
            locationList.add(generate(entity))
        }
        return locationList
    }
}
