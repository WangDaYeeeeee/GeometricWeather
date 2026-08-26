package wangdaye.com.geometricweather.remoteviews.config

import android.view.View
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.remoteviews.presenters.ClockDayHorizontalWidgetIMP

@AndroidEntryPoint
class ClockDayHorizontalWidgetConfigActivity : AbstractWidgetConfigActivity() {
    override fun initData() {
        super.initData()
        val clockFontsArr = resources.getStringArray(R.array.clock_font)
        val clockFontValuesArr = resources.getStringArray(R.array.clock_font_values)
        clockFontValueNow = "light"
        clockFonts = arrayOf(clockFontsArr[0], clockFontsArr[1], clockFontsArr[2])
        clockFontValues = arrayOf(clockFontValuesArr[0], clockFontValuesArr[1], clockFontValuesArr[2])
    }

    override fun initView() {
        super.initView()
        mCardStyleContainer.visibility = View.VISIBLE
        mCardAlphaContainer.visibility = View.VISIBLE
        mTextColorContainer.visibility = View.VISIBLE
        mTextSizeContainer.visibility = View.VISIBLE
        mClockFontContainer.visibility = View.VISIBLE
        mHideLunarContainer.visibility = isHideLunarContainerVisible()
    }

    override fun getRemoteViews(): RemoteViews {
        return ClockDayHorizontalWidgetIMP.getRemoteViews(
            this,
            locationNow,
            cardStyleValueNow, cardAlpha,
            textColorValueNow, textSize, clockFontValueNow, hideLunar
        )
    }

    override fun getConfigStoreName(): String {
        return getString(R.string.sp_widget_clock_day_horizontal_setting)
    }
}
