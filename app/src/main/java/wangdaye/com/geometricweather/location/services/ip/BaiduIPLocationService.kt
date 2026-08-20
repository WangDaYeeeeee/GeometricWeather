package wangdaye.com.geometricweather.location.services.ip

import android.content.Context
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import wangdaye.com.geometricweather.common.utils.CancellableCoroutineScope
import wangdaye.com.geometricweather.location.services.LocationService
import wangdaye.com.geometricweather.settings.SettingsManager
import javax.inject.Inject

class BaiduIPLocationService @Inject constructor(
    private val api: BaiduIPLocationApi
) : LocationService() {

    private val requestScope = CancellableCoroutineScope()

    override fun requestLocation(context: Context, callback: LocationCallback) {
        requestScope.scope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val baidu = api.getLocation(
                        SettingsManager.getInstance(context).providerBaiduIpLocationAk,
                        "gcj02"
                    )
                    Result(
                        baidu.content.point.y.toFloat(),
                        baidu.content.point.x.toFloat()
                    )
                }
                callback.onCompleted(result)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                callback.onCompleted(null)
            }
        }
    }

    override fun cancel() {
        requestScope.cancelChildren()
    }

    override fun hasPermissions(context: Context): Boolean = true

    override val permissions: Array<String> = emptyArray()
}
