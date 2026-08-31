package wangdaye.com.geometricweather.background.polling.work.worker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import wangdaye.com.geometricweather.background.polling.PollingUpdateHelper
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.common.utils.helpers.ShortcutsHelper
import wangdaye.com.geometricweather.domain.usecase.LoadAllLocationsWithWeatherUseCase
import wangdaye.com.geometricweather.location.LocationHelper
import wangdaye.com.geometricweather.remoteviews.NotificationHelper
import wangdaye.com.geometricweather.weather.WeatherHelper
import kotlin.coroutines.resume

abstract class AsyncUpdateWorker(
    context: Context,
    workerParams: WorkerParameters,
    locationHelper: LocationHelper,
    weatherHelper: WeatherHelper,
    loadAllLocationsWithWeather: LoadAllLocationsWithWeatherUseCase
) : CoroutineWorker(context, workerParams), PollingUpdateHelper.OnPollingUpdateListener {

    private val pollingUpdateHelper = PollingUpdateHelper(
        context,
        locationHelper,
        weatherHelper,
        loadAllLocationsWithWeather
    ).also {
        it.setOnPollingUpdateListener(this)
    }

    private var continuation: CancellableContinuation<Result>? = null
    private var failed = false

    override suspend fun doWork(): Result = suspendCancellableCoroutine { cont ->
        continuation = cont
        failed = false
        cont.invokeOnCancellation { pollingUpdateHelper.cancel() }
        pollingUpdateHelper.pollingUpdate()
    }

    abstract fun updateView(context: Context, location: Location)

    abstract fun updateView(context: Context, locationList: List<Location>)

    abstract fun handleUpdateResult(failed: Boolean): Result

    override fun onUpdateCompleted(
        location: Location,
        old: Weather?,
        succeed: Boolean,
        index: Int,
        total: Int
    ) {
        if (index == 0) {
            updateView(applicationContext, location)
            if (succeed) {
                NotificationHelper.checkAndSendAlert(applicationContext, location, old)
                NotificationHelper.checkAndSendPrecipitationForecast(applicationContext, location)
            } else {
                failed = true
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onPollingCompleted(locationList: List<Location>?) {
        updateView(applicationContext, locationList ?: emptyList())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutsHelper.refreshShortcutsInNewThread(
                applicationContext,
                locationList ?: emptyList()
            )
        }
        val cont = continuation
        if (cont != null && cont.isActive) {
            cont.resume(handleUpdateResult(failed))
        }
    }
}
