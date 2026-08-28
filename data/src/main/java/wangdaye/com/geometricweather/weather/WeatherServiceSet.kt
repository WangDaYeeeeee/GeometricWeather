package wangdaye.com.geometricweather.weather

import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.weather.services.AccuWeatherService
import wangdaye.com.geometricweather.weather.services.CaiYunWeatherService
import wangdaye.com.geometricweather.weather.services.MfWeatherService
import wangdaye.com.geometricweather.weather.services.OwmWeatherService
import wangdaye.com.geometricweather.weather.services.WeatherService
import javax.inject.Inject

class WeatherServiceSet @Inject constructor(
    accuWeatherService: AccuWeatherService,
    caiYunWeatherService: CaiYunWeatherService,
    mfWeatherService: MfWeatherService,
    owmWeatherService: OwmWeatherService
) {

    private val mWeatherServices: Array<WeatherService> = arrayOf(
        accuWeatherService,
        caiYunWeatherService,
        mfWeatherService,
        owmWeatherService
    )

    fun get(source: WeatherSource): WeatherService {
        return when (source) {
            WeatherSource.OWM -> mWeatherServices[3]
            WeatherSource.MF -> mWeatherServices[2]
            WeatherSource.CAIYUN -> mWeatherServices[1]
            WeatherSource.ACCU -> mWeatherServices[0]
        }
    }

    fun getAll(): Array<WeatherService> {
        return mWeatherServices
    }
}
