package wangdaye.com.geometricweather.settings.preference.composables

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import tech.apter.junit.jupiter.robolectric.RobolectricExtension
import wangdaye.com.geometricweather.theme.compose.GeometricWeatherTheme

/**
 * JVM Compose UI tests for settings preference rows.
 * Robolectric 4.14 has no official JUnit5 runner; [RobolectricExtension] is the documented
 * Jupiter path so existing [org.junit.jupiter.api.Test] tests keep using JUnit Platform.
 */
@ExtendWith(RobolectricExtension::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [28])
class SettingsPreferenceComposeTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun sectionHeaderDisplaysTitle() = runComposeUiTest {
        setContent {
            GeometricWeatherTheme(lightTheme = true) {
                SectionHeader(title = "Appearance")
            }
        }
        onNodeWithText("Appearance").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun preferenceViewDisplaysTitleAndSummaryAndHandlesClick() = runComposeUiTest {
        var clicked = false
        setContent {
            GeometricWeatherTheme(lightTheme = true) {
                PreferenceView(
                    title = "Dark mode",
                    summary = "Follow system",
                    onClick = { clicked = true },
                )
            }
        }
        onNodeWithText("Dark mode").assertIsDisplayed()
        onNodeWithText("Follow system").assertIsDisplayed()
        onNodeWithText("Dark mode").performClick()
        assertTrue(clicked)
    }
}
