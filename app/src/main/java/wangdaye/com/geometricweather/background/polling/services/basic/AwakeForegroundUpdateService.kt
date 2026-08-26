package wangdaye.com.geometricweather.background.polling.services.basic

import android.content.Context
import dagger.hilt.android.AndroidEntryPoint
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.background.polling.PollingManager
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.remoteviews.NotificationHelper
import wangdaye.com.geometricweather.remoteviews.WidgetHelper

@AndroidEntryPoint
class AwakeForegroundUpdateService : ForegroundUpdateService() {

    override fun updateView(context: Context, location: Location) {
        WidgetHelper.updateWidgetIfNecessary(context, location)
    }

    override fun updateView(context: Context, locationList: List<Location>) {
        WidgetHelper.updateWidgetIfNecessary(context, locationList)
        NotificationHelper.updateNotificationIfNecessary(context, locationList)
    }

    override fun handlePollingResult(failed: Boolean) {
        PollingManager.resetAllBackgroundTask(this, false)
    }

    override val foregroundNotificationId: Int
        get() = GeometricWeather.NOTIFICATION_ID_UPDATING_AWAKE
}
