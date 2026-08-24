package wangdaye.com.geometricweather.daily.adapter.model

import wangdaye.com.geometricweather.common.basic.models.weather.Pollen
import wangdaye.com.geometricweather.daily.adapter.DailyWeatherAdapter

class DailyPollen(var pollen: Pollen) : DailyWeatherAdapter.ViewModel {

    override fun getCode(): Int = 6

    companion object {
        @JvmStatic
        fun isCode(code: Int): Boolean = code == 6
    }
}
