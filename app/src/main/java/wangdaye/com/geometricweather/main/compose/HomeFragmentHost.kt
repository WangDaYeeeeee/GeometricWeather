package wangdaye.com.geometricweather.main.compose

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import wangdaye.com.geometricweather.R
import kotlinx.coroutines.delay
import wangdaye.com.geometricweather.main.fragments.HomeFragment

@Composable
fun HomeFragmentHost(
    weatherAnimating: Boolean,
    modifier: Modifier = Modifier,
) {
    val activity = LocalContext.current as FragmentActivity

    AndroidView(
        modifier = modifier,
        factory = { _ ->
            activity.layoutInflater.inflate(
                R.layout.compose_home_host,
                null,
                false
            ).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    )

    LaunchedEffect(weatherAnimating) {
        repeat(30) {
            val home = findHomeFragment(activity)
            if (home != null) {
                home.setWeatherDrawableEnabled(weatherAnimating)
                return@LaunchedEffect
            }
            delay(16)
        }
    }

    DisposableEffect(activity) {
        onDispose {
            findHomeFragment(activity)?.setWeatherDrawableEnabled(false)
        }
    }
}

fun findHomeFragment(activity: FragmentActivity): HomeFragment? {
    return activity.supportFragmentManager.findFragmentByTag("fragment_main") as? HomeFragment
        ?: activity.supportFragmentManager.findFragmentById(R.id.fragment_home) as? HomeFragment
}
