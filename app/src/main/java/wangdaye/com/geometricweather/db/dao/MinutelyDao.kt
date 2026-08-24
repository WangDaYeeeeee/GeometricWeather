package wangdaye.com.geometricweather.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import wangdaye.com.geometricweather.db.entities.MinutelyEntity

@Dao
interface MinutelyDao {

    @Insert
    fun insertMinutelyList(entityList: List<MinutelyEntity>)

    @Query("DELETE FROM MINUTELY_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source")
    fun deleteMinutelyEntityList(cityId: String, source: String)

    @Query(
        "SELECT * FROM MINUTELY_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source " +
            "ORDER BY DATE ASC"
    )
    fun selectMinutelyEntityList(cityId: String, source: String): List<MinutelyEntity>
}
