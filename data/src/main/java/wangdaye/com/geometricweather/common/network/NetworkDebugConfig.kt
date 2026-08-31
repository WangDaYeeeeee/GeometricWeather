package wangdaye.com.geometricweather.common.network

/**
 * App-level debug flags for the shared OkHttp client in `:data`.
 * Implemented in `:app` so `:data` does not read [wangdaye.com.geometricweather.GeometricWeather].
 */
interface NetworkDebugConfig {
    val httpLoggingEnabled: Boolean
}
