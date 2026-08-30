package wangdaye.com.geometricweather.daily.adapter.model

import wangdaye.com.geometricweather.common.basic.models.weather.HalfDay
import wangdaye.com.geometricweather.daily.adapter.DailyWeatherAdapter

class Overview(
    var halfDay: HalfDay,
    var isDaytime: Boolean
) : DailyWeatherAdapter.ViewModel {

    override val code: Int = 1

    companion object {
        @JvmStatic
        fun isCode(code: Int): Boolean = code == 1
    }
}
