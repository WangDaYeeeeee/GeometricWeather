package wangdaye.com.geometricweather.location.services

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.common.utils.helpers.BuglyHelper
import wangdaye.com.geometricweather.location.utils.LocationException

class BaiduLocationService(context: Context) : LocationService() {

    private var locationCallback: LocationCallback? = null
    private val notificationManager = NotificationManagerCompat.from(context)
    private var baiduClient: LocationClient? = null

    private val baiduListener: BDAbstractLocationListener = object : BDAbstractLocationListener() {
        override fun onReceiveLocation(bdLocation: BDLocation) {
            cancel()
            val callback = locationCallback
            if (callback != null) {
                when (bdLocation.locType) {
                    61, 161 -> {
                        val result = Result(
                            bdLocation.latitude.toFloat(),
                            bdLocation.longitude.toFloat()
                        )
                        callback.onCompleted(result)
                    }
                    else -> {
                        BuglyHelper.report(
                            LocationException(
                                bdLocation.locType,
                                bdLocation.locTypeDescription
                            )
                        )
                        callback.onCompleted(null)
                    }
                }
            }
        }
    }

    override fun requestLocation(context: Context, callback: LocationCallback) {
        locationCallback = callback

        val option = LocationClientOption()
        option.locationMode = LocationClientOption.LocationMode.Battery_Saving
        option.setCoorType("wgs84")
        option.setScanSpan(0)
        option.setIsNeedAddress(true)
        option.isOpenGps = false
        option.setLocationNotify(false)
        option.setIsNeedLocationDescribe(false)
        option.setIsNeedLocationPoiList(false)
        option.setIgnoreKillProcess(false)
        option.SetIgnoreCacheException(true)
        option.setEnableSimulateGps(false)
        option.setWifiCacheTimeOut((5 * 60 * 1000).toLong())
        val client = LocationClient(context.applicationContext)
        baiduClient = client
        client.locOption = option
        client.registerLocationListener(baiduListener)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(getLocationNotificationChannel(context))
            client.enableLocInForeground(
                GeometricWeather.NOTIFICATION_ID_LOCATION,
                getLocationNotification(context)
            )
        }
        client.start()
    }

    override fun cancel() {
        val client = baiduClient
        if (client != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                client.disableLocInForeground(true)
            }
            client.stop()
            baiduClient = null
        }
    }

    override val permissions: Array<String>
        get() = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
}
