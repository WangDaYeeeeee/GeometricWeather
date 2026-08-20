package wangdaye.com.geometricweather.db.dao;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

import wangdaye.com.geometricweather.db.entities.HistoryEntity;

@Dao
public interface HistoryDao {

    @Insert
    void insertHistoryEntity(HistoryEntity entity);

    @Query("DELETE FROM HISTORY_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source")
    void deleteLocationHistoryEntity(String cityId, String source);

    @Query("SELECT * FROM HISTORY_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source "
            + "AND DATE >= :from AND DATE < :to LIMIT 1")
    @Nullable
    HistoryEntity selectHistoryEntity(String cityId, String source, Date from, Date to);

    @Query("SELECT * FROM HISTORY_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source")
    List<HistoryEntity> selectHistoryEntityList(String cityId, String source);
}
