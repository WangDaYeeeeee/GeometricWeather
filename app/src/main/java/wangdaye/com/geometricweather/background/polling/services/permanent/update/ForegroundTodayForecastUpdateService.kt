package wangdaye.com.geometricweather.background.polling.services.permanent.update

import android.content.Context
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.background.polling.services.basic.ForegroundUpdateService
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.remoteviews.presenters.notification.ForecastNotificationIMP

@AndroidEntryPoint
class ForegroundTodayForecastUpdateService : ForegroundUpdateService() {

    override fun updateView(context: Context, location: Location) {
        if (ForecastNotificationIMP.isEnable(this, true)) {
            ForecastNotificationIMP.buildForecastAndSendIt(context, location, true)
        }
    }

    override fun updateView(context: Context, locationList: List<Location>) {
    }

    override fun handlePollingResult(failed: Boolean) {
        // do nothing.
    }

    override fun getForegroundNotification(total: Int): NotificationCompat.Builder {
        return super.getForegroundNotification(total).setContentTitle(
            getString(R.string.geometric_weather) + " " + getString(R.string.forecast)
        )
    }

    override val foregroundNotificationId: Int
        get() = GeometricWeather.NOTIFICATION_ID_UPDATING_TODAY_FORECAST
}
