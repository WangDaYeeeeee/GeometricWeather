package wangdaye.com.geometricweather.daily

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.daily.compose.DailyWeatherRoute
import wangdaye.com.geometricweather.theme.compose.GeometricWeatherTheme

class DailyWeatherActivity : GeoActivity() {

    companion object {
        const val KEY_FORMATTED_LOCATION_ID = "FORMATTED_LOCATION_ID"
        const val KEY_CURRENT_DAILY_INDEX = "CURRENT_DAILY_INDEX"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val formattedId = intent.getStringExtra(KEY_FORMATTED_LOCATION_ID)
        val initialIndex = intent.getIntExtra(KEY_CURRENT_DAILY_INDEX, 0)
        setContent {
            GeometricWeatherTheme(lightTheme = !isSystemInDarkTheme()) {
                DailyWeatherRoute(
                    formattedId = formattedId,
                    initialIndex = initialIndex,
                    onMissingData = { finish() },
                    onBack = { finish() },
                )
            }
        }
    }
}
