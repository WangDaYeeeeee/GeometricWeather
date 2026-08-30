package wangdaye.com.geometricweather.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.provider.LocationProvider
import wangdaye.com.geometricweather.common.utils.NetworkUtils
import wangdaye.com.geometricweather.db.DatabaseHelper
import wangdaye.com.geometricweather.location.services.LocationService
import wangdaye.com.geometricweather.location.services.ip.BaiduIPLocationService
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.weather.WeatherServiceSet
import wangdaye.com.geometricweather.weather.services.WeatherService
import java.util.TimeZone
import javax.inject.Inject

class LocationHelper @Inject constructor(
    baiduIPService: BaiduIPLocationService,
    @AndroidLocation androidLocationService: LocationService,
    @BaiduLocation baiduLocationService: LocationService,
    @AMapLocation amapLocationService: LocationService,
    private val weatherServiceSet: WeatherServiceSet
) {

    private val locationServices: Array<LocationService> = arrayOf(
        androidLocationService,
        baiduLocationService,
        baiduIPService,
        amapLocationService
    )

    interface OnRequestLocationListener {
        fun requestLocationSuccess(requestLocation: Location)
        fun requestLocationFailed(requestLocation: Location)
    }

    private fun getLocationService(provider: LocationProvider): LocationService {
        return when (provider) {
            LocationProvider.BAIDU -> locationServices[1]
            LocationProvider.BAIDU_IP -> locationServices[2]
            LocationProvider.AMAP -> locationServices[3]
            else -> locationServices[0]
        }
    }

    fun requestLocation(
        context: Context,
        location: Location,
        background: Boolean,
        l: OnRequestLocationListener
    ) {
        val usableCheckListener = object : OnRequestLocationListener {
            override fun requestLocationSuccess(requestLocation: Location) {
                l.requestLocationSuccess(requestLocation)
            }

            override fun requestLocationFailed(requestLocation: Location) {
                if (requestLocation.isUsable) {
                    l.requestLocationFailed(requestLocation)
                } else {
                    val finalLocation = Location.copy(
                        Location.buildDefaultLocation(
                            SettingsManager.getInstance(context).weatherSource
                        ),
                        true,
                        false
                    )
                    DatabaseHelper.getInstance(context).writeLocation(finalLocation)
                    l.requestLocationFailed(finalLocation)
                }
            }
        }

        val provider = SettingsManager.getInstance(context).locationProvider
        val service = getLocationService(provider)
        if (service.permissions.isNotEmpty()) {
            if (!NetworkUtils.isAvailable(context) || (
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                    )
            ) {
                usableCheckListener.requestLocationFailed(location)
                return
            }
            if (background) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    usableCheckListener.requestLocationFailed(location)
                    return
                }
            }
        }

        service.requestLocation(
            context,
            object : LocationService.LocationCallback {
                override fun onCompleted(result: LocationService.Result?) {
                    if (result == null) {
                        usableCheckListener.requestLocationFailed(location)
                        return
                    }
                    requestAvailableWeatherLocation(
                        context,
                        Location.copy(
                            location,
                            result.latitude,
                            result.longitude,
                            TimeZone.getDefault()
                        ),
                        usableCheckListener
                    )
                }
            }
        )
    }

    private fun requestAvailableWeatherLocation(
        context: Context,
        location: Location,
        l: OnRequestLocationListener
    ) {
        val source = SettingsManager.getInstance(context).weatherSource
        val service = weatherServiceSet.get(source)
        service.requestLocation(
            context,
            location,
            object : WeatherService.RequestLocationCallback {
                override fun requestLocationSuccess(query: String, locationList: List<Location>) {
                    if (locationList.isNotEmpty()) {
                        val src = locationList[0]
                        val result = Location.copy(src, true, src.isResidentPosition)
                        DatabaseHelper.getInstance(context).writeLocation(result)
                        l.requestLocationSuccess(result)
                    } else {
                        requestLocationFailed(query)
                    }
                }

                override fun requestLocationFailed(query: String) {
                    l.requestLocationFailed(location)
                }
            }
        )
    }

    fun cancel() {
        for (s in locationServices) {
            s.cancel()
        }
        for (s in weatherServiceSet.getAll()) {
            s.cancel()
        }
    }

    fun getPermissions(context: Context): Array<String> {
        val provider = SettingsManager.getInstance(context).locationProvider
        val service = getLocationService(provider)
        val permissions = service.permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || permissions.isEmpty()) {
            return permissions
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return permissions + Manifest.permission.ACCESS_BACKGROUND_LOCATION
        }
        return permissions
    }
}
