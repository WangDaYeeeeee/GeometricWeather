package wangdaye.com.geometricweather.main.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.options.unit.ProbabilityUnit
import wangdaye.com.geometricweather.common.ui.widgets.AnimatableIconView
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory
import wangdaye.com.geometricweather.common.basic.models.weather.Hourly
import java.text.SimpleDateFormat

object HourlyWeatherDialog {

    @JvmStatic
    @SuppressLint("SimpleDateFormat")
    fun show(activity: Activity, hourly: Hourly) {
        val view = LayoutInflater
            .from(activity)
            .inflate(R.layout.dialog_weather_hourly, null, false)
        initWidget(view, hourly)

        MaterialAlertDialogBuilder(activity)
            .setTitle(
                hourly.getHour(activity)
                    + " - "
                    + SimpleDateFormat(activity.getString(R.string.date_format_long))
                    .format(hourly.date)
            )
            .setView(view)
            .show()
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun initWidget(view: android.view.View, hourly: Hourly) {
        val provider = ResourcesProviderFactory.getNewInstance()

        val weatherIcon = view.findViewById<AnimatableIconView>(R.id.dialog_weather_hourly_icon)

        view.findViewById<android.view.View>(R.id.dialog_weather_hourly_weatherContainer).setOnClickListener {
            weatherIcon.startAnimators()
        }

        val weatherCode = hourly.weatherCode
        val daytime = hourly.isDaylight
        weatherIcon.setAnimatableIcon(
            ResourceHelper.getWeatherIcons(provider, weatherCode, daytime),
            ResourceHelper.getWeatherAnimators(provider, weatherCode, daytime)
        )

        val weatherText = view.findViewById<TextView>(R.id.dialog_weather_hourly_text)

        val settings = SettingsManager.getInstance(view.context)
        val temperatureUnit = settings.temperatureUnit
        val precipitationUnit = settings.precipitationUnit

        val builder = StringBuilder(
            hourly.weatherText
                + ",  "
                + hourly.temperature.getTemperature(view.context, temperatureUnit)
        )
        if (hourly.temperature.realFeelTemperature != null) {
            builder.append("\n")
                .append(view.context.getString(R.string.feels_like))
                .append(" ")
                .append(hourly.temperature.getRealFeelTemperature(view.context, temperatureUnit))
        }
        if (hourly.precipitation.total != null) {
            val p = hourly.precipitation.total
            builder.append("\n")
                .append(view.context.getString(R.string.precipitation))
                .append(" : ")
                .append(precipitationUnit.getValueText(view.context, p!!))
        }
        if (hourly.precipitationProbability.total != null
            && hourly.precipitationProbability.total!! > 0
        ) {
            val p = hourly.precipitationProbability.total
            builder.append("\n")
                .append(view.context.getString(R.string.precipitation_probability))
                .append(" : ")
                .append(ProbabilityUnit.PERCENT.getValueText(view.context, p!!.toInt()))
        }
        weatherText.text = builder.toString()
    }
}
