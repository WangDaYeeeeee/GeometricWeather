package wangdaye.com.geometricweather.db.controllers;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource;
import wangdaye.com.geometricweather.db.GeometricWeatherDatabase;
import wangdaye.com.geometricweather.db.converters.WeatherSourceConverter;
import wangdaye.com.geometricweather.db.entities.HistoryEntity;

public class HistoryEntityController extends AbsEntityController {

    public static void insertHistoryEntity(@NonNull GeometricWeatherDatabase database,
                                           @NonNull HistoryEntity entity) {
        database.historyDao().insertHistoryEntity(entity);
    }

    public static void deleteLocationHistoryEntity(@NonNull GeometricWeatherDatabase database,
                                                   @NonNull String cityId,
                                                   @NonNull WeatherSource source) {
        database.historyDao().deleteLocationHistoryEntity(
                cityId,
                new WeatherSourceConverter().convertToDatabaseValue(source)
        );
    }

    @SuppressLint("SimpleDateFormat")
    @Nullable
    public static HistoryEntity selectYesterdayHistoryEntity(
            @NonNull GeometricWeatherDatabase database,
            @NonNull String cityId,
            @NonNull WeatherSource source,
            @NonNull Date currentDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date today = format.parse(format.format(currentDate));
            if (today == null) {
                throw new NullPointerException("Get null Date object.");
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);
            calendar.add(Calendar.DATE, -1);
            Date yesterday = calendar.getTime();

            return database.historyDao().selectHistoryEntity(
                    cityId,
                    new WeatherSourceConverter().convertToDatabaseValue(source),
                    yesterday,
                    today
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @NonNull
    public static List<HistoryEntity> selectHistoryEntityList(
            @NonNull GeometricWeatherDatabase database,
            @NonNull String cityId,
            @NonNull WeatherSource source) {
        return getNonNullList(
                database.historyDao().selectHistoryEntityList(
                        cityId,
                        new WeatherSourceConverter().convertToDatabaseValue(source)
                )
        );
    }
}
