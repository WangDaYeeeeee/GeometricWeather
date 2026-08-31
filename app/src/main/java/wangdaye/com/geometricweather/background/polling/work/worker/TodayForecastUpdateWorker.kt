package wangdaye.com.geometricweather.background.polling.work.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import wangdaye.com.geometricweather.background.polling.PollingManager
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.domain.usecase.LoadAllLocationsWithWeatherUseCase
import wangdaye.com.geometricweather.location.LocationHelper
import wangdaye.com.geometricweather.remoteviews.presenters.notification.ForecastNotificationIMP
import wangdaye.com.geometricweather.weather.WeatherHelper

@HiltWorker
class TodayForecastUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    locationHelper: LocationHelper,
    weatherHelper: WeatherHelper,
    loadAllLocationsWithWeather: LoadAllLocationsWithWeatherUseCase
) : AsyncUpdateWorker(context, workerParams, locationHelper, weatherHelper, loadAllLocationsWithWeather) {

    override fun updateView(context: Context, location: Location) {
        if (ForecastNotificationIMP.isEnable(context, true)) {
            ForecastNotificationIMP.buildForecastAndSendIt(context, location, true)
        }
    }

    override fun updateView(context: Context, locationList: List<Location>) {
    }

    override fun handleUpdateResult(failed: Boolean): Result {
        PollingManager.resetTodayForecastBackgroundTask(
            applicationContext, false, true
        )
        return if (failed) Result.failure() else Result.success()
    }
}
