package wangdaye.com.geometricweather.main.adapters.main.holder

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.unit.TemperatureUnit
import wangdaye.com.geometricweather.common.ui.widgets.NumberAnimTextView
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.ThemeManager
import wangdaye.com.geometricweather.theme.weatherThemeDelegate
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider
import wangdaye.com.geometricweather.theme.weatherView.WeatherView
import kotlin.math.abs
import kotlin.math.min

class HeaderViewHolder(
    parent: ViewGroup,
    weatherView: WeatherView
) : AbstractMainViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.container_main_header, parent, false)
) {
    private val container: LinearLayout = itemView.findViewById(R.id.container_main_header)
    private val temperature: NumberAnimTextView = itemView.findViewById(R.id.container_main_header_tempTxt)
    private val weather: TextView = itemView.findViewById(R.id.container_main_header_weatherTxt)
    private val aqiOrWind: TextView = itemView.findViewById(R.id.container_main_header_aqiOrWindTxt)

    private var temperatureCFrom = 0
    private var temperatureCTo = 0
    private var temperatureUnit: TemperatureUnit? = null

    val currentTemperatureHeight: Int
        get() = container.measuredHeight - temperature.top

    init {
        container.setOnClickListener { weatherView.onClick() }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindView(
        context: Context,
        location: Location,
        provider: ResourceProvider,
        listAnimationEnabled: Boolean,
        itemAnimationEnabled: Boolean
    ) {
        super.onBindView(context, location, provider, listAnimationEnabled, itemAnimationEnabled)

        val params = container.layoutParams as ViewGroup.MarginLayoutParams
        params.height = ThemeManager
            .getInstance(context)
            .weatherThemeDelegate
            .getHeaderHeight(context)
        container.layoutParams = params

        val textColor = ThemeManager
            .getInstance(context)
            .weatherThemeDelegate
            .getHeaderTextColor(context)
        temperature.setTextColor(textColor)
        weather.setTextColor(textColor)
        aqiOrWind.setTextColor(textColor)

        temperatureUnit = SettingsManager.getInstance(context).temperatureUnit
        val locWeather = location.weather
        if (locWeather != null) {
            temperatureCFrom = temperatureCTo
            temperatureCTo = locWeather.current.temperature.temperature

            temperature.setEnableAnim(itemAnimationEnabled)
            temperature.setDuration(
                min(
                    2000.0,
                    abs(temperatureCTo - temperatureCFrom) / 10f * 1000.0
                ).toLong()
            )
            temperature.setPostfixString(temperatureUnit!!.getShortName(context))

            val title = StringBuilder(locWeather.current.weatherText)
            if (locWeather.current.temperature.realFeelTemperature != null) {
                title.append(", ")
                    .append(context.getString(R.string.feels_like))
                    .append(" ")
                    .append(locWeather.current.temperature.getShortRealFeeTemperature(context, temperatureUnit!!))
            }
            weather.text = title.toString()

            if (locWeather.current.airQuality.aqiText == null) {
                aqiOrWind.text =
                    context.getString(R.string.wind) +
                        " - " +
                        locWeather.current.wind.shortWindDescription
            } else {
                aqiOrWind.text =
                    context.getString(R.string.air_quality) +
                        " - " +
                        locWeather.current.airQuality.aqiText
            }

            itemView.contentDescription = location.getCityName(context) +
                ", " + locWeather.current.temperature.getTemperature(context, temperatureUnit!!) +
                ", " + weather.text +
                ", " + aqiOrWind.text
        }
    }

    override fun getEnterAnimator(pendingAnimatorList: List<Animator>): Animator {
        val a: Animator = ObjectAnimator.ofFloat(itemView, "alpha", 0f, 1f)
        a.duration = 300
        a.startDelay = 100
        a.interpolator = FastOutSlowInInterpolator()
        return a
    }

    @SuppressLint("DefaultLocale")
    override fun onEnterScreen() {
        super.onEnterScreen()
        temperature.setNumberString(
            String.format("%d", temperatureUnit!!.getValueWithoutUnit(temperatureCFrom)),
            String.format("%d", temperatureUnit!!.getValueWithoutUnit(temperatureCTo))
        )
    }

    override fun onRecycleView() {
    }
}
