package wangdaye.com.geometricweather.settings.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.navigation.InAppRoute
import wangdaye.com.geometricweather.settings.compose.DailyTrendDisplayManageRoute
import wangdaye.com.geometricweather.theme.compose.GeometricWeatherTheme

class DailyTrendDisplayManageActivity : GeoActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GeometricWeatherTheme(lightTheme = !isSystemInDarkTheme()) {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = InAppRoute.DAILY_TREND_DISPLAY,
                ) {
                    composable(InAppRoute.DAILY_TREND_DISPLAY) {
                        DailyTrendDisplayManageRoute(
                            onBack = { finish() },
                            onMutated = { setResult(RESULT_OK) },
                        )
                    }
                }
            }
        }
    }
}
