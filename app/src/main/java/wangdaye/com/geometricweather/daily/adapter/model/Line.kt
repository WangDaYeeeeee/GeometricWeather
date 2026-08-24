package wangdaye.com.geometricweather.daily.adapter.model

import wangdaye.com.geometricweather.daily.adapter.DailyWeatherAdapter

class Line : DailyWeatherAdapter.ViewModel {

    override fun getCode(): Int = -1

    companion object {
        @JvmStatic
        fun isCode(code: Int): Boolean = code == -1
    }
}
