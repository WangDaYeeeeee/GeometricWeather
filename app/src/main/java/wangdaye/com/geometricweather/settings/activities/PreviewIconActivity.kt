package wangdaye.com.geometricweather.settings.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.navigation.InAppRoute
import wangdaye.com.geometricweather.settings.compose.PreviewIconRoute
import wangdaye.com.geometricweather.theme.compose.GeometricWeatherTheme

class PreviewIconActivity : GeoActivity() {

    companion object {
        const val KEY_ICON_PREVIEW_ACTIVITY_PACKAGE_NAME =
            "ICON_PREVIEW_ACTIVITY_PACKAGE_NAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val packageName = intent.getStringExtra(KEY_ICON_PREVIEW_ACTIVITY_PACKAGE_NAME)
        setContent {
            GeometricWeatherTheme(lightTheme = !isSystemInDarkTheme()) {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = InAppRoute.PREVIEW_ICON,
                ) {
                    composable(InAppRoute.PREVIEW_ICON) {
                        PreviewIconRoute(
                            packageName = packageName,
                            onBack = { finish() },
                        )
                    }
                }
            }
        }
    }
}
