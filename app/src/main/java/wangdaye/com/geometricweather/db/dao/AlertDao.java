package wangdaye.com.geometricweather.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import wangdaye.com.geometricweather.db.entities.AlertEntity;

@Dao
public interface AlertDao {

    @Insert
    void insertAlertList(List<AlertEntity> entityList);

    @Query("DELETE FROM ALERT_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source")
    void deleteAlertList(String cityId, String source);

    @Query("SELECT * FROM ALERT_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source "
            + "ORDER BY DATE ASC")
    List<AlertEntity> selectLocationAlertEntity(String cityId, String source);
}
