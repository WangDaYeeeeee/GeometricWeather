package wangdaye.com.geometricweather.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Copies existing GreenDAO tables into Room's schema without dropping user data.
 *
 * GreenDAO schemaVersion is 62 (`PRAGMA user_version`). Room uses version
 * [GeometricWeatherDatabase.VERSION] (63). Column names follow GreenDAO's
 * UPPER_SNAKE mapping; the integer primary key may be `ID` or `_id`.
 */
object GreenDaoToRoomMigration {

    private data class TableSpec(
        val name: String,
        val createSql: String,
        val indexSql: String? = null
    )

    private val TABLES = listOf(
        TableSpec(
            "LOCATION_ENTITY",
            "CREATE TABLE IF NOT EXISTS `LOCATION_ENTITY` (`FORMATTED_ID` TEXT NOT NULL, `CITY_ID` TEXT, `LATITUDE` REAL NOT NULL, `LONGITUDE` REAL NOT NULL, `TIME_ZONE` TEXT, `COUNTRY` TEXT, `PROVINCE` TEXT, `CITY` TEXT, `DISTRICT` TEXT, `WEATHER_SOURCE` TEXT, `CURRENT_POSITION` INTEGER NOT NULL, `RESIDENT_POSITION` INTEGER NOT NULL, `CHINA` INTEGER NOT NULL, PRIMARY KEY(`FORMATTED_ID`))"
        ),
        TableSpec(
            "WEATHER_ENTITY",
            "CREATE TABLE IF NOT EXISTS `WEATHER_ENTITY` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `CITY_ID` TEXT, `WEATHER_SOURCE` TEXT, `TIME_STAMP` INTEGER NOT NULL, `PUBLISH_DATE` INTEGER, `PUBLISH_TIME` INTEGER NOT NULL, `UPDATE_DATE` INTEGER, `UPDATE_TIME` INTEGER NOT NULL, `WEATHER_TEXT` TEXT, `WEATHER_CODE` TEXT, `TEMPERATURE` INTEGER NOT NULL, `REAL_FEEL_TEMPERATURE` INTEGER, `REAL_FEEL_SHADER_TEMPERATURE` INTEGER, `APPARENT_TEMPERATURE` INTEGER, `WIND_CHILL_TEMPERATURE` INTEGER, `WET_BULB_TEMPERATURE` INTEGER, `DEGREE_DAY_TEMPERATURE` INTEGER, `TOTAL_PRECIPITATION` REAL, `THUNDERSTORM_PRECIPITATION` REAL, `RAIN_PRECIPITATION` REAL, `SNOW_PRECIPITATION` REAL, `ICE_PRECIPITATION` REAL, `TOTAL_PRECIPITATION_PROBABILITY` REAL, `THUNDERSTORM_PRECIPITATION_PROBABILITY` REAL, `RAIN_PRECIPITATION_PROBABILITY` REAL, `SNOW_PRECIPITATION_PROBABILITY` REAL, `ICE_PRECIPITATION_PROBABILITY` REAL, `WIND_DIRECTION` TEXT, `WIND_DEGREE` REAL, `WIND_SPEED` REAL, `WIND_LEVEL` TEXT, `UV_INDEX` INTEGER, `UV_LEVEL` TEXT, `UV_DESCRIPTION` TEXT, `AQI_TEXT` TEXT, `AQI_INDEX` INTEGER, `PM25` REAL, `PM10` REAL, `SO2` REAL, `NO2` REAL, `O3` REAL, `CO` REAL, `RELATIVE_HUMIDITY` REAL, `PRESSURE` REAL, `VISIBILITY` REAL, `DEW_POINT` INTEGER, `CLOUD_COVER` INTEGER, `CEILING` REAL, `DAILY_FORECAST` TEXT, `HOURLY_FORECAST` TEXT)"
        ),
        TableSpec(
            "HISTORY_ENTITY",
            "CREATE TABLE IF NOT EXISTS `HISTORY_ENTITY` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `CITY_ID` TEXT, `WEATHER_SOURCE` TEXT, `DATE` INTEGER, `TIME` INTEGER NOT NULL, `DAYTIME_TEMPERATURE` INTEGER NOT NULL, `NIGHTTIME_TEMPERATURE` INTEGER NOT NULL)"
        ),
        TableSpec(
            "HOURLY_ENTITY",
            "CREATE TABLE IF NOT EXISTS `HOURLY_ENTITY` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `CITY_ID` TEXT, `WEATHER_SOURCE` TEXT, `DATE` INTEGER, `TIME` INTEGER NOT NULL, `DAYLIGHT` INTEGER NOT NULL, `WEATHER_TEXT` TEXT, `WEATHER_CODE` TEXT, `TEMPERATURE` INTEGER NOT NULL, `REAL_FEEL_TEMPERATURE` INTEGER, `REAL_FEEL_SHADER_TEMPERATURE` INTEGER, `APPARENT_TEMPERATURE` INTEGER, `WIND_CHILL_TEMPERATURE` INTEGER, `WET_BULB_TEMPERATURE` INTEGER, `DEGREE_DAY_TEMPERATURE` INTEGER, `TOTAL_PRECIPITATION` REAL, `THUNDERSTORM_PRECIPITATION` REAL, `RAIN_PRECIPITATION` REAL, `SNOW_PRECIPITATION` REAL, `ICE_PRECIPITATION` REAL, `TOTAL_PRECIPITATION_PROBABILITY` REAL, `THUNDERSTORM_PRECIPITATION_PROBABILITY` REAL, `RAIN_PRECIPITATION_PROBABILITY` REAL, `SNOW_PRECIPITATION_PROBABILITY` REAL, `ICE_PRECIPITATION_PROBABILITY` REAL, `WIND_DIRECTION` TEXT, `WIND_DEGREE` REAL, `WIND_SPEED` REAL, `WIND_LEVEL` TEXT, `UV_INDEX` INTEGER, `UV_LEVEL` TEXT, `UV_DESCRIPTION` TEXT)",
            "CREATE INDEX IF NOT EXISTS `index_HOURLY_ENTITY_CITY_ID_WEATHER_SOURCE` ON `HOURLY_ENTITY` (`CITY_ID`, `WEATHER_SOURCE`)"
        ),
        TableSpec(
            "MINUTELY_ENTITY",
            "CREATE TABLE IF NOT EXISTS `MINUTELY_ENTITY` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `CITY_ID` TEXT, `WEATHER_SOURCE` TEXT, `DATE` INTEGER, `TIME` INTEGER NOT NULL, `DAYLIGHT` INTEGER NOT NULL, `WEATHER_TEXT` TEXT, `WEATHER_CODE` TEXT, `MINUTE_INTERVAL` INTEGER NOT NULL, `DBZ` INTEGER, `CLOUD_COVER` INTEGER)",
            "CREATE INDEX IF NOT EXISTS `index_MINUTELY_ENTITY_CITY_ID_WEATHER_SOURCE` ON `MINUTELY_ENTITY` (`CITY_ID`, `WEATHER_SOURCE`)"
        ),
        TableSpec(
            "ALERT_ENTITY",
            "CREATE TABLE IF NOT EXISTS `ALERT_ENTITY` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `CITY_ID` TEXT, `WEATHER_SOURCE` TEXT, `ALERT_ID` INTEGER NOT NULL, `DATE` INTEGER, `TIME` INTEGER NOT NULL, `DESCRIPTION` TEXT, `CONTENT` TEXT, `TYPE` TEXT, `PRIORITY` INTEGER NOT NULL, `COLOR` INTEGER NOT NULL)",
            "CREATE INDEX IF NOT EXISTS `index_ALERT_ENTITY_CITY_ID_WEATHER_SOURCE` ON `ALERT_ENTITY` (`CITY_ID`, `WEATHER_SOURCE`)"
        ),
        TableSpec(
            "CHINESE_CITY_ENTITY",
            "CREATE TABLE IF NOT EXISTS `CHINESE_CITY_ENTITY` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `CITY_ID` TEXT, `PROVINCE` TEXT, `CITY` TEXT, `DISTRICT` TEXT, `LATITUDE` TEXT, `LONGITUDE` TEXT)"
        ),
        TableSpec(
            "DAILY_ENTITY",
            "CREATE TABLE IF NOT EXISTS `DAILY_ENTITY` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `CITY_ID` TEXT, `WEATHER_SOURCE` TEXT, `DATE` INTEGER, `TIME` INTEGER NOT NULL, `DAYTIME_WEATHER_TEXT` TEXT, `DAYTIME_WEATHER_PHASE` TEXT, `DAYTIME_WEATHER_CODE` TEXT, `DAYTIME_TEMPERATURE` INTEGER NOT NULL, `DAYTIME_REAL_FEEL_TEMPERATURE` INTEGER, `DAYTIME_REAL_FEEL_SHADER_TEMPERATURE` INTEGER, `DAYTIME_APPARENT_TEMPERATURE` INTEGER, `DAYTIME_WIND_CHILL_TEMPERATURE` INTEGER, `DAYTIME_WET_BULB_TEMPERATURE` INTEGER, `DAYTIME_DEGREE_DAY_TEMPERATURE` INTEGER, `DAYTIME_TOTAL_PRECIPITATION` REAL, `DAYTIME_THUNDERSTORM_PRECIPITATION` REAL, `DAYTIME_RAIN_PRECIPITATION` REAL, `DAYTIME_SNOW_PRECIPITATION` REAL, `DAYTIME_ICE_PRECIPITATION` REAL, `DAYTIME_TOTAL_PRECIPITATION_PROBABILITY` REAL, `DAYTIME_THUNDERSTORM_PRECIPITATION_PROBABILITY` REAL, `DAYTIME_RAIN_PRECIPITATION_PROBABILITY` REAL, `DAYTIME_SNOW_PRECIPITATION_PROBABILITY` REAL, `DAYTIME_ICE_PRECIPITATION_PROBABILITY` REAL, `DAYTIME_TOTAL_PRECIPITATION_DURATION` REAL, `DAYTIME_THUNDERSTORM_PRECIPITATION_DURATION` REAL, `DAYTIME_RAIN_PRECIPITATION_DURATION` REAL, `DAYTIME_SNOW_PRECIPITATION_DURATION` REAL, `DAYTIME_ICE_PRECIPITATION_DURATION` REAL, `DAYTIME_WIND_DIRECTION` TEXT, `DAYTIME_WIND_DEGREE` REAL, `DAYTIME_WIND_SPEED` REAL, `DAYTIME_WIND_LEVEL` TEXT, `DAYTIME_CLOUD_COVER` INTEGER, `NIGHTTIME_WEATHER_TEXT` TEXT, `NIGHTTIME_WEATHER_PHASE` TEXT, `NIGHTTIME_WEATHER_CODE` TEXT, `NIGHTTIME_TEMPERATURE` INTEGER NOT NULL, `NIGHTTIME_REAL_FEEL_TEMPERATURE` INTEGER, `NIGHTTIME_REAL_FEEL_SHADER_TEMPERATURE` INTEGER, `NIGHTTIME_APPARENT_TEMPERATURE` INTEGER, `NIGHTTIME_WIND_CHILL_TEMPERATURE` INTEGER, `NIGHTTIME_WET_BULB_TEMPERATURE` INTEGER, `NIGHTTIME_DEGREE_DAY_TEMPERATURE` INTEGER, `NIGHTTIME_TOTAL_PRECIPITATION` REAL, `NIGHTTIME_THUNDERSTORM_PRECIPITATION` REAL, `NIGHTTIME_RAIN_PRECIPITATION` REAL, `NIGHTTIME_SNOW_PRECIPITATION` REAL, `NIGHTTIME_ICE_PRECIPITATION` REAL, `NIGHTTIME_TOTAL_PRECIPITATION_PROBABILITY` REAL, `NIGHTTIME_THUNDERSTORM_PRECIPITATION_PROBABILITY` REAL, `NIGHTTIME_RAIN_PRECIPITATION_PROBABILITY` REAL, `NIGHTTIME_SNOW_PRECIPITATION_PROBABILITY` REAL, `NIGHTTIME_ICE_PRECIPITATION_PROBABILITY` REAL, `NIGHTTIME_TOTAL_PRECIPITATION_DURATION` REAL, `NIGHTTIME_THUNDERSTORM_PRECIPITATION_DURATION` REAL, `NIGHTTIME_RAIN_PRECIPITATION_DURATION` REAL, `NIGHTTIME_SNOW_PRECIPITATION_DURATION` REAL, `NIGHTTIME_ICE_PRECIPITATION_DURATION` REAL, `NIGHTTIME_WIND_DIRECTION` TEXT, `NIGHTTIME_WIND_DEGREE` REAL, `NIGHTTIME_WIND_SPEED` REAL, `NIGHTTIME_WIND_LEVEL` TEXT, `NIGHTTIME_CLOUD_COVER` INTEGER, `SUN_RISE_DATE` INTEGER, `SUN_SET_DATE` INTEGER, `MOON_RISE_DATE` INTEGER, `MOON_SET_DATE` INTEGER, `MOON_PHASE_ANGLE` INTEGER, `MOON_PHASE_DESCRIPTION` TEXT, `AQI_TEXT` TEXT, `AQI_INDEX` INTEGER, `PM25` REAL, `PM10` REAL, `SO2` REAL, `NO2` REAL, `O3` REAL, `CO` REAL, `GRASS_INDEX` INTEGER, `GRASS_LEVEL` INTEGER, `GRASS_DESCRIPTION` TEXT, `MOLD_INDEX` INTEGER, `MOLD_LEVEL` INTEGER, `MOLD_DESCRIPTION` TEXT, `RAGWEED_INDEX` INTEGER, `RAGWEED_LEVEL` INTEGER, `RAGWEED_DESCRIPTION` TEXT, `TREE_INDEX` INTEGER, `TREE_LEVEL` INTEGER, `TREE_DESCRIPTION` TEXT, `UV_INDEX` INTEGER, `UV_LEVEL` TEXT, `UV_DESCRIPTION` TEXT, `HOURS_OF_SUN` REAL NOT NULL)",
            "CREATE INDEX IF NOT EXISTS `index_DAILY_ENTITY_CITY_ID_WEATHER_SOURCE` ON `DAILY_ENTITY` (`CITY_ID`, `WEATHER_SOURCE`)"
        )
    )

    fun migrate(database: SupportSQLiteDatabase) {
        for (spec in TABLES) {
            rebuildTable(database, spec)
        }
    }

    fun migrationsToRoom(): Array<Migration> {
        return (1 until GeometricWeatherDatabase.VERSION).map { from ->
            object : Migration(from, GeometricWeatherDatabase.VERSION) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    migrate(db)
                }
            }
        }.toTypedArray()
    }

    private fun rebuildTable(database: SupportSQLiteDatabase, spec: TableSpec) {
        if (!tableExists(database, spec.name)) {
            database.execSQL(spec.createSql)
            spec.indexSql?.let { database.execSQL(it) }
            return
        }
        val backup = spec.name + "_GDAO_OLD"
        dropTableIfExists(database, backup)
        database.execSQL("ALTER TABLE `${spec.name}` RENAME TO `$backup`")
        database.execSQL(spec.createSql)
        copyRows(database, backup, spec.name)
        database.execSQL("DROP TABLE `$backup`")
        spec.indexSql?.let { database.execSQL(it) }
    }

    private fun copyRows(database: SupportSQLiteDatabase, from: String, to: String) {
        val sourceColumns = columnNames(database, from)
        val destColumns = columnInfo(database, to)
        if (destColumns.isEmpty() || sourceColumns.isEmpty()) {
            return
        }
        val insertCols = mutableListOf<String>()
        val selectExprs = mutableListOf<String>()
        for (dest in destColumns) {
            val sourceName = resolveSourceColumn(sourceColumns, dest.name)
            if (sourceName != null) {
                insertCols.add(dest.name)
                selectExprs.add("`$sourceName`")
            } else if (dest.notNull && dest.name != "ID") {
                insertCols.add(dest.name)
                selectExprs.add(defaultLiteral(dest.type))
            }
        }
        if (insertCols.isEmpty()) {
            return
        }
        val sql = "INSERT INTO `$to` (${insertCols.joinToString { "`$it`" }}) " +
            "SELECT ${selectExprs.joinToString()} FROM `$from`"
        database.execSQL(sql)
    }

    fun resolveSourceColumn(sourceColumns: Set<String>, dest: String): String? {
        val byUpper = sourceColumns.associateBy { it.uppercase() }
        byUpper[dest.uppercase()]?.let { return it }
        if (dest.uppercase() == "ID") {
            listOf("_ID", "ID").forEach { candidate ->
                byUpper[candidate]?.let { return it }
            }
        }
        return null
    }

    private fun defaultLiteral(type: String): String {
        val upper = type.uppercase()
        return when {
            upper.contains("INT") -> "0"
            upper.contains("REAL") || upper.contains("FLOA") || upper.contains("DOUB") -> "0"
            else -> "''"
        }
    }

    private fun tableExists(database: SupportSQLiteDatabase, name: String): Boolean {
        database.query(
            "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ? LIMIT 1",
            arrayOf(name)
        ).use { cursor ->
            return cursor.moveToFirst()
        }
    }

    private fun dropTableIfExists(database: SupportSQLiteDatabase, name: String) {
        database.execSQL("DROP TABLE IF EXISTS `$name`")
    }

    private fun columnNames(database: SupportSQLiteDatabase, table: String): Set<String> {
        val names = mutableSetOf<String>()
        database.query("PRAGMA table_info(`$table`)").use { cursor ->
            val nameIndex = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) {
                names.add(cursor.getString(nameIndex))
            }
        }
        return names
    }

    private data class ColumnMeta(val name: String, val type: String, val notNull: Boolean)

    private fun columnInfo(database: SupportSQLiteDatabase, table: String): List<ColumnMeta> {
        val columns = mutableListOf<ColumnMeta>()
        database.query("PRAGMA table_info(`$table`)").use { cursor ->
            val nameIndex = cursor.getColumnIndex("name")
            val typeIndex = cursor.getColumnIndex("type")
            val notNullIndex = cursor.getColumnIndex("notnull")
            while (cursor.moveToNext()) {
                columns.add(
                    ColumnMeta(
                        name = cursor.getString(nameIndex),
                        type = cursor.getString(typeIndex) ?: "",
                        notNull = cursor.getInt(notNullIndex) != 0
                    )
                )
            }
        }
        return columns
    }
}
