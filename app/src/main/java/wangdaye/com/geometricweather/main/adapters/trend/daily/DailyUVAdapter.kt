package wangdaye.com.geometricweather.main.adapters.trend.daily

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.weather.UV
import wangdaye.com.geometricweather.common.ui.widgets.trend.TrendRecyclerView
import wangdaye.com.geometricweather.common.ui.widgets.trend.chart.PolylineAndHistogramView
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider
import wangdaye.com.geometricweather.theme.ThemeManager
import wangdaye.com.geometricweather.theme.weatherView.WeatherViewController

class DailyUVAdapter(
    activity: GeoActivity,
    location: Location
) : AbsDailyTrendAdapter(activity, location) {

    private var highestIndex = 0

    inner class ViewHolder(itemView: View) : AbsDailyTrendAdapter.ViewHolder(itemView) {
        private val polylineAndHistogramView = PolylineAndHistogramView(itemView.context)

        init {
            dailyItem.setChartItemView(polylineAndHistogramView)
        }

        @SuppressLint("SetTextI18n, InflateParams", "DefaultLocale")
        fun onBindView(activity: GeoActivity, location: Location, position: Int) {
            val talkBackBuilder = StringBuilder(activity.getString(R.string.tag_uv))
            super.onBindView(activity, location, talkBackBuilder, position)

            val daily = location.weather!!.dailyForecast[position]
            val index = daily.uv.index
            talkBackBuilder.append(", ").append(index).append(", ").append(daily.uv.level)
            polylineAndHistogramView.setData(
                null, null,
                null, null,
                null, null,
                (index ?: 0).toFloat(),
                String.format("%d", index ?: 0),
                highestIndex.toFloat(),
                0f
            )
            polylineAndHistogramView.setLineColors(
                daily.uv.getUVColor(activity),
                daily.uv.getUVColor(activity),
                MainThemeColorProvider.getColor(location, R.attr.colorOutline)
            )
            val themeColors = ThemeManager
                .getInstance(itemView.context)
                .weatherThemeDelegate
                .getThemeColors(
                    itemView.context,
                    WeatherViewController.getWeatherKind(location.weather),
                    location.isDaylight
                )
            val lightTheme = MainThemeColorProvider.isLightTheme(itemView.context, location)
            polylineAndHistogramView.setShadowColors(themeColors[1], themeColors[2], lightTheme)
            polylineAndHistogramView.setTextColors(
                MainThemeColorProvider.getColor(location, R.attr.colorTitleText),
                MainThemeColorProvider.getColor(location, R.attr.colorBodyText),
                MainThemeColorProvider.getColor(location, R.attr.colorTitleText)
            )
            polylineAndHistogramView.setHistogramAlpha(if (lightTheme) 1f else 0.5f)
            dailyItem.contentDescription = talkBackBuilder.toString()
        }
    }

    init {
        val weather = location.weather!!
        for (i in weather.dailyForecast.indices.reversed()) {
            val index = weather.dailyForecast[i].uv.index
            if (index != null && index > highestIndex) {
                highestIndex = index
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trend_daily, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AbsDailyTrendAdapter.ViewHolder, position: Int) {
        (holder as ViewHolder).onBindView(activity, location, position)
    }

    override fun getItemCount(): Int = location.weather!!.dailyForecast.size

    override fun isValid(location: Location): Boolean = highestIndex > 0

    override fun getDisplayName(context: Context): String = context.getString(R.string.tag_uv)

    override fun bindBackgroundForHost(host: TrendRecyclerView) {
        val keyLineList = ArrayList<TrendRecyclerView.KeyLine>()
        keyLineList.add(
            TrendRecyclerView.KeyLine(
                UV.UV_INDEX_HIGH.toFloat(),
                UV.UV_INDEX_HIGH.toString(),
                activity.getString(R.string.action_alert),
                TrendRecyclerView.KeyLine.ContentPosition.ABOVE_LINE
            )
        )
        host.setData(keyLineList, highestIndex.toFloat(), 0f)
    }
}
