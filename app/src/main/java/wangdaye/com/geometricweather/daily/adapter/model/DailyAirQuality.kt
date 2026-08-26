package wangdaye.com.geometricweather.daily.adapter.model

import wangdaye.com.geometricweather.common.basic.models.weather.AirQuality
import wangdaye.com.geometricweather.daily.adapter.DailyWeatherAdapter

class DailyAirQuality(
    var airQuality: AirQuality
) : DailyWeatherAdapter.ViewModel {

    override val code: Int = 5

    companion object {
        @JvmStatic
        fun isCode(code: Int): Boolean = code == 5
    }
}
