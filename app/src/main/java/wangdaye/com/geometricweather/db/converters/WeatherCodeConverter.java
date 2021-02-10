package wangdaye.com.geometricweather.db.converters;

import org.greenrobot.greendao.converter.PropertyConverter;

import wangdaye.com.geometricweather.basic.models.weather.WeatherCode;

public class WeatherCodeConverter implements PropertyConverter<WeatherCode, String> {

    @Override
    public WeatherCode convertToEntityProperty(String databaseValue) {
        return WeatherCode.valueOf(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(WeatherCode entityProperty) {
        return entityProperty.name();
    }
}