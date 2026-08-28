package wangdaye.com.geometricweather.main

import wangdaye.com.geometricweather.common.basic.models.Location

data class Indicator(val total: Int, val index: Int)

class PermissionsRequest(
    val permissionList: List<String>,
    val target: Location?,
    val triggeredByUser: Boolean
) {

    private var consumed = false

    fun consume(): Boolean {
        if (consumed) {
            return false
        }

        consumed = true
        return true
    }
}

data class SelectableLocationList(
    val locationList: List<Location>,
    val selectedId: String,
)

enum class MainMessage {
    LOCATION_FAILED,
    WEATHER_REQ_FAILED,
}

data class DayNightLocation(
    val location: Location,
    val daylight: Boolean = location.isDaylight
)
