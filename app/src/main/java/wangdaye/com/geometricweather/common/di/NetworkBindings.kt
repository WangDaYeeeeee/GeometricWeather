package wangdaye.com.geometricweather.common.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import wangdaye.com.geometricweather.common.network.AppNetworkDebugConfig
import wangdaye.com.geometricweather.common.network.AppNetworkExceptionReporter
import wangdaye.com.geometricweather.common.network.NetworkDebugConfig
import wangdaye.com.geometricweather.common.network.NetworkExceptionReporter

@InstallIn(SingletonComponent::class)
@Module
abstract class NetworkBindings {

    @Binds
    abstract fun bindNetworkDebugConfig(
        impl: AppNetworkDebugConfig
    ): NetworkDebugConfig

    @Binds
    abstract fun bindNetworkExceptionReporter(
        impl: AppNetworkExceptionReporter
    ): NetworkExceptionReporter
}
