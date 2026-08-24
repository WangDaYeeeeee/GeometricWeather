package wangdaye.com.geometricweather.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import kotlin.jvm.JvmField

/**
 * Alert entity matching GreenDAO schema 62.
 */
@Entity(
    tableName = "ALERT_ENTITY",
    indices = [Index(value = ["CITY_ID", "WEATHER_SOURCE"])]
)
class AlertEntity {

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

    @ColumnInfo(name = "ALERT_ID")
    @JvmField
    var alertId: Long = 0L

    @ColumnInfo(name = "DATE")
    @JvmField
    var date: Date? = null

    @ColumnInfo(name = "TIME")
    @JvmField
    var time: Long = 0L

    @ColumnInfo(name = "DESCRIPTION")
    @JvmField
    var description: String? = null

    @ColumnInfo(name = "CONTENT")
    @JvmField
    var content: String? = null

    @ColumnInfo(name = "TYPE")
    @JvmField
    var type: String? = null

    @ColumnInfo(name = "PRIORITY")
    @JvmField
    var priority: Int = 0

    @ColumnInfo(name = "COLOR")
    @JvmField
    var color: Int = 0
}
