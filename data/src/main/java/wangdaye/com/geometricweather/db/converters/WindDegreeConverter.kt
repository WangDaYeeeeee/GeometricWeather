package wangdaye.com.geometricweather.db.converters

import androidx.room.TypeConverter
import wangdaye.com.geometricweather.common.basic.models.weather.WindDegree

class WindDegreeConverter {

    @TypeConverter
    fun convertToEntityProperty(databaseValue: Float?): WindDegree {
        return if (databaseValue == null) {
            WindDegree(-1f, true)
        } else {
            WindDegree(databaseValue, false)
        }
    }

    @TypeConverter
    fun convertToDatabaseValue(entityProperty: WindDegree?): Float? {
        return if (entityProperty == null || entityProperty.isNoDirection) {
            null
        } else {
            entityProperty.degree
        }
    }
}
