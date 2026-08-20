package wangdaye.com.geometricweather.weather.di;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import wangdaye.com.geometricweather.BuildConfig;
import wangdaye.com.geometricweather.weather.apis.AccuWeatherApi;
import wangdaye.com.geometricweather.weather.apis.AtmoAuraIqaApi;
import wangdaye.com.geometricweather.weather.apis.CaiYunApi;
import wangdaye.com.geometricweather.weather.apis.MfWeatherApi;
import wangdaye.com.geometricweather.weather.apis.OwmApi;

@InstallIn(SingletonComponent.class)
@Module
public class ApiModule {

    @Provides
    public AccuWeatherApi provideAccuWeatherApi(OkHttpClient client,
                                                GsonConverterFactory converterFactory) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.ACCU_WEATHER_BASE_URL)
                .client(client)
                .addConverterFactory(converterFactory)
                .build()
                .create((AccuWeatherApi.class));
    }

    @Provides
    public OwmApi provideOpenWeatherMapApi(OkHttpClient client,
                                           GsonConverterFactory converterFactory) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.OWM_BASE_URL)
                .client(client)
                .addConverterFactory(converterFactory)
                .build()
                .create((OwmApi.class));
    }

    @Provides
    public CaiYunApi provideCaiYunApi(OkHttpClient client,
                                      GsonConverterFactory converterFactory) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.CAIYUN_WEATHER_BASE_URL)
                .client(client)
                .addConverterFactory(converterFactory)
                .build()
                .create((CaiYunApi.class));
    }

    @Provides
    public MfWeatherApi provideMfWeatherApi(OkHttpClient client,
                                            GsonConverterFactory converterFactory) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.MF_WSFT_BASE_URL)
                .client(client)
                .addConverterFactory(converterFactory)
                .build()
                .create((MfWeatherApi.class));
    }

    @Provides
    public AtmoAuraIqaApi provideAtmoAuraIqaApi(OkHttpClient client,
                                                GsonConverterFactory converterFactory) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.IQA_ATMO_AURA_URL)
                .client(client)
                .addConverterFactory(converterFactory)
                .build()
                .create((AtmoAuraIqaApi.class));
    }
}
