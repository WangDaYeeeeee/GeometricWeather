package wangdaye.com.geometricweather.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import wangdaye.com.geometricweather.common.basic.models.ChineseCity;
import wangdaye.com.geometricweather.common.basic.models.Location;
import wangdaye.com.geometricweather.common.basic.models.weather.History;
import wangdaye.com.geometricweather.common.basic.models.weather.Weather;
import wangdaye.com.geometricweather.common.utils.FileUtils;
import wangdaye.com.geometricweather.db.controllers.AlertEntityController;
import wangdaye.com.geometricweather.db.controllers.ChineseCityEntityController;
import wangdaye.com.geometricweather.db.controllers.DailyEntityController;
import wangdaye.com.geometricweather.db.controllers.HistoryEntityController;
import wangdaye.com.geometricweather.db.controllers.HourlyEntityController;
import wangdaye.com.geometricweather.db.controllers.LocationEntityController;
import wangdaye.com.geometricweather.db.controllers.MinutelyEntityController;
import wangdaye.com.geometricweather.db.controllers.WeatherEntityController;
import wangdaye.com.geometricweather.db.entities.ChineseCityEntity;
import wangdaye.com.geometricweather.db.entities.HistoryEntity;
import wangdaye.com.geometricweather.db.entities.LocationEntity;
import wangdaye.com.geometricweather.db.entities.WeatherEntity;
import wangdaye.com.geometricweather.db.generators.AlertEntityGenerator;
import wangdaye.com.geometricweather.db.generators.ChineseCityEntityGenerator;
import wangdaye.com.geometricweather.db.generators.DailyEntityGenerator;
import wangdaye.com.geometricweather.db.generators.HistoryEntityGenerator;
import wangdaye.com.geometricweather.db.generators.HourlyEntityGenerator;
import wangdaye.com.geometricweather.db.generators.LocationEntityGenerator;
import wangdaye.com.geometricweather.db.generators.MinutelyEntityGenerator;
import wangdaye.com.geometricweather.db.generators.WeatherEntityGenerator;

/**
 * Database helper.
 */
public class DatabaseHelper {

    private static volatile DatabaseHelper sInstance;

    public static DatabaseHelper getInstance(Context c) {
        if (sInstance == null) {
            synchronized (DatabaseHelper.class) {
                if (sInstance == null) {
                    sInstance = new DatabaseHelper(c);
                }
            }
        }
        return sInstance;
    }

    private final GeometricWeatherDatabase mDatabase;
    private final Object mWritingLock;

    private DatabaseHelper(Context c) {
        mDatabase = GeometricWeatherDatabase.getInstance(c);
        mWritingLock = new Object();
    }

    // location.

    public void writeLocation(@NonNull Location location) {
        LocationEntity entity = LocationEntityGenerator.generate(location);
        mDatabase.runInTransaction(() -> {
            if (LocationEntityController.selectLocationEntity(mDatabase, location.getFormattedId()) == null) {
                LocationEntityController.insertLocationEntity(mDatabase, entity);
            } else {
                LocationEntityController.updateLocationEntity(mDatabase, entity);
            }
        });
    }

    public void writeLocationList(@NonNull List<Location> list) {
        mDatabase.runInTransaction(() -> {
            LocationEntityController.deleteLocationEntityList(mDatabase);
            LocationEntityController.insertLocationEntityList(
                    mDatabase,
                    LocationEntityGenerator.generateEntityList(list)
            );
        });
    }

    public void deleteLocation(@NonNull Location location) {
        LocationEntityController.deleteLocationEntity(
                mDatabase, LocationEntityGenerator.generate(location));
    }

    @Nullable
    public Location readLocation(@NonNull Location location) {
        return readLocation(location.getFormattedId());
    }

    @Nullable
    public Location readLocation(@NonNull String formattedId) {
        LocationEntity entity = LocationEntityController.selectLocationEntity(mDatabase, formattedId);
        if (entity != null) {
            return LocationEntityGenerator.generate(entity);
        } else {
            return null;
        }
    }

    @NonNull
    public List<Location> readLocationList() {
        List<LocationEntity> entityList = LocationEntityController.selectLocationEntityList(mDatabase);

        if (entityList.size() == 0) {
            synchronized (mWritingLock) {
                if (countLocation() == 0) {
                    LocationEntity entity = LocationEntityGenerator.generate(
                            Location.buildLocal());
                    entityList.add(entity);

                    LocationEntityController.insertLocationEntityList(mDatabase, entityList);

                    return LocationEntityGenerator.generateModuleList(entityList);
                }
            }
        }

        return LocationEntityGenerator.generateModuleList(entityList);
    }

    public int countLocation() {
        return LocationEntityController.countLocationEntity(mDatabase);
    }

    // weather.

    public void writeWeather(@NonNull Location location, @NonNull Weather weather) {
        mDatabase.runInTransaction(() -> {
            deleteWeather(location);

            WeatherEntityController.insertWeatherEntity(
                    mDatabase,
                    WeatherEntityGenerator.generate(location, weather)
            );
            DailyEntityController.insertDailyList(
                    mDatabase,
                    DailyEntityGenerator.generate(
                            location.getCityId(),
                            location.getWeatherSource(),
                            weather.getDailyForecast()
                    )
            );
            HourlyEntityController.insertHourlyList(
                    mDatabase,
                    HourlyEntityGenerator.generateEntityList(
                            location.getCityId(),
                            location.getWeatherSource(),
                            weather.getHourlyForecast()
                    )
            );
            MinutelyEntityController.insertMinutelyList(
                    mDatabase,
                    MinutelyEntityGenerator.generate(
                            location.getCityId(),
                            location.getWeatherSource(),
                            weather.getMinutelyForecast()
                    )
            );
            AlertEntityController.insertAlertList(
                    mDatabase,
                    AlertEntityGenerator.generate(
                            location.getCityId(),
                            location.getWeatherSource(),
                            weather.getAlertList()
                    )
            );
            HistoryEntityController.insertHistoryEntity(
                    mDatabase,
                    HistoryEntityGenerator.generate(
                            location.getCityId(), location.getWeatherSource(), weather
                    )
            );
            if (weather.getYesterday() != null) {
                HistoryEntityController.insertHistoryEntity(
                        mDatabase,
                        HistoryEntityGenerator.generate(
                                location.getCityId(), location.getWeatherSource(), weather.getYesterday()
                        )
                );
            }
        });
    }

    @Nullable
    public Weather readWeather(@NonNull Location location) {
        WeatherEntity weatherEntity = WeatherEntityController.selectWeatherEntity(
                mDatabase, location.getCityId(), location.getWeatherSource());
        if (weatherEntity == null) {
            return null;
        }

        HistoryEntity historyEntity = HistoryEntityController.selectYesterdayHistoryEntity(
                mDatabase, location.getCityId(), location.getWeatherSource(), weatherEntity.publishDate);

        return WeatherEntityGenerator.generate(
                weatherEntity,
                historyEntity,
                DailyEntityController.selectDailyEntityList(
                        mDatabase, location.getCityId(), location.getWeatherSource()),
                HourlyEntityController.selectHourlyEntityList(
                        mDatabase, location.getCityId(), location.getWeatherSource()),
                MinutelyEntityController.selectMinutelyEntityList(
                        mDatabase, location.getCityId(), location.getWeatherSource()),
                AlertEntityController.selectLocationAlertEntity(
                        mDatabase, location.getCityId(), location.getWeatherSource())
        );
    }

    public void deleteWeather(@NonNull Location location) {
        mDatabase.runInTransaction(() -> {
            WeatherEntityController.deleteWeather(
                    mDatabase,
                    WeatherEntityController.selectWeatherEntityList(
                            mDatabase,
                            location.getCityId(),
                            location.getWeatherSource()
                    )
            );
            HistoryEntityController.deleteLocationHistoryEntity(
                    mDatabase,
                    location.getCityId(),
                    location.getWeatherSource()
            );
            DailyEntityController.deleteDailyEntityList(
                    mDatabase,
                    location.getCityId(),
                    location.getWeatherSource()
            );
            HourlyEntityController.deleteHourlyEntityList(
                    mDatabase,
                    location.getCityId(),
                    location.getWeatherSource()
            );
            MinutelyEntityController.deleteMinutelyEntityList(
                    mDatabase,
                    location.getCityId(),
                    location.getWeatherSource()
            );
            AlertEntityController.deleteAlertList(
                    mDatabase,
                    location.getCityId(),
                    location.getWeatherSource()
            );
        });
    }

    // history.

    public History readHistory(@NonNull Location location, @NonNull Weather weather) {
        return HistoryEntityGenerator.generate(
                HistoryEntityController.selectYesterdayHistoryEntity(
                        mDatabase,
                        location.getCityId(),
                        location.getWeatherSource(),
                        weather.getBase().getPublishDate()
                )
        );
    }

    // chinese city.

    public void ensureChineseCityList(Context context) {
        if (countChineseCity() < 3216) {
            synchronized (mWritingLock) {
                if (countChineseCity() < 3216) {
                    List<ChineseCity> list = FileUtils.readCityList(context);

                    ChineseCityEntityController.deleteChineseCityEntityList(mDatabase);
                    ChineseCityEntityController.insertChineseCityEntityList(
                            mDatabase, ChineseCityEntityGenerator.generateEntityList(list));
                }
            }
        }
    }

    @Nullable
    public ChineseCity readChineseCity(@NonNull String name) {
        ChineseCityEntity entity = ChineseCityEntityController.selectChineseCityEntity(mDatabase, name);
        if (entity != null) {
            return ChineseCityEntityGenerator.generate(entity);
        } else {
            return null;
        }
    }

    @Nullable
    public ChineseCity readChineseCity(@NonNull String province,
                                       @NonNull String city,
                                       @NonNull String district) {
        ChineseCityEntity entity = ChineseCityEntityController.selectChineseCityEntity(
                mDatabase, province, city, district);
        if (entity != null) {
            return ChineseCityEntityGenerator.generate(entity);
        } else {
            return null;
        }
    }

    @Nullable
    public ChineseCity readChineseCity(float latitude, float longitude) {
        ChineseCityEntity entity = ChineseCityEntityController.selectChineseCityEntity(
                mDatabase, latitude, longitude);
        if (entity != null) {
            return ChineseCityEntityGenerator.generate(entity);
        } else {
            return null;
        }
    }

    @NonNull
    public List<ChineseCity> readChineseCityList(@NonNull String name) {
        return ChineseCityEntityGenerator.generateModuleList(
                ChineseCityEntityController.selectChineseCityEntityList(mDatabase, name));
    }

    public int countChineseCity() {
        return ChineseCityEntityController.countChineseCityEntity(mDatabase);
    }
}
