package wangdaye.com.geometricweather.main.adapters.location

import android.content.Context
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.common.basic.models.options.unit.TemperatureUnit
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode
import java.text.DateFormat

class LocationModel(
    context: Context,
    location: Location,
    unit: TemperatureUnit,
    selected: Boolean
) {
    var location: Location = location
    var weatherCode: WeatherCode? = null
    var weatherSource: WeatherSource
    var currentPosition: Boolean
    var residentPosition: Boolean
    var title1: String
    var title2: String
    var subtitle: String
    var alerts: String? = null
    var selected: Boolean = selected

    init {
        weatherCode = if (location.weather != null) {
            if (location.isDaylight) {
                location.weather!!.dailyForecast[0].day().weatherCode
            } else {
                location.weather!!.dailyForecast[0].night().weatherCode
            }
        } else {
            null
        }

        weatherSource = location.weatherSource
        currentPosition = location.isCurrentPosition
        residentPosition = location.isResidentPosition

        title1 = if (location.isCurrentPosition) {
            context.getString(R.string.current_location)
        } else {
            location.getCityName(context)
        }
        title2 = if (location.weather == null) {
            ""
        } else {
            location.weather!!.current.weatherText +
                ", " +
                unit.getShortValueText(
                    context,
                    location.weather!!.current.temperature.temperature
                )
        }

        subtitle = if (!location.isCurrentPosition || location.isUsable) {
            location.toString()
        } else {
            context.getString(R.string.feedback_not_yet_location)
        }

        alerts = if (location.weather != null) {
            val alertList = location.weather!!.alertList
            if (alertList.isNotEmpty()) {
                val builder = StringBuilder()
                for (i in alertList.indices) {
                    builder.append(alertList[i].description)
                        .append(", ")
                        .append(
                            DateFormat.getDateTimeInstance(
                                DateFormat.SHORT,
                                DateFormat.SHORT
                            ).format(alertList[i].date)
                        )
                    if (i != alertList.size - 1) {
                        builder.append("\n")
                    }
                }
                builder.toString()
            } else {
                null
            }
        } else {
            null
        }
    }

    fun areItemsTheSame(newItem: LocationModel): Boolean {
        return location.formattedId == newItem.location.formattedId
    }

    fun areContentsTheSame(newItem: LocationModel): Boolean {
        return location == newItem.location && selected == newItem.selected
    }
}
