package wangdaye.com.geometricweather.wallpaper

import android.content.Context
import wangdaye.com.geometricweather.settings.ConfigStore

class LiveWallpaperConfigManager private constructor(context: Context) {

    val weatherKind: String
    val dayNightType: String

    init {
        val config = ConfigStore.getInstance(context, SP_LIVE_WALLPAPER_CONFIG)
        weatherKind = config.getString(KEY_WEATHER_KIND, "auto") ?: "auto"
        dayNightType = config.getString(KEY_DAY_NIGHT_TYPE, "auto") ?: "auto"
    }

    companion object {
        private const val SP_LIVE_WALLPAPER_CONFIG = "live_wallpaper_config"
        private const val KEY_WEATHER_KIND = "weather_kind"
        private const val KEY_DAY_NIGHT_TYPE = "day_night_type"

        @JvmStatic
        fun getInstance(context: Context): LiveWallpaperConfigManager {
            return LiveWallpaperConfigManager(context)
        }

        @JvmStatic
        fun update(context: Context, weatherKind: String, dayNightType: String) {
            ConfigStore.getInstance(context, SP_LIVE_WALLPAPER_CONFIG)
                .edit()
                .putString(KEY_WEATHER_KIND, weatherKind)
                .putString(KEY_DAY_NIGHT_TYPE, dayNightType)
                .apply()
        }
    }
}
