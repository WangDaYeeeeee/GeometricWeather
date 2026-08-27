package wangdaye.com.geometricweather.theme.resource.providers

import android.animation.Animator
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.IntRange
import androidx.annotation.Size
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode
import wangdaye.com.geometricweather.common.ui.images.pixel.PixelMoonDrawable
import wangdaye.com.geometricweather.common.ui.images.pixel.PixelSunDrawable
import wangdaye.com.geometricweather.theme.resource.utils.Constants
import wangdaye.com.geometricweather.theme.resource.utils.ResourceUtils

class PixelResourcesProvider(
    defaultProvider: ResourceProvider
) : IconPackResourcesProvider(
    GeometricWeather.instance,
    GeometricWeather.instance.packageName,
    defaultProvider
) {

    override fun getDrawableUri(resName: String): Uri {
        return ResourceUtils.getDrawableUri(super.packageName, "drawable", resName)
    }

    override val packageName: String
        get() = super.packageName + ".Pixel"

    override val providerName: String
        get() = "Pixel"

    override val providerIcon: Drawable
        get() = getWeatherIcon(WeatherCode.PARTLY_CLOUDY, true)

    @Size(3)
    override fun getWeatherIcons(code: WeatherCode, dayTime: Boolean): Array<Drawable?> {
        return arrayOf(getWeatherIcon(code, dayTime), null, null)
    }

    override fun getWeatherIconName(code: WeatherCode, daytime: Boolean): String {
        return super.getWeatherIconName(code, daytime) + Constants.SEPARATOR + "pixel"
    }

    override fun getWeatherIconName(
        code: WeatherCode,
        daytime: Boolean,
        @IntRange(from = 1, to = 3) index: Int
    ): String? {
        return if (index == 1) {
            getWeatherIconName(code, daytime)
        } else {
            null
        }
    }

    @Size(3)
    override fun getWeatherAnimators(code: WeatherCode, dayTime: Boolean): Array<Animator?> {
        return arrayOf(null, null, null)
    }

    override fun getWeatherAnimatorName(
        code: WeatherCode,
        daytime: Boolean,
        @IntRange(from = 1, to = 3) index: Int
    ): String? {
        return null
    }

    override fun getSunDrawable(): Drawable {
        return PixelSunDrawable()
    }

    override fun getMoonDrawable(): Drawable {
        return PixelMoonDrawable()
    }

    override fun getSunDrawableClassName(): String {
        return PixelSunDrawable::class.java.toString()
    }

    override fun getMoonDrawableClassName(): String {
        return PixelMoonDrawable::class.java.toString()
    }

    companion object {
        @JvmStatic
        fun isPixelIconProvider(packageName: String): Boolean {
            return packageName == GeometricWeather.instance.packageName + ".Pixel"
        }
    }
}
