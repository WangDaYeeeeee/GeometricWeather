package wangdaye.com.geometricweather.common.bus

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.settings.SettingsChangedMessage

/**
 * Process-wide UI events that have no single ViewModel owner
 * (settings persistence, home system-bar requests, polling → MainActivity).
 */
object AppEvents {

    private val _settingsChanged = MutableSharedFlow<SettingsChangedMessage>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val settingsChanged: SharedFlow<SettingsChangedMessage> = _settingsChanged.asSharedFlow()

    private val _modifyMainSystemBar = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val modifyMainSystemBar: SharedFlow<Unit> = _modifyMainSystemBar.asSharedFlow()

    private val _locationUpdatedFromBackground = MutableSharedFlow<Location>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val locationUpdatedFromBackground: SharedFlow<Location> =
        _locationUpdatedFromBackground.asSharedFlow()

    fun notifySettingsChanged() {
        _settingsChanged.tryEmit(SettingsChangedMessage())
    }

    fun requestModifyMainSystemBar() {
        _modifyMainSystemBar.tryEmit(Unit)
    }

    fun notifyLocationUpdatedFromBackground(location: Location) {
        _locationUpdatedFromBackground.tryEmit(location)
    }
}
