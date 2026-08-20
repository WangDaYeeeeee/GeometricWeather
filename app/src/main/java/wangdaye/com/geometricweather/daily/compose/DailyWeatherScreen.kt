package wangdaye.com.geometricweather.daily.compose

import androidx.compose.foundation.ExperimentalFoundationApi

import android.view.LayoutInflater
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.unit.PollenUnit
import wangdaye.com.geometricweather.common.basic.models.weather.Daily
import wangdaye.com.geometricweather.db.DatabaseHelper
import wangdaye.com.geometricweather.common.basic.models.weather.Pollen
import wangdaye.com.geometricweather.common.ui.widgets.AnimatableIconView
import wangdaye.com.geometricweather.common.ui.widgets.Material3Scaffold
import wangdaye.com.geometricweather.common.ui.widgets.RoundProgress
import wangdaye.com.geometricweather.common.ui.widgets.astro.MoonPhaseView
import wangdaye.com.geometricweather.common.ui.widgets.getWidgetSurfaceColor
import wangdaye.com.geometricweather.daily.adapter.DailyWeatherAdapter
import wangdaye.com.geometricweather.daily.adapter.model.DailyAirQuality
import wangdaye.com.geometricweather.daily.adapter.model.DailyAstro
import wangdaye.com.geometricweather.daily.adapter.model.DailyPollen
import wangdaye.com.geometricweather.daily.adapter.model.DailyUV
import wangdaye.com.geometricweather.daily.adapter.model.DailyWind
import wangdaye.com.geometricweather.daily.adapter.model.LargeTitle
import wangdaye.com.geometricweather.daily.adapter.model.Line
import wangdaye.com.geometricweather.daily.adapter.model.Margin
import wangdaye.com.geometricweather.daily.adapter.model.Overview
import wangdaye.com.geometricweather.daily.adapter.model.Title
import wangdaye.com.geometricweather.daily.adapter.model.Value
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.ThemeManager
import wangdaye.com.geometricweather.theme.compose.DayNightTheme
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.TimeZone

private const val SPAN_COUNT = 3

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DailyWeatherRoute(
    formattedId: String?,
    initialIndex: Int,
    onMissingData: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    var location by remember { mutableStateOf<Location?>(null) }
    var failed by remember { mutableStateOf(false) }

    LaunchedEffect(formattedId) {
        val loaded = withContext(Dispatchers.IO) {
            val helper = DatabaseHelper.getInstance(context)
            val loc = if (!formattedId.isNullOrEmpty()) {
                helper.readLocation(formattedId)
            } else {
                null
            }
            val resolved = loc ?: helper.readLocationList().firstOrNull()
            resolved?.let { Location.copy(it, helper.readWeather(it)) }
        }
        if (loaded?.weather == null) {
            failed = true
        } else {
            location = loaded
        }
    }

    if (failed) {
        LaunchedEffect(Unit) { onMissingData() }
        return
    }
    val weather = location?.weather ?: return
    val days = weather.dailyForecast
    if (days.isEmpty()) {
        LaunchedEffect(Unit) { onMissingData() }
        return
    }
    DailyWeatherScreen(
        dailyForecast = days,
        timeZone = location!!.timeZone,
        initialIndex = initialIndex.coerceIn(0, days.size - 1),
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DailyWeatherScreen(
    dailyForecast: List<Daily>,
    timeZone: TimeZone,
    initialIndex: Int,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val showLunar = SettingsManager.getInstance(context).language.isChinese
    val pagerState = rememberPagerState(
        initialPage = initialIndex.coerceIn(0, (dailyForecast.size - 1).coerceAtLeast(0)),
        pageCount = { dailyForecast.size }
    )
    val current = dailyForecast.getOrNull(pagerState.currentPage)

    Material3Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = current?.getDate(stringResource(R.string.date_format_widget_long))
                                ?: "",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = dimensionResource(R.dimen.title_text_size).value.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (showLunar && current != null) {
                            Text(
                                text = current.lunar,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = dimensionResource(R.dimen.subtitle_text_size).value.sp,
                                modifier = Modifier.padding(top = 2.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_desc_back),
                        )
                    }
                },
                actions = {
                    val indicator = when {
                        current == null -> ""
                        current.isToday(timeZone) -> stringResource(R.string.today)
                        else -> "${pagerState.currentPage + 1}/${dailyForecast.size}"
                    }
                    Text(
                        text = indicator,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = dimensionResource(R.dimen.subtitle_text_size).value.sp,
                        modifier = Modifier.padding(end = dimensionResource(R.dimen.normal_margin)),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = getWidgetSurfaceColor(6.dp),
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.primary,
                ),
            )
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            pageSpacing = 1.dp,
        ) { page ->
            val daily = dailyForecast[page]
            val models = remember(daily, timeZone) {
                DailyWeatherAdapter.buildModelList(context, timeZone, daily)
            }
            DailyWeatherPage(models = models)
        }
    }
}

@Composable
private fun DailyWeatherPage(models: List<DailyWeatherAdapter.ViewModel>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(SPAN_COUNT),
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
    ) {
        itemsIndexed(
            items = models,
            span = { _, model ->
                GridItemSpan(if (Value.isCode(model.code)) 1 else SPAN_COUNT)
            }
        ) { _, model ->
            DailyWeatherItem(model)
        }
    }
}

@Composable
private fun DailyWeatherItem(model: DailyWeatherAdapter.ViewModel) {
    when (model) {
        is LargeTitle -> LargeTitleItem(model.title)
        is Overview -> OverviewItem(model)
        is Line -> Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outline)
        )
        is Margin -> Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.normal_margin))
        )
        is Value -> ValueItem(model.title, model.value)
        is Title -> TitleItem(model.resId, model.title)
        is DailyWind -> WindItem(model)
        is DailyAirQuality -> AirQualityItem(model)
        is DailyAstro -> AstroItem(model)
        is DailyPollen -> PollenItem(model)
        is DailyUV -> UvItem(model)
    }
}

@Composable
private fun LargeTitleItem(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        fontSize = dimensionResource(R.dimen.content_text_size).value.sp,
        modifier = Modifier.padding(
            start = dimensionResource(R.dimen.normal_margin),
            end = dimensionResource(R.dimen.normal_margin),
            top = dimensionResource(R.dimen.normal_margin),
        )
    )
}

@Composable
private fun TitleItem(resId: Int?, title: String) {
    Row(
        modifier = Modifier.padding(
            start = dimensionResource(R.dimen.normal_margin),
            end = dimensionResource(R.dimen.normal_margin),
            top = dimensionResource(R.dimen.normal_margin),
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (resId != null) {
            Icon(
                painter = painterResource(resId),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.material_icon_size))
                    .padding(end = dimensionResource(R.dimen.little_margin)),
            )
        }
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = dimensionResource(R.dimen.subtitle_text_size).value.sp,
        )
    }
}

@Composable
private fun ValueItem(title: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "$title, $value" }
            .padding(
                top = dimensionResource(R.dimen.little_margin),
                start = dimensionResource(R.dimen.normal_margin),
                end = dimensionResource(R.dimen.normal_margin),
            )
    ) {
        Text(
            text = title,
            color = DayNightTheme.colors.captionColor,
            fontSize = dimensionResource(R.dimen.subtitle_text_size).value.sp,
        )
        Text(
            text = value,
            color = DayNightTheme.colors.titleColor,
            fontWeight = FontWeight.Bold,
            fontSize = dimensionResource(R.dimen.title_text_size).value.sp,
        )
    }
}

@Composable
private fun OverviewItem(model: Overview) {
    val context = LocalContext.current
    val provider = remember { ResourcesProviderFactory.getNewInstance() }
    val unit = SettingsManager.getInstance(context).temperatureUnit
    val text = model.halfDay.weatherText + ", " +
            model.halfDay.temperature.getTemperature(context, unit)
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { ctx ->
            LayoutInflater.from(ctx).inflate(R.layout.item_weather_daily_overview, null, false)
        },
        update = { view ->
            val icon = view.findViewById<AnimatableIconView>(R.id.item_weather_daily_overview_icon)
            val title = view.findViewById<TextView>(R.id.item_weather_daily_overview_text)
            icon.setAnimatableIcon(
                provider.getWeatherIcons(model.halfDay.weatherCode, model.isDaytime),
                provider.getWeatherAnimators(model.halfDay.weatherCode, model.isDaytime)
            )
            title.text = text
            view.setOnClickListener { icon.startAnimators() }
        }
    )
}

@Composable
private fun WindItem(model: DailyWind) {
    val context = LocalContext.current
    val wind = model.wind
    val speedUnit = SettingsManager.getInstance(context).speedUnit
    val speed = wind.speed
    val directionText = if (wind.degree.isNoDirection || wind.degree.degree % 45f == 0f) {
        wind.direction
    } else {
        "${wind.direction} (${(wind.degree.degree % 360).toInt()}°)"
    }
    val talkBack = buildString {
        append(stringResource(R.string.wind))
        append(", ").append(wind.direction)
        if (speed != null && speed > 0) {
            append(", ").append(speedUnit.getValueText(context, speed))
        }
        append(", ").append(wind.level)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = talkBack }
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_navigation),
            contentDescription = null,
            tint = Color(wind.getWindColor(context)),
            modifier = Modifier
                .padding(25.dp)
                .size(dimensionResource(R.dimen.material_icon_size))
                .rotate(wind.degree.degree + 180f)
                .align(Alignment.CenterVertically),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    end = dimensionResource(R.dimen.normal_margin),
                    bottom = dimensionResource(R.dimen.little_margin),
                )
        ) {
            LabeledValue(
                label = stringResource(R.string.wind_direction),
                value = directionText,
            )
            if (speed != null && speed > 0) {
                LabeledValue(
                    label = stringResource(R.string.wind_speed),
                    value = speedUnit.getValueText(context, speed),
                )
            }
            LabeledValue(
                label = stringResource(R.string.wind_level),
                value = wind.level,
            )
        }
    }
}

@Composable
private fun LabeledValue(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = dimensionResource(R.dimen.little_margin))) {
        Text(
            text = label,
            color = DayNightTheme.colors.captionColor,
            fontSize = dimensionResource(R.dimen.subtitle_text_size).value.sp,
        )
        Text(
            text = value,
            color = DayNightTheme.colors.titleColor,
            fontWeight = FontWeight.Bold,
            fontSize = dimensionResource(R.dimen.title_text_size).value.sp,
        )
    }
}

@Composable
private fun AirQualityItem(model: DailyAirQuality) {
    val context = LocalContext.current
    val airQuality = model.airQuality
    val aqi = airQuality.aqiIndex ?: 0
    val color = airQuality.getAqiColor(context)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = dimensionResource(R.dimen.normal_margin),
                start = dimensionResource(R.dimen.normal_margin),
                end = dimensionResource(R.dimen.normal_margin),
            )
    ) {
        Text(
            text = stringResource(R.string.air_quality),
            color = DayNightTheme.colors.titleColor,
            fontSize = dimensionResource(R.dimen.title_text_size).value.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            factory = { RoundProgress(it) },
            update = { progress ->
                progress.max = 400f
                progress.progress = aqi.toFloat()
                progress.setProgressColor(color)
                progress.setProgressBackgroundColor(ColorUtils.setAlphaComponent(color, (255 * 0.1).toInt()))
            }
        )
        Text(
            text = "$aqi / ${airQuality.aqiText}",
            color = DayNightTheme.colors.bodyColor,
            fontSize = dimensionResource(R.dimen.content_text_size).value.sp,
            modifier = Modifier.align(Alignment.End),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun AstroItem(model: DailyAstro) {
    val context = LocalContext.current
    val sun = model.sun
    val moon = model.moon
    val phase = model.moonPhase
    val timeZone = model.timeZone
    val talkBack = remember(model) {
        val builder = StringBuilder(context.getString(R.string.sunrise_sunset))
        if (sun.isValid) {
            builder.append(", ")
                .append(context.getString(R.string.content_des_sunrise).replace("$", sun.getRiseTime(context, timeZone) ?: ""))
                .append(", ")
                .append(context.getString(R.string.content_des_sunset).replace("$", sun.getSetTime(context, timeZone) ?: ""))
        }
        if (moon.isValid) {
            builder.append(", ")
                .append(context.getString(R.string.content_des_moonrise).replace("$", moon.getRiseTime(context, timeZone) ?: ""))
                .append(", ")
                .append(context.getString(R.string.content_des_moonset).replace("$", moon.getSetTime(context, timeZone) ?: ""))
        }
        if (phase.isValid) {
            builder.append(", ").append(phase.getMoonPhase(context))
        }
        builder.toString()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = talkBack }
    ) {
        if (sun.isValid) {
            AstroRow(
                icon = R.drawable.weather_clear_day_mini_xml,
                text = "${sun.getRiseTime(context, timeZone)}↑ / ${sun.getSetTime(context, timeZone)}↓",
            )
        }
        if (moon.isValid) {
            AstroRow(
                icon = R.drawable.weather_clear_night_mini_xml,
                text = "${moon.getRiseTime(context, timeZone)}↑ / ${moon.getSetTime(context, timeZone)}↓",
            )
        }
        if (phase.isValid) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val light = ContextCompat.getColor(context, R.color.colorTextLight2nd)
                val dark = ContextCompat.getColor(context, R.color.colorTextDark2nd)
                val stroke = ThemeManager.getInstance(context)
                    .getThemeColor(context, R.attr.colorBodyText)
                AndroidView(
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.normal_margin))
                        .size(dimensionResource(R.dimen.material_icon_size)),
                    factory = { MoonPhaseView(it) },
                    update = { view ->
                        view.setSurfaceAngle((phase.angle ?: 0).toFloat())
                        view.setColor(light, dark, stroke)
                    }
                )
                Text(
                    text = phase.getMoonPhase(context) ?: "",
                    color = DayNightTheme.colors.titleColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = dimensionResource(R.dimen.title_text_size).value.sp,
                    modifier = Modifier.padding(
                        top = dimensionResource(R.dimen.little_margin),
                        bottom = dimensionResource(R.dimen.little_margin),
                        end = dimensionResource(R.dimen.normal_margin),
                    )
                )
            }
        }
    }
}

@Composable
private fun AstroRow(icon: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.normal_margin))
                .size(dimensionResource(R.dimen.material_icon_size)),
        )
        Text(
            text = text,
            color = DayNightTheme.colors.titleColor,
            fontWeight = FontWeight.Bold,
            fontSize = dimensionResource(R.dimen.title_text_size).value.sp,
            modifier = Modifier.padding(
                top = dimensionResource(R.dimen.little_margin),
                bottom = dimensionResource(R.dimen.little_margin),
                end = dimensionResource(R.dimen.normal_margin),
            )
        )
    }
}

@Composable
private fun PollenItem(model: DailyPollen) {
    val context = LocalContext.current
    val pollen = model.pollen
    val unit = PollenUnit.PPCM
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            PollenCell(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.grass),
                value = unit.getValueText(context, pollen.grassIndex ?: 0) +
                        " - " + pollen.grassDescription,
                color = Color(Pollen.getPollenColor(context, pollen.grassLevel)),
            )
            PollenCell(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.ragweed),
                value = unit.getValueText(context, pollen.ragweedIndex ?: 0) +
                        " - " + pollen.ragweedDescription,
                color = Color(Pollen.getPollenColor(context, pollen.ragweedLevel)),
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            PollenCell(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.tree),
                value = unit.getValueText(context, pollen.treeIndex ?: 0) +
                        " - " + pollen.treeDescription,
                color = Color(Pollen.getPollenColor(context, pollen.treeLevel)),
            )
            PollenCell(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.mold),
                value = unit.getValueText(context, pollen.moldIndex ?: 0) +
                        " - " + pollen.moldDescription,
                color = Color(Pollen.getPollenColor(context, pollen.moldLevel)),
            )
        }
    }
}

@Composable
private fun PollenCell(
    modifier: Modifier,
    title: String,
    value: String,
    color: Color,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_circle_medium),
            contentDescription = null,
            tint = color,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.normal_margin))
                .size(dimensionResource(R.dimen.material_icon_size)),
        )
        Column(
            modifier = Modifier.padding(
                top = dimensionResource(R.dimen.normal_margin),
                bottom = dimensionResource(R.dimen.normal_margin),
            )
        ) {
            Text(
                text = title,
                color = DayNightTheme.colors.bodyColor,
                fontSize = dimensionResource(R.dimen.content_text_size).value.sp,
            )
            Text(
                text = value,
                color = DayNightTheme.colors.captionColor,
                fontSize = dimensionResource(R.dimen.subtitle_text_size).value.sp,
            )
        }
    }
}

@Composable
private fun UvItem(model: DailyUV) {
    val context = LocalContext.current
    val uv = model.uv
    val description = stringResource(R.string.uv_index) + ", " + uv.uvDescription
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_circle_medium),
            contentDescription = null,
            tint = Color(uv.getUVColor(context)),
            modifier = Modifier
                .padding(dimensionResource(R.dimen.normal_margin))
                .size(dimensionResource(R.dimen.material_icon_size)),
        )
        Text(
            text = uv.uvDescription,
            color = DayNightTheme.colors.titleColor,
            fontWeight = FontWeight.Bold,
            fontSize = dimensionResource(R.dimen.title_text_size).value.sp,
            modifier = Modifier.padding(
                top = dimensionResource(R.dimen.little_margin),
                bottom = dimensionResource(R.dimen.little_margin),
                end = dimensionResource(R.dimen.normal_margin),
            )
        )
    }
}
