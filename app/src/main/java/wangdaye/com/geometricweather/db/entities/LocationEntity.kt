package wangdaye.com.geometricweather.db.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.TimeZone
import kotlin.jvm.JvmField
import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource

/**
 * Location entity matching GreenDAO schema 62.
 */
@Entity(tableName = "LOCATION_ENTITY")
class LocationEntity {

    @PrimaryKey
    @ColumnInfo(name = "FORMATTED_ID")
    @NonNull
    @JvmField
    var formattedId: String = ""

    @ColumnInfo(name = "CITY_ID")
    @JvmField
    var cityId: String? = null

    @ColumnInfo(name = "LATITUDE")
    @JvmField
    var latitude: Float = 0f

    @ColumnInfo(name = "LONGITUDE")
    @JvmField
    var longitude: Float = 0f

    @ColumnInfo(name = "TIME_ZONE")
    @JvmField
    var timeZone: TimeZone? = null

    @ColumnInfo(name = "COUNTRY")
    @JvmField
    var country: String? = null

    @ColumnInfo(name = "PROVINCE")
    @JvmField
    var province: String? = null

    @ColumnInfo(name = "CITY")
    @JvmField
    var city: String? = null

    @ColumnInfo(name = "DISTRICT")
    @JvmField
    var district: String? = null

    @ColumnInfo(name = "WEATHER_SOURCE")
    @JvmField
    var weatherSource: WeatherSource? = null

    @ColumnInfo(name = "CURRENT_POSITION")
    @JvmField
    var currentPosition: Boolean = false

    @ColumnInfo(name = "RESIDENT_POSITION")
    @JvmField
    var residentPosition: Boolean = false

    @ColumnInfo(name = "CHINA")
    @JvmField
    var china: Boolean = false
}
