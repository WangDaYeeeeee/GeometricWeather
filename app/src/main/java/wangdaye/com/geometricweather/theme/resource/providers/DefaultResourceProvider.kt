package wangdaye.com.geometricweather.theme.resource.providers

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import androidx.annotation.Size
import androidx.core.content.res.ResourcesCompat
import wangdaye.com.geometricweather.GeometricWeather
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode
import wangdaye.com.geometricweather.common.ui.images.MoonDrawable
import wangdaye.com.geometricweather.common.ui.images.SunDrawable
import wangdaye.com.geometricweather.theme.resource.utils.Constants
import wangdaye.com.geometricweather.theme.resource.utils.ResourceUtils
import wangdaye.com.geometricweather.theme.resource.utils.XmlHelper
open class DefaultResourceProvider : ResourceProvider() {

    private val mContext: Context = GeometricWeather.instance
    private val mProviderName: String = mContext.getString(R.string.geometric_weather)
    private val mIconDrawable: Drawable? =
        mContext.applicationInfo.loadIcon(mContext.packageManager)

    private var mDrawableFilter: Map<String, String>
    private var mAnimatorFilter: Map<String, String>
    private var mShortcutFilter: Map<String, String>

    init {
        val res = mContext.resources
        try {
            mDrawableFilter = XmlHelper.getFilterMap(res.getXml(R.xml.icon_provider_drawable_filter))
            mAnimatorFilter = XmlHelper.getFilterMap(res.getXml(R.xml.icon_provider_animator_filter))
            mShortcutFilter = XmlHelper.getFilterMap(res.getXml(R.xml.icon_provider_shortcut_filter))
        } catch (e: Exception) {
            mDrawableFilter = HashMap()
            mAnimatorFilter = HashMap()
            mShortcutFilter = HashMap()
        }
    }

    override val packageName: String
        get() = mContext.packageName

    override val providerName: String
        get() = mProviderName

    override val providerIcon: Drawable
        get() = mIconDrawable!!

    override fun getWeatherIcon(code: WeatherCode, dayTime: Boolean): Drawable {
        return requireNotNull(getDrawable(getWeatherIconName(code, dayTime)))
    }

    override fun getWeatherIconUri(code: WeatherCode, dayTime: Boolean): Uri {
        return requireNotNull(getDrawableUri(getWeatherIconName(code, dayTime)))
    }

    @Size(3)
    override fun getWeatherIcons(code: WeatherCode, dayTime: Boolean): Array<Drawable?> {
        return arrayOf(
            getDrawable(getWeatherIconName(code, dayTime, 1)),
            getDrawable(getWeatherIconName(code, dayTime, 2)),
            getDrawable(getWeatherIconName(code, dayTime, 3))
        )
    }

    private fun getDrawable(resName: String): Drawable? {
        return try {
            ResourcesCompat.getDrawable(
                mContext.resources,
                ResourceUtils.nonNull(ResourceUtils.getResId(mContext, resName, "drawable")),
                null
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun getWeatherIconName(code: WeatherCode, daytime: Boolean): String {
        return getFilterResource(mDrawableFilter, innerGetWeatherIconName(code, daytime))
    }

    private fun getWeatherIconName(
        code: WeatherCode,
        daytime: Boolean,
        @IntRange(from = 1, to = 3) index: Int
    ): String {
        return getFilterResource(
            mDrawableFilter,
            innerGetWeatherIconName(code, daytime) + Constants.SEPARATOR + index
        )
    }

    @Size(3)
    override fun getWeatherAnimators(code: WeatherCode, dayTime: Boolean): Array<Animator?> {
        return arrayOf(
            getAnimator(getWeatherAnimatorName(code, dayTime, 1)),
            getAnimator(getWeatherAnimatorName(code, dayTime, 2)),
            getAnimator(getWeatherAnimatorName(code, dayTime, 3))
        )
    }

    private fun getAnimator(resName: String): Animator? {
        return try {
            AnimatorInflater.loadAnimator(
                mContext,
                ResourceUtils.nonNull(ResourceUtils.getResId(mContext, resName, "animator"))
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun getWeatherAnimatorName(
        code: WeatherCode,
        daytime: Boolean,
        @IntRange(from = 1, to = 3) index: Int
    ): String {
        return getFilterResource(
            mAnimatorFilter,
            innerGetWeatherAnimatorName(code, daytime) + Constants.SEPARATOR + index
        )
    }

    override fun getMinimalLightIcon(code: WeatherCode, dayTime: Boolean): Drawable {
        return requireNotNull(getDrawable(getMiniLightIconName(code, dayTime)))
    }

    override fun getMinimalLightIconUri(code: WeatherCode, dayTime: Boolean): Uri {
        return requireNotNull(getDrawableUri(getMiniLightIconName(code, dayTime)))
    }

    override fun getMinimalGreyIcon(code: WeatherCode, dayTime: Boolean): Drawable {
        return requireNotNull(getDrawable(getMiniGreyIconName(code, dayTime)))
    }

    override fun getMinimalGreyIconUri(code: WeatherCode, dayTime: Boolean): Uri {
        return requireNotNull(getDrawableUri(getMiniGreyIconName(code, dayTime)))
    }

    override fun getMinimalDarkIcon(code: WeatherCode, dayTime: Boolean): Drawable {
        return requireNotNull(getDrawable(getMiniDarkIconName(code, dayTime)))
    }

    override fun getMinimalDarkIconUri(code: WeatherCode, dayTime: Boolean): Uri {
        return requireNotNull(getDrawableUri(getMiniDarkIconName(code, dayTime)))
    }

    override fun getMinimalXmlIcon(code: WeatherCode, dayTime: Boolean): Drawable {
        return requireNotNull(getDrawable(getMiniXmlIconName(code, dayTime)))
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun getMinimalIcon(code: WeatherCode, dayTime: Boolean): Icon {
        return requireNotNull(
            Icon.createWithResource(mContext, getMinimalXmlIconId(code, dayTime))
        )
    }

    @DrawableRes
    fun getMinimalXmlIconId(code: WeatherCode, dayTime: Boolean): Int {
        return ResourceUtils.getResId(mContext, getMiniXmlIconName(code, dayTime), "drawable")
    }

    private fun getMiniLightIconName(code: WeatherCode, daytime: Boolean): String {
        return getFilterResource(
            mDrawableFilter,
            innerGetMiniIconName(code, daytime) + Constants.SEPARATOR + Constants.LIGHT
        )
    }

    private fun getMiniGreyIconName(code: WeatherCode, daytime: Boolean): String {
        return getFilterResource(
            mDrawableFilter,
            innerGetMiniIconName(code, daytime) + Constants.SEPARATOR + Constants.GREY
        )
    }

    private fun getMiniDarkIconName(code: WeatherCode, daytime: Boolean): String {
        return getFilterResource(
            mDrawableFilter,
            innerGetMiniIconName(code, daytime) + Constants.SEPARATOR + Constants.DARK
        )
    }

    private fun getMiniXmlIconName(code: WeatherCode, daytime: Boolean): String {
        return getFilterResource(
            mDrawableFilter,
            innerGetMiniIconName(code, daytime) + Constants.SEPARATOR + Constants.XML
        )
    }

    override fun getShortcutsIcon(code: WeatherCode, dayTime: Boolean): Drawable {
        return requireNotNull(getDrawable(getShortcutsIconName(code, dayTime)))
    }

    override fun getShortcutsForegroundIcon(code: WeatherCode, dayTime: Boolean): Drawable {
        return requireNotNull(getDrawable(getShortcutsForegroundIconName(code, dayTime)))
    }

    private fun getShortcutsIconName(code: WeatherCode, daytime: Boolean): String {
        return getFilterResource(mShortcutFilter, innerGetShortcutsIconName(code, daytime))
    }

    private fun getShortcutsForegroundIconName(code: WeatherCode, daytime: Boolean): String {
        return getFilterResource(
            mShortcutFilter,
            getShortcutsIconName(code, daytime) + Constants.SEPARATOR + Constants.FOREGROUND
        )
    }

    override fun getSunDrawable(): Drawable = SunDrawable()

    override fun getMoonDrawable(): Drawable = MoonDrawable()

    companion object {
        @JvmStatic
        fun isDefaultIconProvider(packageName: String?): Boolean {
            return packageName == GeometricWeather.instance.packageName
        }

        private fun getFilterResource(filter: Map<String, String>, key: String): String {
            val value = filter[key]
            return if (TextUtils.isEmpty(value)) key else value!!
        }

        private fun innerGetWeatherIconName(code: WeatherCode, daytime: Boolean): String {
            return Constants.getResourcesName(code) + Constants.SEPARATOR +
                if (daytime) Constants.DAY else Constants.NIGHT
        }

        private fun innerGetWeatherAnimatorName(code: WeatherCode, daytime: Boolean): String {
            return Constants.getResourcesName(code) + Constants.SEPARATOR +
                if (daytime) Constants.DAY else Constants.NIGHT
        }

        private fun innerGetMiniIconName(code: WeatherCode, daytime: Boolean): String {
            return innerGetWeatherIconName(code, daytime) + Constants.SEPARATOR + Constants.MINI
        }

        private fun innerGetShortcutsIconName(code: WeatherCode, daytime: Boolean): String {
            return Constants.getShortcutsName(code) + Constants.SEPARATOR +
                if (daytime) Constants.DAY else Constants.NIGHT
        }
    }
}
