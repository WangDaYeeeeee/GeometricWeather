package wangdaye.com.geometricweather.location

import android.content.Context
import wangdaye.com.geometricweather.location.services.LocationService

/**
 * Flavor-specific GPS/SDK location services (native, Baidu, AMap) stay in `:app`.
 * `:data` [LocationHelper] uses this factory so it does not compile against flavor sources.
 */
interface FlavorLocationFactory {
    fun createAndroid(): LocationService
    fun createBaidu(context: Context): LocationService
    fun createAmap(context: Context): LocationService
}
