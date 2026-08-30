package wangdaye.com.geometricweather.main

import android.content.Context
import wangdaye.com.geometricweather.common.basic.models.Location

/**
 * App-owned widget / notification / shortcut / polling side effects so
 * `:feature:main` does not import RemoteViews presenters or `:app` types.
 */
interface WidgetUpdateGateway {
    fun resetAllBackgroundTasks(context: Context, forceRefresh: Boolean)
    fun updateNotificationIfNecessary(context: Context, locationList: List<Location>)
    fun updateWidgetsIfNecessary(context: Context, current: Location)
    fun updateWidgetsIfNecessary(context: Context, locationList: List<Location>)
    fun refreshShortcuts(context: Context, locationList: List<Location>)
}
