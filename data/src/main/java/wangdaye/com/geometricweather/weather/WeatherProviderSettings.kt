package wangdaye.com.geometricweather.weather

import android.content.Context
import androidx.preference.PreferenceManager
import wangdaye.com.geometricweather.common.basic.models.options.appearance.Language
import wangdaye.com.geometricweather.common.basic.models.options.unit.PrecipitationUnit
import wangdaye.com.geometricweather.data.BuildConfig

/**
 * Reads weather-provider keys, language, and precipitation unit from the same
 * default SharedPreferences that [wangdaye.com.geometricweather.settings.SettingsManager]
 * writes. Lives in `:data` so network code does not depend on `:app`.
 */
class WeatherProviderSettings private constructor(context: Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    val languageCode: String
        get() = Language.getInstance(prefs.getString(KEY_LANGUAGE, "follow_system") ?: "").code

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

    companion object {

        private const val KEY_LANGUAGE = "language"
        private const val KEY_PRECIPITATION_UNIT = "precipitation_unit"
        private const val KEY_ACCU_WEATHER = "provider_accu_weather_key"
        private const val KEY_ACCU_CURRENT = "provider_accu_current_key"
        private const val KEY_ACCU_AQI = "provider_accu_aqi_key"
        private const val KEY_OWM = "provider_owm_key"
        private const val KEY_MF_WSFT = "provider_mf_wsft_key"
        private const val KEY_IQA_ATMO_AURA = "provider_iqa_atmo_aura_key"

        @JvmStatic
        fun getInstance(context: Context): WeatherProviderSettings {
            return WeatherProviderSettings(context.applicationContext)
        }

        internal fun resolveProviderKey(customValue: String, defaultValue: String): String {
            return customValue.ifEmpty { defaultValue }
        }
    }
}
