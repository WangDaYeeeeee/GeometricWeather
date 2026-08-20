package wangdaye.com.geometricweather.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

/**
 * History entity matching GreenDAO schema 62.
 */
@Entity(tableName = "HISTORY_ENTITY")
public class HistoryEntity {

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

    @ColumnInfo(name = "DAYTIME_TEMPERATURE")
    public int daytimeTemperature;

    @ColumnInfo(name = "NIGHTTIME_TEMPERATURE")
    public int nighttimeTemperature;

    public HistoryEntity() {
    }
}
