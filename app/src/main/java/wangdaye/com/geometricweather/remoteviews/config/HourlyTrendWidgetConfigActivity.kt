package wangdaye.com.geometricweather.remoteviews.config

import android.view.View
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.remoteviews.presenters.HourlyTrendWidgetIMP

@AndroidEntryPoint
class HourlyTrendWidgetConfigActivity : AbstractWidgetConfigActivity() {

    override fun initData() {
        super.initData()
        val cardStyles = resources.getStringArray(R.array.widget_card_styles)
        val cardStyleValues = resources.getStringArray(R.array.widget_card_style_values)
        cardStyleValueNow = "light"
        this.cardStyles = arrayOf(cardStyles[2], cardStyles[3], cardStyles[1])
        this.cardStyleValues = arrayOf(cardStyleValues[2], cardStyleValues[3], cardStyleValues[1])
    }

    override fun initView() {
        super.initView()
        mCardStyleContainer.visibility = View.VISIBLE
        mCardAlphaContainer.visibility = View.VISIBLE
    }

    override fun getRemoteViews(): RemoteViews {
        return HourlyTrendWidgetIMP.getRemoteViews(
            this, locationNow,
            resources.displayMetrics.widthPixels,
            cardStyleValueNow, cardAlpha
        )
    }

    override fun getConfigStoreName(): String {
        return getString(R.string.sp_widget_hourly_trend_setting)
    }
}
