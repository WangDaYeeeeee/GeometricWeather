package wangdaye.com.geometricweather.background.polling.services.basic

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import wangdaye.com.geometricweather.background.polling.PollingUpdateHelper
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.common.utils.helpers.AsyncHelper
import wangdaye.com.geometricweather.common.utils.helpers.ShortcutsHelper
import wangdaye.com.geometricweather.domain.usecase.LoadAllLocationsWithWeatherUseCase
import wangdaye.com.geometricweather.location.LocationHelper
import wangdaye.com.geometricweather.remoteviews.NotificationHelper
import wangdaye.com.geometricweather.weather.WeatherHelper
import javax.inject.Inject

abstract class UpdateService : Service(), PollingUpdateHelper.OnPollingUpdateListener {

    private var pollingHelper: PollingUpdateHelper? = null
    @Inject lateinit var locationHelper: LocationHelper
    @Inject lateinit var weatherHelper: WeatherHelper
    @Inject lateinit var loadAllLocationsWithWeather: LoadAllLocationsWithWeatherUseCase
    private var delayController: AsyncHelper.Controller? = null
    private var failed = false

    override fun onCreate() {
        super.onCreate()

        failed = false

        pollingHelper = PollingUpdateHelper(
            this,
            locationHelper,
            weatherHelper,
            loadAllLocationsWithWeather
        ).also {
            it.setOnPollingUpdateListener(this)
            it.pollingUpdate()
        }

        delayController = AsyncHelper.delayRunOnIO({ stopService(true) }, 30 * 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        delayController?.cancel()
        delayController = null
        pollingHelper?.setOnPollingUpdateListener(null)
        pollingHelper?.cancel()
        pollingHelper = null
    }

    override fun onBind(intent: Intent?): IBinder? = null

    abstract fun updateView(context: Context, location: Location)

    abstract fun updateView(context: Context, locationList: List<Location>)

    abstract fun handlePollingResult(updateSucceed: Boolean)

    open fun stopService(updateFailed: Boolean) {
        handlePollingResult(updateFailed)
        stopSelf()
    }

    override fun onUpdateCompleted(
        location: Location,
        old: Weather?,
        succeed: Boolean,
        index: Int,
        total: Int
    ) {
        if (index == 0) {
            updateView(this, location)
            if (succeed) {
                NotificationHelper.checkAndSendAlert(this, location, old)
                NotificationHelper.checkAndSendPrecipitationForecast(this, location)
            } else {
                failed = true
            }
        }
    }

    override fun onPollingCompleted(locationList: List<Location>?) {
        updateView(this, locationList ?: emptyList())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutsHelper.refreshShortcutsInNewThread(this, locationList ?: emptyList())
        }
        stopService(failed)
    }
}
