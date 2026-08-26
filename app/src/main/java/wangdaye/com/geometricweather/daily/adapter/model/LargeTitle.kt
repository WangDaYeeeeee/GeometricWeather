package wangdaye.com.geometricweather.daily.adapter.model

import wangdaye.com.geometricweather.daily.adapter.DailyWeatherAdapter

class LargeTitle(
    var title: String
) : DailyWeatherAdapter.ViewModel {

    override val code: Int = 0

    companion object {
        @JvmStatic
        fun isCode(code: Int): Boolean = code == 0
    }
}
