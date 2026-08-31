package wangdaye.com.geometricweather.domain.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import wangdaye.com.geometricweather.db.DatabaseHelper
import wangdaye.com.geometricweather.db.DatabaseLocationWeatherStore
import wangdaye.com.geometricweather.domain.repository.LocationWeatherStore
import wangdaye.com.geometricweather.domain.usecase.CacheRequestedWeatherUseCase
import wangdaye.com.geometricweather.domain.usecase.DeleteLocationUseCase
import wangdaye.com.geometricweather.domain.usecase.HydrateWeatherCacheUseCase
import wangdaye.com.geometricweather.domain.usecase.LoadAllLocationsWithWeatherUseCase
import wangdaye.com.geometricweather.domain.usecase.LoadLocationsWithWeatherUseCase
import wangdaye.com.geometricweather.domain.weather.LocationSearcher
import wangdaye.com.geometricweather.domain.weather.WeatherRequester
import wangdaye.com.geometricweather.weather.WeatherHelper

@InstallIn(SingletonComponent::class)
@Module
abstract class DomainBindings {

    @Binds
    abstract fun bindLocationWeatherStore(
        impl: DatabaseLocationWeatherStore
    ): LocationWeatherStore

    @Binds
    abstract fun bindWeatherRequester(impl: WeatherHelper): WeatherRequester

    @Binds
    abstract fun bindLocationSearcher(impl: WeatherHelper): LocationSearcher
}

@InstallIn(SingletonComponent::class)
@Module
object DomainUseCasesModule {

    @Provides
    fun provideDatabaseHelper(@ApplicationContext context: Context): DatabaseHelper {
        return DatabaseHelper.getInstance(context)
    }

    @Provides
    fun provideLoadLocationsWithWeather(
        store: LocationWeatherStore
    ) = LoadLocationsWithWeatherUseCase(store)

    @Provides
    fun provideHydrateWeatherCache(
        store: LocationWeatherStore
    ) = HydrateWeatherCacheUseCase(store)

    @Provides
    fun provideLoadAllLocationsWithWeather(
        store: LocationWeatherStore
    ) = LoadAllLocationsWithWeatherUseCase(store)

    @Provides
    fun provideDeleteLocation(
        store: LocationWeatherStore
    ) = DeleteLocationUseCase(store)

    @Provides
    fun provideCacheRequestedWeather(
        store: LocationWeatherStore
    ) = CacheRequestedWeatherUseCase(store)
}
