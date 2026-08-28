package wangdaye.com.geometricweather.weather.services

import kotlinx.coroutines.CancellationException

internal suspend fun <T> resumeOrNull(block: suspend () -> T): T? {
    return try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        null
    }
}

internal suspend fun <T> resumeWithDefault(default: T, block: suspend () -> T): T {
    return try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        default
    }
}
