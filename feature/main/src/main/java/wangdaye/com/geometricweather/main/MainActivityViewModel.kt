package wangdaye.com.geometricweather.main

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import wangdaye.com.geometricweather.common.basic.GeoViewModel
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.main.utils.StatementManager
import wangdaye.com.geometricweather.settings.SettingsManager
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    application: Application,
    private val savedStateHandle: SavedStateHandle,
    private val repository: MainWeatherRepository,
    val statementManager: StatementManager,
) : GeoViewModel(application),
    MainWeatherRepository.WeatherRequestCallback {

    private val app = application

    // async UI state.

    private val _currentLocation = MutableStateFlow<DayNightLocation?>(null)
    val currentLocation: StateFlow<DayNightLocation?> = _currentLocation.asStateFlow()

    private val _validLocationList = MutableStateFlow<SelectableLocationList?>(null)
    val validLocationList: StateFlow<SelectableLocationList?> = _validLocationList.asStateFlow()

    private val _totalLocationList = MutableStateFlow<SelectableLocationList?>(null)
    val totalLocationList: StateFlow<SelectableLocationList?> = _totalLocationList.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _indicator = MutableStateFlow<Indicator?>(null)
    val indicator: StateFlow<Indicator?> = _indicator.asStateFlow()

    private val _permissionsRequest = MutableStateFlow<PermissionsRequest?>(null)
    val permissionsRequest: StateFlow<PermissionsRequest?> = _permissionsRequest.asStateFlow()

    private val _mainMessage = MutableSharedFlow<MainMessage?>(extraBufferCapacity = 1)
    val mainMessage: SharedFlow<MainMessage?> = _mainMessage.asSharedFlow()

    // inner data.

    private var initCompleted = false
    private var updating = false

    companion object {
        private const val KEY_FORMATTED_ID = "formatted_id"
    }

    // life cycle.

    override fun onCleared() {
        super.onCleared()
        repository.destroy()
    }

    @JvmOverloads
    fun init(formattedId: String? = null) {
        onCleared()

        var id = formattedId ?: savedStateHandle[KEY_FORMATTED_ID]

        // init live data.
        val totalList = repository.initLocations(
            context = app,
            formattedId = id ?: ""
        )
        val validList = Location.excludeInvalidResidentLocation(app, totalList)

        id = formattedId ?: validList[0].formattedId
        val current = validList.first { item -> item.formattedId == id }

        initCompleted = false

        _currentLocation.value = DayNightLocation(location = current)
        _validLocationList.value = SelectableLocationList(locationList = validList, selectedId = id)
        _totalLocationList.value = SelectableLocationList(locationList = totalList, selectedId = id)

        _loading.value = false
        _indicator.value = Indicator(
            total = validList.size,
            index = validList.indexOfFirst { it.formattedId == id }
        )

        _permissionsRequest.value = null
        _mainMessage.tryEmit(null)

        // read weather caches.
        repository.getWeatherCacheForLocations(
            context = app,
            oldList = totalList,
            ignoredFormattedId = id,
        ) { newList, _ ->
            initCompleted = true
            newList?.let { updateInnerData(it) }
        }
    }

    // update inner data.

    private fun updateInnerData(location: Location) {
        val total = ArrayList(
            totalLocationList.value?.locationList ?: emptyList()
        )
        for (i in total.indices) {
            if (total[i].formattedId == location.formattedId) {
                total[i] = location
                break
            }
        }

        updateInnerData(total)
    }

    private fun updateInnerData(total: List<Location>) {
        // get valid locations and current index.
        val valid = Location.excludeInvalidResidentLocation(
            app,
            total,
        )

        var index = 0
        for (i in valid.indices) {
            if (valid[i].formattedId == currentLocation.value?.location?.formattedId) {
                index = i
                break
            }
        }

        _indicator.value = Indicator(total = valid.size, index = index)

        // update current location.
        setCurrentLocation(valid[index])

        // check difference in valid locations.
        val diffInValidLocations = validLocationList.value?.locationList != valid
        if (
            diffInValidLocations
            || validLocationList.value?.selectedId != valid[index].formattedId
        ) {
            _validLocationList.value = SelectableLocationList(
                locationList = valid,
                selectedId = valid[index].formattedId,
            )
        }

        // update total locations.
        _totalLocationList.value = SelectableLocationList(
            locationList = total,
            selectedId = valid[index].formattedId,
        )
    }

    private fun setCurrentLocation(location: Location) {
        _currentLocation.value = DayNightLocation(location = location)
        savedStateHandle[KEY_FORMATTED_ID] = location.formattedId

        checkToUpdateCurrentLocation()
    }

    private fun onUpdateResult(
        location: Location,
        locationResult: Boolean,
        weatherUpdateResult: Boolean,
    ) {
        if (!weatherUpdateResult) {
            _mainMessage.tryEmit(MainMessage.WEATHER_REQ_FAILED)
        } else if (!locationResult) {
            _mainMessage.tryEmit(MainMessage.LOCATION_FAILED)
        }

        updateInnerData(location)

        _loading.value = false
        updating = false
    }

    private fun checkToUpdateCurrentLocation() {
        // is not loading
        if (!updating) {
            // if already valid, just return.
            if (currentLocationIsValid()) {
                return
            }

            // if is not valid, we need:
            // update if init completed.
            // otherwise, mark a loading state and wait the init progress complete.
            if (initCompleted) {
                updateWithUpdatingChecking(
                    triggeredByUser = false,
                    checkPermissions = true,
                )
            } else {
                _loading.value = true
                updating = false
            }
            return
        }

        // is loading, do nothing.
    }

    private fun currentLocationIsValid() =
        currentLocation.value?.location?.weather?.isValid(
            SettingsManager
                .getInstance(app)
                .updateInterval
                .intervalInHour
        ) ?: false

    // update.

    fun updateWithUpdatingChecking(
        triggeredByUser: Boolean,
        checkPermissions: Boolean,
    ) {
        if (updating) {
            return
        }

        _loading.value = true

        // don't need to request any permission -> request data directly.
        if (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M
            || currentLocation.value?.location?.isCurrentPosition == false
            || !checkPermissions
        ) {
            updating = true
            repository.getWeather(
                app,
                currentLocation.value!!.location,
                currentLocation.value!!.location.isCurrentPosition,
                this
            )
            return
        }

        // check permissions.
        val permissionList = repository
            .getLocatePermissionList(app)
            .filter {
                ActivityCompat.checkSelfPermission(app, it) != PackageManager.PERMISSION_GRANTED
            }
            .toMutableList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && !statementManager.isPostNotificationRequired) {
            statementManager.setPostNotificationRequired(app)
            permissionList.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (permissionList.isEmpty()) {
            // already got all permissions -> request data directly.
            updating = true
            repository.getWeather(
                app,
                currentLocation.value!!.location,
                true,
                this
            )
            return
        }

        // request permissions.
        updating = false
        _permissionsRequest.value = PermissionsRequest(
            permissionList,
            currentLocation.value!!.location,
            triggeredByUser
        )
    }

    fun cancelRequest() {
        updating = false
        _loading.value = false
        repository.cancelWeatherRequest()
    }

    fun checkToUpdate() {
        checkToUpdateCurrentLocation()
    }

    fun updateLocationFromBackground(location: Location) {
        if (!initCompleted) {
            return
        }

        if (currentLocation.value?.location?.formattedId == location.formattedId) {
            cancelRequest()
        }
        updateInnerData(location)
    }

    // set location.

    fun setLocation(index: Int) {
        validLocationList.value?.locationList?.let {
            setLocation(it[index].formattedId)
        }
    }

    fun setLocation(formattedId: String) {
        cancelRequest()

        validLocationList.value?.locationList?.let {
            for (i in it.indices) {
                if (it[i].formattedId != formattedId) {
                    continue
                }

                setCurrentLocation(it[i])

                _indicator.value = Indicator(total = it.size, index = i)

                _totalLocationList.value = SelectableLocationList(
                    locationList = totalLocationList.value?.locationList ?: emptyList(),
                    selectedId = formattedId,
                )
                _validLocationList.value = SelectableLocationList(
                    locationList = validLocationList.value?.locationList ?: emptyList(),
                    selectedId = formattedId,
                )
                break
            }
        }
    }

    // return true if current location changed.
    fun offsetLocation(offset: Int): Boolean {
        cancelRequest()

        val oldFormattedId = currentLocation.value?.location?.formattedId ?: ""

        // ensure current index.
        var index = 0
        validLocationList.value?.locationList?.let {
            for (i in it.indices) {
                if (it[i].formattedId == currentLocation.value?.location?.formattedId) {
                    index = i
                    break
                }
            }
        }

        // update index.
        index = (
                index + offset + (validLocationList.value?.locationList?.size ?: 0)
        ) % (
                validLocationList.value?.locationList?.size ?: 1
        )

        // update location.
        setCurrentLocation(validLocationList.value!!.locationList[index])

        _indicator.value =
            Indicator(total = validLocationList.value!!.locationList.size, index = index)

        _totalLocationList.value = SelectableLocationList(
            locationList = totalLocationList.value?.locationList ?: emptyList(),
            selectedId = currentLocation.value?.location?.formattedId ?: "",
        )
        _validLocationList.value = SelectableLocationList(
            locationList = validLocationList.value?.locationList ?: emptyList(),
            selectedId = currentLocation.value?.location?.formattedId ?: "",
        )

        return currentLocation.value?.location?.formattedId != oldFormattedId
    }

    // list.

    // return false if failed.
    fun addLocation(
        location: Location,
        index: Int? = null,
    ): Boolean {
        // do not add an existed location.
        if (totalLocationList.value!!.locationList.firstOrNull {
                it.formattedId == location.formattedId
        } != null) {
            return false
        }

        val total = ArrayList(totalLocationList.value?.locationList ?: emptyList())
        total.add(index ?: total.size, location)

        updateInnerData(total)
        repository.writeLocationList(context = app, locationList = total)

        return true
    }

    fun moveLocation(from: Int, to: Int) {
        if (from == to) {
            return
        }

        val total = ArrayList(totalLocationList.value?.locationList ?: emptyList())
        total.add(to, total.removeAt(from))

        updateInnerData(total)

        repository.writeLocationList(
            context = app,
            locationList = totalLocationList.value?.locationList ?: emptyList()
        )
    }

    fun updateLocation(location: Location) {
        updateInnerData(location)
        repository.writeLocationList(
            context = app,
            locationList = totalLocationList.value?.locationList ?: emptyList(),
        )
    }

    fun deleteLocation(position: Int): Location {
        val total = ArrayList(totalLocationList.value?.locationList ?: emptyList())
        val location = total.removeAt(position)

        updateInnerData(total)
        repository.deleteLocation(context = app, location = location)

        return location
    }

    // MARK: - getter.

    fun getValidLocation(offset: Int): Location {
        // ensure current index.
        var index = 0
        validLocationList.value?.locationList?.let {
            for (i in it.indices) {
                if (it[i].formattedId == currentLocation.value?.location?.formattedId) {
                    index = i
                    break
                }
            }
        }

        // update index.
        index = (
                index + offset + (validLocationList.value?.locationList?.size ?: 0)
        ) % (
                validLocationList.value?.locationList?.size ?: 1
        )

        return validLocationList.value!!.locationList[index]
    }

    // impl.

    override fun onCompleted(
        location: Location,
        locationFailed: Boolean?,
        weatherRequestFailed: Boolean
    ) {
        onUpdateResult(
            location = location,
            locationResult = locationFailed != true,
            weatherUpdateResult = !weatherRequestFailed
        )
    }
}