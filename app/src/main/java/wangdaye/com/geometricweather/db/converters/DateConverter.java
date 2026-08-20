package wangdaye.com.geometricweather.db.converters;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Stores {@link Date} as epoch milliseconds, matching GreenDAO's Date mapping.
 */
public class DateConverter {

    @TypeConverter
    public Date convertToEntityProperty(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public Long convertToDatabaseValue(Date date) {
        return date == null ? null : date.getTime();
    }
}
