package wangdaye.com.geometricweather.search

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.common.utils.helpers.AsyncHelper
import wangdaye.com.geometricweather.settings.ConfigStore
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.domain.weather.LocationSearcher
import javax.inject.Inject

private const val PREFERENCE_SEARCH_CONFIG = "SEARCH_CONFIG"
private const val KEY_DISABLED_SOURCES = "DISABLED_SOURCES"
private const val KEY_LAST_DEFAULT_SOURCE = "LAST_DEFAULT_SOURCE"
private const val DEFAULT_DISABLED_SOURCES_VALUE = "ENABLE_DEFAULT_SOURCE_ONLY"

class SearchActivityRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val locationSearcher: LocationSearcher
) {

    private val config: ConfigStore = ConfigStore.getInstance(context, PREFERENCE_SEARCH_CONFIG)

    private var validSourceCache: List<WeatherSource>? = null
    private var lastDefaultSourceCache: WeatherSource? = null

    fun searchLocationList(
        context: Context,
        query: String,
        enabledSources: List<WeatherSource>,
        callback: AsyncHelper.Callback<List<Location>>
    ) {
        locationSearcher.requestLocation(
            context,
            query,
            enabledSources,
            object : LocationSearcher.Listener {
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
        val cached = validSourceCache
        if (cached != null && defaultSource == lastDefaultSourceCache) {
            return cached
        }

        val totals = enumValues<WeatherSource>()
        if (totals.isEmpty()) {
            return emptyList()
        }

        val lastDefaultSource = config.getString(KEY_LAST_DEFAULT_SOURCE, "") ?: ""
        lastDefaultSourceCache = WeatherSource.getInstance(lastDefaultSource)

        val value: String
        if (defaultSource.id != lastDefaultSource) {
            value = DEFAULT_DISABLED_SOURCES_VALUE
            config.edit()
                .putString(KEY_DISABLED_SOURCES, value)
                .putString(KEY_LAST_DEFAULT_SOURCE, defaultSource.id)
                .apply()
        } else {
            value = config.getString(KEY_DISABLED_SOURCES, "") ?: ""
            config.edit()
                .putString(KEY_LAST_DEFAULT_SOURCE, defaultSource.id)
                .apply()
        }

        if (value.isEmpty()) {
            return totals.toList()
        }

        if (value == DEFAULT_DISABLED_SOURCES_VALUE) {
            return listOf(defaultSource)
        }

        val invalids = value.split(",").map { WeatherSource.getInstance(it) }.toSet()
        val validList = totals.filter { it !in invalids }
        validSourceCache = validList
        return validList
    }

    fun setValidWeatherSources(validList: List<WeatherSource>) {
        validSourceCache = validList

        val totals = enumValues<WeatherSource>()
        if (totals.isEmpty()) {
            return
        }

        val value = totals.filter { it !in validList }.joinToString(",") { it.id }
        config.edit().putString(KEY_DISABLED_SOURCES, value).apply()
    }

    fun cancel() {
        locationSearcher.cancel()
    }
}
