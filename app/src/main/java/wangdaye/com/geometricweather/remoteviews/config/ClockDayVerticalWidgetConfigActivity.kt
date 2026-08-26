package wangdaye.com.geometricweather.remoteviews.config

import android.view.View
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.remoteviews.presenters.ClockDayVerticalWidgetIMP

@AndroidEntryPoint
class ClockDayVerticalWidgetConfigActivity : AbstractWidgetConfigActivity() {
    override fun initData() {
        super.initData()
        val widgetStyles = resources.getStringArray(R.array.widget_styles)
        val widgetStyleValues = resources.getStringArray(R.array.widget_style_values)
        viewTypeValueNow = "rectangle"
        viewTypes = arrayOf(
            widgetStyles[0], widgetStyles[1], widgetStyles[2],
            widgetStyles[3], widgetStyles[6], widgetStyles[9]
        )
        viewTypeValues = arrayOf(
            widgetStyleValues[0], widgetStyleValues[1], widgetStyleValues[2],
            widgetStyleValues[3], widgetStyleValues[6], widgetStyleValues[9]
        )
    }

    override fun initView() {
        super.initView()
        mViewTypeContainer.visibility = View.VISIBLE
        mCardStyleContainer.visibility = View.VISIBLE
        mCardAlphaContainer.visibility = View.VISIBLE
        mHideSubtitleContainer.visibility = View.VISIBLE
        mSubtitleDataContainer.visibility = View.VISIBLE
        mTextColorContainer.visibility = View.VISIBLE
        mTextSizeContainer.visibility = View.VISIBLE
        mClockFontContainer.visibility = View.VISIBLE
    }

    override fun getRemoteViews(): RemoteViews {
        return ClockDayVerticalWidgetIMP.getRemoteViews(
            this, locationNow,
            viewTypeValueNow, cardStyleValueNow, cardAlpha, textColorValueNow, textSize,
            hideSubtitle, subtitleDataValueNow, clockFontValueNow
        )
    }

    override fun getConfigStoreName(): String {
        return getString(R.string.sp_widget_clock_day_vertical_setting)
    }
}
