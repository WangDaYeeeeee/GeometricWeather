package wangdaye.com.geometricweather.db.converters

import androidx.room.TypeConverter
import java.util.TimeZone

class TimeZoneConverter {

    @TypeConverter
    fun convertToEntityProperty(databaseValue: String?): TimeZone? {
        if (databaseValue == null) {
            return null
        }
        return TimeZone.getTimeZone(databaseValue)
    }

    @TypeConverter
    fun convertToDatabaseValue(entityProperty: TimeZone?): String? {
        return entityProperty?.id
    }
}
