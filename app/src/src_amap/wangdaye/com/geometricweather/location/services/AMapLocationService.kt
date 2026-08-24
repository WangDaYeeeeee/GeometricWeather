package wangdaye.com.geometricweather.location.services

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.common.utils.helpers.BuglyHelper
import wangdaye.com.geometricweather.location.utils.LocationException

class AMapLocationService(context: Context) : LocationService() {

    private var locationCallback: LocationCallback? = null
    private val notificationManager = NotificationManagerCompat.from(context)
    private var amapClient: AMapLocationClient? = null

    private val amapListener = AMapLocationListener { aMapLocation ->
        cancel()
        val callback = locationCallback
        if (callback != null) {
            if (aMapLocation.errorCode == 0) {
                val result = Result(
                    aMapLocation.latitude.toFloat(),
                    aMapLocation.longitude.toFloat()
                )
                callback.onCompleted(result)
            } else {
                BuglyHelper.report(
                    LocationException(
                        aMapLocation.errorCode,
                        aMapLocation.errorInfo
                    )
                )
                callback.onCompleted(null)
            }
        }
    }

    override fun requestLocation(context: Context, callback: LocationCallback) {
        locationCallback = callback

        val option = AMapLocationClientOption()
        option.locationMode = AMapLocationClientOption.AMapLocationMode.Battery_Saving
        option.isOnceLocation = true
        option.isOnceLocationLatest = true
        option.isNeedAddress = true
        option.isMockEnable = false
        option.isLocationCacheEnable = false
        val client = AMapLocationClient(context.applicationContext)
        amapClient = client
        client.setLocationOption(option)
        client.setLocationListener(amapListener)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(getLocationNotificationChannel(context))
            client.enableBackgroundLocation(
                GeometricWeather.NOTIFICATION_ID_LOCATION,
                getLocationNotification(context)
            )
        }
        client.startLocation()
    }

    override fun cancel() {
        val client = amapClient
        if (client != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                client.disableBackgroundLocation(true)
            }
            client.stopLocation()
            client.onDestroy()
            amapClient = null
        }
    }

    override val permissions: Array<String>
        get() = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
}
