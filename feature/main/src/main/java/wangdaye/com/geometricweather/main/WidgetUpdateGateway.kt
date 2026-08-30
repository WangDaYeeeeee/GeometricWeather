package wangdaye.com.geometricweather.main

import android.content.Context
import wangdaye.com.geometricweather.common.basic.models.Location

/**
 * App-owned widget / notification / shortcut side effects so `:feature:main`
 * does not import RemoteViews presenters or AppWidget providers.
 */
interface WidgetUpdateGateway {
    fun updateNotificationIfNecessary(context: Context, locationList: List<Location>)
    fun updateWidgetsAndNotifications(context: Context, locationList: List<Location>)
    fun resetAllBackgroundTask(context: Context, forceRefresh: Boolean)
    fun refreshShortcuts(context: Context, locationList: List<Location>)
}
