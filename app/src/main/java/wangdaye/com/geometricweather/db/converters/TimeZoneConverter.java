package wangdaye.com.geometricweather.db.converters;

import androidx.room.TypeConverter;

import java.util.TimeZone;

public class TimeZoneConverter {

    @TypeConverter
    public TimeZone convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        return TimeZone.getTimeZone(databaseValue);
    }

    @TypeConverter
    public String convertToDatabaseValue(TimeZone entityProperty) {
        if (entityProperty == null) {
            return null;
        }
        return entityProperty.getID();
    }
}
