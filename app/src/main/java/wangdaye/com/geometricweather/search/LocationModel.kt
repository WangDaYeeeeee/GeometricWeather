package wangdaye.com.geometricweather.search

import android.content.Context
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.settings.SettingsManager

class LocationModel(
    context: Context,
    val location: Location,
) {
    val weatherSource: WeatherSource = if (location.isCurrentPosition) {
        SettingsManager.getInstance(context).weatherSource
    } else {
        location.weatherSource
    }

    val title: String = if (location.isCurrentPosition) {
        context.getString(R.string.current_location)
    } else {
        location.getCityName(context)
    }

    val subtitle: String = if (!location.isCurrentPosition || location.isUsable) {
        location.toString()
    } else {
        context.getString(R.string.feedback_not_yet_location)
    }
}
