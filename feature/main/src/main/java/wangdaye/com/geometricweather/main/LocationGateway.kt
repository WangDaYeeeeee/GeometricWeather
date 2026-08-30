package wangdaye.com.geometricweather.main

import android.content.Context
import wangdaye.com.geometricweather.common.basic.models.Location

/**
 * App-owned location SDK (Baidu/AMap/GMS flavor services) so `:feature:main` does
 * not depend on `:app` types.
 */
interface LocationGateway {

    interface OnRequestLocationListener {
        fun requestLocationSuccess(requestLocation: Location)
        fun requestLocationFailed(requestLocation: Location)
    }

    fun requestLocation(
        context: Context,
        location: Location,
        background: Boolean,
        listener: OnRequestLocationListener,
    )

    fun cancel()

    fun getPermissions(context: Context): Array<String>
}
