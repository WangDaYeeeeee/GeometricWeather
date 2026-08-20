package wangdaye.com.geometricweather.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;
import java.util.Date;

/**
 * Alert entity matching GreenDAO schema 62.
 */
@Entity(tableName = "ALERT_ENTITY",
        indices = {@Index(value = {"CITY_ID", "WEATHER_SOURCE"})})
public class AlertEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    public Long id;

    @ColumnInfo(name = "CITY_ID")
    public String cityId;

    @ColumnInfo(name = "WEATHER_SOURCE")
    public String weatherSource;

    @ColumnInfo(name = "ALERT_ID")
    public long alertId;

    @ColumnInfo(name = "DATE")
    public Date date;

    @ColumnInfo(name = "TIME")
    public long time;

    @ColumnInfo(name = "DESCRIPTION")
    public String description;

    @ColumnInfo(name = "CONTENT")
    public String content;

    @ColumnInfo(name = "TYPE")
    public String type;

    @ColumnInfo(name = "PRIORITY")
    public int priority;

    @ColumnInfo(name = "COLOR")
    public int color;

    public AlertEntity() {
    }
}
