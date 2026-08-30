package wangdaye.com.geometricweather.main

import android.content.Context
import android.os.Build
import wangdaye.com.geometricweather.background.polling.PollingManager
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.utils.helpers.ShortcutsHelper
import wangdaye.com.geometricweather.remoteviews.NotificationHelper
import wangdaye.com.geometricweather.remoteviews.WidgetHelper
import javax.inject.Inject

class AppWidgetUpdateGateway @Inject constructor() : WidgetUpdateGateway {

    override fun resetAllBackgroundTasks(context: Context, forceRefresh: Boolean) {
        PollingManager.resetAllBackgroundTask(context, forceRefresh)
    }

    override fun updateNotificationIfNecessary(context: Context, locationList: List<Location>) {
        NotificationHelper.updateNotificationIfNecessary(context, locationList)
    }

    override fun updateWidgetsIfNecessary(context: Context, current: Location) {
        WidgetHelper.updateWidgetIfNecessary(context, current)
    }

    override fun updateWidgetsIfNecessary(context: Context, locationList: List<Location>) {
        WidgetHelper.updateWidgetIfNecessary(context, locationList)
    }

    override fun refreshShortcuts(context: Context, locationList: List<Location>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutsHelper.refreshShortcutsInNewThread(context, locationList)
        }
    }
}
