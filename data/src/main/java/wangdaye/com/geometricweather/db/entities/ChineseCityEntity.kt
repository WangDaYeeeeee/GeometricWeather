package wangdaye.com.geometricweather.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.jvm.JvmField

/**
 * Chinese city entity matching GreenDAO schema 62.
 */
@Entity(tableName = "CHINESE_CITY_ENTITY")
class ChineseCityEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    @JvmField
    var id: Long? = null

    @ColumnInfo(name = "CITY_ID")
    @JvmField
    var cityId: String? = null

    @ColumnInfo(name = "PROVINCE")
    @JvmField
    var province: String? = null

    @ColumnInfo(name = "CITY")
    @JvmField
    var city: String? = null

    @ColumnInfo(name = "DISTRICT")
    @JvmField
    var district: String? = null

    @ColumnInfo(name = "LATITUDE")
    @JvmField
    var latitude: String? = null

    @ColumnInfo(name = "LONGITUDE")
    @JvmField
    var longitude: String? = null
}
