package wangdaye.com.geometricweather.remoteviews.config

import android.view.View
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.remoteviews.presenters.WeekWidgetIMP

@AndroidEntryPoint
class WeekWidgetConfigActivity : AbstractWidgetConfigActivity() {

    override fun initData() {
        super.initData()
        val widgetStyles = resources.getStringArray(R.array.week_widget_styles)
        val widgetStyleValues = resources.getStringArray(R.array.week_widget_style_values)
        viewTypeValueNow = "5_days"
        viewTypes = arrayOf(widgetStyles[0], widgetStyles[1])
        viewTypeValues = arrayOf(widgetStyleValues[0], widgetStyleValues[1])
    }

    override fun initView() {
        super.initView()
        mViewTypeContainer.visibility = View.VISIBLE
        mCardStyleContainer.visibility = View.VISIBLE
        mCardAlphaContainer.visibility = View.VISIBLE
        mTextColorContainer.visibility = View.VISIBLE
        mTextSizeContainer.visibility = View.VISIBLE
    }

    override fun getRemoteViews(): RemoteViews {
        return WeekWidgetIMP.getRemoteViews(
            this, locationNow,
            viewTypeValueNow, cardStyleValueNow, cardAlpha, textColorValueNow, textSize
        )
    }

    override fun getConfigStoreName(): String {
        return getString(R.string.sp_widget_week_setting)
    }
}
