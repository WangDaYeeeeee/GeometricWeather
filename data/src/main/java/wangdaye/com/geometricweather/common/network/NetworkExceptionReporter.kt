package wangdaye.com.geometricweather.common.network

/**
 * Crash/exception sink for OkHttp interceptors. `:app` binds Bugly (or a no-op stub).
 */
fun interface NetworkExceptionReporter {
    fun report(e: Exception)
}
