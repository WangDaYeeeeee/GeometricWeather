package wangdaye.com.geometricweather.location.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import wangdaye.com.geometricweather.BuildConfig
import wangdaye.com.geometricweather.location.services.ip.BaiduIPLocationApi

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    @Provides
    fun provideBaiduIPLocationApi(
        client: OkHttpClient,
        converterFactory: Converter.Factory
    ): BaiduIPLocationApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BAIDU_IP_LOCATION_BASE_URL)
            .client(client)
            .addConverterFactory(converterFactory)
            .build()
            .create(BaiduIPLocationApi::class.java)
    }
}
