package wangdaye.com.geometricweather.db.controllers;

import androidx.annotation.NonNull;

import java.util.List;

import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource;
import wangdaye.com.geometricweather.db.GeometricWeatherDatabase;
import wangdaye.com.geometricweather.db.converters.WeatherSourceConverter;
import wangdaye.com.geometricweather.db.entities.AlertEntity;

public class AlertEntityController extends AbsEntityController {

    public static void insertAlertList(@NonNull GeometricWeatherDatabase database,
                                       @NonNull List<AlertEntity> entityList) {
        if (!entityList.isEmpty()) {
            database.alertDao().insertAlertList(entityList);
        }
    }

    public static void deleteAlertList(@NonNull GeometricWeatherDatabase database,
                                       @NonNull String cityId,
                                       @NonNull WeatherSource source) {
        database.alertDao().deleteAlertList(
                cityId,
                new WeatherSourceConverter().convertToDatabaseValue(source)
        );
    }

    public static List<AlertEntity> selectLocationAlertEntity(
            @NonNull GeometricWeatherDatabase database,
            @NonNull String cityId,
            @NonNull WeatherSource source) {
        return getNonNullList(
                database.alertDao().selectLocationAlertEntity(
                        cityId,
                        new WeatherSourceConverter().convertToDatabaseValue(source)
                )
        );
    }
}
