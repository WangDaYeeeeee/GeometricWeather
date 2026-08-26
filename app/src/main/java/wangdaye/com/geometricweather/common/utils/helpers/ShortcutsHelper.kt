package wangdaye.com.geometricweather.common.utils.helpers

import android.content.Context
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode
import wangdaye.com.geometricweather.db.DatabaseHelper
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider

/**
 * Shortcuts manager.
 */
@RequiresApi(api = Build.VERSION_CODES.N_MR1)
object ShortcutsHelper {

    @JvmStatic
    fun refreshShortcutsInNewThread(c: Context, locationList: List<Location>) {
        AsyncHelper.runOnIO {
            val shortcutManager = c.getSystemService(ShortcutManager::class.java) ?: return@runOnIO
            val list = Location.excludeInvalidResidentLocation(c, locationList)
            val provider = ResourcesProviderFactory.getNewInstance()
            val shortcutList = ArrayList<ShortcutInfo>()

            var icon: Icon
            var title = c.getString(R.string.refresh)
            icon = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Icon.createWithAdaptiveBitmap(
                    drawableToBitmap(
                        ContextCompat.getDrawable(c, R.drawable.shortcuts_refresh_foreground)!!
                    )
                )
            } else {
                Icon.createWithResource(c, R.drawable.shortcuts_refresh)
            }
            shortcutList.add(
                ShortcutInfo.Builder(c, "refresh_data")
                    .setIcon(icon)
                    .setShortLabel(title)
                    .setLongLabel(title)
                    .setIntent(IntentHelper.buildAwakeUpdateActivityIntent())
                    .build()
            )

            val count = (shortcutManager.maxShortcutCountPerActivity - 1).coerceAtMost(list.size)
            for (i in 0 until count) {
                val weather = DatabaseHelper.getInstance(c).readWeather(list[i])
                icon = if (weather != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getAdaptiveIcon(
                            provider,
                            weather.current.weatherCode,
                            list[i].isDaylight
                        )
                    } else {
                        getIcon(
                            provider,
                            weather.current.weatherCode,
                            list[i].isDaylight
                        )
                    }
                } else {
                    getIcon(provider, WeatherCode.CLEAR, true)
                }
                title = if (list[i].isCurrentPosition) c.getString(R.string.current_location) else list[i].getCityName(c)
                shortcutList.add(
                    ShortcutInfo.Builder(c, list[i].formattedId)
                        .setIcon(icon)
                        .setShortLabel(title)
                        .setLongLabel(title)
                        .setIntent(IntentHelper.buildMainActivityIntent(list[i]))
                        .build()
                )
            }
            try {
                shortcutManager.dynamicShortcuts = shortcutList
            } catch (_: Exception) {
            }
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun getAdaptiveIcon(provider: ResourceProvider, code: WeatherCode, daytime: Boolean): Icon {
        return Icon.createWithAdaptiveBitmap(
            drawableToBitmap(ResourceHelper.getShortcutsForegroundIcon(provider, code, daytime))
        )
    }

    private fun getIcon(provider: ResourceProvider, code: WeatherCode, daytime: Boolean): Icon {
        return Icon.createWithBitmap(
            drawableToBitmap(ResourceHelper.getShortcutsIcon(provider, code, daytime))
        )
    }
}
