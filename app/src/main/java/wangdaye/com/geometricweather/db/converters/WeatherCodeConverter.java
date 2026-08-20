package wangdaye.com.geometricweather.db.converters;

import androidx.room.TypeConverter;

import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode;

public class WeatherCodeConverter {

    @TypeConverter
    public WeatherCode convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        // use get instance method but not getValue method.
        return WeatherCode.getInstance(databaseValue);
    }

    @TypeConverter
    public String convertToDatabaseValue(WeatherCode entityProperty) {
        if (entityProperty == null) {
            return null;
        }
        return entityProperty.getId();
    }
}
