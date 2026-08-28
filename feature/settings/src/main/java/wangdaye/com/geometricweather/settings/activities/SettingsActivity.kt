package wangdaye.com.geometricweather.settings.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.bus.EventBus
import wangdaye.com.geometricweather.common.ui.widgets.Material3Scaffold
import wangdaye.com.geometricweather.common.ui.widgets.generateCollapsedScrollBehavior
import wangdaye.com.geometricweather.common.ui.widgets.insets.FitStatusBarTopAppBar
import wangdaye.com.geometricweather.common.utils.helpers.IntentHelper
import wangdaye.com.geometricweather.common.utils.helpers.startAboutActivity
import wangdaye.com.geometricweather.common.utils.helpers.startCardDisplayManageActivity
import wangdaye.com.geometricweather.common.utils.helpers.startDailyTrendDisplayManageActivity
import wangdaye.com.geometricweather.common.utils.helpers.startHourlyTrendDisplayManageActivityForResult
import wangdaye.com.geometricweather.common.utils.helpers.startPreviewIconActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSelectProviderActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSettingsActivity
import wangdaye.com.geometricweather.settings.SettingsChangedMessage
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.settings.compose.*
import wangdaye.com.geometricweather.theme.compose.GeometricWeatherTheme

class SettingsActivity : GeoActivity() {

    private val cardDisplayState = mutableStateOf(
        SettingsManager.getInstance(this).cardDisplayList
    )
    private val dailyTrendDisplayState = mutableStateOf(
        SettingsManager.getInstance(this).dailyTrendDisplayList
    )
    private val hourlyTrendDisplayState = mutableStateOf(
        SettingsManager.getInstance(this).hourlyTrendDisplayList
    )

    private var requestPostNotificationPermissionSucceedCallback: (() -> Unit)? = null

    // Activity Result API for POST_NOTIFICATIONS permission.
    private val postNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            requestPostNotificationPermissionSucceedCallback?.let { it() }
            requestPostNotificationPermissionSucceedCallback = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GeometricWeatherTheme(lightTheme = !isSystemInDarkTheme()) {
                ContentView()
            }
        }

        EventBus.instance.with(SettingsChangedMessage::class.java).observe(this) {
            val cardDisplayList = SettingsManager.getInstance(this).cardDisplayList
            if (cardDisplayState.value != cardDisplayList) {
                cardDisplayState.value = cardDisplayList
            }

            val dailyTrendDisplayList = SettingsManager.getInstance(this).dailyTrendDisplayList
            if (dailyTrendDisplayState.value != dailyTrendDisplayList) {
                dailyTrendDisplayState.value = dailyTrendDisplayList
            }

            val hourlyTrendDisplayList = SettingsManager.getInstance(this).hourlyTrendDisplayList
            if (hourlyTrendDisplayState.value != hourlyTrendDisplayList) {
                hourlyTrendDisplayState.value = hourlyTrendDisplayList
            }
        }
    }

    @Composable
    private fun ContentView() {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = SettingsScreenRouter.Root.route
        ) {
            composable(SettingsScreenRouter.Root.route) {
                SettingsListScaffold(showAbout = true) { paddings ->
                    RootSettingsView(
                        context = this@SettingsActivity,
                        navController = navController,
                        paddingValues = paddings,
                        postNotificationPermissionEnsurer = { succeedCallback ->
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                                succeedCallback()
                                return@RootSettingsView
                            }
                            if (ContextCompat.checkSelfPermission(
                                    this@SettingsActivity,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED) {
                                return@RootSettingsView
                            }

                            requestPostNotificationPermissionSucceedCallback = succeedCallback
                            postNotificationPermissionLauncher.launch(
                                Manifest.permission.POST_NOTIFICATIONS
                            )
                        }
                    )
                }
            }
            composable(SettingsScreenRouter.Appearance.route) {
                SettingsListScaffold(showAbout = false) { paddings ->
                    AppearanceSettingsScreen(
                        context = this@SettingsActivity,
                        navController = navController,
                        cardDisplayList = remember { cardDisplayState }.value,
                        dailyTrendDisplayList = remember { dailyTrendDisplayState }.value,
                        hourlyTrendDisplayList = remember { hourlyTrendDisplayState }.value,
                        paddingValues = paddings,
                    )
                }
            }
            composable(SettingsScreenRouter.ServiceProvider.route) {
                SettingsListScaffold(showAbout = false) { paddings ->
                    ServiceProviderSettingsScreen(
                        context = this@SettingsActivity,
                        navController = navController,
                        paddingValues = paddings,
                    )
                }
            }
            composable(SettingsScreenRouter.ServiceProviderAdvanced.route) {
                SettingsListScaffold(showAbout = false) { paddings ->
                    SettingsProviderAdvancedSettingsScreen(
                        context = this@SettingsActivity,
                        paddingValues = paddings,
                    )
                }
            }
            composable(SettingsScreenRouter.Unit.route) {
                SettingsListScaffold(showAbout = false) { paddings ->
                    UnitSettingsScreen(
                        context = this@SettingsActivity,
                        paddingValues = paddings,
                    )
                }
            }
            composable(SettingsScreenRouter.CardDisplay.route) {
                CardDisplayManageRoute(
                    onBack = { navController.popBackStack() },
                    onMutated = { setResult(RESULT_OK) },
                )
            }
            composable(SettingsScreenRouter.DailyTrendDisplay.route) {
                DailyTrendDisplayManageRoute(
                    onBack = { navController.popBackStack() },
                    onMutated = { setResult(RESULT_OK) },
                )
            }
            composable(SettingsScreenRouter.HourlyTrendDisplay.route) {
                HourlyTrendDisplayManageRoute(
                    onBack = { navController.popBackStack() },
                    onMutated = { setResult(RESULT_OK) },
                )
            }
        }
    }

    @Composable
    private fun SettingsListScaffold(
        showAbout: Boolean,
        content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit,
    ) {
        val scrollBehavior = generateCollapsedScrollBehavior()
        Material3Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                FitStatusBarTopAppBar(
                    title = stringResource(R.string.action_settings),
                    onBackPressed = { finish() },
                    actions = {
                        if (showAbout) {
                            IconButton(
                                onClick = {
                                    IntentHelper.startAboutActivity(this@SettingsActivity)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = stringResource(R.string.action_about),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            content = content,
        )
    }

    @Preview
    @Composable
    private fun DefaultPreview() {
        GeometricWeatherTheme(lightTheme = isSystemInDarkTheme()) {
            ContentView()
        }
    }
}