package wangdaye.com.geometricweather.common.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren

/**
 * Main-thread scope whose children can be cancelled without killing later requests.
 */
class CancellableCoroutineScope {
    private val job = SupervisorJob()
    val scope: CoroutineScope = CoroutineScope(job + Dispatchers.Main.immediate)

    fun cancelChildren() {
        job.cancelChildren()
    }
}
