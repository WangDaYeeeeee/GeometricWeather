package wangdaye.com.geometricweather.db.controllers

import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.db.GeometricWeatherDatabase
import wangdaye.com.geometricweather.db.converters.WeatherSourceConverter
import wangdaye.com.geometricweather.db.entities.MinutelyEntity

class MinutelyEntityController : AbsEntityController() {

    companion object {
        @JvmStatic
        fun insertMinutelyList(database: GeometricWeatherDatabase, entityList: List<MinutelyEntity>) {
            if (entityList.isNotEmpty()) {
                database.minutelyDao().insertMinutelyList(entityList)
            }
        }

        @JvmStatic
        fun deleteMinutelyEntityList(
            database: GeometricWeatherDatabase,
            cityId: String,
            source: WeatherSource
        ) {
            database.minutelyDao().deleteMinutelyEntityList(
                cityId,
                WeatherSourceConverter().convertToDatabaseValue(source)
            )
        }

        @JvmStatic
        fun selectMinutelyEntityList(
            database: GeometricWeatherDatabase,
            cityId: String,
            source: WeatherSource
        ): List<MinutelyEntity> {
            return getNonNullList(
                database.minutelyDao().selectMinutelyEntityList(
                    cityId,
                    WeatherSourceConverter().convertToDatabaseValue(source)
                )
            )
        }
    }
}
