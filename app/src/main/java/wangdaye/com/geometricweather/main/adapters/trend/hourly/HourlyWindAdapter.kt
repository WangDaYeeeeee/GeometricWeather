package wangdaye.com.geometricweather.main.adapters.trend.hourly

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
import wangdaye.com.geometricweather.common.ui.widgets.trend.chart.PolylineAndHistogramView
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider

class HourlyWindAdapter(
    activity: GeoActivity,
    location: Location,
    private val speedUnit: SpeedUnit
) : AbsHourlyTrendAdapter(activity, location) {

    private var highestWindSpeed = 0f

    inner class ViewHolder(itemView: View) : AbsHourlyTrendAdapter.ViewHolder(itemView) {
        private val polylineAndHistogramView = PolylineAndHistogramView(itemView.context)

        init {
            hourlyItem.setChartItemView(polylineAndHistogramView)
        }

        @SuppressLint("SetTextI18n, InflateParams")
        fun onBindView(activity: GeoActivity, location: Location, position: Int) {
            val talkBackBuilder = StringBuilder(activity.getString(R.string.tag_wind))
            super.onBindView(activity, location, talkBackBuilder, position)

            val weather = location.weather!!
            val hourly = weather.hourlyForecast[position]
            talkBackBuilder
                .append(", ").append(activity.getString(R.string.tag_wind))
                .append(" : ").append(hourly.wind.getWindDescription(activity, speedUnit))

            val daytimeWindColor = hourly.wind.getWindColor(activity)
            val dayIcon = RotateDrawable(
                ContextCompat.getDrawable(
                    activity,
                    if (hourly.wind.isValidSpeed) R.drawable.ic_navigation else R.drawable.ic_circle_medium
                )
            )
            dayIcon.rotate(hourly.wind.degree.degree + 180)
            dayIcon.colorFilter = PorterDuffColorFilter(daytimeWindColor, PorterDuff.Mode.SRC_ATOP)
            hourlyItem.setIconDrawable(dayIcon)

            val daytimeWindSpeed = weather.hourlyForecast[position].wind.speed
            polylineAndHistogramView.setData(
                null, null,
                null, null,
                null, null,
                weather.hourlyForecast[position].wind.speed,
                speedUnit.getValueTextWithoutUnit(daytimeWindSpeed ?: 0f),
                highestWindSpeed, 0f
            )
            polylineAndHistogramView.setLineColors(
                daytimeWindColor,
                daytimeWindColor,
                MainThemeColorProvider.getColor(location, R.attr.colorOutline)
            )
            polylineAndHistogramView.setTextColors(
                MainThemeColorProvider.getColor(location, R.attr.colorTitleText),
                MainThemeColorProvider.getColor(location, R.attr.colorBodyText),
                MainThemeColorProvider.getColor(location, R.attr.colorTitleText)
            )
            polylineAndHistogramView.setHistogramAlpha(1f)
            hourlyItem.contentDescription = talkBackBuilder.toString()
        }
    }

    init {
        val weather = location.weather!!
        for (i in weather.hourlyForecast.indices.reversed()) {
            val daytimeWindSpeed = weather.hourlyForecast[i].wind.speed
            if (daytimeWindSpeed != null && daytimeWindSpeed > highestWindSpeed) {
                highestWindSpeed = daytimeWindSpeed
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trend_hourly, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AbsHourlyTrendAdapter.ViewHolder, position: Int) {
        (holder as ViewHolder).onBindView(activity, location, position)
    }

    override fun getItemCount(): Int = location.weather!!.hourlyForecast.size

    override fun isValid(location: Location): Boolean = highestWindSpeed > 0

    override fun getDisplayName(context: Context): String = context.getString(R.string.tag_wind)

    override fun bindBackgroundForHost(host: TrendRecyclerView) {
        val keyLineList = ArrayList<TrendRecyclerView.KeyLine>()
        keyLineList.add(
            TrendRecyclerView.KeyLine(
                Wind.WIND_SPEED_3,
                speedUnit.getValueTextWithoutUnit(Wind.WIND_SPEED_3),
                activity.getString(R.string.wind_3),
                TrendRecyclerView.KeyLine.ContentPosition.ABOVE_LINE
            )
        )
        keyLineList.add(
            TrendRecyclerView.KeyLine(
                Wind.WIND_SPEED_7,
                speedUnit.getValueTextWithoutUnit(Wind.WIND_SPEED_7),
                activity.getString(R.string.wind_7),
                TrendRecyclerView.KeyLine.ContentPosition.ABOVE_LINE
            )
        )
        keyLineList.add(
            TrendRecyclerView.KeyLine(
                -Wind.WIND_SPEED_3,
                speedUnit.getValueTextWithoutUnit(Wind.WIND_SPEED_3),
                activity.getString(R.string.wind_3),
                TrendRecyclerView.KeyLine.ContentPosition.BELOW_LINE
            )
        )
        keyLineList.add(
            TrendRecyclerView.KeyLine(
                -Wind.WIND_SPEED_7,
                speedUnit.getValueTextWithoutUnit(Wind.WIND_SPEED_7),
                activity.getString(R.string.wind_7),
                TrendRecyclerView.KeyLine.ContentPosition.BELOW_LINE
            )
        )
        host.setData(keyLineList, highestWindSpeed, 0f)
    }
}
