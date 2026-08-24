package wangdaye.com.geometricweather.common.basic.models.weather

import java.io.Serializable

class WindDegree(
    val degree: Float,
    val isNoDirection: Boolean
) : Serializable {

    fun getWindArrow(): String? {
        return if (isNoDirection) {
            null
        } else if (22.5 < degree && degree <= 67.5) {
            "↙"
        } else if (67.5 < degree && degree <= 112.5) {
            "←"
        } else if (112.5 < degree && degree <= 157.5) {
            "↖"
        } else if (157.5 < degree && degree <= 202.5) {
            "↑"
        } else if (202.5 < degree && degree <= 247.5) {
            "↗"
        } else if (247.5 < degree && degree <= 292.5) {
            "→"
        } else if (292.0 < degree && degree <= 337.5) {
            "↘"
        } else {
            "↓"
        }
    }
}
