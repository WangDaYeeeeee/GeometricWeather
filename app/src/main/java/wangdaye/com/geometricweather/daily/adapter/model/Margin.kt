package wangdaye.com.geometricweather.daily.adapter.model

import wangdaye.com.geometricweather.daily.adapter.DailyWeatherAdapter

class Margin : DailyWeatherAdapter.ViewModel {

    override fun getCode(): Int = -2

    companion object {
        @JvmStatic
        fun isCode(code: Int): Boolean = code == -2
    }
}
