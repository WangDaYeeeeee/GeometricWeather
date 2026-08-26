package wangdaye.com.geometricweather.daily.adapter.model

import wangdaye.com.geometricweather.common.basic.models.weather.UV
import wangdaye.com.geometricweather.daily.adapter.DailyWeatherAdapter

class DailyUV(
    var uv: UV
) : DailyWeatherAdapter.ViewModel {

    override val code: Int = 8

    companion object {
        @JvmStatic
        fun isCode(code: Int): Boolean = code == 8
    }
}
