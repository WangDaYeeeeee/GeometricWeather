package wangdaye.com.geometricweather.main.adapters.trend.daily

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.unit.PrecipitationUnit
import wangdaye.com.geometricweather.common.basic.models.weather.Precipitation
import wangdaye.com.geometricweather.common.ui.widgets.trend.TrendRecyclerView
import wangdaye.com.geometricweather.common.ui.widgets.trend.chart.DoubleHistogramView
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider

class DailyPrecipitationAdapter(
    activity: GeoActivity,
    location: Location,
    private val resourceProvider: ResourceProvider,
    private val precipitationUnit: PrecipitationUnit
) : AbsDailyTrendAdapter(activity, location) {

    private var highestPrecipitation = 0f

    inner class ViewHolder(itemView: View) : AbsDailyTrendAdapter.ViewHolder(itemView) {
        private val doubleHistogramView = DoubleHistogramView(itemView.context)

        init {
            dailyItem.setChartItemView(doubleHistogramView)
        }

        @SuppressLint("SetTextI18n, InflateParams")
        fun onBindView(activity: GeoActivity, location: Location, position: Int) {
            val talkBackBuilder = StringBuilder(activity.getString(R.string.tag_precipitation))
            super.onBindView(activity, location, talkBackBuilder, position)

            val weather = location.weather!!
            val daily = weather.dailyForecast[position]
            var daytimePrecipitation = weather.dailyForecast[position].day().precipitation.total ?: 0f
            var nighttimePrecipitation = weather.dailyForecast[position].night().precipitation.total ?: 0f

            if (daytimePrecipitation != 0f || nighttimePrecipitation != 0f) {
                talkBackBuilder.append(", ")
                    .append(activity.getString(R.string.daytime))
                    .append(" : ")
                    .append(precipitationUnit.getValueVoice(activity, daytimePrecipitation))
                talkBackBuilder.append(", ")
                    .append(activity.getString(R.string.nighttime))
                    .append(" : ")
                    .append(precipitationUnit.getValueVoice(activity, nighttimePrecipitation))
            } else {
                talkBackBuilder.append(", ")
                    .append(activity.getString(R.string.content_des_no_precipitation))
            }

            dailyItem.setDayIconDrawable(
                ResourceHelper.getWeatherIcon(resourceProvider, daily.day().weatherCode, true)
            )
            doubleHistogramView.setData(
                weather.dailyForecast[position].day().precipitation.total,
                weather.dailyForecast[position].night().precipitation.total,
                precipitationUnit.getValueTextWithoutUnit(daytimePrecipitation),
                precipitationUnit.getValueTextWithoutUnit(nighttimePrecipitation),
                highestPrecipitation
            )
            doubleHistogramView.setLineColors(
                daily.day().precipitation.getPrecipitationColor(activity),
                daily.night().precipitation.getPrecipitationColor(activity),
                MainThemeColorProvider.getColor(location, R.attr.colorOutline)
            )
            doubleHistogramView.setTextColors(
                MainThemeColorProvider.getColor(location, R.attr.colorBodyText)
            )
            doubleHistogramView.setHistogramAlphas(1f, 0.5f)
            dailyItem.setNightIconDrawable(
                ResourceHelper.getWeatherIcon(resourceProvider, daily.night().weatherCode, false)
            )
            dailyItem.contentDescription = talkBackBuilder.toString()
        }
    }

    init {
        val weather = location.weather!!
        for (i in weather.dailyForecast.indices.reversed()) {
            val daytimePrecipitation = weather.dailyForecast[i].day().precipitation.total
            val nighttimePrecipitation = weather.dailyForecast[i].night().precipitation.total
            if (daytimePrecipitation != null && daytimePrecipitation > highestPrecipitation) {
                highestPrecipitation = daytimePrecipitation
            }
            if (nighttimePrecipitation != null && nighttimePrecipitation > highestPrecipitation) {
                highestPrecipitation = nighttimePrecipitation
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

    override fun isValid(location: Location): Boolean = highestPrecipitation > 0

    override fun getDisplayName(context: Context): String = context.getString(R.string.tag_precipitation)

    override fun bindBackgroundForHost(host: TrendRecyclerView) {
        val unit = SettingsManager.getInstance(activity).precipitationUnit
        val keyLineList = ArrayList<TrendRecyclerView.KeyLine>()
        keyLineList.add(
            TrendRecyclerView.KeyLine(
                Precipitation.PRECIPITATION_LIGHT,
                unit.getValueTextWithoutUnit(Precipitation.PRECIPITATION_LIGHT),
                activity.getString(R.string.precipitation_light),
                TrendRecyclerView.KeyLine.ContentPosition.ABOVE_LINE
            )
        )
        keyLineList.add(
            TrendRecyclerView.KeyLine(
                Precipitation.PRECIPITATION_HEAVY,
                unit.getValueTextWithoutUnit(Precipitation.PRECIPITATION_HEAVY),
                activity.getString(R.string.precipitation_heavy),
                TrendRecyclerView.KeyLine.ContentPosition.ABOVE_LINE
            )
        )
        keyLineList.add(
            TrendRecyclerView.KeyLine(
                -Precipitation.PRECIPITATION_LIGHT,
                unit.getValueTextWithoutUnit(Precipitation.PRECIPITATION_LIGHT),
                activity.getString(R.string.precipitation_light),
                TrendRecyclerView.KeyLine.ContentPosition.BELOW_LINE
            )
        )
        keyLineList.add(
            TrendRecyclerView.KeyLine(
                -Precipitation.PRECIPITATION_HEAVY,
                unit.getValueTextWithoutUnit(Precipitation.PRECIPITATION_HEAVY),
                activity.getString(R.string.precipitation_heavy),
                TrendRecyclerView.KeyLine.ContentPosition.BELOW_LINE
            )
        )
        host.setData(keyLineList, highestPrecipitation, -highestPrecipitation)
    }
}
