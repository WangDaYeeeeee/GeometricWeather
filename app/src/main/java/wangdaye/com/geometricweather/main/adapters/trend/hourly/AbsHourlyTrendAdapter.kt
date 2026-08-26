package wangdaye.com.geometricweather.main.adapters.trend.hourly

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.ui.widgets.trend.TrendRecyclerView
import wangdaye.com.geometricweather.common.ui.widgets.trend.TrendRecyclerViewAdapter
import wangdaye.com.geometricweather.common.ui.widgets.trend.item.HourlyTrendItemView
import wangdaye.com.geometricweather.main.dialogs.HourlyWeatherDialog
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider

abstract class AbsHourlyTrendAdapter(
    val activity: GeoActivity,
    location: Location
) : TrendRecyclerViewAdapter<AbsHourlyTrendAdapter.ViewHolder>(location) {

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hourlyItem: HourlyTrendItemView = itemView.findViewById(R.id.item_trend_hourly)

        open fun onBindView(
            activity: GeoActivity,
            location: Location,
            talkBackBuilder: StringBuilder,
            position: Int
        ) {
            val context = itemView.context
            val weather = location.weather!!
            val hourly = weather.hourlyForecast[position]

            talkBackBuilder.append(", ").append(hourly.getLongDate(context))
            hourlyItem.setDayText(hourly.getShortDate(context))

            talkBackBuilder
                .append(", ").append(hourly.getLongDate(activity))
                .append(", ").append(hourly.getHour(activity))
            hourlyItem.setHourText(hourly.getHour(context))

            val useAccentColorForDate = position == 0 || hourly.getHourIn24Format() == 0
            hourlyItem.setTextColor(
                MainThemeColorProvider.getColor(location, R.attr.colorTitleText),
                MainThemeColorProvider.getColor(
                    location,
                    if (useAccentColorForDate) R.attr.colorBodyText else R.attr.colorCaptionText
                )
            )

            hourlyItem.setOnClickListener {
                onItemClicked(activity, location, adapterPosition)
            }
        }
    }

    abstract fun isValid(location: Location): Boolean

    abstract fun getDisplayName(context: Context): String

    abstract fun bindBackgroundForHost(host: TrendRecyclerView)

    companion object {
        @JvmStatic
        protected fun onItemClicked(
            activity: GeoActivity,
            location: Location,
            adapterPosition: Int
        ) {
            if (activity.isActivityResumed) {
                HourlyWeatherDialog.show(
                    activity,
                    location.weather!!.hourlyForecast[adapterPosition]
                )
            }
        }
    }
}
