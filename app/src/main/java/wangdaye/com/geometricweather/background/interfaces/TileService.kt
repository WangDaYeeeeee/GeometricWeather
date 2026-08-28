package wangdaye.com.geometricweather.background.interfaces

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.service.quicksettings.Tile
import androidx.annotation.RequiresApi
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.utils.helpers.IntentHelper
import wangdaye.com.geometricweather.common.utils.helpers.startAboutActivity
import wangdaye.com.geometricweather.common.utils.helpers.startAlertActivity
import wangdaye.com.geometricweather.common.utils.helpers.startAllergenActivity
import wangdaye.com.geometricweather.common.utils.helpers.startAwakeForegroundUpdateService
import wangdaye.com.geometricweather.common.utils.helpers.startCardDisplayManageActivity
import wangdaye.com.geometricweather.common.utils.helpers.startDailyTrendDisplayManageActivity
import wangdaye.com.geometricweather.common.utils.helpers.startDailyWeatherActivity
import wangdaye.com.geometricweather.common.utils.helpers.startHourlyTrendDisplayManageActivityForResult
import wangdaye.com.geometricweather.common.utils.helpers.startLiveWallpaperActivity
import wangdaye.com.geometricweather.common.utils.helpers.startMainActivity
import wangdaye.com.geometricweather.common.utils.helpers.startMainActivityForManagement
import wangdaye.com.geometricweather.common.utils.helpers.startPreviewIconActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSearchActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSelectProviderActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSettingsActivity
import wangdaye.com.geometricweather.common.utils.helpers.buildAwakeUpdateActivityIntent
import wangdaye.com.geometricweather.common.utils.helpers.buildMainActivityIntent
import wangdaye.com.geometricweather.common.utils.helpers.buildMainActivityShowAlertsIntent
import wangdaye.com.geometricweather.common.utils.helpers.buildMainActivityShowDailyForecastIntent
import wangdaye.com.geometricweather.common.utils.helpers.getAwakeForegroundUpdateServiceIntent
import wangdaye.com.geometricweather.db.DatabaseHelper
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory

@RequiresApi(api = Build.VERSION_CODES.N)
class TileService : android.service.quicksettings.TileService() {

    override fun onTileAdded() {
        refreshTile(this, qsTile)
    }

    override fun onTileRemoved() {
        // do nothing.
    }

    override fun onStartListening() {
        refreshTile(this, qsTile)
    }

    override fun onStopListening() {
        refreshTile(this, qsTile)
    }

    @SuppressLint("WrongConstant")
    override fun onClick() {
        try {
            val statusBarManager = getSystemService("statusbar")
            if (statusBarManager != null) {
                statusBarManager.javaClass
                    .getMethod("collapsePanels")
                    .invoke(statusBarManager)
            }
        } catch (ignored: Exception) {
        }
        IntentHelper.startMainActivity(this)
    }

    companion object {
        private fun refreshTile(context: Context, tile: Tile?) {
            if (tile == null) {
                return
            }
            var location = DatabaseHelper.getInstance(context).readLocationList()[0]
            location = Location.copy(location, DatabaseHelper.getInstance(context).readWeather(location))
            val weather = location.weather ?: return
            tile.icon = ResourceHelper.getMinimalIcon(
                ResourcesProviderFactory.getNewInstance(),
                weather.current.weatherCode,
                location.isDaylight
            )
            tile.label = weather.current.temperature.getTemperature(
                context,
                SettingsManager.getInstance(context).temperatureUnit
            )
            tile.state = Tile.STATE_INACTIVE
            tile.updateTile()
        }
    }
}
