package wangdaye.com.geometricweather.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;
import java.util.Date;
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode;

/**
 * Minutely entity matching GreenDAO schema 62.
 */
@Entity(tableName = "MINUTELY_ENTITY",
        indices = {@Index(value = {"CITY_ID", "WEATHER_SOURCE"})})
public class MinutelyEntity {

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

    @ColumnInfo(name = "DAYLIGHT")
    public boolean daylight;

    @ColumnInfo(name = "WEATHER_TEXT")
    public String weatherText;

    @ColumnInfo(name = "WEATHER_CODE")
    public WeatherCode weatherCode;

    @ColumnInfo(name = "MINUTE_INTERVAL")
    public int minuteInterval;

    @ColumnInfo(name = "DBZ")
    public Integer dbz;

    @ColumnInfo(name = "CLOUD_COVER")
    public Integer cloudCover;

    public MinutelyEntity() {
    }
}
