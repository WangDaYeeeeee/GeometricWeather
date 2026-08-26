package wangdaye.com.geometricweather.main.adapters.trend.daily

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.unit.SpeedUnit
import wangdaye.com.geometricweather.common.basic.models.weather.Wind
import wangdaye.com.geometricweather.common.ui.images.RotateDrawable
import wangdaye.com.geometricweather.common.ui.widgets.trend.TrendRecyclerView
import wangdaye.com.geometricweather.common.ui.widgets.trend.chart.DoubleHistogramView
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider
import wangdaye.com.geometricweather.settings.SettingsManager

class DailyWindAdapter(
    activity: GeoActivity,
    location: Location,
    private val speedUnit: SpeedUnit
) : AbsDailyTrendAdapter(activity, location) {

    private var highestWindSpeed = 0f

    inner class ViewHolder(itemView: View) : AbsDailyTrendAdapter.ViewHolder(itemView) {
        private val doubleHistogramView = DoubleHistogramView(itemView.context)

        init {
            dailyItem.setChartItemView(doubleHistogramView)
        }

        @SuppressLint("SetTextI18n, InflateParams")
        fun onBindView(activity: GeoActivity, location: Location, position: Int) {
            val talkBackBuilder = StringBuilder(activity.getString(R.string.tag_wind))
            super.onBindView(activity, location, talkBackBuilder, position)

            val weather = location.weather!!
            val daily = weather.dailyForecast[position]
            talkBackBuilder
                .append(", ").append(activity.getString(R.string.daytime))
                .append(" : ").append(daily.day().wind.getWindDescription(activity, speedUnit))
                .append(", ").append(activity.getString(R.string.nighttime))
                .append(" : ").append(daily.night().wind.getWindDescription(activity, speedUnit))

            val daytimeWindColor = daily.day().wind.getWindColor(activity)
            val nighttimeWindColor = daily.night().wind.getWindColor(activity)

            val dayIcon = RotateDrawable(
                ContextCompat.getDrawable(
                    activity,
                    if (daily.day().wind.isValidSpeed) R.drawable.ic_navigation else R.drawable.ic_circle_medium
                )
            )
            dayIcon.rotate(daily.day().wind.degree.degree + 180)
            dayIcon.colorFilter = PorterDuffColorFilter(daytimeWindColor, PorterDuff.Mode.SRC_ATOP)
            dailyItem.setDayIconDrawable(dayIcon)

            val daytimeWindSpeed = weather.dailyForecast[position].day().wind.speed
            val nighttimeWindSpeed = weather.dailyForecast[position].night().wind.speed
            doubleHistogramView.setData(
                weather.dailyForecast[position].day().wind.speed,
                weather.dailyForecast[position].night().wind.speed,
                speedUnit.getValueTextWithoutUnit(daytimeWindSpeed ?: 0f),
                speedUnit.getValueTextWithoutUnit(nighttimeWindSpeed ?: 0f),
                highestWindSpeed
            )
            doubleHistogramView.setLineColors(
                daytimeWindColor,
                nighttimeWindColor,
                MainThemeColorProvider.getColor(location, R.attr.colorOutline)
            )
            doubleHistogramView.setTextColors(
                MainThemeColorProvider.getColor(location, R.attr.colorBodyText)
            )
            doubleHistogramView.setHistogramAlphas(1f, 0.5f)

            val nightIcon = RotateDrawable(
                ContextCompat.getDrawable(
                    activity,
                    if (daily.night().wind.isValidSpeed) R.drawable.ic_navigation else R.drawable.ic_circle_medium
                )
            )
            nightIcon.rotate(daily.night().wind.degree.degree + 180)
            nightIcon.colorFilter = PorterDuffColorFilter(nighttimeWindColor, PorterDuff.Mode.SRC_ATOP)
            dailyItem.setNightIconDrawable(nightIcon)
            dailyItem.contentDescription = talkBackBuilder.toString()
        }
    }

    init {
        val weather = location.weather!!
        for (i in weather.dailyForecast.indices.reversed()) {
            val daytimeWindSpeed = weather.dailyForecast[i].day().wind.speed
            val nighttimeWindSpeed = weather.dailyForecast[i].night().wind.speed
            if (daytimeWindSpeed != null && daytimeWindSpeed > highestWindSpeed) {
                highestWindSpeed = daytimeWindSpeed
            }
            if (nighttimeWindSpeed != null && nighttimeWindSpeed > highestWindSpeed) {
                highestWindSpeed = nighttimeWindSpeed
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

    override fun isValid(location: Location): Boolean = highestWindSpeed > 0

    override fun getDisplayName(context: Context): String = context.getString(R.string.tag_wind)

    override fun bindBackgroundForHost(host: TrendRecyclerView) {
        val unit = SettingsManager.getInstance(activity).speedUnit
        val keyLineList = ArrayList<TrendRecyclerView.KeyLine>()
        keyLineList.add(
            TrendRecyclerView.KeyLine(
                Wind.WIND_SPEED_3,
                unit.getValueTextWithoutUnit(Wind.WIND_SPEED_3),
                activity.getString(R.string.wind_3),
                TrendRecyclerView.KeyLine.ContentPosition.ABOVE_LINE
            )
        )
        keyLineList.add(
            TrendRecyclerView.KeyLine(
                Wind.WIND_SPEED_7,
                unit.getValueTextWithoutUnit(Wind.WIND_SPEED_7),
                activity.getString(R.string.wind_7),
                TrendRecyclerView.KeyLine.ContentPosition.ABOVE_LINE
            )
        )
        keyLineList.add(
            TrendRecyclerView.KeyLine(
                -Wind.WIND_SPEED_3,
                unit.getValueTextWithoutUnit(Wind.WIND_SPEED_3),
                activity.getString(R.string.wind_3),
                TrendRecyclerView.KeyLine.ContentPosition.BELOW_LINE
            )
        )
        keyLineList.add(
            TrendRecyclerView.KeyLine(
                -Wind.WIND_SPEED_7,
                unit.getValueTextWithoutUnit(Wind.WIND_SPEED_7),
                activity.getString(R.string.wind_7),
                TrendRecyclerView.KeyLine.ContentPosition.BELOW_LINE
            )
        )
        host.setData(keyLineList, highestWindSpeed, -highestWindSpeed)
    }
}
