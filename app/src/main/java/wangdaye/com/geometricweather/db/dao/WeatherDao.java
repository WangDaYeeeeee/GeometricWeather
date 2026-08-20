package wangdaye.com.geometricweather.db.dao;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import wangdaye.com.geometricweather.db.entities.WeatherEntity;

@Dao
public interface WeatherDao {

    @Insert
    void insertWeatherEntity(WeatherEntity entity);

    @Delete
    void deleteWeather(List<WeatherEntity> entityList);

    @Query("SELECT * FROM WEATHER_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source LIMIT 1")
    @Nullable
    WeatherEntity selectWeatherEntity(String cityId, String source);

    @Query("SELECT * FROM WEATHER_ENTITY WHERE CITY_ID = :cityId AND WEATHER_SOURCE = :source")
    List<WeatherEntity> selectWeatherEntityList(String cityId, String source);
}
