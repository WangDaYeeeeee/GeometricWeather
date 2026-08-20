package wangdaye.com.geometricweather.db.converters

import androidx.room.TypeConverter
import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource

class WeatherSourceConverter {

    @TypeConverter
    fun convertToEntityProperty(databaseValue: String?) =
        // use get instance method but not getValue method.
        WeatherSource.getInstance(databaseValue ?: "")

    @TypeConverter
    fun convertToDatabaseValue(entityProperty: WeatherSource?) =
        entityProperty?.id ?: ""
}
