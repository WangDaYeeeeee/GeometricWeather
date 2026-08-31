package wangdaye.com.geometricweather.weather

import android.content.Context
import androidx.preference.PreferenceManager
import wangdaye.com.geometricweather.common.basic.models.options.appearance.Language
import wangdaye.com.geometricweather.common.basic.models.options.provider.LocationProvider
import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.common.basic.models.options.unit.PrecipitationUnit
import wangdaye.com.geometricweather.data.BuildConfig

/**
 * Reads weather-provider keys, weather source, location provider, language, and
 * precipitation unit from the same default SharedPreferences that
 * [wangdaye.com.geometricweather.settings.SettingsManager] writes. Lives in `:data`
 * so network and location code do not depend on `:presentation`.
 */
class WeatherProviderSettings private constructor(context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    val languageCode: String
        get() = Language.getInstance(prefs.getString(KEY_LANGUAGE, "follow_system") ?: "").code

    val weatherSource: WeatherSource
        get() = WeatherSource.getInstance(
            prefs.getString(KEY_WEATHER_SOURCE, "accu") ?: ""
        )

    val locationProvider: LocationProvider
        get() = LocationProvider.getInstance(
            prefs.getString(KEY_LOCATION_SERVICE, "native") ?: ""
        )

    val precipitationUnit: PrecipitationUnit
        get() = PrecipitationUnit.getInstance(
            prefs.getString(KEY_PRECIPITATION_UNIT, "mm") ?: ""
        )

    val providerAccuWeatherKey: String
        get() = resolveProviderKey(
            prefs.getString(KEY_ACCU_WEATHER, "") ?: "",
            BuildConfig.ACCU_WEATHER_KEY
        )

    val providerAccuCurrentKey: String
        get() = resolveProviderKey(
            prefs.getString(KEY_ACCU_CURRENT, "") ?: "",
            BuildConfig.ACCU_CURRENT_KEY
        )

    val providerAccuAqiKey: String
        get() = resolveProviderKey(
            prefs.getString(KEY_ACCU_AQI, "") ?: "",
            BuildConfig.ACCU_AQI_KEY
        )

    val providerOwmKey: String
        get() = resolveProviderKey(
            prefs.getString(KEY_OWM, "") ?: "",
            BuildConfig.OWM_KEY
        )

    val providerMfWsftKey: String
        get() = resolveProviderKey(
            prefs.getString(KEY_MF_WSFT, "") ?: "",
            BuildConfig.MF_WSFT_KEY
        )

    val providerIqaAtmoAuraKey: String
        get() = resolveProviderKey(
            prefs.getString(KEY_IQA_ATMO_AURA, "") ?: "",
            BuildConfig.IQA_ATMO_AURA_KEY
        )

    val providerBaiduIpLocationAk: String
        get() = resolveProviderKey(
            prefs.getString(KEY_BAIDU_IP_LOCATION_AK, "") ?: "",
            BuildConfig.BAIDU_IP_LOCATION_AK
        )

    companion object {

        private const val KEY_LANGUAGE = "language"
        private const val KEY_WEATHER_SOURCE = "weather_source"
        private const val KEY_LOCATION_SERVICE = "location_service"
        private const val KEY_PRECIPITATION_UNIT = "precipitation_unit"
        private const val KEY_ACCU_WEATHER = "provider_accu_weather_key"
        private const val KEY_ACCU_CURRENT = "provider_accu_current_key"
        private const val KEY_ACCU_AQI = "provider_accu_aqi_key"
        private const val KEY_OWM = "provider_owm_key"
        private const val KEY_MF_WSFT = "provider_mf_wsft_key"
        private const val KEY_IQA_ATMO_AURA = "provider_iqa_atmo_aura_key"
        private const val KEY_BAIDU_IP_LOCATION_AK = "provider_baidu_ip_location_ak"

        @JvmStatic
        fun getInstance(context: Context): WeatherProviderSettings {
            return WeatherProviderSettings(context.applicationContext)
        }

        internal fun resolveProviderKey(customValue: String, defaultValue: String): String {
            return customValue.ifEmpty { defaultValue }
        }
    }
}
