package wangdaye.com.geometricweather.settings.preference.composables

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import wangdaye.com.geometricweather.theme.compose.GeometricWeatherTheme

/**
 * JVM Compose UI tests for settings preference rows (no Hilt/network).
 *
 * [createComposeRule] is a JUnit4 [Rule], so this class uses [AndroidJUnit4] + Robolectric
 * on the JVM. Other tests in this module stay on JUnit5 Jupiter; vintage is testRuntimeOnly
 * here so this one class is discovered without PowerMock or app-wide JUnit4.
 */
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [28])
class SettingsPreferenceComposeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun sectionHeaderDisplaysTitle() {
        composeRule.setContent {
            GeometricWeatherTheme(lightTheme = true) {
                SectionHeader(title = "Appearance")
            }
        }
        composeRule.onNodeWithText("Appearance").assertIsDisplayed()
    }

    @Test
    fun preferenceViewDisplaysTitleAndSummaryAndHandlesClick() {
        var clicked = false
        composeRule.setContent {
            GeometricWeatherTheme(lightTheme = true) {
                PreferenceView(
                    title = "Dark mode",
                    summary = "Follow system",
                    onClick = { clicked = true },
                )
            }
        }
        composeRule.onNodeWithText("Dark mode").assertIsDisplayed()
        composeRule.onNodeWithText("Follow system").assertIsDisplayed()
        composeRule.onNodeWithText("Dark mode").performClick()
        assertTrue(clicked)
    }
}
