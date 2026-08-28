package wangdaye.com.geometricweather.db.controllers

import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.db.GeometricWeatherDatabase
import wangdaye.com.geometricweather.db.converters.WeatherSourceConverter
import wangdaye.com.geometricweather.db.entities.AlertEntity

object AlertEntityController {

    @JvmStatic
    fun insertAlertList(database: GeometricWeatherDatabase, entityList: List<AlertEntity>) {
        if (entityList.isNotEmpty()) {
            database.alertDao().insertAlertList(entityList)
        }
    }

    @JvmStatic
    fun deleteAlertList(
        database: GeometricWeatherDatabase,
        cityId: String,
        source: WeatherSource
    ) {
        database.alertDao().deleteAlertList(
            cityId,
            WeatherSourceConverter().convertToDatabaseValue(source)
        )
    }

    @JvmStatic
    fun selectLocationAlertEntity(
        database: GeometricWeatherDatabase,
        cityId: String,
        source: WeatherSource
    ): List<AlertEntity> {
        return getNonNullList(
            database.alertDao().selectLocationAlertEntity(
                cityId,
                WeatherSourceConverter().convertToDatabaseValue(source)
            )
        )
    }
}
