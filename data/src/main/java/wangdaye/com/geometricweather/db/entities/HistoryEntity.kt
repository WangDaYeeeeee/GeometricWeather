package wangdaye.com.geometricweather.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import kotlin.jvm.JvmField

/**
 * History entity matching GreenDAO schema 62.
 */
@Entity(tableName = "HISTORY_ENTITY")
class HistoryEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    @JvmField
    var id: Long? = null

    @ColumnInfo(name = "CITY_ID")
    @JvmField
    var cityId: String? = null

    @ColumnInfo(name = "WEATHER_SOURCE")
    @JvmField
    var weatherSource: String? = null

    @ColumnInfo(name = "DATE")
    @JvmField
    var date: Date? = null

    @ColumnInfo(name = "TIME")
    @JvmField
    var time: Long = 0L

    @ColumnInfo(name = "DAYTIME_TEMPERATURE")
    @JvmField
    var daytimeTemperature: Int = 0

    @ColumnInfo(name = "NIGHTTIME_TEMPERATURE")
    @JvmField
    var nighttimeTemperature: Int = 0
}
