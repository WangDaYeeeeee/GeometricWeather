package wangdaye.com.geometricweather.theme.compose

import androidx.compose.material3.Text
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.junit.jupiter.RobolectricExtension

@OptIn(ExperimentalTestApi::class)
@ExtendWith(RobolectricExtension::class)
@Config(sdk = [28])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class GeometricWeatherThemeTest {

    @Test
    fun lightThemeRendersContent() = runComposeUiTest {
        setContent {
            GeometricWeatherTheme(lightTheme = true) {
                Text("theme-ok")
            }
        }
        onNodeWithText("theme-ok").assertExists()
    }
}
