package wangdaye.com.geometricweather.db.controllers;

import androidx.annotation.NonNull;

import java.util.List;

import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource;
import wangdaye.com.geometricweather.db.GeometricWeatherDatabase;
import wangdaye.com.geometricweather.db.converters.WeatherSourceConverter;
import wangdaye.com.geometricweather.db.entities.DailyEntity;

public class DailyEntityController extends AbsEntityController {

    public static void insertDailyList(@NonNull GeometricWeatherDatabase database,
                                       @NonNull List<DailyEntity> entityList) {
        if (!entityList.isEmpty()) {
            database.dailyDao().insertDailyList(entityList);
        }
    }

    public static void deleteDailyEntityList(@NonNull GeometricWeatherDatabase database,
                                             @NonNull String cityId,
                                             @NonNull WeatherSource source) {
        database.dailyDao().deleteDailyEntityList(
                cityId,
                new WeatherSourceConverter().convertToDatabaseValue(source)
        );
    }

    @NonNull
    public static List<DailyEntity> selectDailyEntityList(@NonNull GeometricWeatherDatabase database,
                                                          @NonNull String cityId,
                                                          @NonNull WeatherSource source) {
        return getNonNullList(
                database.dailyDao().selectDailyEntityList(
                        cityId,
                        new WeatherSourceConverter().convertToDatabaseValue(source)
                )
        );
    }
}
