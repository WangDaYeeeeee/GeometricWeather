package wangdaye.com.geometricweather.daily.adapter.model

import wangdaye.com.geometricweather.common.basic.models.weather.Astro
import wangdaye.com.geometricweather.common.basic.models.weather.MoonPhase
import wangdaye.com.geometricweather.daily.adapter.DailyWeatherAdapter
import java.util.TimeZone

class DailyAstro(
    var timeZone: TimeZone,
    var sun: Astro,
    var moon: Astro,
    var moonPhase: MoonPhase
) : DailyWeatherAdapter.ViewModel {

    override val code: Int = 7

    companion object {
        @JvmStatic
        fun isCode(code: Int): Boolean = code == 7
    }
}
