package wangdaye.com.geometricweather.db.converters

import androidx.room.TypeConverter
import java.util.Date

/**
 * Stores [Date] as epoch milliseconds, matching GreenDAO's Date mapping.
 */
class DateConverter {

    @TypeConverter
    fun convertToEntityProperty(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun convertToDatabaseValue(date: Date?): Long? {
        return date?.time
    }
}
