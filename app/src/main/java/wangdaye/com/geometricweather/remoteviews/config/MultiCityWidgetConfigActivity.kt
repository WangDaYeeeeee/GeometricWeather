package wangdaye.com.geometricweather.remoteviews.config

import android.view.View
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.db.DatabaseHelper
import wangdaye.com.geometricweather.remoteviews.presenters.MultiCityWidgetIMP

@AndroidEntryPoint
class MultiCityWidgetConfigActivity : AbstractWidgetConfigActivity() {

    private lateinit var locationList: MutableList<Location>

    override fun initData() {
        super.initData()
        locationList = DatabaseHelper.getInstance(this).readLocationList()
        for (i in locationList.indices) {
            locationList[i] = Location.copy(
                locationList[i],
                DatabaseHelper.getInstance(this).readWeather(locationList[i])
            )
        }
    }

    override fun initView() {
        super.initView()
        mCardStyleContainer.visibility = View.VISIBLE
        mCardAlphaContainer.visibility = View.VISIBLE
        mTextColorContainer.visibility = View.VISIBLE
        mTextSizeContainer.visibility = View.VISIBLE
    }

    override fun getRemoteViews(): RemoteViews {
        return MultiCityWidgetIMP.getRemoteViews(
            this,
            locationList,
            cardStyleValueNow, cardAlpha,
            textColorValueNow, textSize
        )
    }

    override fun getConfigStoreName(): String {
        return getString(R.string.sp_widget_multi_city)
    }
}
