package wangdaye.com.geometricweather.navigation

import org.junit.Assert.assertTrue
import org.junit.Test

class InAppRouteTest {

    @Test
    fun searchAndSettingsRoutesAreStable() {
        assertTrue(InAppRoute.SEARCH.startsWith("inapp/"))
        assertTrue(InAppRoute.SETTINGS_ROOT.contains("settings"))
        assertTrue(InAppRoute.ABOUT.startsWith("inapp/"))
    }
}
