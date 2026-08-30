package wangdaye.com.geometricweather.main.adapters.trend.daily

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.ui.widgets.trend.TrendRecyclerView
import wangdaye.com.geometricweather.common.ui.widgets.trend.TrendRecyclerViewAdapter
import wangdaye.com.geometricweather.common.ui.widgets.trend.item.DailyTrendItemView
import wangdaye.com.geometricweather.common.utils.helpers.IntentHelper
import wangdaye.com.geometricweather.common.utils.helpers.startAboutActivity
import wangdaye.com.geometricweather.common.utils.helpers.startAlertActivity
import wangdaye.com.geometricweather.common.utils.helpers.startAllergenActivity
import wangdaye.com.geometricweather.common.utils.helpers.startCardDisplayManageActivity
import wangdaye.com.geometricweather.common.utils.helpers.startDailyTrendDisplayManageActivity
import wangdaye.com.geometricweather.common.utils.helpers.startDailyWeatherActivity
import wangdaye.com.geometricweather.common.utils.helpers.startHourlyTrendDisplayManageActivityForResult
import wangdaye.com.geometricweather.common.utils.helpers.startMainActivity
import wangdaye.com.geometricweather.common.utils.helpers.startMainActivityForManagement
import wangdaye.com.geometricweather.common.utils.helpers.startPreviewIconActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSearchActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSelectProviderActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSettingsActivity
import wangdaye.com.geometricweather.common.utils.helpers.buildAwakeUpdateActivityIntent
import wangdaye.com.geometricweather.common.utils.helpers.buildMainActivityIntent
import wangdaye.com.geometricweather.common.utils.helpers.buildMainActivityShowAlertsIntent
import wangdaye.com.geometricweather.common.utils.helpers.buildMainActivityShowDailyForecastIntent
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider

abstract class AbsDailyTrendAdapter(
    val activity: GeoActivity,
    location: Location
) : TrendRecyclerViewAdapter<AbsDailyTrendAdapter.ViewHolder>(location) {

    open class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dailyItem: DailyTrendItemView = itemView.findViewById(R.id.item_trend_daily)

        @SuppressLint("SetTextI18n, InflateParams", "DefaultLocale")
        open fun onBindView(
            activity: GeoActivity,
            location: Location,
            talkBackBuilder: StringBuilder,
            position: Int
        ) {
            val context = itemView.context
            val weather = location.weather!!
            val timeZone = location.timeZone
            val daily = weather.dailyForecast[position]

            if (daily.isToday(timeZone)) {
                talkBackBuilder.append(", ").append(context.getString(R.string.today))
                dailyItem.setWeekText(context.getString(R.string.today))
            } else {
                talkBackBuilder.append(", ").append(daily.getWeek(context))
                dailyItem.setWeekText(daily.getWeek(context))
            }

            talkBackBuilder.append(", ").append(daily.getLongDate(context))
            dailyItem.setDateText(daily.getShortDate(context))

            dailyItem.setTextColor(
                MainThemeColorProvider.getColor(location, R.attr.colorTitleText),
                MainThemeColorProvider.getColor(location, R.attr.colorBodyText)
            )

            dailyItem.setOnClickListener { onItemClicked(activity, location, adapterPosition) }
        }
    }

    abstract fun isValid(location: Location): Boolean

    open fun getKey(): String = javaClass.name

    abstract fun getDisplayName(context: Context): String

    abstract fun bindBackgroundForHost(host: TrendRecyclerView)

    companion object {
        @JvmStatic
        protected fun onItemClicked(activity: GeoActivity, location: Location, adapterPosition: Int) {
            if (activity.isActivityResumed) {
                IntentHelper.startDailyWeatherActivity(activity, location.formattedId, adapterPosition)
            }
        }
    }
}
