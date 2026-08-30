package wangdaye.com.geometricweather.location

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AndroidLocation

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaiduLocation

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AMapLocation
