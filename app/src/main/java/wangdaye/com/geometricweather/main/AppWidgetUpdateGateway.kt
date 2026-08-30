package wangdaye.com.geometricweather.main

import android.content.Context
import android.os.Build
import wangdaye.com.geometricweather.background.polling.PollingManager
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.utils.helpers.ShortcutsHelper
import wangdaye.com.geometricweather.remoteviews.NotificationHelper
import wangdaye.com.geometricweather.remoteviews.WidgetHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppWidgetUpdateGateway @Inject constructor() : WidgetUpdateGateway {

    override fun updateNotificationIfNecessary(context: Context, locationList: List<Location>) {
        NotificationHelper.updateNotificationIfNecessary(context, locationList)
    }

    override fun updateWidgetsAndNotifications(context: Context, locationList: List<Location>) {
        if (locationList.isEmpty()) {
            return
        }
        WidgetHelper.updateWidgetIfNecessary(context, locationList[0])
        NotificationHelper.updateNotificationIfNecessary(context, locationList)
        WidgetHelper.updateWidgetIfNecessary(context, locationList)
    }

    override fun resetAllBackgroundTask(context: Context, forceRefresh: Boolean) {
        PollingManager.resetAllBackgroundTask(context, forceRefresh)
    }

    override fun refreshShortcuts(context: Context, locationList: List<Location>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutsHelper.refreshShortcutsInNewThread(context, locationList)
        }
    }
}
