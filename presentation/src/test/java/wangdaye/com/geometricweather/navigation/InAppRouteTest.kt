package wangdaye.com.geometricweather.navigation

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InAppRouteTest {

    @Test
    fun searchAndSettingsRoutesAreStable() {
        assertTrue(InAppRoute.SEARCH.startsWith("inapp/"))
        assertTrue(InAppRoute.SETTINGS_ROOT.contains("settings"))
        assertTrue(InAppRoute.ABOUT.startsWith("inapp/"))
    }
}
