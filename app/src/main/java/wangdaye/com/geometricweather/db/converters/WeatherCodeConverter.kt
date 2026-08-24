package wangdaye.com.geometricweather.db.converters

import androidx.room.TypeConverter
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode

class WeatherCodeConverter {

    @TypeConverter
    fun convertToEntityProperty(databaseValue: String?): WeatherCode? {
        if (databaseValue == null) {
            return null
        }
        // use get instance method but not getValue method.
        return WeatherCode.getInstance(databaseValue)
    }

    @TypeConverter
    fun convertToDatabaseValue(entityProperty: WeatherCode?): String? {
        return entityProperty?.id
    }
}
