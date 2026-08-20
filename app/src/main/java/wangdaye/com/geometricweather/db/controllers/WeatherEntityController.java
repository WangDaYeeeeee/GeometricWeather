package wangdaye.com.geometricweather.db.controllers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource;
import wangdaye.com.geometricweather.db.GeometricWeatherDatabase;
import wangdaye.com.geometricweather.db.converters.WeatherSourceConverter;
import wangdaye.com.geometricweather.db.entities.WeatherEntity;

public class WeatherEntityController extends AbsEntityController {

    public static void insertWeatherEntity(@NonNull GeometricWeatherDatabase database,
                                           @NonNull WeatherEntity entity) {
        database.weatherDao().insertWeatherEntity(entity);
    }

    public static void deleteWeather(@NonNull GeometricWeatherDatabase database,
                                     @NonNull List<WeatherEntity> entityList) {
        if (!entityList.isEmpty()) {
            database.weatherDao().deleteWeather(entityList);
        }
    }

    @Nullable
    public static WeatherEntity selectWeatherEntity(@NonNull GeometricWeatherDatabase database,
                                                    @NonNull String cityId,
                                                    @NonNull WeatherSource source) {
        List<WeatherEntity> entityList = selectWeatherEntityList(database, cityId, source);
        if (entityList.isEmpty()) {
            return null;
        } else {
            return entityList.get(0);
        }
    }

    @NonNull
    public static List<WeatherEntity> selectWeatherEntityList(
            @NonNull GeometricWeatherDatabase database,
            @NonNull String cityId,
            @NonNull WeatherSource source) {
        return getNonNullList(
                database.weatherDao().selectWeatherEntityList(
                        cityId,
                        new WeatherSourceConverter().convertToDatabaseValue(source)
                )
        );
    }
}
