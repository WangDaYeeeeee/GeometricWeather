package wangdaye.com.geometricweather.db.controllers

import wangdaye.com.geometricweather.db.GeometricWeatherDatabase
import wangdaye.com.geometricweather.db.entities.LocationEntity

object LocationEntityController {

    @JvmStatic
    fun insertLocationEntity(database: GeometricWeatherDatabase, entity: LocationEntity) {
        database.locationDao().insertLocationEntity(entity)
    }

    @JvmStatic
    fun insertLocationEntityList(
        database: GeometricWeatherDatabase,
        entityList: List<LocationEntity>
    ) {
        if (entityList.isNotEmpty()) {
            database.locationDao().insertLocationEntityList(entityList)
        }
    }

    @JvmStatic
    fun deleteLocationEntity(database: GeometricWeatherDatabase, entity: LocationEntity) {
        database.locationDao().deleteLocationEntity(entity)
    }

    @JvmStatic
    fun deleteLocationEntityList(database: GeometricWeatherDatabase) {
        database.locationDao().deleteLocationEntityList()
    }

    @JvmStatic
    fun updateLocationEntity(database: GeometricWeatherDatabase, entity: LocationEntity) {
        database.locationDao().updateLocationEntity(entity)
    }

    @JvmStatic
    fun selectLocationEntity(
        database: GeometricWeatherDatabase,
        formattedId: String
    ): LocationEntity? {
        return database.locationDao().selectLocationEntity(formattedId)
    }

    @JvmStatic
    fun selectLocationEntityList(database: GeometricWeatherDatabase): List<LocationEntity> {
        return getNonNullList(database.locationDao().selectLocationEntityList())
    }

    @JvmStatic
    fun countLocationEntity(database: GeometricWeatherDatabase): Int {
        return database.locationDao().countLocationEntity()
    }
}
