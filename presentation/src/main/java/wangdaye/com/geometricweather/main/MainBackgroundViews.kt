package wangdaye.com.geometricweather.main

import android.content.Context
import wangdaye.com.geometricweather.common.basic.models.Location

/**
 * Widget / notification / polling / shortcut side effects owned by `:app`.
 */
interface MainBackgroundViews {
    fun resetAllBackgroundTasks(context: Context, forceRefresh: Boolean)
    fun updateWidgetsAndNotifications(context: Context, locationList: List<Location>)
    fun refreshShortcuts(context: Context, locationList: List<Location>)
}

object MainBackgroundBridge {
    lateinit var callbacks: MainBackgroundViews
}
