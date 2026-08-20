package wangdaye.com.geometricweather.db.dao;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import wangdaye.com.geometricweather.db.entities.ChineseCityEntity;

@Dao
public interface ChineseCityDao {

    @Insert
    void insertChineseCityEntityList(List<ChineseCityEntity> entityList);

    @Query("DELETE FROM CHINESE_CITY_ENTITY")
    void deleteChineseCityEntityList();

    @Query("SELECT * FROM CHINESE_CITY_ENTITY WHERE DISTRICT = :name OR CITY = :name LIMIT 1")
    @Nullable
    ChineseCityEntity selectChineseCityEntity(String name);

    @Query("SELECT * FROM CHINESE_CITY_ENTITY WHERE DISTRICT = :district AND CITY = :city LIMIT 1")
    @Nullable
    ChineseCityEntity selectByDistrictAndCity(String district, String city);

    @Query("SELECT * FROM CHINESE_CITY_ENTITY WHERE DISTRICT = :district AND PROVINCE = :province LIMIT 1")
    @Nullable
    ChineseCityEntity selectByDistrictAndProvince(String district, String province);

    @Query("SELECT * FROM CHINESE_CITY_ENTITY WHERE CITY = :city AND PROVINCE = :province LIMIT 1")
    @Nullable
    ChineseCityEntity selectByCityAndProvince(String city, String province);

    @Query("SELECT * FROM CHINESE_CITY_ENTITY WHERE CITY = :city LIMIT 1")
    @Nullable
    ChineseCityEntity selectByCity(String city);

    @Query("SELECT * FROM CHINESE_CITY_ENTITY WHERE DISTRICT = :district LIMIT 1")
    @Nullable
    ChineseCityEntity selectByDistrict(String district);

    @Query("SELECT * FROM CHINESE_CITY_ENTITY")
    List<ChineseCityEntity> selectChineseCityEntityList();

    @Query("SELECT * FROM CHINESE_CITY_ENTITY "
            + "WHERE DISTRICT LIKE '%' || :name || '%' "
            + "OR CITY LIKE '%' || :name || '%' "
            + "OR PROVINCE LIKE '%' || :name || '%'")
    List<ChineseCityEntity> selectChineseCityEntityList(String name);

    @Query("SELECT COUNT(*) FROM CHINESE_CITY_ENTITY")
    int countChineseCityEntity();
}
