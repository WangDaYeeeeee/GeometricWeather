package wangdaye.com.geometricweather.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import wangdaye.com.geometricweather.db.converters.DateConverter
import wangdaye.com.geometricweather.db.converters.TimeZoneConverter
import wangdaye.com.geometricweather.db.converters.WeatherCodeConverter
import wangdaye.com.geometricweather.db.converters.WeatherSourceConverter
import wangdaye.com.geometricweather.db.converters.WindDegreeConverter
import wangdaye.com.geometricweather.db.dao.AlertDao
import wangdaye.com.geometricweather.db.dao.ChineseCityDao
import wangdaye.com.geometricweather.db.dao.DailyDao
import wangdaye.com.geometricweather.db.dao.HistoryDao
import wangdaye.com.geometricweather.db.dao.HourlyDao
import wangdaye.com.geometricweather.db.dao.LocationDao
import wangdaye.com.geometricweather.db.dao.MinutelyDao
import wangdaye.com.geometricweather.db.dao.WeatherDao
import wangdaye.com.geometricweather.db.entities.AlertEntity
import wangdaye.com.geometricweather.db.entities.ChineseCityEntity
import wangdaye.com.geometricweather.db.entities.DailyEntity
import wangdaye.com.geometricweather.db.entities.HistoryEntity
import wangdaye.com.geometricweather.db.entities.HourlyEntity
import wangdaye.com.geometricweather.db.entities.LocationEntity
import wangdaye.com.geometricweather.db.entities.MinutelyEntity
import wangdaye.com.geometricweather.db.entities.WeatherEntity

@Database(
    entities = [
        AlertEntity::class,
        ChineseCityEntity::class,
        DailyEntity::class,
        HistoryEntity::class,
        HourlyEntity::class,
        LocationEntity::class,
        MinutelyEntity::class,
        WeatherEntity::class
    ],
    version = GeometricWeatherDatabase.VERSION,
    exportSchema = true
)
@TypeConverters(
    DateConverter::class,
    TimeZoneConverter::class,
    WeatherCodeConverter::class,
    WeatherSourceConverter::class,
    WindDegreeConverter::class
)
abstract class GeometricWeatherDatabase : RoomDatabase() {

    abstract fun alertDao(): AlertDao
    abstract fun chineseCityDao(): ChineseCityDao
    abstract fun dailyDao(): DailyDao
    abstract fun historyDao(): HistoryDao
    abstract fun hourlyDao(): HourlyDao
    abstract fun locationDao(): LocationDao
    abstract fun minutelyDao(): MinutelyDao
    abstract fun weatherDao(): WeatherDao

    companion object {
        const val VERSION = 63
        const val DATABASE_NAME = "Geometric_Weather_db"

        @Volatile
        private var instance: GeometricWeatherDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): GeometricWeatherDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    GeometricWeatherDatabase::class.java,
                    DATABASE_NAME
                )
                    .allowMainThreadQueries()
                    .addMigrations(*GreenDaoToRoomMigration.migrationsToRoom())
                    .build()
                    .also { instance = it }
            }
        }
    }
}
