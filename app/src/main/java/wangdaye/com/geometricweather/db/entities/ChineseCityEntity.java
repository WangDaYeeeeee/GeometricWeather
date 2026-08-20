package wangdaye.com.geometricweather.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


/**
 * Chinese city entity matching GreenDAO schema 62.
 */
@Entity(tableName = "CHINESE_CITY_ENTITY")
public class ChineseCityEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    public Long id;

    @ColumnInfo(name = "CITY_ID")
    public String cityId;

    @ColumnInfo(name = "PROVINCE")
    public String province;

    @ColumnInfo(name = "CITY")
    public String city;

    @ColumnInfo(name = "DISTRICT")
    public String district;

    @ColumnInfo(name = "LATITUDE")
    public String latitude;

    @ColumnInfo(name = "LONGITUDE")
    public String longitude;

    public ChineseCityEntity() {
    }
}
