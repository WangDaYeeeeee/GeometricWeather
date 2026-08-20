package wangdaye.com.geometricweather.db.controllers;

import androidx.annotation.NonNull;

import java.util.List;

import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource;
import wangdaye.com.geometricweather.db.GeometricWeatherDatabase;
import wangdaye.com.geometricweather.db.converters.WeatherSourceConverter;
import wangdaye.com.geometricweather.db.entities.HourlyEntity;

public class HourlyEntityController extends AbsEntityController {

    public static void insertHourlyList(@NonNull GeometricWeatherDatabase database,
                                        @NonNull List<HourlyEntity> entityList) {
        if (!entityList.isEmpty()) {
            database.hourlyDao().insertHourlyList(entityList);
        }
    }

    public static void deleteHourlyEntityList(@NonNull GeometricWeatherDatabase database,
                                              @NonNull String cityId,
                                              @NonNull WeatherSource source) {
        database.hourlyDao().deleteHourlyEntityList(
                cityId,
                new WeatherSourceConverter().convertToDatabaseValue(source)
        );
    }

    public static List<HourlyEntity> selectHourlyEntityList(
            @NonNull GeometricWeatherDatabase database,
            @NonNull String cityId,
            @NonNull WeatherSource source) {
        return getNonNullList(
                database.hourlyDao().selectHourlyEntityList(
                        cityId,
                        new WeatherSourceConverter().convertToDatabaseValue(source)
                )
        );
    }
}
