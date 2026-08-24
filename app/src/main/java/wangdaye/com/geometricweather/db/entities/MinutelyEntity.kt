package wangdaye.com.geometricweather.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import kotlin.jvm.JvmField
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode

/**
 * Minutely entity matching GreenDAO schema 62.
 */
@Entity(
    tableName = "MINUTELY_ENTITY",
    indices = [Index(value = ["CITY_ID", "WEATHER_SOURCE"])]
)
class MinutelyEntity {

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

    @ColumnInfo(name = "DAYLIGHT")
    @JvmField
    var daylight: Boolean = false

    @ColumnInfo(name = "WEATHER_TEXT")
    @JvmField
    var weatherText: String? = null

    @ColumnInfo(name = "WEATHER_CODE")
    @JvmField
    var weatherCode: WeatherCode? = null

    @ColumnInfo(name = "MINUTE_INTERVAL")
    @JvmField
    var minuteInterval: Int = 0

    @ColumnInfo(name = "DBZ")
    @JvmField
    var dbz: Int? = null

    @ColumnInfo(name = "CLOUD_COVER")
    @JvmField
    var cloudCover: Int? = null
}
