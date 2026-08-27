package wangdaye.com.geometricweather.theme.resource

import android.content.Context
import wangdaye.com.geometricweather.GeometricWeather
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
                .getInstance(GeometricWeather.getInstance())
                .iconProvider
        )
    }

    @JvmStatic
    fun getNewInstance(packageName: String?): ResourceProvider {
        val context: Context = GeometricWeather.getInstance()
        val defaultProvider = DefaultResourceProvider()

        if (DefaultResourceProvider.isDefaultIconProvider(packageName)) {
            return defaultProvider
        }
        if (PixelResourcesProvider.isPixelIconProvider(packageName)) {
            return PixelResourcesProvider(defaultProvider)
        }
        if (IconPackResourcesProvider.isIconPackIconProvider(context, packageName)) {
            return IconPackResourcesProvider(context, packageName, defaultProvider)
        }
        if (ChronusResourceProvider.isChronusIconProvider(context, packageName)) {
            return ChronusResourceProvider(context, packageName, defaultProvider)
        }
        return IconPackResourcesProvider(context, packageName, defaultProvider)
    }

    @JvmStatic
    fun getProviderList(context: Context): List<ResourceProvider> {
        val providerList = ArrayList<ResourceProvider>()
        val defaultProvider = DefaultResourceProvider()

        providerList.add(defaultProvider)
        providerList.add(PixelResourcesProvider(defaultProvider))
        providerList.addAll(IconPackResourcesProvider.getProviderList(context, defaultProvider))

        val chronusIconPackList = ChronusResourceProvider.getProviderList(context, defaultProvider)
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
