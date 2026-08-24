package wangdaye.com.geometricweather.db.generators

import wangdaye.com.geometricweather.common.basic.models.ChineseCity
import wangdaye.com.geometricweather.db.entities.ChineseCityEntity

object ChineseCityEntityGenerator {

    @JvmStatic
    fun generate(city: ChineseCity): ChineseCityEntity {
        val entity = ChineseCityEntity()
        entity.cityId = city.cityId
        entity.province = city.province
        entity.city = city.city
        entity.district = city.district
        entity.latitude = city.latitude
        entity.longitude = city.longitude
        return entity
    }

    @JvmStatic
    fun generateEntityList(cityList: List<ChineseCity>): List<ChineseCityEntity> {
        val entityList = ArrayList<ChineseCityEntity>(cityList.size)
        for (city in cityList) {
            entityList.add(generate(city))
        }
        return entityList
    }

    @JvmStatic
    fun generate(entity: ChineseCityEntity): ChineseCity {
        return ChineseCity(
            entity.cityId ?: "",
            entity.province ?: "",
            entity.city ?: "",
            entity.district ?: "",
            entity.latitude ?: "",
            entity.longitude ?: ""
        )
    }

    @JvmStatic
    fun generateModuleList(entityList: List<ChineseCityEntity>): List<ChineseCity> {
        val cityList = ArrayList<ChineseCity>(entityList.size)
        for (entity in entityList) {
            cityList.add(generate(entity))
        }
        return cityList
    }
}
