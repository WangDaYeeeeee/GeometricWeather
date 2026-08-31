package wangdaye.com.geometricweather.main

import android.content.Context
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.utils.helpers.AsyncHelper

/**
 * Weather/location list operations used by [MainActivityViewModel].
 * Implemented in `:feature:main` with [wangdaye.com.geometricweather.location.LocationHelper] from `:data`.
 */
interface MainWeatherRepository {

    interface WeatherRequestCallback {
        fun onCompleted(
            location: Location,
            locationFailed: Boolean?,
            weatherRequestFailed: Boolean
        )
    }

    fun destroy()

    fun initLocations(context: Context, formattedId: String): List<Location>

    fun getWeatherCacheForLocations(
        context: Context,
        oldList: List<Location>,
        ignoredFormattedId: String,
        callback: AsyncHelper.Callback<List<Location>>
    )

    fun writeLocationList(context: Context, locationList: List<Location>)

    fun deleteLocation(context: Context, location: Location)

    fun getWeather(
        context: Context,
        location: Location,
        locate: Boolean,
        callback: WeatherRequestCallback,
    )

    fun getLocatePermissionList(context: Context): List<String>

    fun cancelWeatherRequest()
}
