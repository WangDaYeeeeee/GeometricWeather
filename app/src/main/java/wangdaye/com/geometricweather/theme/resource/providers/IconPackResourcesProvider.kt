package wangdaye.com.geometricweather.theme.resource.providers

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import androidx.annotation.Size
import androidx.core.content.res.ResourcesCompat
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode
import wangdaye.com.geometricweather.theme.resource.utils.Config
import wangdaye.com.geometricweather.theme.resource.utils.Constants
import wangdaye.com.geometricweather.theme.resource.utils.ResourceUtils
import wangdaye.com.geometricweather.theme.resource.utils.XmlHelper

open class IconPackResourcesProvider(
    c: Context,
    pkgName: String,
    defaultProvider: ResourceProvider
) : ResourceProvider() {

    private val mDefaultProvider: ResourceProvider = defaultProvider

    private lateinit var mContext: Context
    private lateinit var mProviderName: String
    private var mIconDrawable: Drawable? = null

    private lateinit var mConfig: Config
    private lateinit var mDrawableFilter: Map<String, String>
    private lateinit var mAnimatorFilter: Map<String, String>
    private lateinit var mShortcutFilter: Map<String, String>
    private lateinit var mSunMoonFilter: Map<String, String>

    init {
        try {
            mContext = c.createPackageContext(
                pkgName, Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
            )

            val manager = mContext.packageManager
            val info = manager.getApplicationInfo(pkgName, PackageManager.GET_META_DATA)
            mProviderName = manager.getApplicationLabel(info).toString()

            mIconDrawable = mContext.applicationInfo.loadIcon(mContext.packageManager)

            val res = mContext.resources

            var resId = getMetaDataResource(Constants.META_DATA_PROVIDER_CONFIG)
            mConfig = if (resId != 0) {
                XmlHelper.getConfig(res.getXml(resId))
            } else {
                Config()
            }

            resId = getMetaDataResource(Constants.META_DATA_DRAWABLE_FILTER)
            mDrawableFilter = if (resId != 0) {
                XmlHelper.getFilterMap(res.getXml(resId))
            } else {
                HashMap()
            }

            resId = getMetaDataResource(Constants.META_DATA_ANIMATOR_FILTER)
            mAnimatorFilter = if (resId != 0) {
                XmlHelper.getFilterMap(res.getXml(resId))
            } else {
                HashMap()
            }

            resId = getMetaDataResource(Constants.META_DATA_SHORTCUT_FILTER)
            mShortcutFilter = if (resId != 0) {
                XmlHelper.getFilterMap(res.getXml(resId))
            } else {
                HashMap()
            }

            resId = getMetaDataResource(Constants.META_DATA_SUN_MOON_FILTER)
            mSunMoonFilter = if (resId != 0) {
                XmlHelper.getFilterMap(res.getXml(resId))
            } else {
                HashMap()
            }
        } catch (e: Exception) {
            buildDefaultInstance(c)
        }
    }

    private fun buildDefaultInstance(c: Context) {
        mContext = c.applicationContext
        mProviderName = c.getString(R.string.geometric_weather)
        mIconDrawable = mDefaultProvider.providerIcon

        val res = mContext.resources
        try {
            mConfig = XmlHelper.getConfig(res.getXml(R.xml.icon_provider_config))
            mDrawableFilter = XmlHelper.getFilterMap(res.getXml(R.xml.icon_provider_drawable_filter))
            mAnimatorFilter = XmlHelper.getFilterMap(res.getXml(R.xml.icon_provider_animator_filter))
            mShortcutFilter = XmlHelper.getFilterMap(res.getXml(R.xml.icon_provider_shortcut_filter))
            mSunMoonFilter = XmlHelper.getFilterMap(res.getXml(R.xml.icon_provider_sun_moon_filter))
        } catch (e: Exception) {
            mConfig = Config()
            mDrawableFilter = HashMap()
            mAnimatorFilter = HashMap()
            mShortcutFilter = HashMap()
            mSunMoonFilter = HashMap()
        }
    }

    private fun getMetaDataResource(key: String): Int {
        return try {
            mContext.packageManager.getApplicationInfo(
                mContext.packageName,
                PackageManager.GET_META_DATA
            ).metaData.getInt(key)
        } catch (e: Exception) {
            0
        }
    }

    open override val packageName: String
        get() = mContext.packageName

    open override val providerName: String
        get() = mProviderName

    open override val providerIcon: Drawable
        get() = mIconDrawable ?: getWeatherIcon(WeatherCode.CLEAR, true)

    override fun getWeatherIcon(code: WeatherCode, dayTime: Boolean): Drawable {
        try {
            if (mConfig.hasWeatherIcons) {
                return ResourceUtils.nonNull(
                    getDrawable(getWeatherIconName(code, dayTime))
                )
            }
        } catch (ignore: Exception) {
        }
        return mDefaultProvider.getWeatherIcon(code, dayTime)
    }

    override fun getWeatherIconUri(code: WeatherCode, dayTime: Boolean): Uri {
        if (mConfig.hasWeatherIcons) {
            val resName = getWeatherIconName(code, dayTime)
            val resId = getResId(mContext, resName, "drawable")
            if (resId != 0) {
                return getDrawableUri(resName)
            }
        }
        return mDefaultProvider.getWeatherIconUri(code, dayTime)
    }

    @Size(3)
    open override fun getWeatherIcons(code: WeatherCode, dayTime: Boolean): Array<Drawable?> {
        if (mConfig.hasWeatherIcons) {
            return if (mConfig.hasWeatherAnimators) {
                arrayOf(
                    getDrawable(getWeatherIconName(code, dayTime, 1)),
                    getDrawable(getWeatherIconName(code, dayTime, 2)),
                    getDrawable(getWeatherIconName(code, dayTime, 3))
                )
            } else {
                arrayOf(getWeatherIcon(code, dayTime), null, null)
            }
        }
        return mDefaultProvider.getWeatherIcons(code, dayTime)
    }

    private fun getDrawable(resName: String?): Drawable? {
        if (resName == null) {
            return null
        }
        return try {
            ResourcesCompat.getDrawable(
                mContext.resources,
                ResourceUtils.nonNull(getResId(mContext, resName, "drawable")),
                null
            )
        } catch (e: Exception) {
            null
        }
    }

    open fun getWeatherIconName(code: WeatherCode, daytime: Boolean): String {
        return getFilterResource(
            mDrawableFilter,
            innerGetWeatherIconName(code, daytime)
        )
    }

    open fun getWeatherIconName(
        code: WeatherCode,
        daytime: Boolean,
        @IntRange(from = 1, to = 3) index: Int
    ): String? {
        return getFilterResource(
            mDrawableFilter,
            innerGetWeatherIconName(code, daytime) + Constants.SEPARATOR + index
        )
    }

    @Size(3)
    open override fun getWeatherAnimators(code: WeatherCode, dayTime: Boolean): Array<Animator?> {
        if (mConfig.hasWeatherIcons) {
            return if (mConfig.hasWeatherAnimators) {
                arrayOf(
                    getAnimator(getWeatherAnimatorName(code, dayTime, 1)),
                    getAnimator(getWeatherAnimatorName(code, dayTime, 2)),
                    getAnimator(getWeatherAnimatorName(code, dayTime, 3))
                )
            } else {
                arrayOf(null, null, null)
            }
        }
        return mDefaultProvider.getWeatherAnimators(code, dayTime)
    }

    private fun getAnimator(resName: String?): Animator? {
        if (resName == null) {
            return null
        }
        return try {
            AnimatorInflater.loadAnimator(
                mContext,
                ResourceUtils.nonNull(getResId(mContext, resName, "animator"))
            )
        } catch (e: Exception) {
            null
        }
    }

    open fun getWeatherAnimatorName(
        code: WeatherCode,
        daytime: Boolean,
        @IntRange(from = 1, to = 3) index: Int
    ): String? {
        return getFilterResource(
            mAnimatorFilter,
            innerGetWeatherAnimatorName(code, daytime) + Constants.SEPARATOR + index
        )
    }

    override fun getMinimalLightIcon(code: WeatherCode, dayTime: Boolean): Drawable {
        try {
            if (mConfig.hasMinimalIcons) {
                return ResourceUtils.nonNull(
                    getDrawable(getMiniLightIconName(code, dayTime))
                )
            }
        } catch (ignore: Exception) {
        }
        return mDefaultProvider.getMinimalLightIcon(code, dayTime)
    }

    override fun getMinimalLightIconUri(code: WeatherCode, dayTime: Boolean): Uri {
        if (mConfig.hasMinimalIcons) {
            val resName = getMiniLightIconName(code, dayTime)
            val resId = getResId(mContext, resName, "drawable")
            if (resId != 0) {
                return getDrawableUri(resName)
            }
        }
        return mDefaultProvider.getMinimalLightIconUri(code, dayTime)
    }

    override fun getMinimalGreyIcon(code: WeatherCode, dayTime: Boolean): Drawable {
        try {
            if (mConfig.hasMinimalIcons) {
                return ResourceUtils.nonNull(
                    getDrawable(getMiniGreyIconName(code, dayTime))
                )
            }
        } catch (ignore: Exception) {
        }
        return mDefaultProvider.getMinimalGreyIcon(code, dayTime)
    }

    override fun getMinimalGreyIconUri(code: WeatherCode, dayTime: Boolean): Uri {
        if (mConfig.hasMinimalIcons) {
            val resName = getMiniGreyIconName(code, dayTime)
            val resId = getResId(mContext, resName, "drawable")
            if (resId != 0) {
                return getDrawableUri(resName)
            }
        }
        return mDefaultProvider.getMinimalGreyIconUri(code, dayTime)
    }

    override fun getMinimalDarkIcon(code: WeatherCode, dayTime: Boolean): Drawable {
        try {
            if (mConfig.hasMinimalIcons) {
                return ResourceUtils.nonNull(
                    getDrawable(getMiniDarkIconName(code, dayTime))
                )
            }
        } catch (ignore: Exception) {
        }
        return mDefaultProvider.getMinimalDarkIcon(code, dayTime)
    }

    override fun getMinimalDarkIconUri(code: WeatherCode, dayTime: Boolean): Uri {
        if (mConfig.hasMinimalIcons) {
            val resName = getMiniDarkIconName(code, dayTime)
            val resId = getResId(mContext, resName, "drawable")
            if (resId != 0) {
                return getDrawableUri(resName)
            }
        }
        return mDefaultProvider.getMinimalDarkIconUri(code, dayTime)
    }

    override fun getMinimalXmlIcon(code: WeatherCode, dayTime: Boolean): Drawable {
        try {
            if (mConfig.hasMinimalIcons) {
                return ResourceUtils.nonNull(
                    getDrawable(getMiniXmlIconName(code, dayTime))
                )
            }
        } catch (ignore: Exception) {
        }
        return mDefaultProvider.getMinimalXmlIcon(code, dayTime)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun getMinimalIcon(code: WeatherCode, dayTime: Boolean): Icon {
        try {
            if (mConfig.hasMinimalIcons) {
                return ResourceUtils.nonNull(
                    Icon.createWithResource(
                        mContext,
                        ResourceUtils.nonNull(
                            getResId(
                                mContext,
                                getMiniXmlIconName(code, dayTime),
                                "drawable"
                            )
                        )
                    )
                )
            }
        } catch (ignore: Exception) {
        }
        return mDefaultProvider.getMinimalIcon(code, dayTime)
    }

    fun getMiniLightIconName(code: WeatherCode, daytime: Boolean): String {
        return getFilterResource(
            mDrawableFilter,
            innerGetMiniIconName(code, daytime) + Constants.SEPARATOR + Constants.LIGHT
        )
    }

    fun getMiniGreyIconName(code: WeatherCode, daytime: Boolean): String {
        return getFilterResource(
            mDrawableFilter,
            innerGetMiniIconName(code, daytime) + Constants.SEPARATOR + Constants.GREY
        )
    }

    fun getMiniDarkIconName(code: WeatherCode, daytime: Boolean): String {
        return getFilterResource(
            mDrawableFilter,
            innerGetMiniIconName(code, daytime) + Constants.SEPARATOR + Constants.DARK
        )
    }

    fun getMiniXmlIconName(code: WeatherCode, daytime: Boolean): String {
        return getFilterResource(
            mDrawableFilter,
            innerGetMiniIconName(code, daytime) + Constants.SEPARATOR + Constants.XML
        )
    }

    override fun getShortcutsIcon(code: WeatherCode, dayTime: Boolean): Drawable {
        try {
            if (mConfig.hasShortcutIcons) {
                return ResourceUtils.nonNull(
                    getDrawable(getShortcutsIconName(code, dayTime))
                )
            }
        } catch (ignore: Exception) {
        }
        return mDefaultProvider.getShortcutsIcon(code, dayTime)
    }

    override fun getShortcutsForegroundIcon(code: WeatherCode, dayTime: Boolean): Drawable {
        try {
            if (mConfig.hasShortcutIcons) {
                return ResourceUtils.nonNull(
                    getDrawable(getShortcutsForegroundIconName(code, dayTime))
                )
            }
        } catch (ignore: Exception) {
        }
        return mDefaultProvider.getShortcutsForegroundIcon(code, dayTime)
    }

    fun getShortcutsIconName(code: WeatherCode, daytime: Boolean): String {
        return getFilterResource(
            mShortcutFilter,
            innerGetShortcutsIconName(code, daytime)
        )
    }

    fun getShortcutsForegroundIconName(code: WeatherCode, daytime: Boolean): String {
        return getFilterResource(
            mShortcutFilter,
            innerGetShortcutsIconName(code, daytime) + Constants.SEPARATOR + Constants.FOREGROUND
        )
    }

    open override fun getSunDrawable(): Drawable {
        if (mConfig.hasSunMoonDrawables) {
            return try {
                ResourceUtils.nonNull(
                    getReflectDrawable(getSunDrawableClassName())
                )
            } catch (e: Exception) {
                getWeatherIcon(WeatherCode.CLEAR, true)
            }
        }
        return mDefaultProvider.getSunDrawable()
    }

    open override fun getMoonDrawable(): Drawable {
        if (mConfig.hasSunMoonDrawables) {
            return try {
                ResourceUtils.nonNull(
                    getReflectDrawable(getMoonDrawableClassName())
                )
            } catch (e: Exception) {
                getWeatherIcon(WeatherCode.CLEAR, false)
            }
        }
        return mDefaultProvider.getMoonDrawable()
    }

    @Suppress("DEPRECATION")
    private fun getReflectDrawable(className: String?): Drawable? {
        return try {
            val clazz = mContext.classLoader.loadClass(className)
            clazz.newInstance() as Drawable
        } catch (e: Exception) {
            null
        }
    }

    open fun getSunDrawableClassName(): String? {
        return mSunMoonFilter[Constants.RESOURCES_SUN]
    }

    open fun getMoonDrawableClassName(): String? {
        return mSunMoonFilter[Constants.RESOURCES_MOON]
    }

    companion object {
        @JvmStatic
        fun getProviderList(
            context: Context,
            defaultProvider: ResourceProvider
        ): List<IconPackResourcesProvider> {
            val providerList = ArrayList<IconPackResourcesProvider>()
            val infoList = context.packageManager.queryIntentActivities(
                Intent(Constants.ACTION_ICON_PROVIDER),
                PackageManager.GET_RESOLVED_FILTER
            )
            for (info in infoList) {
                providerList.add(
                    IconPackResourcesProvider(
                        context,
                        info.activityInfo.applicationInfo.packageName,
                        defaultProvider
                    )
                )
            }
            return providerList
        }

        @JvmStatic
        fun isIconPackIconProvider(context: Context, packageName: String): Boolean {
            val infoList = context.packageManager.queryIntentActivities(
                Intent(Constants.ACTION_ICON_PROVIDER),
                PackageManager.GET_RESOLVED_FILTER
            )
            for (info in infoList) {
                if (packageName == info.activityInfo.applicationInfo.packageName) {
                    return true
                }
            }
            return false
        }

        @JvmStatic
        private fun getFilterResource(filter: Map<String, String>, key: String): String {
            return try {
                ResourceUtils.nonNull(filter[key])
            } catch (e: Exception) {
                key
            }
        }

        @JvmStatic
        private fun innerGetWeatherIconName(code: WeatherCode, daytime: Boolean): String {
            return Constants.getResourcesName(code) +
                Constants.SEPARATOR + if (daytime) Constants.DAY else Constants.NIGHT
        }

        @JvmStatic
        private fun innerGetWeatherAnimatorName(code: WeatherCode, daytime: Boolean): String {
            return Constants.getResourcesName(code) +
                Constants.SEPARATOR + if (daytime) Constants.DAY else Constants.NIGHT
        }

        @JvmStatic
        private fun innerGetMiniIconName(code: WeatherCode, daytime: Boolean): String {
            return innerGetWeatherIconName(code, daytime) + Constants.SEPARATOR + Constants.MINI
        }

        @JvmStatic
        private fun innerGetShortcutsIconName(code: WeatherCode, daytime: Boolean): String {
            return Constants.getShortcutsName(code) +
                Constants.SEPARATOR + if (daytime) Constants.DAY else Constants.NIGHT
        }
    }
}
