package wangdaye.com.geometricweather.db.converters;

import androidx.room.TypeConverter;

import wangdaye.com.geometricweather.common.basic.models.weather.WindDegree;

public class WindDegreeConverter {

    @TypeConverter
    public WindDegree convertToEntityProperty(Float databaseValue) {
        if (databaseValue == null) {
            return new WindDegree(-1, true);
        } else {
            return new WindDegree(databaseValue, false);
        }
    }

    @TypeConverter
    public Float convertToDatabaseValue(WindDegree entityProperty) {
        if (entityProperty == null || entityProperty.isNoDirection()) {
            return null;
        } else {
            return entityProperty.getDegree();
        }
    }
}
