package wangdaye.com.geometricweather.theme.weatherView.materialWeatherView

import androidx.annotation.DrawableRes
import androidx.annotation.Size
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.theme.weatherView.WeatherView
import wangdaye.com.geometricweather.theme.weatherView.WeatherView.WeatherKindRule
import wangdaye.com.geometricweather.theme.weatherView.materialWeatherView.MaterialWeatherView.WeatherAnimationImplementor
import wangdaye.com.geometricweather.theme.weatherView.materialWeatherView.implementor.*

object WeatherImplementorFactory {

    @JvmStatic
    fun getWeatherImplementor(
        @WeatherKindRule weatherKind: Int,
        daytime: Boolean,
        @Size(2) sizes: IntArray?
    ): WeatherAnimationImplementor? {
        val canvasSizes = sizes ?: return null
        return when (weatherKind) {
        WeatherView.WEATHER_KIND_CLEAR -> if (daytime) {
            SunImplementor(canvasSizes)
        } else {
            MeteorShowerImplementor(canvasSizes)
        }

        WeatherView.WEATHER_KIND_CLOUDY ->
            CloudImplementor(canvasSizes, CloudImplementor.TYPE_CLOUDY, daytime)

        WeatherView.WEATHER_KIND_CLOUD ->
            CloudImplementor(canvasSizes, CloudImplementor.TYPE_CLOUD, daytime)

        WeatherView.WEATHER_KIND_FOG ->
            CloudImplementor(canvasSizes, CloudImplementor.TYPE_FOG, daytime)

        WeatherView.WEATHER_KIND_HAIL ->
            HailImplementor(canvasSizes, daytime)

        WeatherView.WEATHER_KIND_HAZE ->
            CloudImplementor(canvasSizes, CloudImplementor.TYPE_HAZE, daytime)

        WeatherView.WEATHER_KIND_RAINY ->
            RainImplementor(canvasSizes, RainImplementor.TYPE_RAIN, daytime)

        WeatherView.WEATHER_KIND_SNOW ->
            SnowImplementor(canvasSizes, daytime)

        WeatherView.WEATHER_KIND_THUNDERSTORM ->
            RainImplementor(canvasSizes, RainImplementor.TYPE_THUNDERSTORM, daytime)

        WeatherView.WEATHER_KIND_THUNDER ->
            CloudImplementor(canvasSizes, CloudImplementor.TYPE_THUNDER, daytime)

        WeatherView.WEATHER_KIND_WIND ->
            WindImplementor(canvasSizes, daytime)

        WeatherView.WEATHER_KIND_SLEET ->
            RainImplementor(canvasSizes, RainImplementor.TYPE_SLEET, daytime)

        else -> null
        }
    }

    @JvmStatic
    @DrawableRes
    fun getBackgroundId(
        @WeatherKindRule weatherKind: Int,
        daylight: Boolean,
    ): Int = when (weatherKind) {
        WeatherView.WEATHER_KIND_CLEAR -> if (daylight) {
            R.drawable.weather_background_clear_day
        } else {
            R.drawable.weather_background_clear_night
        }

        WeatherView.WEATHER_KIND_CLOUD -> if (daylight) {
            R.drawable.weather_background_partly_cloudy_day
        } else {
            R.drawable.weather_background_partly_cloudy_night
        }

        WeatherView.WEATHER_KIND_CLOUDY -> if (daylight) {
            R.drawable.weather_background_cloudy_day
        } else {
            R.drawable.weather_background_cloudy_night
        }

        WeatherView.WEATHER_KIND_FOG -> if (daylight) {
            R.drawable.weather_background_fog_day
        } else {
            R.drawable.weather_background_fog_night
        }

        WeatherView.WEATHER_KIND_HAIL -> if (daylight) {
            R.drawable.weather_background_hail_day
        } else {
            R.drawable.weather_background_hail_night
        }

        WeatherView.WEATHER_KIND_HAZE -> if (daylight) {
            R.drawable.weather_background_haze_day
        } else {
            R.drawable.weather_background_haze_night
        }

        WeatherView.WEATHER_KIND_RAINY -> if (daylight) {
            R.drawable.weather_background_rain_day
        } else {
            R.drawable.weather_background_rain_night
        }

        WeatherView.WEATHER_KIND_SLEET -> if (daylight) {
            R.drawable.weather_background_sleet_day
        } else {
            R.drawable.weather_background_sleet_night
        }

        WeatherView.WEATHER_KIND_SNOW -> if (daylight) {
            R.drawable.weather_background_snow_day
        } else {
            R.drawable.weather_background_snow_night
        }

        WeatherView.WEATHER_KIND_THUNDER,
        WeatherView.WEATHER_KIND_THUNDERSTORM -> if (daylight) {
            R.drawable.weather_background_thunder_day
        } else {
            R.drawable.weather_background_thunder_night
        }

        WeatherView.WEATHER_KIND_WIND -> if (daylight) {
            R.drawable.weather_background_wind_day
        } else {
            R.drawable.weather_background_wind_night
        }

        else ->
            R.drawable.weather_background_default
    }
}