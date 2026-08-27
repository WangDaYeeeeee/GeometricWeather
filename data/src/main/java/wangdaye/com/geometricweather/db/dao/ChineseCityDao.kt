package wangdaye.com.geometricweather.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import wangdaye.com.geometricweather.db.entities.ChineseCityEntity

@Dao
interface ChineseCityDao {

    @Insert
    fun insertChineseCityEntityList(entityList: List<ChineseCityEntity>)

    @Query("DELETE FROM CHINESE_CITY_ENTITY")
    fun deleteChineseCityEntityList()

    @Query("SELECT * FROM CHINESE_CITY_ENTITY WHERE DISTRICT = :name OR CITY = :name LIMIT 1")
    fun selectChineseCityEntity(name: String): ChineseCityEntity?

    @Query("SELECT * FROM CHINESE_CITY_ENTITY WHERE DISTRICT = :district AND CITY = :city LIMIT 1")
    fun selectByDistrictAndCity(district: String, city: String): ChineseCityEntity?

    @Query("SELECT * FROM CHINESE_CITY_ENTITY WHERE DISTRICT = :district AND PROVINCE = :province LIMIT 1")
    fun selectByDistrictAndProvince(district: String, province: String): ChineseCityEntity?

    @Query("SELECT * FROM CHINESE_CITY_ENTITY WHERE CITY = :city AND PROVINCE = :province LIMIT 1")
    fun selectByCityAndProvince(city: String, province: String): ChineseCityEntity?

    @Query("SELECT * FROM CHINESE_CITY_ENTITY WHERE CITY = :city LIMIT 1")
    fun selectByCity(city: String): ChineseCityEntity?

    @Query("SELECT * FROM CHINESE_CITY_ENTITY WHERE DISTRICT = :district LIMIT 1")
    fun selectByDistrict(district: String): ChineseCityEntity?

    @Query("SELECT * FROM CHINESE_CITY_ENTITY")
    fun selectChineseCityEntityList(): List<ChineseCityEntity>

    @Query(
        "SELECT * FROM CHINESE_CITY_ENTITY " +
            "WHERE DISTRICT LIKE '%' || :name || '%' " +
            "OR CITY LIKE '%' || :name || '%' " +
            "OR PROVINCE LIKE '%' || :name || '%'"
    )
    fun selectChineseCityEntityList(name: String): List<ChineseCityEntity>

    @Query("SELECT COUNT(*) FROM CHINESE_CITY_ENTITY")
    fun countChineseCityEntity(): Int
}
