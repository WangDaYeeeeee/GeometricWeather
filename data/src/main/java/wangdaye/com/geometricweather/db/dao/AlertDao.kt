package wangdaye.com.geometricweather.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import wangdaye.com.geometricweather.db.entities.AlertEntity

@Dao
interface AlertDao {

    @Insert
    fun insertAlertList(entityList: List<AlertEntity>)

    @Query("DELETE FROM ALERT_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source")
    fun deleteAlertList(cityId: String, source: String)

    @Query(
        "SELECT * FROM ALERT_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source " +
            "ORDER BY DATE ASC"
    )
    fun selectLocationAlertEntity(cityId: String, source: String): List<AlertEntity>
}
