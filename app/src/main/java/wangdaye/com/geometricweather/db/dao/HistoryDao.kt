package wangdaye.com.geometricweather.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.Date
import wangdaye.com.geometricweather.db.entities.HistoryEntity

@Dao
interface HistoryDao {

    @Insert
    fun insertHistoryEntity(entity: HistoryEntity)

    @Query("DELETE FROM HISTORY_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source")
    fun deleteLocationHistoryEntity(cityId: String, source: String)

    @Query(
        "SELECT * FROM HISTORY_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source " +
            "AND DATE >= :from AND DATE < :to LIMIT 1"
    )
    fun selectHistoryEntity(cityId: String, source: String, from: Date, to: Date): HistoryEntity?

    @Query("SELECT * FROM HISTORY_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source")
    fun selectHistoryEntityList(cityId: String, source: String): List<HistoryEntity>
}
