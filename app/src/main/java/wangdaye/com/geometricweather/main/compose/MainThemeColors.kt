package wangdaye.com.geometricweather.main.compose

import androidx.annotation.AttrRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider

@Composable
fun mainThemeColor(@AttrRes id: Int): Color {
    val lightTheme = !DisplayUtils.isDarkMode(LocalContext.current)
    return Color(MainThemeColorProvider.getColor(lightTheme, id))
}
