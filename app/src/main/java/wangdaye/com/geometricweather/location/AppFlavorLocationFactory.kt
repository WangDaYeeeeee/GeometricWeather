package wangdaye.com.geometricweather.location

import android.content.Context
import wangdaye.com.geometricweather.location.services.AMapLocationService
import wangdaye.com.geometricweather.location.services.AndroidLocationService
import wangdaye.com.geometricweather.location.services.BaiduLocationService
import wangdaye.com.geometricweather.location.services.LocationService
import javax.inject.Inject

class AppFlavorLocationFactory @Inject constructor() : FlavorLocationFactory {

    override fun createAndroid(): LocationService = AndroidLocationService()

    override fun createBaidu(context: Context): LocationService = BaiduLocationService(context)

    override fun createAmap(context: Context): LocationService = AMapLocationService(context)
}
