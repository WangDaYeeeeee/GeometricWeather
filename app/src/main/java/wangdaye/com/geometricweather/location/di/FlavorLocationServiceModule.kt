package wangdaye.com.geometricweather.location.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import wangdaye.com.geometricweather.location.AMapLocation
import wangdaye.com.geometricweather.location.AndroidLocation
import wangdaye.com.geometricweather.location.BaiduLocation
import wangdaye.com.geometricweather.location.services.AMapLocationService
import wangdaye.com.geometricweather.location.services.AndroidLocationService
import wangdaye.com.geometricweather.location.services.BaiduLocationService
import wangdaye.com.geometricweather.location.services.LocationService

@InstallIn(SingletonComponent::class)
@Module
class FlavorLocationServiceModule {

    @Provides
    @AndroidLocation
    fun provideAndroidLocationService(): LocationService = AndroidLocationService()

    @Provides
    @BaiduLocation
    fun provideBaiduLocationService(@ApplicationContext context: Context): LocationService =
        BaiduLocationService(context)

    @Provides
    @AMapLocation
    fun provideAMapLocationService(@ApplicationContext context: Context): LocationService =
        AMapLocationService(context)
}
