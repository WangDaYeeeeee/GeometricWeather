package wangdaye.com.geometricweather.main.adapters.main.holder

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.ui.widgets.horizontal.HorizontalViewPager2
import wangdaye.com.geometricweather.common.utils.helpers.IntentHelper
import wangdaye.com.geometricweather.main.adapters.HomePollenAdapter
import wangdaye.com.geometricweather.main.adapters.HomePollenViewHolder
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider
import wangdaye.com.geometricweather.theme.ThemeManager
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider
import wangdaye.com.geometricweather.theme.weatherView.WeatherViewController

class AllergenViewHolder(
    parent: ViewGroup
) : AbstractMainCardViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.container_main_pollen, parent, false)
) {
    private val title: TextView = itemView.findViewById(R.id.container_main_pollen_title)
    private val subtitle: TextView = itemView.findViewById(R.id.container_main_pollen_subtitle)
    private val indicator: TextView = itemView.findViewById(R.id.container_main_pollen_indicator)
    private val pager: HorizontalViewPager2 = itemView.findViewById(R.id.container_main_pollen_pager)
    private var callback: DailyPollenPageChangeCallback? = null

    private class DailyPollenPagerAdapter(
        location: Location
    ) : HomePollenAdapter(location) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePollenViewHolder {
            val holder = super.onCreateViewHolder(parent, viewType)
            holder.itemView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            return holder
        }
    }

    private inner class DailyPollenPageChangeCallback(
        private val context: Context,
        private val location: Location
    ) : HorizontalViewPager2.OnPageChangeCallback() {

        @SuppressLint("SetTextI18n")
        override fun onPageSelected(position: Int) {
            val weather = location.weather!!
            val timeZone = location.timeZone
            val daily = weather.dailyForecast[position]
            indicator.text = if (daily.isToday(timeZone)) {
                context.getString(R.string.today)
            } else {
                (position + 1).toString() + "/" + weather.dailyForecast.size
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindView(
        activity: GeoActivity,
        location: Location,
        provider: ResourceProvider,
        listAnimationEnabled: Boolean,
        itemAnimationEnabled: Boolean,
        firstCard: Boolean
    ) {
        super.onBindView(activity, location, provider, listAnimationEnabled, itemAnimationEnabled, firstCard)

        title.setTextColor(
            ThemeManager
                .getInstance(context)
                .weatherThemeDelegate
                .getThemeColors(
                    context,
                    WeatherViewController.getWeatherKind(location.weather),
                    location.isDaylight
                )[0]
        )
        subtitle.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorCaptionText))

        pager.adapter = DailyPollenPagerAdapter(location)
        pager.currentItem = 0

        callback = DailyPollenPageChangeCallback(activity, location)
        pager.registerOnPageChangeCallback(callback)

        itemView.contentDescription = title.text
        itemView.setOnClickListener {
            IntentHelper.startAllergenActivity(this.context as GeoActivity, location)
        }
    }

    override fun onRecycleView() {
        super.onRecycleView()
        if (callback != null) {
            pager.unregisterOnPageChangeCallback(callback)
            callback = null
        }
    }
}
