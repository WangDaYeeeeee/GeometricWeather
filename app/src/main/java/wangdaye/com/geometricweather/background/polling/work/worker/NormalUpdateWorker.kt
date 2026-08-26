package wangdaye.com.geometricweather.background.polling.work.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.location.LocationHelper
import wangdaye.com.geometricweather.remoteviews.NotificationHelper
import wangdaye.com.geometricweather.remoteviews.WidgetHelper
import wangdaye.com.geometricweather.weather.WeatherHelper

@HiltWorker
class NormalUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    locationHelper: LocationHelper,
    weatherHelper: WeatherHelper
) : AsyncUpdateWorker(context, workerParams, locationHelper, weatherHelper) {

    override fun updateView(context: Context, location: Location) {
        WidgetHelper.updateWidgetIfNecessary(context, location)
    }

    override fun updateView(context: Context, locationList: List<Location>) {
        WidgetHelper.updateWidgetIfNecessary(context, locationList)
        NotificationHelper.updateNotificationIfNecessary(context, locationList)
    }

    override fun handleUpdateResult(failed: Boolean): Result {
        return if (failed) Result.retry() else Result.success()
    }
}
