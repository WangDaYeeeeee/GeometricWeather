package wangdaye.com.geometricweather.daily.adapter.model

import androidx.annotation.DrawableRes
import wangdaye.com.geometricweather.daily.adapter.DailyWeatherAdapter

class Title(
    @DrawableRes var resId: Int?,
    var title: String
) : DailyWeatherAdapter.ViewModel {

    constructor(title: String) : this(null, title)

    override val code: Int = 2

    companion object {
        @JvmStatic
        fun isCode(code: Int): Boolean = code == 2
    }
}
