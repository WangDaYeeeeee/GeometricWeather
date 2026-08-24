package wangdaye.com.geometricweather.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import wangdaye.com.geometricweather.db.entities.LocationEntity

@Dao
interface LocationDao {

    @Insert
    fun insertLocationEntity(entity: LocationEntity)

    @Insert
    fun insertLocationEntityList(entityList: List<LocationEntity>)

    @Delete
    fun deleteLocationEntity(entity: LocationEntity)

    @Query("DELETE FROM LOCATION_ENTITY")
    fun deleteLocationEntityList()

    @Update
    fun updateLocationEntity(entity: LocationEntity)

    @Query("SELECT * FROM LOCATION_ENTITY WHERE FORMATTED_ID = :formattedId LIMIT 1")
    fun selectLocationEntity(formattedId: String): LocationEntity?

    @Query("SELECT * FROM LOCATION_ENTITY")
    fun selectLocationEntityList(): List<LocationEntity>

    @Query("SELECT COUNT(*) FROM LOCATION_ENTITY")
    fun countLocationEntity(): Int
}
