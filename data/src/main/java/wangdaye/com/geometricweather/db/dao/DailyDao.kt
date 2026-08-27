package wangdaye.com.geometricweather.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import wangdaye.com.geometricweather.db.entities.DailyEntity

@Dao
interface DailyDao {

    @Insert
    fun insertDailyList(entityList: List<DailyEntity>)

    @Query("DELETE FROM DAILY_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source")
    fun deleteDailyEntityList(cityId: String, source: String)

    @Query(
        "SELECT * FROM DAILY_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source " +
            "ORDER BY DATE ASC"
    )
    fun selectDailyEntityList(cityId: String, source: String): List<DailyEntity>
}
