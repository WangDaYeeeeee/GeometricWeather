package wangdaye.com.geometricweather.search.compose

import androidx.annotation.AttrRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import wangdaye.com.geometricweather.theme.ThemeManager

@Composable
fun searchThemeColor(@AttrRes id: Int): Color {
    val context = LocalContext.current
    return Color(ThemeManager.getInstance(context).getThemeColor(context, id))
}
