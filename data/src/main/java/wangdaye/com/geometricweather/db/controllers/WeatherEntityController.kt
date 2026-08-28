package wangdaye.com.geometricweather.db.controllers

import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.db.GeometricWeatherDatabase
import wangdaye.com.geometricweather.db.converters.WeatherSourceConverter
import wangdaye.com.geometricweather.db.entities.WeatherEntity

object WeatherEntityController {

    @JvmStatic
    fun insertWeatherEntity(database: GeometricWeatherDatabase, entity: WeatherEntity) {
        database.weatherDao().insertWeatherEntity(entity)
    }

    @JvmStatic
    fun deleteWeather(database: GeometricWeatherDatabase, entityList: List<WeatherEntity>) {
        if (entityList.isNotEmpty()) {
            database.weatherDao().deleteWeather(entityList)
        }
    }

    @JvmStatic
    fun selectWeatherEntity(
        database: GeometricWeatherDatabase,
        cityId: String,
        source: WeatherSource
    ): WeatherEntity? {
        return selectWeatherEntityList(database, cityId, source).firstOrNull()
    }

    @JvmStatic
    fun selectWeatherEntityList(
        database: GeometricWeatherDatabase,
        cityId: String,
        source: WeatherSource
    ): List<WeatherEntity> {
        return getNonNullList(
            database.weatherDao().selectWeatherEntityList(
                cityId,
                WeatherSourceConverter().convertToDatabaseValue(source)
            )
        )
    }
}
