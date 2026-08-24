package wangdaye.com.geometricweather.search

import android.content.Context
import android.text.TextUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.common.utils.helpers.AsyncHelper
import wangdaye.com.geometricweather.settings.ConfigStore
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.weather.WeatherHelper
import java.util.Arrays
import javax.inject.Inject

class SearchActivityRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val mWeatherHelper: WeatherHelper
) {

    private val mConfig: ConfigStore = ConfigStore.getInstance(context, PREFERENCE_SEARCH_CONFIG)

    private var mValidSourceCache: List<WeatherSource>? = null
    private var mLastDefaultSourceCache: WeatherSource? = null

    fun searchLocationList(
        context: Context,
        query: String,
        enabledSources: List<WeatherSource>,
        callback: AsyncHelper.Callback<List<Location>>
    ) {
        mWeatherHelper.requestLocation(
            context,
            query,
            enabledSources,
            object : WeatherHelper.OnRequestLocationListener {
                override fun requestLocationSuccess(query: String, locationList: List<Location>) {
                    callback.call(locationList, true)
                }

                override fun requestLocationFailed(query: String) {
                    callback.call(null, true)
                }
            }
        )
    }

    fun getValidWeatherSources(context: Context): List<WeatherSource> {
        val defaultSource = SettingsManager.getInstance(context).weatherSource

        if (mValidSourceCache != null && defaultSource == mLastDefaultSourceCache) {
            return mValidSourceCache!!
        }

        val totals = enumValues<WeatherSource>()
        if (totals.isEmpty()) {
            return ArrayList()
        }

        val lastDefaultSource = mConfig.getString(KEY_LAST_DEFAULT_SOURCE, "") ?: ""
        mLastDefaultSourceCache = WeatherSource.getInstance(lastDefaultSource)

        val value: String
        if (defaultSource.id != lastDefaultSource) {
            value = DEFAULT_DISABLED_SOURCES_VALUE
            mConfig.edit()
                .putString(KEY_DISABLED_SOURCES, value)
                .putString(KEY_LAST_DEFAULT_SOURCE, defaultSource.id)
                .apply()
        } else {
            value = mConfig.getString(KEY_DISABLED_SOURCES, "") ?: ""
            mConfig.edit()
                .putString(KEY_LAST_DEFAULT_SOURCE, defaultSource.id)
                .apply()
        }

        if (TextUtils.isEmpty(value)) {
            return Arrays.asList(*totals)
        }

        if (value == DEFAULT_DISABLED_SOURCES_VALUE) {
            return listOf(defaultSource)
        }

        val ids = value.split(",").toTypedArray()
        val invalids = Array(ids.size) { i -> WeatherSource.getInstance(ids[i]) }

        val validList = ArrayList<WeatherSource>()
        val invalidList = Arrays.asList(*invalids)
        for (source in totals) {
            if (!invalidList.contains(source)) {
                validList.add(source)
            }
        }

        mValidSourceCache = validList
        return validList
    }

    fun setValidWeatherSources(validList: List<WeatherSource>) {
        mValidSourceCache = validList

        val totals = enumValues<WeatherSource>()
        if (totals.isEmpty()) {
            return
        }

        val b = StringBuilder()
        for (source in totals) {
            if (!validList.contains(source)) {
                b.append(",").append(source.id)
            }
        }

        val value = if (b.isNotEmpty()) b.substring(1) else ""
        mConfig.edit().putString(KEY_DISABLED_SOURCES, value).apply()
    }

    fun cancel() {
        mWeatherHelper.cancel()
    }

    companion object {
        private const val PREFERENCE_SEARCH_CONFIG = "SEARCH_CONFIG"
        private const val KEY_DISABLED_SOURCES = "DISABLED_SOURCES"
        private const val KEY_LAST_DEFAULT_SOURCE = "LAST_DEFAULT_SOURCE"
        private const val DEFAULT_DISABLED_SOURCES_VALUE = "ENABLE_DEFAULT_SOURCE_ONLY"
    }
}
