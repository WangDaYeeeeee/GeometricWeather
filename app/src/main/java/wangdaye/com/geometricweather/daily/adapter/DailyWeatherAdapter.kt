package wangdaye.com.geometricweather.daily.adapter

import android.content.Context
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.options.unit.DurationUnit
import wangdaye.com.geometricweather.common.basic.models.options.unit.PrecipitationUnit
import wangdaye.com.geometricweather.common.basic.models.options.unit.ProbabilityUnit
import wangdaye.com.geometricweather.common.basic.models.options.unit.TemperatureUnit
import wangdaye.com.geometricweather.common.basic.models.weather.Daily
import wangdaye.com.geometricweather.common.basic.models.weather.HalfDay
import wangdaye.com.geometricweather.daily.adapter.model.DailyAirQuality
import wangdaye.com.geometricweather.daily.adapter.model.DailyAstro
import wangdaye.com.geometricweather.daily.adapter.model.DailyPollen
import wangdaye.com.geometricweather.daily.adapter.model.DailyUV
import wangdaye.com.geometricweather.daily.adapter.model.DailyWind
import wangdaye.com.geometricweather.daily.adapter.model.LargeTitle
import wangdaye.com.geometricweather.daily.adapter.model.Line
import wangdaye.com.geometricweather.daily.adapter.model.Margin
import wangdaye.com.geometricweather.daily.adapter.model.Overview
import wangdaye.com.geometricweather.daily.adapter.model.Title
import wangdaye.com.geometricweather.daily.adapter.model.Value
import wangdaye.com.geometricweather.settings.SettingsManager
import java.util.TimeZone

/** Builds Compose daily-detail models. RecyclerView holders were removed after Phase 6. */
object DailyWeatherAdapter {

    interface ViewModel {
        val code: Int
    }

    @JvmStatic
    fun buildModelList(context: Context, timeZone: TimeZone, daily: Daily): List<ViewModel> {
        val modelList = ArrayList<ViewModel>()

        modelList.add(LargeTitle(context.getString(R.string.daytime)))
        modelList.add(Overview(daily.day(), true))
        modelList.add(DailyWind(daily.day().wind))
        modelList.addAll(getHalfDayOptionalModelList(context, daily.day()))

        modelList.add(Line())
        modelList.add(LargeTitle(context.getString(R.string.nighttime)))
        modelList.add(Overview(daily.night(), false))
        modelList.add(DailyWind(daily.night().wind))
        modelList.addAll(getHalfDayOptionalModelList(context, daily.night()))

        modelList.add(Line())
        modelList.add(LargeTitle(context.getString(R.string.life_details)))
        modelList.add(DailyAstro(timeZone, daily.sun(), daily.moon(), daily.moonPhase))
        if (daily.airQuality.isValid) {
            modelList.add(Title(R.drawable.weather_haze_mini_xml, context.getString(R.string.air_quality)))
            modelList.add(DailyAirQuality(daily.airQuality))
        }
        if (daily.pollen.isValid) {
            modelList.add(Title(R.drawable.ic_flower, context.getString(R.string.allergen)))
            modelList.add(DailyPollen(daily.pollen))
        }
        if (daily.uv.isValid) {
            modelList.add(Title(R.drawable.ic_uv, context.getString(R.string.uv_index)))
            modelList.add(DailyUV(daily.uv))
        }
        modelList.add(Line())
        modelList.add(
            Value(
                context.getString(R.string.hours_of_sun),
                DurationUnit.H.getValueText(context, daily.hoursOfSun)
            )
        )
        modelList.add(Margin())
        return modelList
    }

    private fun getHalfDayOptionalModelList(context: Context, halfDay: HalfDay): List<ViewModel> {
        val list = ArrayList<ViewModel>()
        val temperature = halfDay.temperature
        val temperatureUnit = SettingsManager.getInstance(context).temperatureUnit
        if (temperature.isValid) {
            val unit = SettingsManager.getInstance(context).temperatureUnit
            val resId = when (unit) {
                TemperatureUnit.C -> R.drawable.ic_temperature_celsius
                TemperatureUnit.F -> R.drawable.ic_temperature_fahrenheit
                else -> R.drawable.ic_temperature_kelvin
            }
            list.add(Title(resId, context.getString(R.string.temperature)))
            temperature.realFeelTemperature?.let { value ->
                list.add(
                    Value(
                        context.getString(R.string.real_feel_temperature),
                        temperatureUnit.getValueText(context, value)
                    )
                )
            }
            temperature.realFeelShaderTemperature?.let { value ->
                list.add(
                    Value(
                        context.getString(R.string.real_feel_shade_temperature),
                        temperatureUnit.getValueText(context, value)
                    )
                )
            }
            temperature.apparentTemperature?.let { value ->
                list.add(
                    Value(
                        context.getString(R.string.apparent_temperature),
                        temperatureUnit.getValueText(context, value)
                    )
                )
            }
            temperature.windChillTemperature?.let { value ->
                list.add(
                    Value(
                        context.getString(R.string.wind_chill_temperature),
                        temperatureUnit.getValueText(context, value)
                    )
                )
            }
            temperature.wetBulbTemperature?.let { value ->
                list.add(
                    Value(
                        context.getString(R.string.wet_bulb_temperature),
                        temperatureUnit.getValueText(context, value)
                    )
                )
            }
            temperature.degreeDayTemperature?.let { value ->
                list.add(
                    Value(
                        context.getString(R.string.degree_day_temperature),
                        temperatureUnit.getValueText(context, value)
                    )
                )
            }
            list.add(Margin())
        }

        val precipitation = halfDay.precipitation
        val precipitationUnit = SettingsManager.getInstance(context).precipitationUnit
        val precipitationTotal = precipitation.total
        if (precipitationTotal != null && precipitationTotal > 0) {
            list.add(Title(R.drawable.ic_water, context.getString(R.string.precipitation)))
            list.add(
                Value(
                    context.getString(R.string.total),
                    precipitationUnit.getValueText(context, precipitationTotal)
                )
            )
            val rain = precipitation.rain
            if (rain != null && rain > 0) {
                list.add(
                    Value(
                        context.getString(R.string.rain),
                        precipitationUnit.getValueText(context, rain)
                    )
                )
            }
            val snow = precipitation.snow
            if (snow != null && snow > 0) {
                list.add(
                    Value(
                        context.getString(R.string.snow),
                        precipitationUnit.getValueText(context, snow)
                    )
                )
            }
            val ice = precipitation.ice
            if (ice != null && ice > 0) {
                list.add(
                    Value(
                        context.getString(R.string.ice),
                        precipitationUnit.getValueText(context, ice)
                    )
                )
            }
            val thunderstorm = precipitation.thunderstorm
            if (thunderstorm != null && thunderstorm > 0) {
                list.add(
                    Value(
                        context.getString(R.string.thunderstorm),
                        precipitationUnit.getValueText(context, thunderstorm)
                    )
                )
            }
            list.add(Margin())
        }

        val probability = halfDay.precipitationProbability
        val probabilityTotal = probability.total
        if (probabilityTotal != null && probabilityTotal > 0) {
            list.add(Title(R.drawable.ic_water_percent, context.getString(R.string.precipitation_probability)))
            list.add(
                Value(
                    context.getString(R.string.total),
                    ProbabilityUnit.PERCENT.getValueText(context, probabilityTotal.toInt())
                )
            )
            val rainProbability = probability.rain
            if (rainProbability != null && rainProbability > 0) {
                list.add(
                    Value(
                        context.getString(R.string.rain),
                        ProbabilityUnit.PERCENT.getValueText(context, rainProbability.toInt())
                    )
                )
            }
            val snowProbability = probability.snow
            if (snowProbability != null && snowProbability > 0) {
                list.add(
                    Value(
                        context.getString(R.string.snow),
                        ProbabilityUnit.PERCENT.getValueText(context, snowProbability.toInt())
                    )
                )
            }
            val iceProbability = probability.ice
            if (iceProbability != null && iceProbability > 0) {
                list.add(
                    Value(
                        context.getString(R.string.ice),
                        ProbabilityUnit.PERCENT.getValueText(context, iceProbability.toInt())
                    )
                )
            }
            val thunderstormProbability = probability.thunderstorm
            if (thunderstormProbability != null && thunderstormProbability > 0) {
                list.add(
                    Value(
                        context.getString(R.string.thunderstorm),
                        ProbabilityUnit.PERCENT.getValueText(context, thunderstormProbability.toInt())
                    )
                )
            }
            list.add(Margin())
        }

        val duration = halfDay.precipitationDuration
        val durationTotal = duration.total
        if (durationTotal != null && durationTotal > 0) {
            list.add(Title(R.drawable.ic_time, context.getString(R.string.precipitation_duration)))
            list.add(
                Value(
                    context.getString(R.string.total),
                    DurationUnit.H.getValueText(context, durationTotal)
                )
            )
            val rainDuration = duration.rain
            if (rainDuration != null && rainDuration > 0) {
                list.add(
                    Value(
                        context.getString(R.string.rain),
                        DurationUnit.H.getValueText(context, rainDuration)
                    )
                )
            }
            val snowDuration = duration.snow
            if (snowDuration != null && snowDuration > 0) {
                list.add(
                    Value(
                        context.getString(R.string.snow),
                        DurationUnit.H.getValueText(context, snowDuration)
                    )
                )
            }
            val iceDuration = duration.ice
            if (iceDuration != null && iceDuration > 0) {
                list.add(
                    Value(
                        context.getString(R.string.ice),
                        DurationUnit.H.getValueText(context, iceDuration)
                    )
                )
            }
            val thunderstormDuration = duration.thunderstorm
            if (thunderstormDuration != null && thunderstormDuration > 0) {
                list.add(
                    Value(
                        context.getString(R.string.thunderstorm),
                        DurationUnit.H.getValueText(context, thunderstormDuration)
                    )
                )
            }
            list.add(Margin())
        }
        return list
    }
}
