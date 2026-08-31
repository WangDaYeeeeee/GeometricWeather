package wangdaye.com.geometricweather.common.network

import android.content.Context
import android.content.pm.ApplicationInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppNetworkDebugConfig @Inject constructor(
    @ApplicationContext private val context: Context
) : NetworkDebugConfig {

    override val httpLoggingEnabled: Boolean
        get() = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
}
