package wangdaye.com.geometricweather.daily.adapter.model

import wangdaye.com.geometricweather.daily.adapter.DailyWeatherAdapter

class Value(
    var title: String,
    var value: String
) : DailyWeatherAdapter.ViewModel {

    override fun getCode(): Int = 3

    companion object {
        @JvmStatic
        fun isCode(code: Int): Boolean = code == 3
    }
}
