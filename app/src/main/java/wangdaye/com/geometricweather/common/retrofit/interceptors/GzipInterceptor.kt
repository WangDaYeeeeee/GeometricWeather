package wangdaye.com.geometricweather.common.retrofit.interceptors

import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import okio.GzipSource
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class GzipInterceptor @Inject constructor() : ReportExceptionInterceptor() {

    override fun intercept(chain: okhttp3.Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .build()
        return buildResponse(request, chain.proceed(request))
    }

    private fun buildResponse(request: Request, response: Response): Response {
        val body = response.body ?: return response

        val source = body.source()
        source.request(Long.MAX_VALUE) // Buffer the entire body.
        var buffer = source.buffer()

        if ("gzip".equals(response.headers["Content-Encoding"], ignoreCase = true)) {
            GzipSource(buffer.clone()).use { gzippedResponseBody ->
                buffer = Buffer()
                buffer.writeAll(gzippedResponseBody)
            }
        }

        var charset: Charset? = UTF8
        val contentType: MediaType? = body.contentType()
        if (contentType != null) {
            charset = contentType.charset(UTF8)
        }

        var bodyString = ""
        if (charset != null) {
            bodyString = buffer.clone().readString(charset)
        }

        return Response.Builder()
            .addHeader("Content-Type", "application/json")
            .code(response.code)
            .body(ResponseBody.create(body.contentType(), bodyString))
            .message(response.message)
            .request(request)
            .protocol(Protocol.HTTP_2)
            .build()
    }

    companion object {
        private val UTF8: Charset = StandardCharsets.UTF_8
    }
}
