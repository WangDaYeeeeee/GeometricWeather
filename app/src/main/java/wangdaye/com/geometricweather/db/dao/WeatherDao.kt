package wangdaye.com.geometricweather.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import wangdaye.com.geometricweather.db.entities.WeatherEntity

@Dao
interface WeatherDao {

    @Insert
    fun insertWeatherEntity(entity: WeatherEntity)

    @Delete
    fun deleteWeather(entityList: List<WeatherEntity>)

    @Query("SELECT * FROM WEATHER_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source LIMIT 1")
    fun selectWeatherEntity(cityId: String, source: String): WeatherEntity?

    @Query("SELECT * FROM WEATHER_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source")
    fun selectWeatherEntityList(cityId: String, source: String): List<WeatherEntity>
}
