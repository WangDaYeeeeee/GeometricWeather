package wangdaye.com.geometricweather.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import wangdaye.com.geometricweather.db.entities.HourlyEntity;

@Dao
public interface HourlyDao {

    @Insert
    void insertHourlyList(List<HourlyEntity> entityList);

    @Query("DELETE FROM HOURLY_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source")
    void deleteHourlyEntityList(String cityId, String source);

    @Query("SELECT * FROM HOURLY_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source "
            + "ORDER BY DATE ASC")
    List<HourlyEntity> selectHourlyEntityList(String cityId, String source);
}
