package wangdaye.com.geometricweather.common.retrofit

import android.os.Build
import android.util.Log
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object TLSCompactHelper {

    /**
     * Enables TLS v1.2 when creating SSLSockets.
     *
     * For some reason, android supports TLS v1.2 from API 16, but enables it by
     * default only from API 20.
     */
    private class Tls12SocketFactory(base: SSLSocketFactory) : SSLSocketFactory() {

        private val delegate: SSLSocketFactory = base

        override fun getDefaultCipherSuites(): Array<String> {
            return delegate.defaultCipherSuites
        }

        override fun getSupportedCipherSuites(): Array<String> {
            return delegate.supportedCipherSuites
        }

        @Throws(IOException::class)
        override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean): Socket {
            return patch(delegate.createSocket(s, host, port, autoClose))
        }

        @Throws(IOException::class)
        override fun createSocket(host: String, port: Int): Socket {
            return patch(delegate.createSocket(host, port))
        }

        @Throws(IOException::class)
        override fun createSocket(
            host: String,
            port: Int,
            localHost: InetAddress,
            localPort: Int
        ): Socket {
            return patch(delegate.createSocket(host, port, localHost, localPort))
        }

        @Throws(IOException::class)
        override fun createSocket(host: InetAddress, port: Int): Socket {
            return patch(delegate.createSocket(host, port))
        }

        @Throws(IOException::class)
        override fun createSocket(
            address: InetAddress,
            port: Int,
            localAddress: InetAddress,
            localPort: Int
        ): Socket {
            return patch(delegate.createSocket(address, port, localAddress, localPort))
        }

        private fun patch(s: Socket): Socket {
            if (s is SSLSocket) {
                s.enabledProtocols = TLS_V12_ONLY
            }
            return s
        }

        companion object {
            private val TLS_V12_ONLY = arrayOf("TLSv1.2")
        }
    }

    @JvmStatic
    fun getClientBuilder(): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                val sc = SSLContext.getInstance("TLSv1.2")
                sc.init(null, null, null)
                val trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm()
                )
                trustManagerFactory.init(null as KeyStore?)
                val trustManager = trustManagerFactory.trustManagers[0] as X509TrustManager
                builder.sslSocketFactory(
                    Tls12SocketFactory(sc.socketFactory),
                    trustManager
                )

                val cs = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .build()

                val specs = ArrayList<ConnectionSpec>()
                specs.add(cs)
                specs.add(ConnectionSpec.COMPATIBLE_TLS)
                specs.add(ConnectionSpec.CLEARTEXT)

                builder.connectionSpecs(specs)
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .retryOnConnectionFailure(true)
                    .cache(null)
            } catch (e: Exception) {
                Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", e)
            }
        }
        return builder
    }
}
