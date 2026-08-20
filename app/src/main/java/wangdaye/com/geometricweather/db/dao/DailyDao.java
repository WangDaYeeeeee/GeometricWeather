package wangdaye.com.geometricweather.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import wangdaye.com.geometricweather.db.entities.DailyEntity;

@Dao
public interface DailyDao {

    @Insert
    void insertDailyList(List<DailyEntity> entityList);

    @Query("DELETE FROM DAILY_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source")
    void deleteDailyEntityList(String cityId, String source);

    @Query("SELECT * FROM DAILY_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source "
            + "ORDER BY DATE ASC")
    List<DailyEntity> selectDailyEntityList(String cityId, String source);
}
