package wangdaye.com.geometricweather.db.controllers

import wangdaye.com.geometricweather.db.GeometricWeatherDatabase
import wangdaye.com.geometricweather.db.entities.ChineseCityEntity
import kotlin.math.pow

object ChineseCityEntityController {

    @JvmStatic
    fun insertChineseCityEntityList(
        database: GeometricWeatherDatabase,
        entityList: List<ChineseCityEntity>
    ) {
        if (entityList.isNotEmpty()) {
            database.chineseCityDao().insertChineseCityEntityList(entityList)
        }
    }

    @JvmStatic
    fun deleteChineseCityEntityList(database: GeometricWeatherDatabase) {
        database.chineseCityDao().deleteChineseCityEntityList()
    }

    @JvmStatic
    fun selectChineseCityEntity(
        database: GeometricWeatherDatabase,
        name: String
    ): ChineseCityEntity? {
        if (name.isEmpty()) {
            return null
        }
        return database.chineseCityDao().selectChineseCityEntity(name)
    }

    @JvmStatic
    fun selectChineseCityEntity(
        database: GeometricWeatherDatabase,
        province: String,
        city: String,
        district: String
    ): ChineseCityEntity? {
        val dao = database.chineseCityDao()
        return dao.selectByDistrictAndCity(district, city)
            ?: dao.selectByDistrictAndProvince(district, province)
            ?: dao.selectByCityAndProvince(city, province)
            ?: dao.selectByCity(city)
            ?: dao.selectByDistrictAndProvince(city, province)
            ?: dao.selectByDistrictAndCity(city, province)
            ?: dao.selectByDistrict(city)
            ?: dao.selectByCity(district)
    }

    @JvmStatic
    fun selectChineseCityEntity(
        database: GeometricWeatherDatabase,
        latitude: Float,
        longitude: Float
    ): ChineseCityEntity? {
        val entityList = getNonNullList(
            database.chineseCityDao().selectChineseCityEntityList()
        )
        return entityList.minByOrNull { entity ->
            (latitude - entity.latitude!!.toDouble()).pow(2.0) +
                (longitude - entity.longitude!!.toDouble()).pow(2.0)
        }
    }

    @JvmStatic
    fun selectChineseCityEntityList(
        database: GeometricWeatherDatabase,
        name: String
    ): List<ChineseCityEntity> {
        if (name.isEmpty()) {
            return emptyList()
        }
        return getNonNullList(database.chineseCityDao().selectChineseCityEntityList(name))
    }

    @JvmStatic
    fun countChineseCityEntity(database: GeometricWeatherDatabase): Int {
        return database.chineseCityDao().countChineseCityEntity()
    }
}
