package wangdaye.com.geometricweather.common.json

import kotlinx.serialization.json.Json

/**
 * Shared JSON config for Retrofit and asset decoding. Unknown keys are ignored
 * the same way Gson did by default.
 */
val AppJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
    encodeDefaults = true
}
