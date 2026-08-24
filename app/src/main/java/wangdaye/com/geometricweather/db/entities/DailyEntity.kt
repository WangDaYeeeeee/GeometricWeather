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
 * Daily entity matching GreenDAO schema 62.
 */
@Entity(
    tableName = "DAILY_ENTITY",
    indices = [Index(value = ["CITY_ID", "WEATHER_SOURCE"])]
)
class DailyEntity {

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

    @ColumnInfo(name = "DAYTIME_WEATHER_TEXT")
    @JvmField
    var daytimeWeatherText: String? = null

    @ColumnInfo(name = "DAYTIME_WEATHER_PHASE")
    @JvmField
    var daytimeWeatherPhase: String? = null

    @ColumnInfo(name = "DAYTIME_WEATHER_CODE")
    @JvmField
    var daytimeWeatherCode: WeatherCode? = null

    @ColumnInfo(name = "DAYTIME_TEMPERATURE")
    @JvmField
    var daytimeTemperature: Int = 0

    @ColumnInfo(name = "DAYTIME_REAL_FEEL_TEMPERATURE")
    @JvmField
    var daytimeRealFeelTemperature: Int? = null

    @ColumnInfo(name = "DAYTIME_REAL_FEEL_SHADER_TEMPERATURE")
    @JvmField
    var daytimeRealFeelShaderTemperature: Int? = null

    @ColumnInfo(name = "DAYTIME_APPARENT_TEMPERATURE")
    @JvmField
    var daytimeApparentTemperature: Int? = null

    @ColumnInfo(name = "DAYTIME_WIND_CHILL_TEMPERATURE")
    @JvmField
    var daytimeWindChillTemperature: Int? = null

    @ColumnInfo(name = "DAYTIME_WET_BULB_TEMPERATURE")
    @JvmField
    var daytimeWetBulbTemperature: Int? = null

    @ColumnInfo(name = "DAYTIME_DEGREE_DAY_TEMPERATURE")
    @JvmField
    var daytimeDegreeDayTemperature: Int? = null

    @ColumnInfo(name = "DAYTIME_TOTAL_PRECIPITATION")
    @JvmField
    var daytimeTotalPrecipitation: Float? = null

    @ColumnInfo(name = "DAYTIME_THUNDERSTORM_PRECIPITATION")
    @JvmField
    var daytimeThunderstormPrecipitation: Float? = null

    @ColumnInfo(name = "DAYTIME_RAIN_PRECIPITATION")
    @JvmField
    var daytimeRainPrecipitation: Float? = null

    @ColumnInfo(name = "DAYTIME_SNOW_PRECIPITATION")
    @JvmField
    var daytimeSnowPrecipitation: Float? = null

    @ColumnInfo(name = "DAYTIME_ICE_PRECIPITATION")
    @JvmField
    var daytimeIcePrecipitation: Float? = null

    @ColumnInfo(name = "DAYTIME_TOTAL_PRECIPITATION_PROBABILITY")
    @JvmField
    var daytimeTotalPrecipitationProbability: Float? = null

    @ColumnInfo(name = "DAYTIME_THUNDERSTORM_PRECIPITATION_PROBABILITY")
    @JvmField
    var daytimeThunderstormPrecipitationProbability: Float? = null

    @ColumnInfo(name = "DAYTIME_RAIN_PRECIPITATION_PROBABILITY")
    @JvmField
    var daytimeRainPrecipitationProbability: Float? = null

    @ColumnInfo(name = "DAYTIME_SNOW_PRECIPITATION_PROBABILITY")
    @JvmField
    var daytimeSnowPrecipitationProbability: Float? = null

    @ColumnInfo(name = "DAYTIME_ICE_PRECIPITATION_PROBABILITY")
    @JvmField
    var daytimeIcePrecipitationProbability: Float? = null

    @ColumnInfo(name = "DAYTIME_TOTAL_PRECIPITATION_DURATION")
    @JvmField
    var daytimeTotalPrecipitationDuration: Float? = null

    @ColumnInfo(name = "DAYTIME_THUNDERSTORM_PRECIPITATION_DURATION")
    @JvmField
    var daytimeThunderstormPrecipitationDuration: Float? = null

    @ColumnInfo(name = "DAYTIME_RAIN_PRECIPITATION_DURATION")
    @JvmField
    var daytimeRainPrecipitationDuration: Float? = null

    @ColumnInfo(name = "DAYTIME_SNOW_PRECIPITATION_DURATION")
    @JvmField
    var daytimeSnowPrecipitationDuration: Float? = null

    @ColumnInfo(name = "DAYTIME_ICE_PRECIPITATION_DURATION")
    @JvmField
    var daytimeIcePrecipitationDuration: Float? = null

    @ColumnInfo(name = "DAYTIME_WIND_DIRECTION")
    @JvmField
    var daytimeWindDirection: String? = null

    @ColumnInfo(name = "DAYTIME_WIND_DEGREE")
    @JvmField
    var daytimeWindDegree: WindDegree? = null

    @ColumnInfo(name = "DAYTIME_WIND_SPEED")
    @JvmField
    var daytimeWindSpeed: Float? = null

    @ColumnInfo(name = "DAYTIME_WIND_LEVEL")
    @JvmField
    var daytimeWindLevel: String? = null

    @ColumnInfo(name = "DAYTIME_CLOUD_COVER")
    @JvmField
    var daytimeCloudCover: Int? = null

    @ColumnInfo(name = "NIGHTTIME_WEATHER_TEXT")
    @JvmField
    var nighttimeWeatherText: String? = null

    @ColumnInfo(name = "NIGHTTIME_WEATHER_PHASE")
    @JvmField
    var nighttimeWeatherPhase: String? = null

    @ColumnInfo(name = "NIGHTTIME_WEATHER_CODE")
    @JvmField
    var nighttimeWeatherCode: WeatherCode? = null

    @ColumnInfo(name = "NIGHTTIME_TEMPERATURE")
    @JvmField
    var nighttimeTemperature: Int = 0

    @ColumnInfo(name = "NIGHTTIME_REAL_FEEL_TEMPERATURE")
    @JvmField
    var nighttimeRealFeelTemperature: Int? = null

    @ColumnInfo(name = "NIGHTTIME_REAL_FEEL_SHADER_TEMPERATURE")
    @JvmField
    var nighttimeRealFeelShaderTemperature: Int? = null

    @ColumnInfo(name = "NIGHTTIME_APPARENT_TEMPERATURE")
    @JvmField
    var nighttimeApparentTemperature: Int? = null

    @ColumnInfo(name = "NIGHTTIME_WIND_CHILL_TEMPERATURE")
    @JvmField
    var nighttimeWindChillTemperature: Int? = null

    @ColumnInfo(name = "NIGHTTIME_WET_BULB_TEMPERATURE")
    @JvmField
    var nighttimeWetBulbTemperature: Int? = null

    @ColumnInfo(name = "NIGHTTIME_DEGREE_DAY_TEMPERATURE")
    @JvmField
    var nighttimeDegreeDayTemperature: Int? = null

    @ColumnInfo(name = "NIGHTTIME_TOTAL_PRECIPITATION")
    @JvmField
    var nighttimeTotalPrecipitation: Float? = null

    @ColumnInfo(name = "NIGHTTIME_THUNDERSTORM_PRECIPITATION")
    @JvmField
    var nighttimeThunderstormPrecipitation: Float? = null

    @ColumnInfo(name = "NIGHTTIME_RAIN_PRECIPITATION")
    @JvmField
    var nighttimeRainPrecipitation: Float? = null

    @ColumnInfo(name = "NIGHTTIME_SNOW_PRECIPITATION")
    @JvmField
    var nighttimeSnowPrecipitation: Float? = null

    @ColumnInfo(name = "NIGHTTIME_ICE_PRECIPITATION")
    @JvmField
    var nighttimeIcePrecipitation: Float? = null

    @ColumnInfo(name = "NIGHTTIME_TOTAL_PRECIPITATION_PROBABILITY")
    @JvmField
    var nighttimeTotalPrecipitationProbability: Float? = null

    @ColumnInfo(name = "NIGHTTIME_THUNDERSTORM_PRECIPITATION_PROBABILITY")
    @JvmField
    var nighttimeThunderstormPrecipitationProbability: Float? = null

    @ColumnInfo(name = "NIGHTTIME_RAIN_PRECIPITATION_PROBABILITY")
    @JvmField
    var nighttimeRainPrecipitationProbability: Float? = null

    @ColumnInfo(name = "NIGHTTIME_SNOW_PRECIPITATION_PROBABILITY")
    @JvmField
    var nighttimeSnowPrecipitationProbability: Float? = null

    @ColumnInfo(name = "NIGHTTIME_ICE_PRECIPITATION_PROBABILITY")
    @JvmField
    var nighttimeIcePrecipitationProbability: Float? = null

    @ColumnInfo(name = "NIGHTTIME_TOTAL_PRECIPITATION_DURATION")
    @JvmField
    var nighttimeTotalPrecipitationDuration: Float? = null

    @ColumnInfo(name = "NIGHTTIME_THUNDERSTORM_PRECIPITATION_DURATION")
    @JvmField
    var nighttimeThunderstormPrecipitationDuration: Float? = null

    @ColumnInfo(name = "NIGHTTIME_RAIN_PRECIPITATION_DURATION")
    @JvmField
    var nighttimeRainPrecipitationDuration: Float? = null

    @ColumnInfo(name = "NIGHTTIME_SNOW_PRECIPITATION_DURATION")
    @JvmField
    var nighttimeSnowPrecipitationDuration: Float? = null

    @ColumnInfo(name = "NIGHTTIME_ICE_PRECIPITATION_DURATION")
    @JvmField
    var nighttimeIcePrecipitationDuration: Float? = null

    @ColumnInfo(name = "NIGHTTIME_WIND_DIRECTION")
    @JvmField
    var nighttimeWindDirection: String? = null

    @ColumnInfo(name = "NIGHTTIME_WIND_DEGREE")
    @JvmField
    var nighttimeWindDegree: WindDegree? = null

    @ColumnInfo(name = "NIGHTTIME_WIND_SPEED")
    @JvmField
    var nighttimeWindSpeed: Float? = null

    @ColumnInfo(name = "NIGHTTIME_WIND_LEVEL")
    @JvmField
    var nighttimeWindLevel: String? = null

    @ColumnInfo(name = "NIGHTTIME_CLOUD_COVER")
    @JvmField
    var nighttimeCloudCover: Int? = null

    @ColumnInfo(name = "SUN_RISE_DATE")
    @JvmField
    var sunRiseDate: Date? = null

    @ColumnInfo(name = "SUN_SET_DATE")
    @JvmField
    var sunSetDate: Date? = null

    @ColumnInfo(name = "MOON_RISE_DATE")
    @JvmField
    var moonRiseDate: Date? = null

    @ColumnInfo(name = "MOON_SET_DATE")
    @JvmField
    var moonSetDate: Date? = null

    @ColumnInfo(name = "MOON_PHASE_ANGLE")
    @JvmField
    var moonPhaseAngle: Int? = null

    @ColumnInfo(name = "MOON_PHASE_DESCRIPTION")
    @JvmField
    var moonPhaseDescription: String? = null

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

    @ColumnInfo(name = "GRASS_INDEX")
    @JvmField
    var grassIndex: Int? = null

    @ColumnInfo(name = "GRASS_LEVEL")
    @JvmField
    var grassLevel: Int? = null

    @ColumnInfo(name = "GRASS_DESCRIPTION")
    @JvmField
    var grassDescription: String? = null

    @ColumnInfo(name = "MOLD_INDEX")
    @JvmField
    var moldIndex: Int? = null

    @ColumnInfo(name = "MOLD_LEVEL")
    @JvmField
    var moldLevel: Int? = null

    @ColumnInfo(name = "MOLD_DESCRIPTION")
    @JvmField
    var moldDescription: String? = null

    @ColumnInfo(name = "RAGWEED_INDEX")
    @JvmField
    var ragweedIndex: Int? = null

    @ColumnInfo(name = "RAGWEED_LEVEL")
    @JvmField
    var ragweedLevel: Int? = null

    @ColumnInfo(name = "RAGWEED_DESCRIPTION")
    @JvmField
    var ragweedDescription: String? = null

    @ColumnInfo(name = "TREE_INDEX")
    @JvmField
    var treeIndex: Int? = null

    @ColumnInfo(name = "TREE_LEVEL")
    @JvmField
    var treeLevel: Int? = null

    @ColumnInfo(name = "TREE_DESCRIPTION")
    @JvmField
    var treeDescription: String? = null

    @ColumnInfo(name = "UV_INDEX")
    @JvmField
    var uvIndex: Int? = null

    @ColumnInfo(name = "UV_LEVEL")
    @JvmField
    var uvLevel: String? = null

    @ColumnInfo(name = "UV_DESCRIPTION")
    @JvmField
    var uvDescription: String? = null

    @ColumnInfo(name = "HOURS_OF_SUN")
    @JvmField
    var hoursOfSun: Float = 0f
}
