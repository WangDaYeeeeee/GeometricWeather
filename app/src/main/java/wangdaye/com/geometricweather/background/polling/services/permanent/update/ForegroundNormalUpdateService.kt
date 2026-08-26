package wangdaye.com.geometricweather.background.polling.services.permanent.update

import android.content.Context
import dagger.hilt.android.AndroidEntryPoint
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.background.polling.services.basic.ForegroundUpdateService
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.background.polling.services.permanent.PermanentServiceHelper
import wangdaye.com.geometricweather.remoteviews.NotificationHelper
import wangdaye.com.geometricweather.remoteviews.WidgetHelper

@AndroidEntryPoint
class ForegroundNormalUpdateService : ForegroundUpdateService() {

    override fun updateView(context: Context, location: Location) {
        WidgetHelper.updateWidgetIfNecessary(context, location)
    }

    override fun updateView(context: Context, locationList: List<Location>) {
        WidgetHelper.updateWidgetIfNecessary(context, locationList)
        NotificationHelper.updateNotificationIfNecessary(context, locationList)
    }

    override fun handlePollingResult(failed: Boolean) {
        PermanentServiceHelper.updatePollingService(this, failed)
    }

    override val foregroundNotificationId: Int
        get() = GeometricWeather.NOTIFICATION_ID_UPDATING_NORMALLY
}
