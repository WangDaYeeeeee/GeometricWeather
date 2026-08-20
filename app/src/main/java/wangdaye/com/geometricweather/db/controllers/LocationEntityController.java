package wangdaye.com.geometricweather.db.controllers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import wangdaye.com.geometricweather.db.GeometricWeatherDatabase;
import wangdaye.com.geometricweather.db.entities.LocationEntity;

public class LocationEntityController extends AbsEntityController {

    public static void insertLocationEntity(@NonNull GeometricWeatherDatabase database,
                                            @NonNull LocationEntity entity) {
        database.locationDao().insertLocationEntity(entity);
    }

    public static void insertLocationEntityList(@NonNull GeometricWeatherDatabase database,
                                                @NonNull List<LocationEntity> entityList) {
        if (!entityList.isEmpty()) {
            database.locationDao().insertLocationEntityList(entityList);
        }
    }

    public static void deleteLocationEntity(@NonNull GeometricWeatherDatabase database,
                                            @NonNull LocationEntity entity) {
        database.locationDao().deleteLocationEntity(entity);
    }

    public static void deleteLocationEntityList(@NonNull GeometricWeatherDatabase database) {
        database.locationDao().deleteLocationEntityList();
    }

    public static void updateLocationEntity(@NonNull GeometricWeatherDatabase database,
                                            @NonNull LocationEntity entity) {
        database.locationDao().updateLocationEntity(entity);
    }

    @Nullable
    public static LocationEntity selectLocationEntity(@NonNull GeometricWeatherDatabase database,
                                                      @NonNull String formattedId) {
        return database.locationDao().selectLocationEntity(formattedId);
    }

    @NonNull
    public static List<LocationEntity> selectLocationEntityList(
            @NonNull GeometricWeatherDatabase database) {
        return getNonNullList(database.locationDao().selectLocationEntityList());
    }

    public static int countLocationEntity(@NonNull GeometricWeatherDatabase database) {
        return database.locationDao().countLocationEntity();
    }
}
