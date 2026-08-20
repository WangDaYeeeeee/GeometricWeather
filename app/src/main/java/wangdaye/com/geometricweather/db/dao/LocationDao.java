package wangdaye.com.geometricweather.db.dao;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import wangdaye.com.geometricweather.db.entities.LocationEntity;

@Dao
public interface LocationDao {

    @Insert
    void insertLocationEntity(LocationEntity entity);

    @Insert
    void insertLocationEntityList(List<LocationEntity> entityList);

    @Delete
    void deleteLocationEntity(LocationEntity entity);

    @Query("DELETE FROM LOCATION_ENTITY")
    void deleteLocationEntityList();

    @Update
    void updateLocationEntity(LocationEntity entity);

    @Query("SELECT * FROM LOCATION_ENTITY WHERE FORMATTED_ID = :formattedId LIMIT 1")
    @Nullable
    LocationEntity selectLocationEntity(String formattedId);

    @Query("SELECT * FROM LOCATION_ENTITY")
    List<LocationEntity> selectLocationEntityList();

    @Query("SELECT COUNT(*) FROM LOCATION_ENTITY")
    int countLocationEntity();
}
