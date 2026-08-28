package wangdaye.com.geometricweather.common.basic

import android.content.Context
import java.util.Locale

/**
 * Application-owned activity stack used by [GeoActivity] without depending on `:app`.
 */
interface GeoActivityHost {
    fun addActivity(activity: GeoActivity)
    fun removeActivity(activity: GeoActivity)
    fun setTopActivity(activity: GeoActivity)
    fun checkToCleanTopActivity(activity: GeoActivity)
    val topActivity: GeoActivity?
    fun recreateAllActivities()
}

object GeoApp {
    @JvmStatic
    lateinit var activityHost: GeoActivityHost

    @JvmStatic
    var languageLocale: (Context) -> Locale = { Locale.getDefault() }
}

object ApplicationContextHolder {
    @Volatile
    lateinit var application: Context
        private set

    @JvmStatic
    fun install(context: Context) {
        application = context.applicationContext
    }
}
