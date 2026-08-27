package wangdaye.com.geometricweather.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import wangdaye.com.geometricweather.db.entities.HourlyEntity

@Dao
interface HourlyDao {

    @Insert
    fun insertHourlyList(entityList: List<HourlyEntity>)

    @Query("DELETE FROM HOURLY_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source")
    fun deleteHourlyEntityList(cityId: String, source: String)

    @Query(
        "SELECT * FROM HOURLY_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source " +
            "ORDER BY DATE ASC"
    )
    fun selectHourlyEntityList(cityId: String, source: String): List<HourlyEntity>
}
