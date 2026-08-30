package wangdaye.com.geometricweather.main.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.google.android.material.R as MaterialR
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.Location.Companion.buildLocal
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import wangdaye.com.geometricweather.common.utils.helpers.SnackbarHelper
import wangdaye.com.geometricweather.main.MainActivityViewModel
import wangdaye.com.geometricweather.main.adapters.location.LocationModel
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun ManagementScreen(
    viewModel: MainActivityViewModel,
    onSearchBarClick: () -> Unit,
    onSelectProvider: () -> Unit,
    onLocationSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = context as GeoActivity
    val density = LocalDensity.current
    val lightTheme = !DisplayUtils.isDarkMode(context)

    val totalListHolder by viewModel.totalLocationList.collectAsState()
    val remoteLocations = totalListHolder?.locationList.orEmpty()
    val selectedId = totalListHolder?.selectedId
    val temperatureUnit = remember {
        SettingsManager.getInstance(context).temperatureUnit
    }
    val resourceProvider = remember { ResourcesProviderFactory.getNewInstance() }

    var draggingFrom by remember { mutableIntStateOf(-1) }
    var dragOrigin by remember { mutableIntStateOf(-1) }
    var dragAccumPx by remember { mutableStateOf(0f) }
    var localLocations by remember { mutableStateOf(remoteLocations) }

    LaunchedEffect(remoteLocations) {
        if (draggingFrom < 0) {
            localLocations = remoteLocations
        }
    }

    val models = remember(localLocations, selectedId) {
        localLocations.map { location ->
            LocationModel(
                context,
                location,
                temperatureUnit,
                location.formattedId == selectedId
            )
        }
    }

    val itemHeightPx = with(density) { 112.dp.toPx() }
    val listState = rememberLazyListState()
    val statusBarPx = WindowInsets.statusBars.getTop(density)
    val appBarHeightPx = with(density) { 56.dp.toPx() } + statusBarPx
    val scrollOffset = if (listState.firstVisibleItemIndex == 0) {
        listState.firstVisibleItemScrollOffset.toFloat()
    } else {
        appBarHeightPx
    }
    val ratio = max(0f, min(scrollOffset / appBarHeightPx.coerceAtLeast(1f), 1f))
    val appBarColor = DisplayUtils.blendColor(
        ColorUtils.setAlphaComponent(
            MainThemeColorProvider.getColor(lightTheme, MaterialR.attr.colorPrimary),
            (255 * 0.2 * ratio).toInt()
        ),
        MainThemeColorProvider.getColor(lightTheme, R.attr.colorSurfaceVariant)
    )

    val showCurrentLocationButton = localLocations.none { it.isCurrentPosition }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(mainThemeColor(R.attr.colorSurfaceVariant))
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = with(density) { appBarHeightPx.toDp() }
                    + dimensionResource(R.dimen.little_margin),
                bottom = dimensionResource(R.dimen.little_margin)
            ),
        ) {
            itemsIndexed(
                items = models,
                key = { _, model -> model.location.formattedId }
            ) { index, model ->
                LocationCard(
                    model = model,
                    weatherIcon = model.weatherCode?.let { code ->
                        resourceProvider.getWeatherIcon(code, model.location.isDaylight)
                    },
                    onClick = { onLocationSelected(model.location.formattedId) },
                    onSwipeTowardStart = {
                        LocationListActions.onSwipeTowardStart(
                            activity,
                            viewModel,
                            model.location,
                            onSelectProvider,
                        )
                    },
                    onSwipeTowardEnd = {
                        val position = viewModel.totalLocationList.value
                            ?.locationList
                            ?.indexOfFirst { it.formattedId == model.location.formattedId }
                            ?: index
                        LocationListActions.onSwipeTowardEnd(
                            activity,
                            viewModel,
                            model.location,
                            position,
                        )
                    },
                    onDrag = { dy ->
                        if (draggingFrom < 0) {
                            draggingFrom = index
                            dragOrigin = index
                            dragAccumPx = 0f
                        }
                        dragAccumPx += dy
                        val offset = (dragAccumPx / itemHeightPx).roundToInt()
                        val to = (draggingFrom + offset).coerceIn(0, localLocations.lastIndex)
                        if (to != draggingFrom) {
                            val next = localLocations.toMutableList()
                            next.add(to, next.removeAt(draggingFrom))
                            localLocations = next
                            draggingFrom = to
                            dragAccumPx = 0f
                        }
                    },
                    onDragEnd = {
                        if (dragOrigin >= 0 && draggingFrom >= 0 && dragOrigin != draggingFrom) {
                            viewModel.moveLocation(dragOrigin, draggingFrom)
                        }
                        draggingFrom = -1
                        dragOrigin = -1
                        dragAccumPx = 0f
                    },
                )
            }
            item {
                Spacer(Modifier.navigationBarsPadding())
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(appBarColor))
                .statusBarsPadding()
                .height(56.dp)
                .padding(dimensionResource(R.dimen.little_margin)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(mainThemeColor(R.attr.colorSurface))
                    .clickable(onClick = onSearchBarClick)
                    .padding(horizontal = dimensionResource(R.dimen.little_margin)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = mainThemeColor(R.attr.colorBodyText),
                    modifier = Modifier.size(dimensionResource(R.dimen.material_icon_size)),
                )
                Spacer(Modifier.width(dimensionResource(R.dimen.little_margin)))
                Text(
                    text = stringResource(R.string.feedback_search_location),
                    color = mainThemeColor(R.attr.colorBodyText),
                    fontSize = dimensionResource(R.dimen.content_text_size).value.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )
                if (showCurrentLocationButton) {
                    IconButton(
                        onClick = {
                            viewModel.addLocation(buildLocal(), null)
                            SnackbarHelper.showSnackbar(
                                context.getString(R.string.feedback_collect_succeed)
                            )
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_location),
                            contentDescription = stringResource(
                                R.string.content_des_add_current_location
                            ),
                            tint = mainThemeColor(MaterialR.attr.colorPrimary),
                        )
                    }
                }
            }
        }
    }
}
