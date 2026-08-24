package wangdaye.com.geometricweather.common.retrofit.interceptors

import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import wangdaye.com.geometricweather.common.utils.helpers.BuglyHelper

abstract class ReportExceptionInterceptor : Interceptor {

    fun handleException(e: Exception) {
        e.printStackTrace()
        BuglyHelper.report(e)
    }

    fun nullResponse(request: Request): Response {
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(400)
            .message("Handle an error in GeometricWeather.")
            .body(null)
            .build()
    }
}
