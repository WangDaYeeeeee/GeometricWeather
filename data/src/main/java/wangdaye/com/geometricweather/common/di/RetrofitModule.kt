package wangdaye.com.geometricweather.common.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import wangdaye.com.geometricweather.common.json.AppJson
import wangdaye.com.geometricweather.common.network.NetworkDebugConfig
import wangdaye.com.geometricweather.common.retrofit.TLSCompactHelper
import wangdaye.com.geometricweather.common.retrofit.interceptors.GzipInterceptor
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RetrofitModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        gzipInterceptor: GzipInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return TLSCompactHelper.getClientBuilder()
            .addInterceptor(gzipInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideJson(): Json = AppJson

    @Provides
    @Singleton
    fun provideConverterFactory(json: Json): Converter.Factory {
        return json.asConverterFactory("application/json".toMediaType())
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(
        debugConfig: NetworkDebugConfig
    ): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(
            if (debugConfig.httpLoggingEnabled) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        )
    }
}
