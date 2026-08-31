package wangdaye.com.geometricweather.common.network

import wangdaye.com.geometricweather.common.utils.helpers.BuglyHelper
import javax.inject.Inject

class AppNetworkExceptionReporter @Inject constructor() : NetworkExceptionReporter {

    override fun report(e: Exception) {
        BuglyHelper.report(e)
    }
}
