package wangdaye.com.geometricweather.theme.resource.providers

import android.animation.Animator
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.Size
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode
import wangdaye.com.geometricweather.theme.resource.utils.ResourceUtils

abstract class ResourceProvider {

    abstract val packageName: String
    abstract val providerName: String
    abstract val providerIcon: Drawable

    override fun equals(other: Any?): Boolean {
        if (other is ResourceProvider) {
            return other.packageName == packageName
        }
        return false
    }

    override fun hashCode(): Int = packageName.hashCode()

    abstract fun getWeatherIcon(code: WeatherCode, dayTime: Boolean): Drawable
    abstract fun getWeatherIconUri(code: WeatherCode, dayTime: Boolean): Uri

    @Size(3)
    abstract fun getWeatherIcons(code: WeatherCode, dayTime: Boolean): Array<Drawable?>

    @Size(3)
    abstract fun getWeatherAnimators(code: WeatherCode, dayTime: Boolean): Array<Animator?>

    abstract fun getMinimalLightIcon(code: WeatherCode, dayTime: Boolean): Drawable
    abstract fun getMinimalLightIconUri(code: WeatherCode, dayTime: Boolean): Uri
    abstract fun getMinimalGreyIcon(code: WeatherCode, dayTime: Boolean): Drawable
    abstract fun getMinimalGreyIconUri(code: WeatherCode, dayTime: Boolean): Uri
    abstract fun getMinimalDarkIcon(code: WeatherCode, dayTime: Boolean): Drawable
    abstract fun getMinimalDarkIconUri(code: WeatherCode, dayTime: Boolean): Uri
    abstract fun getMinimalXmlIcon(code: WeatherCode, dayTime: Boolean): Drawable

    @RequiresApi(api = Build.VERSION_CODES.M)
    abstract fun getMinimalIcon(code: WeatherCode, dayTime: Boolean): Icon

    abstract fun getShortcutsIcon(code: WeatherCode, dayTime: Boolean): Drawable
    abstract fun getShortcutsForegroundIcon(code: WeatherCode, dayTime: Boolean): Drawable
    abstract fun getSunDrawable(): Drawable
    abstract fun getMoonDrawable(): Drawable

    protected open fun getDrawableUri(resName: String): Uri {
        return ResourceUtils.getDrawableUri(packageName, "drawable", resName)
    }

    companion object {
        @JvmStatic
        fun getResId(context: Context, resName: String, type: String): Int {
            return ResourceUtils.getResId(context, resName, type)
        }
    }
}
