package wangdaye.com.geometricweather.db

import android.content.Context
import wangdaye.com.geometricweather.common.basic.models.ChineseCity
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.weather.History
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.common.utils.FileUtils
import wangdaye.com.geometricweather.db.controllers.AlertEntityController
import wangdaye.com.geometricweather.db.controllers.ChineseCityEntityController
import wangdaye.com.geometricweather.db.controllers.DailyEntityController
import wangdaye.com.geometricweather.db.controllers.HistoryEntityController
import wangdaye.com.geometricweather.db.controllers.HourlyEntityController
import wangdaye.com.geometricweather.db.controllers.LocationEntityController
import wangdaye.com.geometricweather.db.controllers.MinutelyEntityController
import wangdaye.com.geometricweather.db.controllers.WeatherEntityController
import wangdaye.com.geometricweather.db.generators.AlertEntityGenerator
import wangdaye.com.geometricweather.db.generators.ChineseCityEntityGenerator
import wangdaye.com.geometricweather.db.generators.DailyEntityGenerator
import wangdaye.com.geometricweather.db.generators.HistoryEntityGenerator
import wangdaye.com.geometricweather.db.generators.HourlyEntityGenerator
import wangdaye.com.geometricweather.db.generators.LocationEntityGenerator
import wangdaye.com.geometricweather.db.generators.MinutelyEntityGenerator
import wangdaye.com.geometricweather.db.generators.WeatherEntityGenerator

class DatabaseHelper private constructor(c: Context) {

    private val mDatabase: GeometricWeatherDatabase = GeometricWeatherDatabase.getInstance(c)
    private val mWritingLock = Any()

    // location.

    fun writeLocation(location: Location) {
        val entity = LocationEntityGenerator.generate(location)
        mDatabase.runInTransaction {
            if (LocationEntityController.selectLocationEntity(mDatabase, location.formattedId) == null) {
                LocationEntityController.insertLocationEntity(mDatabase, entity)
            } else {
                LocationEntityController.updateLocationEntity(mDatabase, entity)
            }
        }
    }

    fun writeLocationList(list: List<Location>) {
        mDatabase.runInTransaction {
            LocationEntityController.deleteLocationEntityList(mDatabase)
            LocationEntityController.insertLocationEntityList(
                mDatabase,
                LocationEntityGenerator.generateEntityList(list)
            )
        }
    }

    fun deleteLocation(location: Location) {
        LocationEntityController.deleteLocationEntity(
            mDatabase, LocationEntityGenerator.generate(location)
        )
    }

    fun readLocation(location: Location): Location? {
        return readLocation(location.formattedId)
    }

    fun readLocation(formattedId: String): Location? {
        val entity = LocationEntityController.selectLocationEntity(mDatabase, formattedId)
        return if (entity != null) {
            LocationEntityGenerator.generate(entity)
        } else {
            null
        }
    }

    fun readLocationList(): MutableList<Location> {
        val entityList = LocationEntityController.selectLocationEntityList(mDatabase).toMutableList()

        if (entityList.size == 0) {
            synchronized(mWritingLock) {
                if (countLocation() == 0) {
                    val entity = LocationEntityGenerator.generate(Location.buildLocal())
                    entityList.add(entity)

                    LocationEntityController.insertLocationEntityList(mDatabase, entityList)

                    return LocationEntityGenerator.generateModuleList(entityList).toMutableList()
                }
            }
        }

        return LocationEntityGenerator.generateModuleList(entityList).toMutableList()
    }

    fun countLocation(): Int {
        return LocationEntityController.countLocationEntity(mDatabase)
    }

    // weather.

    fun writeWeather(location: Location, weather: Weather) {
        mDatabase.runInTransaction {
            deleteWeather(location)

            WeatherEntityController.insertWeatherEntity(
                mDatabase,
                WeatherEntityGenerator.generate(location, weather)
            )
            DailyEntityController.insertDailyList(
                mDatabase,
                DailyEntityGenerator.generate(
                    location.cityId,
                    location.weatherSource,
                    weather.dailyForecast
                )
            )
            HourlyEntityController.insertHourlyList(
                mDatabase,
                HourlyEntityGenerator.generateEntityList(
                    location.cityId,
                    location.weatherSource,
                    weather.hourlyForecast
                )
            )
            MinutelyEntityController.insertMinutelyList(
                mDatabase,
                MinutelyEntityGenerator.generate(
                    location.cityId,
                    location.weatherSource,
                    weather.minutelyForecast
                )
            )
            AlertEntityController.insertAlertList(
                mDatabase,
                AlertEntityGenerator.generate(
                    location.cityId,
                    location.weatherSource,
                    weather.alertList
                )
            )
            HistoryEntityController.insertHistoryEntity(
                mDatabase,
                HistoryEntityGenerator.generate(
                    location.cityId, location.weatherSource, weather
                )
            )
            val yesterday = weather.yesterday
            if (yesterday != null) {
                HistoryEntityController.insertHistoryEntity(
                    mDatabase,
                    HistoryEntityGenerator.generate(
                        location.cityId, location.weatherSource, yesterday
                    )
                )
            }
        }
    }

    fun readWeather(location: Location): Weather? {
        val weatherEntity = WeatherEntityController.selectWeatherEntity(
            mDatabase, location.cityId, location.weatherSource
        ) ?: return null

        val historyEntity = HistoryEntityController.selectYesterdayHistoryEntity(
            mDatabase, location.cityId, location.weatherSource, weatherEntity.publishDate!!
        )

        return WeatherEntityGenerator.generate(
            weatherEntity,
            historyEntity,
            DailyEntityController.selectDailyEntityList(
                mDatabase, location.cityId, location.weatherSource
            ),
            HourlyEntityController.selectHourlyEntityList(
                mDatabase, location.cityId, location.weatherSource
            ),
            MinutelyEntityController.selectMinutelyEntityList(
                mDatabase, location.cityId, location.weatherSource
            ),
            AlertEntityController.selectLocationAlertEntity(
                mDatabase, location.cityId, location.weatherSource
            )
        )
    }

    fun deleteWeather(location: Location) {
        mDatabase.runInTransaction {
            WeatherEntityController.deleteWeather(
                mDatabase,
                WeatherEntityController.selectWeatherEntityList(
                    mDatabase,
                    location.cityId,
                    location.weatherSource
                )
            )
            HistoryEntityController.deleteLocationHistoryEntity(
                mDatabase,
                location.cityId,
                location.weatherSource
            )
            DailyEntityController.deleteDailyEntityList(
                mDatabase,
                location.cityId,
                location.weatherSource
            )
            HourlyEntityController.deleteHourlyEntityList(
                mDatabase,
                location.cityId,
                location.weatherSource
            )
            MinutelyEntityController.deleteMinutelyEntityList(
                mDatabase,
                location.cityId,
                location.weatherSource
            )
            AlertEntityController.deleteAlertList(
                mDatabase,
                location.cityId,
                location.weatherSource
            )
        }
    }

    // history.

    fun readHistory(location: Location, weather: Weather): History {
        return HistoryEntityGenerator.generate(
            HistoryEntityController.selectYesterdayHistoryEntity(
                mDatabase,
                location.cityId,
                location.weatherSource,
                weather.base.publishDate
            )
        )
    }

    // chinese city.

    fun ensureChineseCityList(context: Context) {
        if (countChineseCity() < 3216) {
            synchronized(mWritingLock) {
                if (countChineseCity() < 3216) {
                    val list = FileUtils.readCityList(context)

                    ChineseCityEntityController.deleteChineseCityEntityList(mDatabase)
                    ChineseCityEntityController.insertChineseCityEntityList(
                        mDatabase, ChineseCityEntityGenerator.generateEntityList(list)
                    )
                }
            }
        }
    }

    fun readChineseCity(name: String): ChineseCity? {
        val entity = ChineseCityEntityController.selectChineseCityEntity(mDatabase, name)
        return if (entity != null) {
            ChineseCityEntityGenerator.generate(entity)
        } else {
            null
        }
    }

    fun readChineseCity(province: String, city: String, district: String): ChineseCity? {
        val entity = ChineseCityEntityController.selectChineseCityEntity(
            mDatabase, province, city, district
        )
        return if (entity != null) {
            ChineseCityEntityGenerator.generate(entity)
        } else {
            null
        }
    }

    fun readChineseCity(latitude: Float, longitude: Float): ChineseCity? {
        val entity = ChineseCityEntityController.selectChineseCityEntity(
            mDatabase, latitude, longitude
        )
        return if (entity != null) {
            ChineseCityEntityGenerator.generate(entity)
        } else {
            null
        }
    }

    fun readChineseCityList(name: String): List<ChineseCity> {
        return ChineseCityEntityGenerator.generateModuleList(
            ChineseCityEntityController.selectChineseCityEntityList(mDatabase, name)
        )
    }

    fun countChineseCity(): Int {
        return ChineseCityEntityController.countChineseCityEntity(mDatabase)
    }

    companion object {
        @Volatile
        private var sInstance: DatabaseHelper? = null

        @JvmStatic
        fun getInstance(c: Context): DatabaseHelper {
            if (sInstance == null) {
                synchronized(DatabaseHelper::class.java) {
                    if (sInstance == null) {
                        sInstance = DatabaseHelper(c)
                    }
                }
            }
            return sInstance!!
        }
    }
}
