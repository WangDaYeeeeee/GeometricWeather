package wangdaye.com.geometricweather.db.controllers

import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.db.GeometricWeatherDatabase
import wangdaye.com.geometricweather.db.converters.WeatherSourceConverter
import wangdaye.com.geometricweather.db.entities.DailyEntity

object DailyEntityController {

    @JvmStatic
    fun insertDailyList(database: GeometricWeatherDatabase, entityList: List<DailyEntity>) {
        if (entityList.isNotEmpty()) {
            database.dailyDao().insertDailyList(entityList)
        }
    }

    @JvmStatic
    fun deleteDailyEntityList(
        database: GeometricWeatherDatabase,
        cityId: String,
        source: WeatherSource
    ) {
        database.dailyDao().deleteDailyEntityList(
            cityId,
            WeatherSourceConverter().convertToDatabaseValue(source)
        )
    }

    @JvmStatic
    fun selectDailyEntityList(
        database: GeometricWeatherDatabase,
        cityId: String,
        source: WeatherSource
    ): List<DailyEntity> {
        return getNonNullList(
            database.dailyDao().selectDailyEntityList(
                cityId,
                WeatherSourceConverter().convertToDatabaseValue(source)
            )
        )
    }
}
