package wangdaye.com.geometricweather.main.compose

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import wangdaye.com.geometricweather.main.MainActivity
import wangdaye.com.geometricweather.main.MainActivityViewModel

@Composable
fun HomeScreen(
    viewModel: MainActivityViewModel,
    weatherAnimating: Boolean,
    onManageIconClicked: () -> Unit,
    onSettingsIconClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalContext.current as MainActivity
    val lifecycleOwner = LocalLifecycleOwner.current

    val host = remember(activity) {
        HomeHost(
            activity = activity,
            viewModel = viewModel,
            onManageIconClicked = onManageIconClicked,
            onSettingsIconClicked = onSettingsIconClicked,
        ).also { activity.homeHost = it }
    }

    SideEffect {
        host.onManageIconClicked = onManageIconClicked
        host.onSettingsIconClicked = onSettingsIconClicked
        host.setWeatherDrawableEnabled(weatherAnimating)
        host.onConfigurationChangedIfNeeded()
    }

    AndroidView(
        modifier = modifier,
        factory = { _ ->
            host.root.apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        update = {
            host.setWeatherDrawableEnabled(weatherAnimating)
        }
    )

    DisposableEffect(lifecycleOwner, host) {
        host.startCollecting(lifecycleOwner)
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> host.onActivityResume()
                Lifecycle.Event.ON_PAUSE -> host.onActivityPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            host.onActivityResume()
        }
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            host.release()
            if (activity.homeHost === host) {
                activity.homeHost = null
            }
        }
    }
}
