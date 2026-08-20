package wangdaye.com.geometricweather.db.controllers;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import wangdaye.com.geometricweather.db.GeometricWeatherDatabase;
import wangdaye.com.geometricweather.db.entities.ChineseCityEntity;

public class ChineseCityEntityController extends AbsEntityController {

    public static void insertChineseCityEntityList(@NonNull GeometricWeatherDatabase database,
                                                   @NonNull List<ChineseCityEntity> entityList) {
        if (!entityList.isEmpty()) {
            database.chineseCityDao().insertChineseCityEntityList(entityList);
        }
    }

    public static void deleteChineseCityEntityList(@NonNull GeometricWeatherDatabase database) {
        database.chineseCityDao().deleteChineseCityEntityList();
    }

    @Nullable
    public static ChineseCityEntity selectChineseCityEntity(@NonNull GeometricWeatherDatabase database,
                                                            @NonNull String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        return database.chineseCityDao().selectChineseCityEntity(name);
    }

    @Nullable
    public static ChineseCityEntity selectChineseCityEntity(@NonNull GeometricWeatherDatabase database,
                                                            @NonNull String province,
                                                            @NonNull String city,
                                                            @NonNull String district) {
        ChineseCityEntity entity = database.chineseCityDao().selectByDistrictAndCity(district, city);
        if (entity != null) {
            return entity;
        }
        entity = database.chineseCityDao().selectByDistrictAndProvince(district, province);
        if (entity != null) {
            return entity;
        }
        entity = database.chineseCityDao().selectByCityAndProvince(city, province);
        if (entity != null) {
            return entity;
        }
        entity = database.chineseCityDao().selectByCity(city);
        if (entity != null) {
            return entity;
        }
        entity = database.chineseCityDao().selectByDistrictAndProvince(city, province);
        if (entity != null) {
            return entity;
        }
        entity = database.chineseCityDao().selectByDistrictAndCity(city, province);
        if (entity != null) {
            return entity;
        }
        entity = database.chineseCityDao().selectByDistrict(city);
        if (entity != null) {
            return entity;
        }
        return database.chineseCityDao().selectByCity(district);
    }

    @Nullable
    public static ChineseCityEntity selectChineseCityEntity(@NonNull GeometricWeatherDatabase database,
                                                            float latitude,
                                                            float longitude) {
        List<ChineseCityEntity> entityList = getNonNullList(
                database.chineseCityDao().selectChineseCityEntityList()
        );

        int minIndex = -1;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < entityList.size(); i++) {
            double distance = Math.pow(
                    latitude - Double.parseDouble(entityList.get(i).latitude), 2)
                    + Math.pow(longitude - Double.parseDouble(entityList.get(i).longitude), 2);
            if (distance < minDistance) {
                minIndex = i;
                minDistance = distance;
            }
        }
        if (0 <= minIndex && minIndex < entityList.size()) {
            return entityList.get(minIndex);
        } else {
            return null;
        }
    }

    @NonNull
    public static List<ChineseCityEntity> selectChineseCityEntityList(
            @NonNull GeometricWeatherDatabase database,
            @NonNull String name) {
        if (TextUtils.isEmpty(name)) {
            return new ArrayList<>();
        }
        return getNonNullList(database.chineseCityDao().selectChineseCityEntityList(name));
    }

    public static int countChineseCityEntity(@NonNull GeometricWeatherDatabase database) {
        return database.chineseCityDao().countChineseCityEntity();
    }
}
