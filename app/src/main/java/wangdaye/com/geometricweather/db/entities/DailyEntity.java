package wangdaye.com.geometricweather.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;
import java.util.Date;
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode;
import wangdaye.com.geometricweather.common.basic.models.weather.WindDegree;

/**
 * Daily entity matching GreenDAO schema 62.
 */
@Entity(tableName = "DAILY_ENTITY",
        indices = {@Index(value = {"CITY_ID", "WEATHER_SOURCE"})})
public class DailyEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    public Long id;

    @ColumnInfo(name = "CITY_ID")
    public String cityId;

    @ColumnInfo(name = "WEATHER_SOURCE")
    public String weatherSource;

    @ColumnInfo(name = "DATE")
    public Date date;

    @ColumnInfo(name = "TIME")
    public long time;

    @ColumnInfo(name = "DAYTIME_WEATHER_TEXT")
    public String daytimeWeatherText;

    @ColumnInfo(name = "DAYTIME_WEATHER_PHASE")
    public String daytimeWeatherPhase;

    @ColumnInfo(name = "DAYTIME_WEATHER_CODE")
    public WeatherCode daytimeWeatherCode;

    @ColumnInfo(name = "DAYTIME_TEMPERATURE")
    public int daytimeTemperature;

    @ColumnInfo(name = "DAYTIME_REAL_FEEL_TEMPERATURE")
    public Integer daytimeRealFeelTemperature;

    @ColumnInfo(name = "DAYTIME_REAL_FEEL_SHADER_TEMPERATURE")
    public Integer daytimeRealFeelShaderTemperature;

    @ColumnInfo(name = "DAYTIME_APPARENT_TEMPERATURE")
    public Integer daytimeApparentTemperature;

    @ColumnInfo(name = "DAYTIME_WIND_CHILL_TEMPERATURE")
    public Integer daytimeWindChillTemperature;

    @ColumnInfo(name = "DAYTIME_WET_BULB_TEMPERATURE")
    public Integer daytimeWetBulbTemperature;

    @ColumnInfo(name = "DAYTIME_DEGREE_DAY_TEMPERATURE")
    public Integer daytimeDegreeDayTemperature;

    @ColumnInfo(name = "DAYTIME_TOTAL_PRECIPITATION")
    public Float daytimeTotalPrecipitation;

    @ColumnInfo(name = "DAYTIME_THUNDERSTORM_PRECIPITATION")
    public Float daytimeThunderstormPrecipitation;

    @ColumnInfo(name = "DAYTIME_RAIN_PRECIPITATION")
    public Float daytimeRainPrecipitation;

    @ColumnInfo(name = "DAYTIME_SNOW_PRECIPITATION")
    public Float daytimeSnowPrecipitation;

    @ColumnInfo(name = "DAYTIME_ICE_PRECIPITATION")
    public Float daytimeIcePrecipitation;

    @ColumnInfo(name = "DAYTIME_TOTAL_PRECIPITATION_PROBABILITY")
    public Float daytimeTotalPrecipitationProbability;

    @ColumnInfo(name = "DAYTIME_THUNDERSTORM_PRECIPITATION_PROBABILITY")
    public Float daytimeThunderstormPrecipitationProbability;

    @ColumnInfo(name = "DAYTIME_RAIN_PRECIPITATION_PROBABILITY")
    public Float daytimeRainPrecipitationProbability;

    @ColumnInfo(name = "DAYTIME_SNOW_PRECIPITATION_PROBABILITY")
    public Float daytimeSnowPrecipitationProbability;

    @ColumnInfo(name = "DAYTIME_ICE_PRECIPITATION_PROBABILITY")
    public Float daytimeIcePrecipitationProbability;

    @ColumnInfo(name = "DAYTIME_TOTAL_PRECIPITATION_DURATION")
    public Float daytimeTotalPrecipitationDuration;

    @ColumnInfo(name = "DAYTIME_THUNDERSTORM_PRECIPITATION_DURATION")
    public Float daytimeThunderstormPrecipitationDuration;

    @ColumnInfo(name = "DAYTIME_RAIN_PRECIPITATION_DURATION")
    public Float daytimeRainPrecipitationDuration;

    @ColumnInfo(name = "DAYTIME_SNOW_PRECIPITATION_DURATION")
    public Float daytimeSnowPrecipitationDuration;

    @ColumnInfo(name = "DAYTIME_ICE_PRECIPITATION_DURATION")
    public Float daytimeIcePrecipitationDuration;

    @ColumnInfo(name = "DAYTIME_WIND_DIRECTION")
    public String daytimeWindDirection;

    @ColumnInfo(name = "DAYTIME_WIND_DEGREE")
    public WindDegree daytimeWindDegree;

    @ColumnInfo(name = "DAYTIME_WIND_SPEED")
    public Float daytimeWindSpeed;

    @ColumnInfo(name = "DAYTIME_WIND_LEVEL")
    public String daytimeWindLevel;

    @ColumnInfo(name = "DAYTIME_CLOUD_COVER")
    public Integer daytimeCloudCover;

    @ColumnInfo(name = "NIGHTTIME_WEATHER_TEXT")
    public String nighttimeWeatherText;

    @ColumnInfo(name = "NIGHTTIME_WEATHER_PHASE")
    public String nighttimeWeatherPhase;

    @ColumnInfo(name = "NIGHTTIME_WEATHER_CODE")
    public WeatherCode nighttimeWeatherCode;

    @ColumnInfo(name = "NIGHTTIME_TEMPERATURE")
    public int nighttimeTemperature;

    @ColumnInfo(name = "NIGHTTIME_REAL_FEEL_TEMPERATURE")
    public Integer nighttimeRealFeelTemperature;

    @ColumnInfo(name = "NIGHTTIME_REAL_FEEL_SHADER_TEMPERATURE")
    public Integer nighttimeRealFeelShaderTemperature;

    @ColumnInfo(name = "NIGHTTIME_APPARENT_TEMPERATURE")
    public Integer nighttimeApparentTemperature;

    @ColumnInfo(name = "NIGHTTIME_WIND_CHILL_TEMPERATURE")
    public Integer nighttimeWindChillTemperature;

    @ColumnInfo(name = "NIGHTTIME_WET_BULB_TEMPERATURE")
    public Integer nighttimeWetBulbTemperature;

    @ColumnInfo(name = "NIGHTTIME_DEGREE_DAY_TEMPERATURE")
    public Integer nighttimeDegreeDayTemperature;

    @ColumnInfo(name = "NIGHTTIME_TOTAL_PRECIPITATION")
    public Float nighttimeTotalPrecipitation;

    @ColumnInfo(name = "NIGHTTIME_THUNDERSTORM_PRECIPITATION")
    public Float nighttimeThunderstormPrecipitation;

    @ColumnInfo(name = "NIGHTTIME_RAIN_PRECIPITATION")
    public Float nighttimeRainPrecipitation;

    @ColumnInfo(name = "NIGHTTIME_SNOW_PRECIPITATION")
    public Float nighttimeSnowPrecipitation;

    @ColumnInfo(name = "NIGHTTIME_ICE_PRECIPITATION")
    public Float nighttimeIcePrecipitation;

    @ColumnInfo(name = "NIGHTTIME_TOTAL_PRECIPITATION_PROBABILITY")
    public Float nighttimeTotalPrecipitationProbability;

    @ColumnInfo(name = "NIGHTTIME_THUNDERSTORM_PRECIPITATION_PROBABILITY")
    public Float nighttimeThunderstormPrecipitationProbability;

    @ColumnInfo(name = "NIGHTTIME_RAIN_PRECIPITATION_PROBABILITY")
    public Float nighttimeRainPrecipitationProbability;

    @ColumnInfo(name = "NIGHTTIME_SNOW_PRECIPITATION_PROBABILITY")
    public Float nighttimeSnowPrecipitationProbability;

    @ColumnInfo(name = "NIGHTTIME_ICE_PRECIPITATION_PROBABILITY")
    public Float nighttimeIcePrecipitationProbability;

    @ColumnInfo(name = "NIGHTTIME_TOTAL_PRECIPITATION_DURATION")
    public Float nighttimeTotalPrecipitationDuration;

    @ColumnInfo(name = "NIGHTTIME_THUNDERSTORM_PRECIPITATION_DURATION")
    public Float nighttimeThunderstormPrecipitationDuration;

    @ColumnInfo(name = "NIGHTTIME_RAIN_PRECIPITATION_DURATION")
    public Float nighttimeRainPrecipitationDuration;

    @ColumnInfo(name = "NIGHTTIME_SNOW_PRECIPITATION_DURATION")
    public Float nighttimeSnowPrecipitationDuration;

    @ColumnInfo(name = "NIGHTTIME_ICE_PRECIPITATION_DURATION")
    public Float nighttimeIcePrecipitationDuration;

    @ColumnInfo(name = "NIGHTTIME_WIND_DIRECTION")
    public String nighttimeWindDirection;

    @ColumnInfo(name = "NIGHTTIME_WIND_DEGREE")
    public WindDegree nighttimeWindDegree;

    @ColumnInfo(name = "NIGHTTIME_WIND_SPEED")
    public Float nighttimeWindSpeed;

    @ColumnInfo(name = "NIGHTTIME_WIND_LEVEL")
    public String nighttimeWindLevel;

    @ColumnInfo(name = "NIGHTTIME_CLOUD_COVER")
    public Integer nighttimeCloudCover;

    @ColumnInfo(name = "SUN_RISE_DATE")
    public Date sunRiseDate;

    @ColumnInfo(name = "SUN_SET_DATE")
    public Date sunSetDate;

    @ColumnInfo(name = "MOON_RISE_DATE")
    public Date moonRiseDate;

    @ColumnInfo(name = "MOON_SET_DATE")
    public Date moonSetDate;

    @ColumnInfo(name = "MOON_PHASE_ANGLE")
    public Integer moonPhaseAngle;

    @ColumnInfo(name = "MOON_PHASE_DESCRIPTION")
    public String moonPhaseDescription;

    @ColumnInfo(name = "AQI_TEXT")
    public String aqiText;

    @ColumnInfo(name = "AQI_INDEX")
    public Integer aqiIndex;

    @ColumnInfo(name = "PM25")
    public Float pm25;

    @ColumnInfo(name = "PM10")
    public Float pm10;

    @ColumnInfo(name = "SO2")
    public Float so2;

    @ColumnInfo(name = "NO2")
    public Float no2;

    @ColumnInfo(name = "O3")
    public Float o3;

    @ColumnInfo(name = "CO")
    public Float co;

    @ColumnInfo(name = "GRASS_INDEX")
    public Integer grassIndex;

    @ColumnInfo(name = "GRASS_LEVEL")
    public Integer grassLevel;

    @ColumnInfo(name = "GRASS_DESCRIPTION")
    public String grassDescription;

    @ColumnInfo(name = "MOLD_INDEX")
    public Integer moldIndex;

    @ColumnInfo(name = "MOLD_LEVEL")
    public Integer moldLevel;

    @ColumnInfo(name = "MOLD_DESCRIPTION")
    public String moldDescription;

    @ColumnInfo(name = "RAGWEED_INDEX")
    public Integer ragweedIndex;

    @ColumnInfo(name = "RAGWEED_LEVEL")
    public Integer ragweedLevel;

    @ColumnInfo(name = "RAGWEED_DESCRIPTION")
    public String ragweedDescription;

    @ColumnInfo(name = "TREE_INDEX")
    public Integer treeIndex;

    @ColumnInfo(name = "TREE_LEVEL")
    public Integer treeLevel;

    @ColumnInfo(name = "TREE_DESCRIPTION")
    public String treeDescription;

    @ColumnInfo(name = "UV_INDEX")
    public Integer uvIndex;

    @ColumnInfo(name = "UV_LEVEL")
    public String uvLevel;

    @ColumnInfo(name = "UV_DESCRIPTION")
    public String uvDescription;

    @ColumnInfo(name = "HOURS_OF_SUN")
    public float hoursOfSun;

    public DailyEntity() {
    }
}
