package wangdaye.com.geometricweather.settings.compose

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.weather.WeatherCode
import wangdaye.com.geometricweather.common.ui.widgets.Material3Scaffold
import wangdaye.com.geometricweather.common.ui.widgets.generateCollapsedScrollBehavior
import wangdaye.com.geometricweather.common.ui.widgets.insets.FitStatusBarTopAppBar
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import wangdaye.com.geometricweather.common.utils.helpers.IntentHelper
import wangdaye.com.geometricweather.settings.dialogs.AdaptiveIconDialog
import wangdaye.com.geometricweather.settings.dialogs.AnimatableIconDialog
import wangdaye.com.geometricweather.settings.dialogs.MinimalIconDialog
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory
import wangdaye.com.geometricweather.theme.resource.providers.DefaultResourceProvider
import wangdaye.com.geometricweather.theme.resource.providers.PixelResourcesProvider
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider

sealed interface PreviewIconItem {
    data class Title(val text: String) : PreviewIconItem
    data object Line : PreviewIconItem
    data class Icon(
        val drawable: Drawable,
        val contentDescription: String,
        val onClick: (GeoActivity) -> Unit,
    ) : PreviewIconItem
}

@Composable
fun PreviewIconRoute(
    packageName: String?,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as GeoActivity
    val provider = remember(packageName) {
        ResourcesProviderFactory.getNewInstance(packageName)
    }
    val items = remember(provider) { buildPreviewIconItems(activity, provider) }
    val scrollBehavior = generateCollapsedScrollBehavior()

    Material3Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            FitStatusBarTopAppBar(
                title = provider.providerName ?: "",
                onBackPressed = onBack,
                actions = {
                    IconButton(
                        onClick = {
                            if (provider is DefaultResourceProvider || provider is PixelResourcesProvider) {
                                IntentHelper.startAppStoreDetailsActivity(activity)
                            } else {
                                IntentHelper.startAppStoreDetailsActivity(activity, provider.packageName)
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_google_play),
                            contentDescription = stringResource(R.string.action_appStore),
                        )
                    }
                    IconButton(
                        onClick = {
                            if (provider is DefaultResourceProvider || provider is PixelResourcesProvider) {
                                IntentHelper.startApplicationDetailsActivity(activity)
                            } else {
                                IntentHelper.startApplicationDetailsActivity(activity, provider.packageName)
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_about),
                            contentDescription = stringResource(R.string.action_about),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding,
        ) {
            items(
                items = items,
                span = { item ->
                    GridItemSpan(if (item is PreviewIconItem.Icon) 1 else 4)
                }
            ) { item ->
                when (item) {
                    is PreviewIconItem.Title -> {
                        Text(
                            text = item.text,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(dimensionResource(R.dimen.normal_margin)),
                        )
                    }
                    is PreviewIconItem.Line -> {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = dimensionResource(R.dimen.little_margin)),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        )
                    }
                    is PreviewIconItem.Icon -> {
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable { item.onClick(activity) }
                                .padding(dimensionResource(R.dimen.large_margin)),
                        ) {
                            AndroidView(
                                modifier = Modifier.fillMaxSize(),
                                factory = { ctx ->
                                    AppCompatImageView(ctx).apply {
                                        scaleType = ImageView.ScaleType.FIT_CENTER
                                        contentDescription = item.contentDescription
                                    }
                                },
                                update = { view ->
                                    view.setImageDrawable(item.drawable)
                                    view.contentDescription = item.contentDescription
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun weatherLabel(code: WeatherCode): String {
    val name = code.name.lowercase().replace('_', ' ')
    return name.replaceFirstChar { it.uppercase() }
}

private fun buildPreviewIconItems(
    activity: GeoActivity,
    provider: ResourceProvider,
): List<PreviewIconItem> {
    val darkMode = DisplayUtils.isDarkMode(activity)
    val codes = listOf(
        WeatherCode.CLEAR,
        WeatherCode.PARTLY_CLOUDY,
        WeatherCode.CLOUDY,
        WeatherCode.WIND,
        WeatherCode.RAIN,
        WeatherCode.SNOW,
        WeatherCode.SLEET,
        WeatherCode.HAIL,
        WeatherCode.THUNDER,
        WeatherCode.THUNDERSTORM,
        WeatherCode.FOG,
        WeatherCode.HAZE,
    )
    val items = mutableListOf<PreviewIconItem>()

    fun addWeatherIcons(daytime: Boolean) {
        codes.forEach { code ->
            items.add(
                PreviewIconItem.Icon(
                    drawable = ResourceHelper.getWeatherIcon(provider, code, daytime),
                    contentDescription = weatherLabel(code),
                ) { AnimatableIconDialog.show(it, code, daytime, provider) }
            )
        }
    }

    fun addMinimalIcons(daytime: Boolean) {
        codes.forEach { code ->
            items.add(
                PreviewIconItem.Icon(
                    drawable = ResourceHelper.getWidgetNotificationIcon(
                        provider, code, daytime, true, !darkMode
                    ),
                    contentDescription = weatherLabel(code),
                ) { MinimalIconDialog.show(it, code, daytime, provider) }
            )
        }
    }

    fun addShortcutIcons(daytime: Boolean) {
        codes.forEach { code ->
            items.add(
                PreviewIconItem.Icon(
                    drawable = ResourceHelper.getShortcutsIcon(provider, code, daytime),
                    contentDescription = weatherLabel(code),
                ) { AdaptiveIconDialog.show(it, code, daytime, provider) }
            )
        }
    }

    items.add(PreviewIconItem.Title(activity.getString(R.string.daytime)))
    addWeatherIcons(true)
    items.add(PreviewIconItem.Line)
    items.add(PreviewIconItem.Title(activity.getString(R.string.nighttime)))
    addWeatherIcons(false)
    items.add(PreviewIconItem.Line)

    items.add(PreviewIconItem.Title("Minimal " + activity.getString(R.string.daytime)))
    addMinimalIcons(true)
    items.add(PreviewIconItem.Line)
    items.add(PreviewIconItem.Title("Minimal " + activity.getString(R.string.nighttime)))
    addMinimalIcons(false)
    items.add(PreviewIconItem.Line)

    items.add(PreviewIconItem.Title("Shortcuts " + activity.getString(R.string.daytime)))
    addShortcutIcons(true)
    items.add(PreviewIconItem.Line)
    items.add(PreviewIconItem.Title("Shortcuts " + activity.getString(R.string.nighttime)))
    addShortcutIcons(false)
    items.add(PreviewIconItem.Line)

    items.add(PreviewIconItem.Title(activity.getString(R.string.sunrise_sunset)))
    items.add(
        PreviewIconItem.Icon(
            drawable = ResourceHelper.getSunDrawable(provider),
            contentDescription = "Sun",
        ) { }
    )
    items.add(
        PreviewIconItem.Icon(
            drawable = ResourceHelper.getMoonDrawable(provider),
            contentDescription = "Moon",
        ) { }
    )
    return items
}
