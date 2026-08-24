package wangdaye.com.geometricweather.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import kotlin.jvm.JvmField
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode
import wangdaye.com.geometricweather.common.basic.models.weather.WindDegree

/**
 * Weather entity matching GreenDAO schema 62.
 */
@Entity(tableName = "WEATHER_ENTITY")
class WeatherEntity {

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

    @ColumnInfo(name = "TIME_STAMP")
    @JvmField
    var timeStamp: Long = 0L

    @ColumnInfo(name = "PUBLISH_DATE")
    @JvmField
    var publishDate: Date? = null

    @ColumnInfo(name = "PUBLISH_TIME")
    @JvmField
    var publishTime: Long = 0L

    @ColumnInfo(name = "UPDATE_DATE")
    @JvmField
    var updateDate: Date? = null

    @ColumnInfo(name = "UPDATE_TIME")
    @JvmField
    var updateTime: Long = 0L

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

    @ColumnInfo(name = "AQI_TEXT")
    @JvmField
    var aqiText: String? = null

    @ColumnInfo(name = "AQI_INDEX")
    @JvmField
    var aqiIndex: Int? = null

    @ColumnInfo(name = "PM25")
    @JvmField
    var pm25: Float? = null

    @ColumnInfo(name = "PM10")
    @JvmField
    var pm10: Float? = null

    @ColumnInfo(name = "SO2")
    @JvmField
    var so2: Float? = null

    @ColumnInfo(name = "NO2")
    @JvmField
    var no2: Float? = null

    @ColumnInfo(name = "O3")
    @JvmField
    var o3: Float? = null

    @ColumnInfo(name = "CO")
    @JvmField
    var co: Float? = null

    @ColumnInfo(name = "RELATIVE_HUMIDITY")
    @JvmField
    var relativeHumidity: Float? = null

    @ColumnInfo(name = "PRESSURE")
    @JvmField
    var pressure: Float? = null

    @ColumnInfo(name = "VISIBILITY")
    @JvmField
    var visibility: Float? = null

    @ColumnInfo(name = "DEW_POINT")
    @JvmField
    var dewPoint: Int? = null

    @ColumnInfo(name = "CLOUD_COVER")
    @JvmField
    var cloudCover: Int? = null

    @ColumnInfo(name = "CEILING")
    @JvmField
    var ceiling: Float? = null

    @ColumnInfo(name = "DAILY_FORECAST")
    @JvmField
    var dailyForecast: String? = null

    @ColumnInfo(name = "HOURLY_FORECAST")
    @JvmField
    var hourlyForecast: String? = null
}
