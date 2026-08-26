package wangdaye.com.geometricweather.main.adapters.trend.hourly

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
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider
import wangdaye.com.geometricweather.theme.weatherView.WeatherViewController
import kotlin.math.max

class HourlyTemperatureAdapter(
    activity: GeoActivity,
    location: Location,
    private val resourceProvider: ResourceProvider,
    private val temperatureUnit: TemperatureUnit,
    private val showPrecipitationProbability: Boolean = true
) : AbsHourlyTrendAdapter(activity, location) {

    private val temperatures: FloatArray
    private var highestTemperature: Int
    private var lowestTemperature: Int

    inner class ViewHolder(itemView: View) : AbsHourlyTrendAdapter.ViewHolder(itemView) {
        private val polylineAndHistogramView = PolylineAndHistogramView(itemView.context)

        init {
            hourlyItem.setChartItemView(polylineAndHistogramView)
        }

        fun onBindView(activity: GeoActivity, location: Location, position: Int) {
            val talkBackBuilder = StringBuilder(activity.getString(R.string.tag_temperature))
            super.onBindView(activity, location, talkBackBuilder, position)

            val weather = location.weather!!
            val hourly = weather.hourlyForecast[position]
            talkBackBuilder
                .append(", ").append(hourly.weatherText)
                .append(", ").append(getTemperatureString(weather, position, temperatureUnit))

            hourlyItem.setIconDrawable(
                ResourceHelper.getWeatherIcon(resourceProvider, hourly.weatherCode, hourly.isDaylight)
            )

            var p = hourly.precipitationProbability.total ?: 0f
            if (!showPrecipitationProbability) {
                p = 0f
            }
            polylineAndHistogramView.setData(
                buildTemperatureArrayForItem(temperatures, position),
                null,
                getShortTemperatureString(weather, position, temperatureUnit),
                null,
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
                themeColors[if (lightTheme) 1 else 2],
                themeColors[2],
                MainThemeColorProvider.getColor(location, R.attr.colorOutline)
            )
            polylineAndHistogramView.setShadowColors(
                themeColors[if (lightTheme) 1 else 2],
                themeColors[2],
                lightTheme
            )
            polylineAndHistogramView.setTextColors(
                MainThemeColorProvider.getColor(location, R.attr.colorTitleText),
                MainThemeColorProvider.getColor(location, R.attr.colorBodyText),
                MainThemeColorProvider.getColor(location, R.attr.colorPrecipitationProbability)
            )
            polylineAndHistogramView.setHistogramAlpha(if (lightTheme) 0.2f else 0.5f)
            hourlyItem.contentDescription = talkBackBuilder.toString()
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
        temperatures = FloatArray(max(0, weather.hourlyForecast.size * 2 - 1))
        var i = 0
        while (i < temperatures.size) {
            temperatures[i] = getTemperatureC(weather, i / 2).toFloat()
            i += 2
        }
        i = 1
        while (i < temperatures.size) {
            temperatures[i] = (temperatures[i - 1] + temperatures[i + 1]) * 0.5f
            i += 2
        }

        highestTemperature = weather.yesterday?.daytimeTemperature ?: Int.MIN_VALUE
        lowestTemperature = weather.yesterday?.nighttimeTemperature ?: Int.MAX_VALUE
        for (index in weather.hourlyForecast.indices) {
            if (getTemperatureC(weather, index) > highestTemperature) {
                highestTemperature = getTemperatureC(weather, index)
            }
            if (getTemperatureC(weather, index) < lowestTemperature) {
                lowestTemperature = getTemperatureC(weather, index)
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

    protected fun getTemperatureC(weather: Weather, index: Int): Int {
        return weather.hourlyForecast[index].temperature.temperature
    }

    protected fun getTemperatureString(weather: Weather, index: Int, unit: TemperatureUnit): String? {
        return weather.hourlyForecast[index].temperature.getTemperature(activity, unit)
    }

    protected fun getShortTemperatureString(weather: Weather, index: Int, unit: TemperatureUnit): String? {
        return weather.hourlyForecast[index].temperature.getShortTemperature(activity, unit)
    }
}
