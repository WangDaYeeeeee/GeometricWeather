package wangdaye.com.geometricweather.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import kotlin.jvm.JvmField
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode
import wangdaye.com.geometricweather.common.basic.models.weather.WindDegree

/**
 * Hourly entity matching GreenDAO schema 62.
 */
@Entity(
    tableName = "HOURLY_ENTITY",
    indices = [Index(value = ["CITY_ID", "WEATHER_SOURCE"])]
)
class HourlyEntity {

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

    @ColumnInfo(name = "TEMPERATURE")
    @JvmField
    var temperature: Int = 0

    @ColumnInfo(name = "REAL_FEEL_TEMPERATURE")
    @JvmField
    var realFeelTemperature: Int? = null

    @ColumnInfo(name = "REAL_FEEL_SHADER_TEMPERATURE")
    @JvmField
    var realFeelShaderTemperature: Int? = null

    @ColumnInfo(name = "APPARENT_TEMPERATURE")
    @JvmField
    var apparentTemperature: Int? = null

    @ColumnInfo(name = "WIND_CHILL_TEMPERATURE")
    @JvmField
    var windChillTemperature: Int? = null

    @ColumnInfo(name = "WET_BULB_TEMPERATURE")
    @JvmField
    var wetBulbTemperature: Int? = null

    @ColumnInfo(name = "DEGREE_DAY_TEMPERATURE")
    @JvmField
    var degreeDayTemperature: Int? = null

    @ColumnInfo(name = "TOTAL_PRECIPITATION")
    @JvmField
    var totalPrecipitation: Float? = null

    @ColumnInfo(name = "THUNDERSTORM_PRECIPITATION")
    @JvmField
    var thunderstormPrecipitation: Float? = null

    @ColumnInfo(name = "RAIN_PRECIPITATION")
    @JvmField
    var rainPrecipitation: Float? = null

    @ColumnInfo(name = "SNOW_PRECIPITATION")
    @JvmField
    var snowPrecipitation: Float? = null

    @ColumnInfo(name = "ICE_PRECIPITATION")
    @JvmField
    var icePrecipitation: Float? = null

    @ColumnInfo(name = "TOTAL_PRECIPITATION_PROBABILITY")
    @JvmField
    var totalPrecipitationProbability: Float? = null

    @ColumnInfo(name = "THUNDERSTORM_PRECIPITATION_PROBABILITY")
    @JvmField
    var thunderstormPrecipitationProbability: Float? = null

    @ColumnInfo(name = "RAIN_PRECIPITATION_PROBABILITY")
    @JvmField
    var rainPrecipitationProbability: Float? = null

    @ColumnInfo(name = "SNOW_PRECIPITATION_PROBABILITY")
    @JvmField
    var snowPrecipitationProbability: Float? = null

    @ColumnInfo(name = "ICE_PRECIPITATION_PROBABILITY")
    @JvmField
    var icePrecipitationProbability: Float? = null

    @ColumnInfo(name = "WIND_DIRECTION")
    @JvmField
    var windDirection: String? = null

    @ColumnInfo(name = "WIND_DEGREE")
    @JvmField
    var windDegree: WindDegree? = null

    @ColumnInfo(name = "WIND_SPEED")
    @JvmField
    var windSpeed: Float? = null

    @ColumnInfo(name = "WIND_LEVEL")
    @JvmField
    var windLevel: String? = null

    @ColumnInfo(name = "UV_INDEX")
    @JvmField
    var uvIndex: Int? = null

    @ColumnInfo(name = "UV_LEVEL")
    @JvmField
    var uvLevel: String? = null

    @ColumnInfo(name = "UV_DESCRIPTION")
    @JvmField
    var uvDescription: String? = null
}
