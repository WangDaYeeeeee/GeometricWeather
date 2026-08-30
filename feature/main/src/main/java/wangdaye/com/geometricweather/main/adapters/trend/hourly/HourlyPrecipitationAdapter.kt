package wangdaye.com.geometricweather.main.adapters.trend.hourly

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.unit.PrecipitationUnit
import wangdaye.com.geometricweather.common.basic.models.weather.Precipitation
import wangdaye.com.geometricweather.common.ui.widgets.trend.TrendRecyclerView
import wangdaye.com.geometricweather.common.ui.widgets.trend.chart.PolylineAndHistogramView
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.ThemeManager
import wangdaye.com.geometricweather.theme.weatherThemeDelegate
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider
import wangdaye.com.geometricweather.theme.weatherView.WeatherViewController

class HourlyPrecipitationAdapter(
    activity: GeoActivity,
    location: Location,
    private val resourceProvider: ResourceProvider,
    private val precipitationUnit: PrecipitationUnit
) : AbsHourlyTrendAdapter(activity, location) {

    private var highestPrecipitation = 0f

    inner class ViewHolder(itemView: View) : AbsHourlyTrendAdapter.ViewHolder(itemView) {
        private val polylineAndHistogramView = PolylineAndHistogramView(itemView.context)

        init {
            hourlyItem.setChartItemView(polylineAndHistogramView)
        }

        fun onBindView(activity: GeoActivity, location: Location, position: Int) {
            val talkBackBuilder = StringBuilder(activity.getString(R.string.tag_precipitation))
            super.onBindView(activity, location, talkBackBuilder, position)

            val weather = location.weather!!
            val hourly = weather.hourlyForecast[position]
            hourlyItem.setIconDrawable(
                ResourceHelper.getWeatherIcon(resourceProvider, hourly.weatherCode, hourly.isDaylight)
            )

            val precipitation = weather.hourlyForecast[position].precipitation.total ?: 0f
            if (precipitation != 0f) {
                talkBackBuilder.append(", ").append(precipitationUnit.getValueVoice(activity, precipitation))
            } else {
                talkBackBuilder.append(", ").append(activity.getString(R.string.content_des_no_precipitation))
            }

            polylineAndHistogramView.setData(
                null, null,
                null, null,
                null, null,
                precipitation,
                precipitationUnit.getValueTextWithoutUnit(precipitation),
                highestPrecipitation,
                0f
            )
            polylineAndHistogramView.setLineColors(
                hourly.precipitation.getPrecipitationColor(activity),
                hourly.precipitation.getPrecipitationColor(activity),
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
            polylineAndHistogramView.setShadowColors(
                themeColors[if (lightTheme) 1 else 2],
                themeColors[2],
                lightTheme
            )
            polylineAndHistogramView.setTextColors(
                MainThemeColorProvider.getColor(location, R.attr.colorTitleText),
                MainThemeColorProvider.getColor(location, R.attr.colorBodyText),
                MainThemeColorProvider.getColor(location, R.attr.colorTitleText)
            )
            polylineAndHistogramView.setHistogramAlpha(if (lightTheme) 1f else 0.5f)
            hourlyItem.contentDescription = talkBackBuilder.toString()
        }
    }

    init {
        val weather = location.weather!!
        for (i in weather.hourlyForecast.indices.reversed()) {
            val precipitation = weather.hourlyForecast[i].precipitation.total
            if (precipitation != null && precipitation > highestPrecipitation) {
                highestPrecipitation = precipitation
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

    override fun isValid(location: Location): Boolean = highestPrecipitation > 0

    override fun getDisplayName(context: Context): String = context.getString(R.string.tag_precipitation)

    override fun bindBackgroundForHost(host: TrendRecyclerView) {
        val unit = SettingsManager.getInstance(activity).precipitationUnit
        val keyLineList = ArrayList<TrendRecyclerView.KeyLine>()
        keyLineList.add(
            TrendRecyclerView.KeyLine(
                Precipitation.PRECIPITATION_LIGHT,
                activity.getString(R.string.precipitation_light),
                unit.getValueTextWithoutUnit(Precipitation.PRECIPITATION_LIGHT),
                TrendRecyclerView.KeyLine.ContentPosition.ABOVE_LINE
            )
        )
        keyLineList.add(
            TrendRecyclerView.KeyLine(
                Precipitation.PRECIPITATION_HEAVY,
                activity.getString(R.string.precipitation_heavy),
                unit.getValueTextWithoutUnit(Precipitation.PRECIPITATION_HEAVY),
                TrendRecyclerView.KeyLine.ContentPosition.ABOVE_LINE
            )
        )
        host.setData(keyLineList, highestPrecipitation, 0f)
    }
}
