package wangdaye.com.geometricweather.main.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import wangdaye.com.geometricweather.main.MainActivityRepository
import wangdaye.com.geometricweather.main.MainWeatherRepository

@InstallIn(SingletonComponent::class)
@Module
abstract class MainWeatherBindings {

    @Binds
    abstract fun bindMainWeatherRepository(
        impl: MainActivityRepository
    ): MainWeatherRepository
}
