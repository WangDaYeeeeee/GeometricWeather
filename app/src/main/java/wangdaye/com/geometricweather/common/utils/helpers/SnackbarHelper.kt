package wangdaye.com.geometricweather.common.utils.helpers

import android.view.View
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.snackbar.Snackbar
import wangdaye.com.geometricweather.common.snackbar.SnackbarContainer

object SnackbarHelper {

    @JvmStatic
    fun showSnackbar(content: String?) {
        showSnackbar(content, null, null)
    }

    @JvmStatic
    fun showSnackbar(activity: GeoActivity, content: String?) {
        showSnackbar(activity, content, null, null)
    }

    @JvmStatic
    fun showSnackbar(content: String?, action: String?, l: View.OnClickListener?) {
        showSnackbar(content, action, l, null)
    }

    @JvmStatic
    fun showSnackbar(
        activity: GeoActivity,
        content: String?,
        action: String?,
        l: View.OnClickListener?
    ) {
        showSnackbar(activity, content, action, l, null)
    }

    @JvmStatic
    fun showSnackbar(
        content: String?,
        action: String?,
        l: View.OnClickListener?,
        callback: Snackbar.Callback?
    ) {
        val activity = GeometricWeather.instance.topActivity
        if (activity != null) {
            showSnackbar(activity, content, action, l, callback)
        }
    }

    @JvmStatic
    fun showSnackbar(
        activity: GeoActivity,
        content: String?,
        action: String?,
        l: View.OnClickListener?,
        callback: Snackbar.Callback?
    ) {
        if (action != null && l == null) {
            throw RuntimeException("Must send a non null listener as parameter.")
        }
        var cb = callback
        if (cb == null) {
            cb = Snackbar.Callback()
        }
        val container: SnackbarContainer = activity.provideSnackbarContainer()!!
            Snackbar.make(container.container, content!!, Snackbar.LENGTH_LONG, container.cardStyle)
            .setAction(action, l)
            .setCallback(cb)
            .show()
    }
}
