package wangdaye.com.geometricweather.main

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class WidgetUpdateGatewayModule {

    @Binds
    @Singleton
    abstract fun bindWidgetUpdateGateway(impl: AppWidgetUpdateGateway): WidgetUpdateGateway
}
