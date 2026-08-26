package wangdaye.com.geometricweather.remoteviews.config

import android.view.View
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.remoteviews.presenters.TextWidgetIMP

@AndroidEntryPoint
class TextWidgetConfigActivity : AbstractWidgetConfigActivity() {
    override fun initView() {
        super.initView()
        mTextColorContainer.visibility = View.VISIBLE
        mTextSizeContainer.visibility = View.VISIBLE
        mAlignEndContainer.visibility = View.VISIBLE
    }

    override fun getRemoteViews(): RemoteViews {
        return TextWidgetIMP.getRemoteViews(this, locationNow, textColorValueNow, textSize, alignEnd)
    }

    override fun getConfigStoreName(): String {
        return getString(R.string.sp_widget_text_setting)
    }
}
