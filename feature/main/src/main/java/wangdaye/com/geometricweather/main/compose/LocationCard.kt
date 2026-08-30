package wangdaye.com.geometricweather.main.compose

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import wangdaye.com.geometricweather.main.adapters.location.LocationModel
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider

@Composable
fun LocationCard(
    model: LocationModel,
    weatherIcon: Drawable?,
    onClick: () -> Unit,
    onSwipeTowardStart: () -> Unit,
    onSwipeTowardEnd: () -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lightTheme = !DisplayUtils.isDarkMode(context)
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onSwipeTowardEnd()
                    false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onSwipeTowardStart()
                    false
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    val elevatedSurface = DisplayUtils.getWidgetSurfaceColor(
        DisplayUtils.DEFAULT_CARD_LIST_ITEM_ELEVATION_DP,
        MainThemeColorProvider.getColor(lightTheme, R.attr.colorPrimary),
        MainThemeColorProvider.getColor(lightTheme, R.attr.colorSurface)
    )
    val itemBackground = if (model.selected) {
        DisplayUtils.blendColor(
            ColorUtils.setAlphaComponent(elevatedSurface, (255 * 0.5).toInt()),
            MainThemeColorProvider.getColor(lightTheme, R.attr.colorSurfaceVariant)
        )
    } else {
        elevatedSurface
    }
    val startBg = MainThemeColorProvider.getColor(lightTheme, R.attr.colorErrorContainer)
    val startTint = MainThemeColorProvider.getColor(lightTheme, R.attr.colorOnErrorContainer)
    val endBg = if (model.location.isCurrentPosition) {
        MainThemeColorProvider.getColor(lightTheme, R.attr.colorTertiaryContainer)
    } else {
        MainThemeColorProvider.getColor(lightTheme, R.attr.colorSecondaryContainer)
    }
    val endTint = if (model.location.isCurrentPosition) {
        MainThemeColorProvider.getColor(lightTheme, R.attr.colorOnTertiaryContainer)
    } else {
        MainThemeColorProvider.getColor(lightTheme, R.attr.colorOnSecondaryContainer)
    }
    val endIcon = when {
        model.currentPosition -> R.drawable.ic_settings
        model.residentPosition -> R.drawable.ic_tag_off
        else -> R.drawable.ic_tag_plus
    }

    val talkBack = remember(model) {
        buildString {
            append(model.subtitle)
            if (model.currentPosition) {
                append(", ").append(context.getString(R.string.current_location))
            }
            append(", ").append(
                context.getString(R.string.content_desc_powered_by)
                    .replace("$", model.weatherSource.getVoice(context))
            )
            append(", ").append(
                context.getString(
                    if (DisplayUtils.isRtl(context)) {
                        R.string.content_des_swipe_left_to_delete
                    } else {
                        R.string.content_des_swipe_right_to_delete
                    }
                )
            )
        }
    }

    val corner = dimensionResource(R.dimen.material3_card_list_item_corner_radius)

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier
            .padding(
                start = dimensionResource(R.dimen.little_margin),
                end = dimensionResource(R.dimen.little_margin),
                bottom = dimensionResource(R.dimen.little_margin),
            )
            .semantics { contentDescription = talkBack }
            .then(
                if (model.selected) {
                    Modifier.border(4.dp, Color(elevatedSurface), RoundedCornerShape(corner))
                } else {
                    Modifier
                }
            )
            .clip(RoundedCornerShape(corner)),
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val bg = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Color(startBg)
                SwipeToDismissBoxValue.EndToStart -> Color(endBg)
                else -> Color.Transparent
            }
            val iconRes = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> R.drawable.ic_delete
                SwipeToDismissBoxValue.EndToStart -> endIcon
                else -> R.drawable.ic_delete
            }
            val tint = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Color(startTint)
                SwipeToDismissBoxValue.EndToStart -> Color(endTint)
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bg)
                    .padding(horizontal = dimensionResource(R.dimen.normal_margin)),
                contentAlignment = when (direction) {
                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                    else -> Alignment.CenterEnd
                }
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = tint,
                )
            }
        },
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (model.selected) {
                            Modifier.background(
                                Color(itemBackground),
                                RoundedCornerShape(corner)
                            )
                        } else {
                            Modifier.background(Color(itemBackground))
                        }
                    )
                    .clickable(onClick = onClick)
                    .padding(end = dimensionResource(R.dimen.normal_margin)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_drag),
                    contentDescription = stringResource(R.string.content_des_drag_flag),
                    tint = mainThemeColor(R.attr.colorPrimary),
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.little_margin))
                        .size(dimensionResource(R.dimen.material_icon_size))
                        .pointerInput(model.location.formattedId) {
                            detectDragGestures(
                                onDragEnd = onDragEnd,
                                onDragCancel = onDragEnd,
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    onDrag(dragAmount.y)
                                }
                            )
                        },
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = dimensionResource(R.dimen.normal_margin)),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (weatherIcon != null) {
                            val bitmap = remember(weatherIcon) {
                                weatherIcon.toBitmap().asImageBitmap()
                            }
                            Image(
                                bitmap = bitmap,
                                contentDescription = null,
                                modifier = Modifier.size(
                                    dimensionResource(R.dimen.little_weather_icon_size)
                                ),
                            )
                            Spacer(Modifier.width(dimensionResource(R.dimen.little_margin)))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = model.title1,
                                color = if (model.selected) {
                                    mainThemeColor(R.attr.colorOnPrimaryContainer)
                                } else {
                                    mainThemeColor(R.attr.colorTitleText)
                                },
                                fontSize = dimensionResource(R.dimen.title_text_size).value.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (model.title2.isNotEmpty()) {
                                Text(
                                    text = model.title2,
                                    color = mainThemeColor(R.attr.colorBodyText),
                                    fontSize = dimensionResource(R.dimen.content_text_size).value.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                        if (model.residentPosition) {
                            Icon(
                                painter = painterResource(R.drawable.ic_circle_medium),
                                contentDescription = null,
                                tint = mainThemeColor(R.attr.colorSecondary),
                                modifier = Modifier.size(
                                    dimensionResource(R.dimen.material_icon_size)
                                ),
                            )
                        }
                    }
                    Text(
                        text = model.subtitle,
                        color = mainThemeColor(R.attr.colorCaptionText),
                        fontSize = dimensionResource(R.dimen.subtitle_text_size).value.sp,
                    )
                    Text(
                        text = "Powered by ${model.weatherSource.sourceUrl}",
                        color = Color(model.weatherSource.sourceColor),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    )
}
