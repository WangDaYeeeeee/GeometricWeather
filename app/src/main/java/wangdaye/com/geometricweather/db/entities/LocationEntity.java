package wangdaye.com.geometricweather.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import java.util.TimeZone;
import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource;

/**
 * Location entity matching GreenDAO schema 62.
 */
@Entity(tableName = "LOCATION_ENTITY")
public class LocationEntity {

    @PrimaryKey
    @ColumnInfo(name = "FORMATTED_ID")
    @NonNull
    public String formattedId = "";

    @ColumnInfo(name = "CITY_ID")
    public String cityId;

    @ColumnInfo(name = "LATITUDE")
    public float latitude;

    @ColumnInfo(name = "LONGITUDE")
    public float longitude;

    @ColumnInfo(name = "TIME_ZONE")
    public TimeZone timeZone;

    @ColumnInfo(name = "COUNTRY")
    public String country;

    @ColumnInfo(name = "PROVINCE")
    public String province;

    @ColumnInfo(name = "CITY")
    public String city;

    @ColumnInfo(name = "DISTRICT")
    public String district;

    @ColumnInfo(name = "WEATHER_SOURCE")
    public WeatherSource weatherSource;

    @ColumnInfo(name = "CURRENT_POSITION")
    public boolean currentPosition;

    @ColumnInfo(name = "RESIDENT_POSITION")
    public boolean residentPosition;

    @ColumnInfo(name = "CHINA")
    public boolean china;

    public LocationEntity() {
    }
}
