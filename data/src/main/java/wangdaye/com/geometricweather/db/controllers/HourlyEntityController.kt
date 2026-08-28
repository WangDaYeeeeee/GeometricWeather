package wangdaye.com.geometricweather.db.controllers

import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.db.GeometricWeatherDatabase
import wangdaye.com.geometricweather.db.converters.WeatherSourceConverter
import wangdaye.com.geometricweather.db.entities.HourlyEntity

object HourlyEntityController {

    @JvmStatic
    fun insertHourlyList(database: GeometricWeatherDatabase, entityList: List<HourlyEntity>) {
        if (entityList.isNotEmpty()) {
            database.hourlyDao().insertHourlyList(entityList)
        }
    }

    @JvmStatic
    fun deleteHourlyEntityList(
        database: GeometricWeatherDatabase,
        cityId: String,
        source: WeatherSource
    ) {
        database.hourlyDao().deleteHourlyEntityList(
            cityId,
            WeatherSourceConverter().convertToDatabaseValue(source)
        )
    }

    @JvmStatic
    fun selectHourlyEntityList(
        database: GeometricWeatherDatabase,
        cityId: String,
        source: WeatherSource
    ): List<HourlyEntity> {
        return getNonNullList(
            database.hourlyDao().selectHourlyEntityList(
                cityId,
                WeatherSourceConverter().convertToDatabaseValue(source)
            )
        )
    }
}
