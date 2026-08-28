package wangdaye.com.geometricweather.main.adapters.trend.daily

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.unit.ProbabilityUnit
import wangdaye.com.geometricweather.common.basic.models.options.unit.TemperatureUnit
import wangdaye.com.geometricweather.common.basic.models.weather.Temperature
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.common.ui.widgets.trend.TrendRecyclerView
import wangdaye.com.geometricweather.common.ui.widgets.trend.chart.PolylineAndHistogramView
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.ThemeManager
import wangdaye.com.geometricweather.theme.weatherThemeDelegate
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider
import wangdaye.com.geometricweather.theme.weatherView.WeatherViewController
import kotlin.math.max

class DailyTemperatureAdapter(
    activity: GeoActivity,
    location: Location,
    private val resourceProvider: ResourceProvider,
    private val temperatureUnit: TemperatureUnit,
    private val showPrecipitationProbability: Boolean = true
) : AbsDailyTrendAdapter(activity, location) {

    private val daytimeTemperatures: FloatArray
    private val nighttimeTemperatures: FloatArray
    private var highestTemperature: Int
    private var lowestTemperature: Int

    inner class ViewHolder(itemView: View) : AbsDailyTrendAdapter.ViewHolder(itemView) {
        private val polylineAndHistogramView = PolylineAndHistogramView(itemView.context)

        init {
            dailyItem.setChartItemView(polylineAndHistogramView)
        }

        @SuppressLint("SetTextI18n, InflateParams")
        fun onBindView(activity: GeoActivity, location: Location, position: Int) {
            val talkBackBuilder = StringBuilder(activity.getString(R.string.tag_temperature))
            super.onBindView(activity, location, talkBackBuilder, position)

            val weather = location.weather!!
            val daily = weather.dailyForecast[position]

            talkBackBuilder
                .append(", ").append(activity.getString(R.string.daytime))
                .append(" : ").append(daily.day().weatherText)
                .append(", ").append(getDaytimeTemperatureString(weather, position, temperatureUnit))
                .append(", ").append(activity.getString(R.string.nighttime))
                .append(" : ").append(daily.night().weatherText)
                .append(", ").append(getNighttimeTemperatureString(weather, position, temperatureUnit))

            dailyItem.setDayIconDrawable(
                ResourceHelper.getWeatherIcon(resourceProvider, daily.day().weatherCode, true)
            )

            var p = max(
                daily.day().precipitationProbability.total ?: 0f,
                daily.night().precipitationProbability.total ?: 0f
            )
            if (!showPrecipitationProbability) {
                p = 0f
            }
            polylineAndHistogramView.setData(
                buildTemperatureArrayForItem(daytimeTemperatures, position),
                buildTemperatureArrayForItem(nighttimeTemperatures, position),
                getShortDaytimeTemperatureString(weather, position, temperatureUnit),
                getShortNighttimeTemperatureString(weather, position, temperatureUnit),
                highestTemperature.toFloat(),
                lowestTemperature.toFloat(),
                if (p < 5) null else p,
                if (p < 5) null else ProbabilityUnit.PERCENT.getValueText(activity, p.toInt()),
                100f,
                0f
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
            polylineAndHistogramView.setLineColors(
                themeColors[1],
                themeColors[2],
                MainThemeColorProvider.getColor(location, R.attr.colorOutline)
            )
            polylineAndHistogramView.setShadowColors(themeColors[1], themeColors[2], lightTheme)
            polylineAndHistogramView.setTextColors(
                MainThemeColorProvider.getColor(location, R.attr.colorTitleText),
                MainThemeColorProvider.getColor(location, R.attr.colorBodyText),
                MainThemeColorProvider.getColor(location, R.attr.colorPrecipitationProbability)
            )
            polylineAndHistogramView.setHistogramAlpha(if (lightTheme) 0.2f else 0.5f)

            dailyItem.setNightIconDrawable(
                ResourceHelper.getWeatherIcon(resourceProvider, daily.night().weatherCode, false)
            )
            dailyItem.contentDescription = talkBackBuilder.toString()
        }

        private fun buildTemperatureArrayForItem(temps: FloatArray, adapterPosition: Int): Array<Float?> {
            val a = arrayOfNulls<Float>(3)
            a[1] = temps[2 * adapterPosition]
            a[0] = if (2 * adapterPosition - 1 < 0) null else temps[2 * adapterPosition - 1]
            a[2] = if (2 * adapterPosition + 1 >= temps.size) null else temps[2 * adapterPosition + 1]
            return a
        }
    }

    init {
        val weather = location.weather!!
        daytimeTemperatures = FloatArray(max(0, weather.dailyForecast.size * 2 - 1))
        var i = 0
        while (i < daytimeTemperatures.size) {
            daytimeTemperatures[i] = getDaytimeTemperatureC(weather, i / 2).toFloat()
            i += 2
        }
        i = 1
        while (i < daytimeTemperatures.size) {
            daytimeTemperatures[i] = (daytimeTemperatures[i - 1] + daytimeTemperatures[i + 1]) * 0.5f
            i += 2
        }

        nighttimeTemperatures = FloatArray(max(0, weather.dailyForecast.size * 2 - 1))
        i = 0
        while (i < nighttimeTemperatures.size) {
            nighttimeTemperatures[i] = getNighttimeTemperatureC(weather, i / 2).toFloat()
            i += 2
        }
        i = 1
        while (i < nighttimeTemperatures.size) {
            nighttimeTemperatures[i] = (nighttimeTemperatures[i - 1] + nighttimeTemperatures[i + 1]) * 0.5f
            i += 2
        }

        highestTemperature = weather.yesterday?.daytimeTemperature ?: Int.MIN_VALUE
        lowestTemperature = weather.yesterday?.nighttimeTemperature ?: Int.MAX_VALUE
        for (index in weather.dailyForecast.indices) {
            if (getDaytimeTemperatureC(weather, index) > highestTemperature) {
                highestTemperature = getDaytimeTemperatureC(weather, index)
            }
            if (getNighttimeTemperatureC(weather, index) < lowestTemperature) {
                lowestTemperature = getNighttimeTemperatureC(weather, index)
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

    override fun isValid(location: Location): Boolean = true

    override fun getDisplayName(context: Context): String = context.getString(R.string.tag_temperature)

    override fun bindBackgroundForHost(host: TrendRecyclerView) {
        val weather = location.weather ?: return
        if (weather.yesterday == null) {
            host.setData(null, 0f, 0f)
        } else {
            val keyLineList = ArrayList<TrendRecyclerView.KeyLine>()
            keyLineList.add(
                TrendRecyclerView.KeyLine(
                    weather.yesterday!!.daytimeTemperature.toFloat(),
                    Temperature.getShortTemperature(
                        activity,
                        weather.yesterday!!.daytimeTemperature,
                        SettingsManager.getInstance(activity).temperatureUnit
                    ) ?: "",
                    activity.getString(R.string.yesterday),
                    TrendRecyclerView.KeyLine.ContentPosition.ABOVE_LINE
                )
            )
            keyLineList.add(
                TrendRecyclerView.KeyLine(
                    weather.yesterday!!.nighttimeTemperature.toFloat(),
                    Temperature.getShortTemperature(
                        activity,
                        weather.yesterday!!.nighttimeTemperature,
                        SettingsManager.getInstance(activity).temperatureUnit
                    ) ?: "",
                    activity.getString(R.string.yesterday),
                    TrendRecyclerView.KeyLine.ContentPosition.BELOW_LINE
                )
            )
            host.setData(keyLineList, highestTemperature.toFloat(), lowestTemperature.toFloat())
        }
    }

    protected fun getDaytimeTemperatureC(weather: Weather, index: Int): Int {
        return weather.dailyForecast[index].day().temperature.temperature
    }

    protected fun getNighttimeTemperatureC(weather: Weather, index: Int): Int {
        return weather.dailyForecast[index].night().temperature.temperature
    }

    protected fun getDaytimeTemperatureString(weather: Weather, index: Int, unit: TemperatureUnit): String? {
        return weather.dailyForecast[index].day().temperature.getTemperature(activity, unit)
    }

    protected fun getNighttimeTemperatureString(weather: Weather, index: Int, unit: TemperatureUnit): String? {
        return weather.dailyForecast[index].night().temperature.getTemperature(activity, unit)
    }

    protected fun getShortDaytimeTemperatureString(weather: Weather, index: Int, unit: TemperatureUnit): String? {
        return weather.dailyForecast[index].day().temperature.getShortTemperature(activity, unit)
    }

    protected fun getShortNighttimeTemperatureString(weather: Weather, index: Int, unit: TemperatureUnit): String? {
        return weather.dailyForecast[index].night().temperature.getShortTemperature(activity, unit)
    }
}
