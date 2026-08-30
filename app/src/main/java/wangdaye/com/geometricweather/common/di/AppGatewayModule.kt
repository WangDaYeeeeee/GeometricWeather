package wangdaye.com.geometricweather.common.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import wangdaye.com.geometricweather.location.LocationHelper
import wangdaye.com.geometricweather.main.AppWidgetUpdateGateway
import wangdaye.com.geometricweather.main.LocationGateway
import wangdaye.com.geometricweather.main.WidgetUpdateGateway

@InstallIn(SingletonComponent::class)
@Module
abstract class AppGatewayModule {

    @Binds
    abstract fun bindLocationGateway(impl: LocationHelper): LocationGateway

    @Binds
    abstract fun bindWidgetUpdateGateway(impl: AppWidgetUpdateGateway): WidgetUpdateGateway
}
