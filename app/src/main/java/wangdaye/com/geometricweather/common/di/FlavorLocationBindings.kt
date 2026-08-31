package wangdaye.com.geometricweather.common.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import wangdaye.com.geometricweather.location.AppFlavorLocationFactory
import wangdaye.com.geometricweather.location.FlavorLocationFactory

@InstallIn(SingletonComponent::class)
@Module
abstract class FlavorLocationBindings {

    @Binds
    abstract fun bindFlavorLocationFactory(
        impl: AppFlavorLocationFactory
    ): FlavorLocationFactory
}
