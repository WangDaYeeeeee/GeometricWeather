package wangdaye.com.geometricweather.theme.resource

import android.animation.Animator
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.Size
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.options.NotificationTextColor
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode
import wangdaye.com.geometricweather.theme.resource.providers.DefaultResourceProvider
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider
import wangdaye.com.geometricweather.theme.resource.utils.ResourceUtils
import kotlin.math.abs

object ResourceHelper {

    @JvmStatic
    fun getWeatherIcon(provider: ResourceProvider, code: WeatherCode, dayTime: Boolean): Drawable {
        return provider.getWeatherIcon(code, dayTime)
    }

    @JvmStatic
    @Size(3)
    fun getWeatherIcons(provider: ResourceProvider, code: WeatherCode, dayTime: Boolean): Array<Drawable?> {
        return provider.getWeatherIcons(code, dayTime)
    }

    @JvmStatic
    @Size(3)
    fun getWeatherAnimators(provider: ResourceProvider, code: WeatherCode, dayTime: Boolean): Array<Animator?> {
        return provider.getWeatherAnimators(code, dayTime)
    }

    @JvmStatic
    fun getWidgetNotificationIcon(
        provider: ResourceProvider,
        code: WeatherCode,
        dayTime: Boolean,
        minimal: Boolean,
        textColor: String
    ): Drawable {
        if (minimal) {
            when (textColor) {
                "light" -> return provider.getMinimalLightIcon(code, dayTime)
                "grey" -> return provider.getMinimalGreyIcon(code, dayTime)
                "dark" -> return provider.getMinimalDarkIcon(code, dayTime)
            }
        }
        return provider.getWeatherIcon(code, dayTime)
    }

    @JvmStatic
    fun getWidgetNotificationIcon(
        provider: ResourceProvider,
        code: WeatherCode,
        dayTime: Boolean,
        minimal: Boolean,
        darkText: Boolean
    ): Drawable {
        return getWidgetNotificationIcon(
            provider, code, dayTime, minimal, if (darkText) "dark" else "light"
        )
    }

    @JvmStatic
    fun getWidgetNotificationIconUri(
        provider: ResourceProvider,
        code: WeatherCode,
        dayTime: Boolean,
        minimal: Boolean,
        textColor: NotificationTextColor
    ): Uri {
        if (minimal) {
            when (textColor) {
                NotificationTextColor.LIGHT -> return provider.getMinimalLightIconUri(code, dayTime)
                NotificationTextColor.GREY -> return provider.getMinimalGreyIconUri(code, dayTime)
                NotificationTextColor.DARK -> return provider.getMinimalDarkIconUri(code, dayTime)
            }
        }
        return provider.getWeatherIconUri(code, dayTime)
    }

    @JvmStatic
    fun getMinimalXmlIcon(provider: ResourceProvider, code: WeatherCode, daytime: Boolean): Drawable {
        return provider.getMinimalXmlIcon(code, daytime)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @JvmStatic
    fun getMinimalIcon(provider: ResourceProvider, code: WeatherCode, daytime: Boolean): Icon {
        return provider.getMinimalIcon(code, daytime)
    }

    @JvmStatic
    @DrawableRes
    fun getDefaultMinimalXmlIconId(code: WeatherCode, daytime: Boolean): Int {
        val id = DefaultResourceProvider().getMinimalXmlIconId(code, daytime)
        return if (id == 0) {
            R.drawable.weather_clear_day_mini_xml
        } else {
            id
        }
    }

    @JvmStatic
    fun getShortcutsIcon(provider: ResourceProvider, code: WeatherCode, dayTime: Boolean): Drawable {
        return provider.getShortcutsIcon(code, dayTime)
    }

    @JvmStatic
    fun getShortcutsForegroundIcon(
        provider: ResourceProvider,
        code: WeatherCode,
        dayTime: Boolean
    ): Drawable {
        return provider.getShortcutsForegroundIcon(code, dayTime)
    }

    @JvmStatic
    fun getSunDrawable(provider: ResourceProvider): Drawable {
        return provider.getSunDrawable()
    }

    @JvmStatic
    fun getMoonDrawable(provider: ResourceProvider): Drawable {
        return provider.getMoonDrawable()
    }

    @JvmStatic
    @DrawableRes
    fun getTempIconId(context: Context, temp: Int): Int {
        val builder = StringBuilder("notif_temp_")
        if (temp < 0) {
            builder.append("neg_")
        }
        builder.append(abs(temp))

        val id = ResourceUtils.getResId(context, builder.toString(), "drawable")
        return if (id == 0) {
            R.drawable.notif_temp_0
        } else {
            id
        }
    }
}
