package wangdaye.com.geometricweather.daily.adapter.model

import wangdaye.com.geometricweather.common.basic.models.weather.Wind
import wangdaye.com.geometricweather.daily.adapter.DailyWeatherAdapter

class DailyWind(
    var wind: Wind
) : DailyWeatherAdapter.ViewModel {

    override val code: Int = 4

    companion object {
        @JvmStatic
        fun isCode(code: Int): Boolean = code == 4
    }
}
