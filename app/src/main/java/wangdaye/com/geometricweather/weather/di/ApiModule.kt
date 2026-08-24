package wangdaye.com.geometricweather.weather.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import wangdaye.com.geometricweather.BuildConfig
import wangdaye.com.geometricweather.weather.apis.AccuWeatherApi
import wangdaye.com.geometricweather.weather.apis.AtmoAuraIqaApi
import wangdaye.com.geometricweather.weather.apis.CaiYunApi
import wangdaye.com.geometricweather.weather.apis.MfWeatherApi
import wangdaye.com.geometricweather.weather.apis.OwmApi

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    @Provides
    fun provideAccuWeatherApi(
        client: OkHttpClient,
        converterFactory: Converter.Factory
    ): AccuWeatherApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.ACCU_WEATHER_BASE_URL)
            .client(client)
            .addConverterFactory(converterFactory)
            .build()
            .create(AccuWeatherApi::class.java)
    }

    @Provides
    fun provideOpenWeatherMapApi(
        client: OkHttpClient,
        converterFactory: Converter.Factory
    ): OwmApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.OWM_BASE_URL)
            .client(client)
            .addConverterFactory(converterFactory)
            .build()
            .create(OwmApi::class.java)
    }

    @Provides
    fun provideCaiYunApi(
        client: OkHttpClient,
        converterFactory: Converter.Factory
    ): CaiYunApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.CAIYUN_WEATHER_BASE_URL)
            .client(client)
            .addConverterFactory(converterFactory)
            .build()
            .create(CaiYunApi::class.java)
    }

    @Provides
    fun provideMfWeatherApi(
        client: OkHttpClient,
        converterFactory: Converter.Factory
    ): MfWeatherApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.MF_WSFT_BASE_URL)
            .client(client)
            .addConverterFactory(converterFactory)
            .build()
            .create(MfWeatherApi::class.java)
    }

    @Provides
    fun provideAtmoAuraIqaApi(
        client: OkHttpClient,
        converterFactory: Converter.Factory
    ): AtmoAuraIqaApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.IQA_ATMO_AURA_URL)
            .client(client)
            .addConverterFactory(converterFactory)
            .build()
            .create(AtmoAuraIqaApi::class.java)
    }
}
