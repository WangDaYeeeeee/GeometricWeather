package wangdaye.com.geometricweather.background.interfaces

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.service.quicksettings.Tile
import androidx.annotation.RequiresApi
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.utils.helpers.IntentHelper
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
