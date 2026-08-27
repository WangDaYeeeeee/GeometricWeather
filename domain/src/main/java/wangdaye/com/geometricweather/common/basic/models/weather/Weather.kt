package wangdaye.com.geometricweather.common.basic.models.weather

import java.io.Serializable

class Weather(
    val base: Base,
    val current: Current,
    var yesterday: History?,
    val dailyForecast: List<Daily>,
    val hourlyForecast: List<Hourly>,
    val minutelyForecast: List<Minutely>,
    val alertList: List<Alert>
) : Serializable {

    fun isValid(pollingIntervalHours: Float): Boolean {
        val updateTime = base.updateTime
        val currentTime = System.currentTimeMillis()
        return currentTime >= updateTime
            && currentTime - updateTime < pollingIntervalHours * 60 * 60 * 1000
    }
}
