package wangdaye.com.geometricweather.theme.weatherView

import android.annotation.SuppressLint
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider

object WeatherViewController {

    @JvmStatic
    fun setWeatherCode(
        view: WeatherView,
        weather: Weather?,
        dayTime: Boolean,
        provider: ResourceProvider
    ) {
        view.setWeather(getWeatherKind(weather), dayTime, provider)
    }

    @JvmStatic
    @SuppressLint("SwitchIntDef")
    fun getWeatherCode(
        @WeatherView.WeatherKindRule weatherKind: Int
    ): WeatherCode {
        return when (weatherKind) {
            WeatherView.WEATHER_KIND_CLOUDY -> WeatherCode.CLOUDY
            WeatherView.WEATHER_KIND_CLOUD -> WeatherCode.PARTLY_CLOUDY
            WeatherView.WEATHER_KIND_FOG -> WeatherCode.FOG
            WeatherView.WEATHER_KIND_HAIL -> WeatherCode.HAIL
            WeatherView.WEATHER_KIND_HAZE -> WeatherCode.HAZE
            WeatherView.WEATHER_KIND_RAINY -> WeatherCode.RAIN
            WeatherView.WEATHER_KIND_SLEET -> WeatherCode.SLEET
            WeatherView.WEATHER_KIND_SNOW -> WeatherCode.SNOW
            WeatherView.WEATHER_KIND_THUNDERSTORM -> WeatherCode.THUNDERSTORM
            WeatherView.WEATHER_KIND_THUNDER -> WeatherCode.THUNDER
            WeatherView.WEATHER_KIND_WIND -> WeatherCode.WIND
            else -> WeatherCode.CLEAR
        }
    }

    @JvmStatic
    @WeatherView.WeatherKindRule
    fun getWeatherKind(weather: Weather?): Int {
        if (weather == null) {
            return WeatherView.WEATHER_KIND_CLEAR
        }
        return getWeatherKind(weather.current.weatherCode)
    }

    @JvmStatic
    @WeatherView.WeatherKindRule
    fun getWeatherKind(weatherCode: WeatherCode): Int {
        return when (weatherCode) {
            WeatherCode.CLEAR -> WeatherView.WEATHER_KIND_CLEAR
            WeatherCode.PARTLY_CLOUDY -> WeatherView.WEATHER_KIND_CLOUD
            WeatherCode.CLOUDY -> WeatherView.WEATHER_KIND_CLOUDY
            WeatherCode.RAIN -> WeatherView.WEATHER_KIND_RAINY
            WeatherCode.SNOW -> WeatherView.WEATHER_KIND_SNOW
            WeatherCode.WIND -> WeatherView.WEATHER_KIND_WIND
            WeatherCode.FOG -> WeatherView.WEATHER_KIND_FOG
            WeatherCode.HAZE -> WeatherView.WEATHER_KIND_HAZE
            WeatherCode.SLEET -> WeatherView.WEATHER_KIND_SLEET
            WeatherCode.HAIL -> WeatherView.WEATHER_KIND_HAIL
            WeatherCode.THUNDER -> WeatherView.WEATHER_KIND_THUNDER
            WeatherCode.THUNDERSTORM -> WeatherView.WEATHER_KIND_THUNDERSTORM
        }
    }
}
