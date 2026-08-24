package wangdaye.com.geometricweather.db.controllers

import android.text.TextUtils
import wangdaye.com.geometricweather.db.GeometricWeatherDatabase
import wangdaye.com.geometricweather.db.entities.ChineseCityEntity
import kotlin.math.pow

class ChineseCityEntityController : AbsEntityController() {

    companion object {
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
            if (TextUtils.isEmpty(name)) {
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
            var entity = database.chineseCityDao().selectByDistrictAndCity(district, city)
            if (entity != null) {
                return entity
            }
            entity = database.chineseCityDao().selectByDistrictAndProvince(district, province)
            if (entity != null) {
                return entity
            }
            entity = database.chineseCityDao().selectByCityAndProvince(city, province)
            if (entity != null) {
                return entity
            }
            entity = database.chineseCityDao().selectByCity(city)
            if (entity != null) {
                return entity
            }
            entity = database.chineseCityDao().selectByDistrictAndProvince(city, province)
            if (entity != null) {
                return entity
            }
            entity = database.chineseCityDao().selectByDistrictAndCity(city, province)
            if (entity != null) {
                return entity
            }
            entity = database.chineseCityDao().selectByDistrict(city)
            if (entity != null) {
                return entity
            }
            return database.chineseCityDao().selectByCity(district)
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

            var minIndex = -1
            var minDistance = Double.MAX_VALUE
            for (i in entityList.indices) {
                val distance = (latitude - entityList[i].latitude!!.toDouble()).pow(2.0) +
                    (longitude - entityList[i].longitude!!.toDouble()).pow(2.0)
                if (distance < minDistance) {
                    minIndex = i
                    minDistance = distance
                }
            }
            return if (0 <= minIndex && minIndex < entityList.size) {
                entityList[minIndex]
            } else {
                null
            }
        }

        @JvmStatic
        fun selectChineseCityEntityList(
            database: GeometricWeatherDatabase,
            name: String
        ): List<ChineseCityEntity> {
            if (TextUtils.isEmpty(name)) {
                return ArrayList()
            }
            return getNonNullList(database.chineseCityDao().selectChineseCityEntityList(name))
        }

        @JvmStatic
        fun countChineseCityEntity(database: GeometricWeatherDatabase): Int {
            return database.chineseCityDao().countChineseCityEntity()
        }
    }
}
