package wangdaye.com.geometricweather.search

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import wangdaye.com.geometricweather.common.basic.GeoViewModel
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import javax.inject.Inject

@HiltViewModel
class SearchActivityViewModel @Inject constructor(
    application: Application,
    private val repository: SearchActivityRepository
) : GeoViewModel(application) {

    private val app = application

    private val _listResource = MutableStateFlow(
        LoadableLocationList(ArrayList(), LoadableLocationList.Status.SUCCESS)
    )
    private val listResourceLiveData = _listResource.asLiveData()
    val listResourceFlow: StateFlow<LoadableLocationList> = _listResource.asStateFlow()

    private val _query = MutableStateFlow("")
    private val queryLiveData = _query.asLiveData()
    val queryFlow: StateFlow<String> = _query.asStateFlow()

    private val _enabledSources = MutableStateFlow(
        repository.getValidWeatherSources(application)
    )
    private val enabledSourcesLiveData = _enabledSources.asLiveData()
    val enabledSourcesFlow: StateFlow<List<WeatherSource>> = _enabledSources.asStateFlow()

    fun requestLocationList(query: String) {
        val oldList = innerGetLocationList()

        repository.cancel()
        repository.searchLocationList(
            app,
            query,
            enabledSourcesValue
        ) { locationList, _ ->
            if (locationList != null) {
                _listResource.value = LoadableLocationList(
                    locationList,
                    LoadableLocationList.Status.SUCCESS
                )
            } else {
                _listResource.value = LoadableLocationList(
                    oldList,
                    LoadableLocationList.Status.ERROR
                )
            }
        }

        _listResource.value = LoadableLocationList(oldList, LoadableLocationList.Status.LOADING)
        _query.value = query
    }

    fun requestLocationList() {
        requestLocationList(queryValue)
    }

    fun setEnabledSources(enabledSources: List<WeatherSource>) {
        repository.setValidWeatherSources(enabledSources)
        _enabledSources.value = enabledSources
    }

    override fun onCleared() {
        super.onCleared()
        repository.cancel()
    }

    private fun innerGetLocationList(): List<Location> {
        return _listResource.value.dataList
    }

    fun getListResource(): LiveData<LoadableLocationList> = listResourceLiveData

    val locationList: List<Location>
        get() = innerGetLocationList().toList()

    val locationCount: Int
        get() = innerGetLocationList().size

    fun getQuery(): LiveData<String> = queryLiveData

    val queryValue: String
        get() = _query.value

    fun getEnabledSources(): LiveData<List<WeatherSource>> = enabledSourcesLiveData

    val enabledSourcesValue: List<WeatherSource>
        get() = _enabledSources.value
}
