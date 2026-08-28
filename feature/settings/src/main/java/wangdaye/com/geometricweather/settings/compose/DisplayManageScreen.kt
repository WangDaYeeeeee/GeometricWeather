package wangdaye.com.geometricweather.settings.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.basic.models.options.appearance.CardDisplay
import wangdaye.com.geometricweather.common.basic.models.options.appearance.DailyTrendDisplay
import wangdaye.com.geometricweather.common.basic.models.options.appearance.HourlyTrendDisplay
import wangdaye.com.geometricweather.common.ui.widgets.Material3Scaffold
import wangdaye.com.geometricweather.common.ui.widgets.insets.FitStatusBarTopAppBar
import wangdaye.com.geometricweather.settings.SettingsManager
import kotlin.math.roundToInt

@Composable
fun CardDisplayManageRoute(
    onBack: () -> Unit,
    onMutated: () -> Unit = {},
) {
    val context = LocalContext.current
    val settings = remember { SettingsManager.getInstance(context) }
    val allCards = remember {
        listOf(
            CardDisplay.CARD_DAILY_OVERVIEW,
            CardDisplay.CARD_HOURLY_OVERVIEW,
            CardDisplay.CARD_AIR_QUALITY,
            CardDisplay.CARD_ALLERGEN,
            CardDisplay.CARD_SUNRISE_SUNSET,
            CardDisplay.CARD_LIFE_DETAILS,
        )
    }
    val initialDisplayed = remember { settings.cardDisplayList }
    DisplayManageScreen(
        title = stringResource(R.string.settings_title_card_display),
        displayed = initialDisplayed,
        unused = allCards.filter { it !in initialDisplayed },
        nameOf = { it.getName(context) },
        onDisplayedChange = { newList ->
            if (settings.cardDisplayList != newList) {
                settings.cardDisplayList = newList
            }
        },
        onBack = onBack,
        onMutated = onMutated,
    )
}

@Composable
fun DailyTrendDisplayManageRoute(
    onBack: () -> Unit,
    onMutated: () -> Unit = {},
) {
    val context = LocalContext.current
    val settings = remember { SettingsManager.getInstance(context) }
    val allTags = remember {
        listOf(
            DailyTrendDisplay.TAG_TEMPERATURE,
            DailyTrendDisplay.TAG_AIR_QUALITY,
            DailyTrendDisplay.TAG_WIND,
            DailyTrendDisplay.TAG_UV_INDEX,
            DailyTrendDisplay.TAG_PRECIPITATION,
        )
    }
    val initialDisplayed = remember { settings.dailyTrendDisplayList }
    DisplayManageScreen(
        title = stringResource(R.string.settings_title_daily_trend_display),
        displayed = initialDisplayed,
        unused = allTags.filter { it !in initialDisplayed },
        nameOf = { it.getName(context) },
        onDisplayedChange = { newList ->
            if (settings.dailyTrendDisplayList != newList) {
                settings.dailyTrendDisplayList = newList
            }
        },
        onBack = onBack,
        onMutated = onMutated,
    )
}

@Composable
fun HourlyTrendDisplayManageRoute(
    onBack: () -> Unit,
    onMutated: () -> Unit = {},
) {
    val context = LocalContext.current
    val settings = remember { SettingsManager.getInstance(context) }
    val allTags = remember {
        listOf(
            HourlyTrendDisplay.TAG_TEMPERATURE,
            HourlyTrendDisplay.TAG_WIND,
            HourlyTrendDisplay.TAG_UV_INDEX,
            HourlyTrendDisplay.TAG_PRECIPITATION,
        )
    }
    val initialDisplayed = remember { settings.hourlyTrendDisplayList }
    DisplayManageScreen(
        title = stringResource(R.string.settings_title_hourly_trend_display),
        displayed = initialDisplayed,
        unused = allTags.filter { it !in initialDisplayed },
        nameOf = { it.getName(context) },
        onDisplayedChange = { newList ->
            if (settings.hourlyTrendDisplayList != newList) {
                settings.hourlyTrendDisplayList = newList
            }
        },
        onBack = onBack,
        onMutated = onMutated,
    )
}

@Composable
fun <T : Any> DisplayManageScreen(
    title: String,
    displayed: List<T>,
    unused: List<T>,
    nameOf: (T) -> String,
    onDisplayedChange: (List<T>) -> Unit,
    onBack: () -> Unit,
    onMutated: () -> Unit = {},
) {
    val displayedState = remember { displayed.toMutableStateList() }
    val unusedState = remember { unused.toMutableStateList() }

    DisposableEffect(Unit) {
        onDispose {
            onDisplayedChange(displayedState.toList())
        }
    }

    Material3Scaffold(
        topBar = {
            FitStatusBarTopAppBar(
                title = title,
                onBackPressed = onBack,
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = unusedState.isNotEmpty(),
                enter = fadeIn(tween(350)) + slideInVertically(tween(350)) { it },
                exit = fadeOut(tween(150)) + slideOutVertically(tween(150)) { it },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .horizontalScroll(rememberScrollState())
                        .navigationBarsPadding()
                        .padding(dimensionResource(R.dimen.normal_margin)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    unusedState.toList().forEach { item ->
                        SuggestionChip(
                            onClick = {
                                unusedState.remove(item)
                                displayedState.add(item)
                                onMutated()
                            },
                            label = { Text(nameOf(item)) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            ),
                            modifier = Modifier.padding(end = dimensionResource(R.dimen.little_margin)),
                        )
                    }
                }
            }
        },
    ) { padding ->
        val itemHeight = 56.dp
        val itemHeightPx = with(LocalDensity.current) { itemHeight.toPx() }
        val rise = dimensionResource(R.dimen.touch_rise_z)
        var draggingKey by remember { mutableStateOf<T?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 8.dp),
        ) {
            displayedState.toList().forEach { item ->
                key(item) {
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value != SwipeToDismissBoxValue.Settled) {
                                displayedState.remove(item)
                                unusedState.add(item)
                                onMutated()
                                true
                            } else {
                                false
                            }
                        }
                    )
                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.errorContainer)
                                    .padding(horizontal = dimensionResource(R.dimen.normal_margin)),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_delete),
                                    contentDescription = stringResource(R.string.content_des_delete_flag),
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                )
                            }
                        },
                        modifier = Modifier
                            .zIndex(if (draggingKey == item) 1f else 0f)
                            .then(
                                if (draggingKey == item) Modifier.shadow(rise) else Modifier
                            ),
                    ) {
                        DisplayManageRow(
                            title = nameOf(item),
                            itemHeight = itemHeight,
                            onRemove = {
                                displayedState.remove(item)
                                unusedState.add(item)
                                onMutated()
                            },
                            onDragStartIndex = { displayedState.indexOf(item) },
                            onDragTo = { from, totalY ->
                                if (from < 0 || displayedState.isEmpty()) return@DisplayManageRow
                                val to = (from + (totalY / itemHeightPx).roundToInt())
                                    .coerceIn(0, displayedState.lastIndex)
                                val current = displayedState.indexOf(item)
                                if (current >= 0 && current != to) {
                                    displayedState.add(to, displayedState.removeAt(current))
                                    onMutated()
                                }
                            },
                            onDraggingChange = { dragging ->
                                draggingKey = if (dragging) item else null
                            },
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                }
            }
        }
    }
}

@Composable
private fun DisplayManageRow(
    title: String,
    itemHeight: Dp,
    onRemove: () -> Unit,
    onDragStartIndex: () -> Int,
    onDragTo: (from: Int, totalY: Float) -> Unit,
    onDraggingChange: (Boolean) -> Unit,
) {
    var startIndex by remember { mutableIntStateOf(0) }
    var totalY by remember { mutableFloatStateOf(0f) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight)
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_drag),
            contentDescription = stringResource(R.string.content_des_drag_flag),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(start = 4.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            startIndex = onDragStartIndex()
                            totalY = 0f
                            onDraggingChange(true)
                        },
                        onDragEnd = { onDraggingChange(false) },
                        onDragCancel = { onDraggingChange(false) },
                    ) { change, dragAmount ->
                        change.consume()
                        totalY += dragAmount.y
                        onDragTo(startIndex, totalY)
                    }
                }
                .padding(12.dp),
        )
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimensionResource(R.dimen.little_margin)),
        )
        IconButton(onClick = onRemove) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(R.string.content_des_delete_flag),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
    }
}
