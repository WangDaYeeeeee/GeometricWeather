package wangdaye.com.geometricweather.remoteviews.config

import android.view.View
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.remoteviews.presenters.DailyTrendWidgetIMP

@AndroidEntryPoint
class DailyTrendWidgetConfigActivity : AbstractWidgetConfigActivity() {
    override fun initData() {
        super.initData()
        val cardStylesArr = resources.getStringArray(R.array.widget_card_styles)
        val cardStyleValuesArr = resources.getStringArray(R.array.widget_card_style_values)
        cardStyleValueNow = "light"
        cardStyles = arrayOf(cardStylesArr[2], cardStylesArr[3], cardStylesArr[1])
        cardStyleValues = arrayOf(cardStyleValuesArr[2], cardStyleValuesArr[3], cardStyleValuesArr[1])
    }

    override fun initView() {
        super.initView()
        mCardStyleContainer.visibility = View.VISIBLE
        mCardAlphaContainer.visibility = View.VISIBLE
    }

    override fun getRemoteViews(): RemoteViews {
        return DailyTrendWidgetIMP.getRemoteViews(
            this, locationNow,
            resources.displayMetrics.widthPixels,
            cardStyleValueNow, cardAlpha
        )
    }

    override fun getConfigStoreName(): String {
        return getString(R.string.sp_widget_daily_trend_setting)
    }
}
