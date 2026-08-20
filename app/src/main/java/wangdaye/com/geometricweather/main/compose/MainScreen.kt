package wangdaye.com.geometricweather.main.compose

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import wangdaye.com.geometricweather.main.MainActivityViewModel

const val WIDE_LAYOUT_MIN_DP = 640

@Composable
fun isWideMainLayout(): Boolean {
    return LocalConfiguration.current.screenWidthDp >= WIDE_LAYOUT_MIN_DP
}

@Composable
fun MainScreen(
    viewModel: MainActivityViewModel,
    managementVisible: Boolean,
    onManagementVisibleChange: (Boolean) -> Unit,
    onSearchBarClick: () -> Unit,
    onSelectProvider: () -> Unit,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val wide = isWideMainLayout()

    val drawerWidth = run {
        val screenWidthPx = with(density) {
            LocalConfiguration.current.screenWidthDp.dp.toPx().toInt()
        }
        var widthPx = screenWidthPx - DisplayUtils.getTabletListAdaptiveWidth(
            context,
            screenWidthPx
        )
        if (widthPx <= 0) {
            widthPx = with(density) { 280.dp.roundToPx() }
        }
        val minPx = with(density) { 280.dp.roundToPx() }
        val maxPx = with(density) { 320.dp.roundToPx() }
        widthPx = widthPx.coerceIn(minPx, maxPx)
        with(density) { widthPx.toDp() }
    }

    BackHandler(enabled = managementVisible && !wide) {
        onManagementVisibleChange(false)
    }

    if (wide) {
        Row(modifier = Modifier.fillMaxSize()) {
            if (managementVisible) {
                ManagementScreen(
                    viewModel = viewModel,
                    onSearchBarClick = onSearchBarClick,
                    onSelectProvider = onSelectProvider,
                    onLocationSelected = { formattedId ->
                        viewModel.setLocation(formattedId)
                    },
                    modifier = Modifier
                        .width(drawerWidth)
                        .fillMaxHeight(),
                )
            }
            HomeFragmentHost(
                weatherAnimating = true,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            HomeFragmentHost(
                weatherAnimating = !managementVisible,
                modifier = Modifier.fillMaxSize(),
            )
            AnimatedVisibility(
                visible = managementVisible,
                enter = fadeIn() + slideInVertically { it / 6 },
                exit = fadeOut() + slideOutVertically { it / 6 },
            ) {
                ManagementScreen(
                    viewModel = viewModel,
                    onSearchBarClick = onSearchBarClick,
                    onSelectProvider = onSelectProvider,
                    onLocationSelected = { formattedId ->
                        viewModel.setLocation(formattedId)
                        onManagementVisibleChange(false)
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
