package wangdaye.com.geometricweather.theme.resource

import android.content.Context
import wangdaye.com.geometricweather.common.basic.ApplicationContextHolder
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.resource.providers.ChronusResourceProvider
import wangdaye.com.geometricweather.theme.resource.providers.DefaultResourceProvider
import wangdaye.com.geometricweather.theme.resource.providers.IconPackResourcesProvider
import wangdaye.com.geometricweather.theme.resource.providers.PixelResourcesProvider
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider

object ResourcesProviderFactory {

    @JvmStatic
    fun getNewInstance(): ResourceProvider {
        return getNewInstance(
            SettingsManager
                .getInstance(ApplicationContextHolder.application)
                .iconProvider
        )
    }

    @JvmStatic
    fun getNewInstance(packageName: String?): ResourceProvider {
        val pkg = packageName ?: ApplicationContextHolder.application.packageName
        val context: Context = ApplicationContextHolder.application
        val defaultProvider = DefaultResourceProvider()

        if (DefaultResourceProvider.isDefaultIconProvider(pkg)) {
            return defaultProvider
        }
        if (PixelResourcesProvider.isPixelIconProvider(pkg)) {
            return PixelResourcesProvider(defaultProvider)
        }
        if (IconPackResourcesProvider.isIconPackIconProvider(context, pkg)) {
            return IconPackResourcesProvider(context, pkg, defaultProvider)
        }
        if (ChronusResourceProvider.isChronusIconProvider(context, pkg)) {
            return ChronusResourceProvider(context, pkg, defaultProvider)
        }
        return IconPackResourcesProvider(context, pkg, defaultProvider)
    }

    @JvmStatic
    fun getProviderList(context: Context): List<ResourceProvider> {
        val providerList = ArrayList<ResourceProvider>()
        val defaultProvider = DefaultResourceProvider()

        providerList.add(defaultProvider)
        providerList.add(PixelResourcesProvider(defaultProvider))
        providerList.addAll(IconPackResourcesProvider.getProviderList(context, defaultProvider))

        val chronusIconPackList =
            ChronusResourceProvider.getProviderList(context, defaultProvider).toMutableList()
        var i = chronusIconPackList.size - 1
        while (i >= 0) {
            for (j in providerList.indices) {
                if (chronusIconPackList[i] == providerList[j]) {
                    chronusIconPackList.removeAt(i)
                    break
                }
            }
            i--
        }
        providerList.addAll(chronusIconPackList)
        return providerList
    }
}
