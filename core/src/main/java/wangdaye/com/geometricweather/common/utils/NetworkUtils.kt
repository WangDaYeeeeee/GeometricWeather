package wangdaye.com.geometricweather.common.utils

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtils {

    @JvmStatic
    fun isAvailable(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return manager?.activeNetworkInfo != null
    }
}
