package wangdaye.com.geometricweather.db.controllers;

import androidx.annotation.NonNull;

import java.util.List;

import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource;
import wangdaye.com.geometricweather.db.GeometricWeatherDatabase;
import wangdaye.com.geometricweather.db.converters.WeatherSourceConverter;
import wangdaye.com.geometricweather.db.entities.MinutelyEntity;

public class MinutelyEntityController extends AbsEntityController {

    public static void insertMinutelyList(@NonNull GeometricWeatherDatabase database,
                                          @NonNull List<MinutelyEntity> entityList) {
        if (!entityList.isEmpty()) {
            database.minutelyDao().insertMinutelyList(entityList);
        }
    }

    public static void deleteMinutelyEntityList(@NonNull GeometricWeatherDatabase database,
                                                @NonNull String cityId,
                                                @NonNull WeatherSource source) {
        database.minutelyDao().deleteMinutelyEntityList(
                cityId,
                new WeatherSourceConverter().convertToDatabaseValue(source)
        );
    }

    public static List<MinutelyEntity> selectMinutelyEntityList(
            @NonNull GeometricWeatherDatabase database,
            @NonNull String cityId,
            @NonNull WeatherSource source) {
        return getNonNullList(
                database.minutelyDao().selectMinutelyEntityList(
                        cityId,
                        new WeatherSourceConverter().convertToDatabaseValue(source)
                )
        );
    }
}
