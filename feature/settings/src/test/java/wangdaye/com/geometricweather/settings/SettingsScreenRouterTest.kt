package wangdaye.com.geometricweather.settings

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import wangdaye.com.geometricweather.navigation.InAppRoute
import wangdaye.com.geometricweather.settings.compose.SettingsScreenRouter

class SettingsScreenRouterTest {

    @Test
    fun routerMatchesInAppRoute() {
        assertEquals(InAppRoute.SETTINGS_ROOT, SettingsScreenRouter.Root.route)
        assertEquals(InAppRoute.SETTINGS_APPEARANCE, SettingsScreenRouter.Appearance.route)
        assertEquals(InAppRoute.SETTINGS_PROVIDERS, SettingsScreenRouter.ServiceProvider.route)
        assertEquals(InAppRoute.ABOUT, "inapp/about")
    }
}
