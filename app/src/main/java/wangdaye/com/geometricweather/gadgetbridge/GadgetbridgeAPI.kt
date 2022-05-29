package wangdaye.com.geometricweather.gadgetbridge

import android.content.Context
import android.content.Intent
import nodomain.freeyourgadget.gadgetbridge.model.WeatherSpec
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode
import wangdaye.com.geometricweather.settings.SettingsManager

object GadgetbridgeAPI {
    const val WEATHER_EXTRA = "WeatherSpec"
    const val WEATHER_ACTION = "de.kaffeemitkoffein.broadcast.WEATHERDATA"
    const val GADGETBRIDGE_PACKAGE_NAME = "nodomain.freeyourgadget.gadgetbridge"

    @JvmStatic
    fun sendWeatherBroadcast(context: Context, location: Location) {
        val weatherSpec = location.weather?.translateToGadgetbridgeWeatherSpec() ?: return
        weatherSpec.location = location.city
        val intent = Intent()
        intent.putExtra(WEATHER_EXTRA, weatherSpec)
        intent.setPackage(GADGETBRIDGE_PACKAGE_NAME)
        intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
        intent.action = WEATHER_ACTION
        context.sendBroadcast(intent)
    }

    @JvmStatic
    fun isEnabled(context: Context): Boolean {
        return SettingsManager.getInstance(context).isGadgetbridgeSupportEnabled
    }
}

private fun Weather.translateToGadgetbridgeWeatherSpec(): WeatherSpec {
    val weatherSpec = WeatherSpec()
    val weather = this

    weatherSpec.timestamp = (weather.base.updateTime / 1000).toInt()
    weatherSpec.currentConditionCode = weather.current.weatherCode.translateToOpenWeatherCode()
    weatherSpec.currentCondition = weather.current.weatherCode.name
    weatherSpec.currentTemp = weather.current.temperature.temperature.translateTemp()
    weather.current.relativeHumidity?.let { weatherSpec.currentHumidity = it.toInt() }
    weatherSpec.todayMaxTemp =
        weather.dailyForecast[0].day().temperature.temperature.translateTemp()
    weatherSpec.todayMinTemp =
        weather.dailyForecast[0].night().temperature.temperature.translateTemp()
    weatherSpec.windDirection = weather.current.wind.degree.degree.toInt()
    weather.current.wind.speed?.let { weatherSpec.windSpeed = it }

    weather.dailyForecast
        .filterIndexed { index, daily -> index != 0 }
        .forEach { daily ->
            weatherSpec.forecasts.add(WeatherSpec.Forecast().apply {
                this.conditionCode = daily.day().weatherCode.translateToOpenWeatherCode()
                this.maxTemp = daily.day().temperature.temperature.translateTemp()
                this.minTemp = daily.night().temperature.temperature.translateTemp()
            })
        }
    return weatherSpec
}


private fun WeatherCode.translateToOpenWeatherCode(): Int {
    return when (this) {
        WeatherCode.CLEAR -> {
            OpenWeatherContract.CLEAR_SKY
        }
        WeatherCode.PARTLY_CLOUDY -> {
            OpenWeatherContract.FEW_CLOUDS
        }
        WeatherCode.CLOUDY -> {
            OpenWeatherContract.OVERCAST_CLOUDS
        }
        WeatherCode.RAIN -> {
            OpenWeatherContract.MODERATE_RAIN
        }
        WeatherCode.SNOW -> {
            OpenWeatherContract.SNOW
        }
        WeatherCode.WIND -> {
            OpenWeatherContract.WINDY
        }
        WeatherCode.FOG -> {
            OpenWeatherContract.FOG
        }
        WeatherCode.HAZE -> {
            OpenWeatherContract.HAZE
        }
        WeatherCode.SLEET -> {
            OpenWeatherContract.SLEET
        }
        WeatherCode.HAIL -> {
            OpenWeatherContract.HAIL
        }
        WeatherCode.THUNDER -> {
            OpenWeatherContract.THUNDERSTORM
        }
        WeatherCode.THUNDERSTORM -> {
            OpenWeatherContract.THUNDERSTORM
        }
        else -> {
            OpenWeatherContract.UNKNOWN
        }
    }
}

private fun Int.translateTemp(): Int {
    return this + 17
}