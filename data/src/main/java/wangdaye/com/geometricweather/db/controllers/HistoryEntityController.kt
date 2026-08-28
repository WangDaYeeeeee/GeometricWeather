package wangdaye.com.geometricweather.db.controllers

import android.annotation.SuppressLint
import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.db.GeometricWeatherDatabase
import wangdaye.com.geometricweather.db.converters.WeatherSourceConverter
import wangdaye.com.geometricweather.db.entities.HistoryEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

object HistoryEntityController {

    @JvmStatic
    fun insertHistoryEntity(database: GeometricWeatherDatabase, entity: HistoryEntity) {
        database.historyDao().insertHistoryEntity(entity)
    }

    @JvmStatic
    fun deleteLocationHistoryEntity(
        database: GeometricWeatherDatabase,
        cityId: String,
        source: WeatherSource
    ) {
        database.historyDao().deleteLocationHistoryEntity(
            cityId,
            WeatherSourceConverter().convertToDatabaseValue(source)
        )
    }

    @SuppressLint("SimpleDateFormat")
    @JvmStatic
    fun selectYesterdayHistoryEntity(
        database: GeometricWeatherDatabase,
        cityId: String,
        source: WeatherSource,
        currentDate: Date
    ): HistoryEntity? {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd")
            val today = format.parse(format.format(currentDate))
                ?: throw NullPointerException("Get null Date object.")

            val calendar = Calendar.getInstance()
            calendar.time = today
            calendar.add(Calendar.DATE, -1)
            val yesterday = calendar.time

            database.historyDao().selectHistoryEntity(
                cityId,
                WeatherSourceConverter().convertToDatabaseValue(source),
                yesterday,
                today
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @JvmStatic
    fun selectHistoryEntityList(
        database: GeometricWeatherDatabase,
        cityId: String,
        source: WeatherSource
    ): List<HistoryEntity> {
        return getNonNullList(
            database.historyDao().selectHistoryEntityList(
                cityId,
                WeatherSourceConverter().convertToDatabaseValue(source)
            )
        )
    }
}
