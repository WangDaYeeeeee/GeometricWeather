package wangdaye.com.geometricweather.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import wangdaye.com.geometricweather.db.entities.MinutelyEntity;

@Dao
public interface MinutelyDao {

    @Insert
    void insertMinutelyList(List<MinutelyEntity> entityList);

    @Query("DELETE FROM MINUTELY_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source")
    void deleteMinutelyEntityList(String cityId, String source);

    @Query("SELECT * FROM MINUTELY_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source "
            + "ORDER BY DATE ASC")
    List<MinutelyEntity> selectMinutelyEntityList(String cityId, String source);
}
