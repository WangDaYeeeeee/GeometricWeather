package wangdaye.com.geometricweather.search.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.provider.WeatherSource
import wangdaye.com.geometricweather.common.utils.helpers.SnackbarHelper
import wangdaye.com.geometricweather.search.LoadableLocationList
import wangdaye.com.geometricweather.search.SearchActivityViewModel
import wangdaye.com.geometricweather.search.LocationModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchActivityViewModel,
    existingFormattedIds: Set<String>,
    onClose: (Location?) -> Unit,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val listResource by viewModel.listResourceFlow.collectAsState()
    val enabledSources by viewModel.enabledSourcesFlow.collectAsState()

    var query by remember { mutableStateOf(viewModel.queryValue) }
    var showSourceSheet by remember { mutableStateOf(false) }
    var draftEnabled by remember { mutableStateOf(enabledSources.toSet()) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(listResource.status) {
        if (listResource.status == LoadableLocationList.Status.ERROR) {
            SnackbarHelper.showSnackbar(context.getString(R.string.feedback_search_nothing))
        }
    }

    val submitSearch: () -> Unit = {
        val trimmed = query.trim()
        if (trimmed.isNotEmpty()) {
            keyboardController?.hide()
            viewModel.requestLocationList(trimmed)
        }
    }

    BackHandler {
        if (showSourceSheet) {
            showSourceSheet = false
        } else {
            onClose(null)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(searchThemeColor(R.attr.colorSurfaceVariant))
            .imePadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(searchThemeColor(R.attr.colorSurface))
                    .statusBarsPadding()
                    .height(56.dp)
                    .padding(end = dimensionResource(R.dimen.normal_margin)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { onClose(null) }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = stringResource(R.string.content_desc_back),
                        tint = searchThemeColor(R.attr.colorPrimary),
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    textStyle = TextStyle(
                        color = searchThemeColor(R.attr.colorTitleText),
                        fontSize = dimensionResource(R.dimen.title_text_size).value.sp,
                    ),
                    cursorBrush = SolidColor(searchThemeColor(R.attr.colorPrimary)),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { submitSearch() }),
                    decorationBox = { innerTextField ->
                        Box {
                            if (query.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.feedback_search_location),
                                    color = searchThemeColor(R.attr.colorCaptionText),
                                    fontSize = dimensionResource(R.dimen.title_text_size).value.sp,
                                )
                            }
                            innerTextField()
                        }
                    },
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                if (listResource.status == LoadableLocationList.Status.LOADING) {
                    CircularProgressIndicator(color = searchThemeColor(R.attr.colorPrimary))
                } else {
                    val models = remember(listResource.dataList) {
                        listResource.dataList.map { LocationModel(context, it) }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = dimensionResource(R.dimen.little_margin),
                            bottom = 88.dp,
                        ),
                    ) {
                        items(
                            items = models,
                            key = { it.location.formattedId },
                        ) { model ->
                            SearchResultCard(
                                model = model,
                                onClick = {
                                    if (existingFormattedIds.contains(model.location.formattedId)) {
                                        SnackbarHelper.showSnackbar(
                                            context.getString(R.string.feedback_collect_failed)
                                        )
                                    } else {
                                        val selected = viewModel.locationList.firstOrNull {
                                            it.formattedId == model.location.formattedId
                                        } ?: model.location
                                        onClose(selected)
                                    }
                                },
                            )
                        }
                        item {
                            Spacer(Modifier.navigationBarsPadding())
                        }
                    }
                }
            }
        }

        if (!showSourceSheet) {
            FloatingActionButton(
                onClick = {
                    draftEnabled = enabledSources.toSet()
                    showSourceSheet = true
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(dimensionResource(R.dimen.normal_margin)),
                containerColor = searchThemeColor(R.attr.colorPrimary),
                contentColor = searchThemeColor(R.attr.colorOnPrimary),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_filter),
                    contentDescription = stringResource(R.string.content_desc_filter_weather_sources),
                )
            }
        }
    }

    if (showSourceSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val loading = listResource.status == LoadableLocationList.Status.LOADING
        ModalBottomSheet(
            onDismissRequest = { showSourceSheet = false },
            sheetState = sheetState,
            containerColor = searchThemeColor(R.attr.colorSurface),
        ) {
            LazyColumn {
                items(WeatherSource.entries.toList(), key = { it.id }) { source ->
                    val checked = draftEnabled.contains(source)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clickable {
                                draftEnabled = if (checked) {
                                    draftEnabled - source
                                } else {
                                    draftEnabled + source
                                }
                            }
                            .padding(horizontal = dimensionResource(R.dimen.normal_margin)),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { enabled ->
                                draftEnabled = if (enabled) {
                                    draftEnabled + source
                                } else {
                                    draftEnabled - source
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = searchThemeColor(R.attr.colorPrimary),
                            ),
                        )
                        Spacer(Modifier.width(dimensionResource(R.dimen.little_margin)))
                        Text(
                            text = source.getName(context),
                            color = searchThemeColor(R.attr.colorOnSurfaceVariant),
                            fontSize = dimensionResource(R.dimen.title_text_size).value.sp,
                        )
                    }
                }
                item {
                    Button(
                        onClick = {
                            showSourceSheet = false
                            viewModel.setEnabledSources(
                                WeatherSource.entries.filter { draftEnabled.contains(it) }
                            )
                            if (viewModel.queryValue.isNotEmpty()) {
                                viewModel.requestLocationList()
                            }
                        },
                        enabled = !loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(R.dimen.normal_margin)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = searchThemeColor(R.attr.colorPrimary),
                            contentColor = searchThemeColor(R.attr.colorOnPrimary),
                            disabledContainerColor = searchThemeColor(R.attr.colorPrimary)
                                .copy(alpha = 0.5f),
                        ),
                    ) {
                        Text(text = stringResource(R.string.done))
                    }
                }
            }
        }
    }
}
