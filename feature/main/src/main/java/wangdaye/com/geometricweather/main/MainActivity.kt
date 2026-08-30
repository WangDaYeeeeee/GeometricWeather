package wangdaye.com.geometricweather.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.bus.EventBus
import wangdaye.com.geometricweather.common.snackbar.SnackbarContainer
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import wangdaye.com.geometricweather.common.utils.helpers.AsyncHelper
import wangdaye.com.geometricweather.common.utils.helpers.IntentHelper
import wangdaye.com.geometricweather.common.utils.helpers.startAlertActivity
import wangdaye.com.geometricweather.common.utils.helpers.startDailyWeatherActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSelectProviderActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSettingsActivity
import wangdaye.com.geometricweather.common.utils.helpers.SnackbarHelper
import wangdaye.com.geometricweather.main.compose.HomeHost
import wangdaye.com.geometricweather.main.compose.MainScreen
import wangdaye.com.geometricweather.main.compose.WIDE_LAYOUT_MIN_DP
import wangdaye.com.geometricweather.navigation.InAppRoute
import wangdaye.com.geometricweather.main.dialogs.LocationHelpDialog
import wangdaye.com.geometricweather.main.fragments.ModifyMainSystemBarMessage
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider
import wangdaye.com.geometricweather.search.SearchActivity
import wangdaye.com.geometricweather.settings.SettingsChangedMessage
import wangdaye.com.geometricweather.theme.compose.GeometricWeatherTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : GeoActivity() {

    @Inject lateinit var widgetUpdateGateway: WidgetUpdateGateway

    private lateinit var viewModel: MainActivityViewModel
    private var managementVisible by mutableStateOf(false)
    var homeHost: HomeHost? = null

    companion object {
        const val ACTION_MAIN = "com.wangdaye.geometricweather.Main"
        const val KEY_MAIN_ACTIVITY_LOCATION_FORMATTED_ID = "MAIN_ACTIVITY_LOCATION_FORMATTED_ID"

        const val ACTION_MANAGEMENT = "com.wangdaye.geomtricweather.ACTION_MANAGEMENT"
        const val ACTION_SHOW_ALERTS = "com.wangdaye.geomtricweather.ACTION_SHOW_ALERTS"

        const val ACTION_SHOW_DAILY_FORECAST = "com.wangdaye.geomtricweather.ACTION_SHOW_DAILY_FORECAST"
        const val KEY_DAILY_INDEX = "DAILY_INDEX"
    }

    private val backgroundUpdateObserver: Observer<Location> = Observer { location ->
        viewModel.updateLocationFromBackground(location)

        if (isActivityStarted
            && location.formattedId == viewModel.currentLocation.value?.location?.formattedId) {
            SnackbarHelper.showSnackbar(getString(R.string.feedback_updated_in_background))
        }
    }

    private val searchActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        if (result.resultCode == RESULT_OK && data != null) {
            val location: Location? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data.getParcelableExtra(SearchActivity.KEY_LOCATION, Location::class.java)
            } else {
                data.getParcelableExtra(SearchActivity.KEY_LOCATION)
            }
            if (location != null) {
                viewModel.addLocation(location, null)
                SnackbarHelper.showSnackbar(getString(R.string.feedback_collect_succeed))
            }
        }
    }

    private val permissionsResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val request = viewModel.permissionsRequest.value
        if (request == null
            || request.permissionList.isEmpty()
            || request.target == null) {
            return@registerForActivityResult
        }

        val deniedEssential = permissions.entries.firstOrNull { (permission, granted) ->
            !granted && isEssentialLocationPermission(permission)
        }
        if (deniedEssential != null) {
            if (request.target.isUsable || isLocationPermissionsGranted) {
                viewModel.updateWithUpdatingChecking(
                    request.triggeredByUser,
                    false
                )
            } else {
                viewModel.cancelRequest()
            }
            return@registerForActivityResult
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            && !viewModel.statementManager.isBackgroundLocationDeclared
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.feedback_background_location_title)
                .setMessage(R.string.feedback_background_location_summary)
                .setPositiveButton(R.string.go_to_set) { _, _ ->
                    viewModel.statementManager.setBackgroundLocationDeclared(this)
                    backgroundLocationPermissionLauncher.launch(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                }
                .setCancelable(false)
                .show()
        }
        viewModel.updateWithUpdatingChecking(
            request.triggeredByUser,
            false
        )
    }

    private val backgroundLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        val request = viewModel.permissionsRequest.value
        if (request != null) {
            viewModel.updateWithUpdatingChecking(
                request.triggeredByUser,
                false
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        managementVisible = isWideLayout

        initModel(savedInstanceState == null)
        MainThemeColorProvider.bind(this)

        setContent {
            GeometricWeatherTheme(lightTheme = !isSystemInDarkTheme()) {
                LaunchedEffect(managementVisible, isWideLayout) {
                    updateSystemBarStyle()
                }
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = InAppRoute.HOME,
                ) {
                    composable(InAppRoute.HOME) {
                        MainScreen(
                            viewModel = viewModel,
                            managementVisible = managementVisible,
                            onManagementVisibleChange = { visible ->
                                setManagementFragmentVisibility(visible)
                            },
                            onSearchBarClick = {
                                searchActivityResultLauncher.launch(
                                    Intent(this@MainActivity, SearchActivity::class.java)
                                )
                            },
                            onSelectProvider = {
                                IntentHelper.startSelectProviderActivity(this@MainActivity)
                            },
                            onSettingsIconClicked = {
                                IntentHelper.startSettingsActivity(this@MainActivity)
                            },
                        )
                    }
                }
            }
        }

        initView()

        consumeIntentAction(intent)

        EventBus.instance
            .with(Location::class.java)
            .observeForever(backgroundUpdateObserver)
        EventBus.instance.with(SettingsChangedMessage::class.java).observe(this) {
            viewModel.init()

            homeHost?.updateViews()

            viewModel.validLocationList.value?.locationList?.let {
                AsyncHelper.runOnIO {
                    widgetUpdateGateway.updateNotificationIfNecessary(this, it)
                }
            }
            refreshBackgroundViews(
                resetBackground = true,
                locationList = viewModel.validLocationList.value?.locationList,
            )
        }
        EventBus.instance.with(ModifyMainSystemBarMessage::class.java).observe(this) {
            updateSystemBarStyle()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        consumeIntentAction(getIntent())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (isWideLayout && !managementVisible) {
            managementVisible = true
        }
        updateSystemBarStyle()
        updateDayNightColors()
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkToUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.instance
            .with(Location::class.java)
            .removeObserver(backgroundUpdateObserver)
    }

    override val snackbarContainer: SnackbarContainer?
        get() {
            if (!managementVisible || isWideLayout) {
                return homeHost?.snackbarContainer ?: super.snackbarContainer
            }
            return super.snackbarContainer
        }

    private fun initModel(newActivity: Boolean) {
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        if (!viewModel.checkIsNewInstance()) {
            return
        }
        if (newActivity) {
            viewModel.init(formattedId = getLocationId(intent))
        } else {
            viewModel.init()
        }
    }

    private fun getLocationId(intent: Intent?): String? {
        return intent?.getStringExtra(KEY_MAIN_ACTIVITY_LOCATION_FORMATTED_ID)
    }

    @SuppressLint("ClickableViewAccessibility", "NonConstantResourceId")
    private fun initView() {
        window.decorView.post {
            if (isActivityCreated) {
                updateDayNightColors()
            }
        }

        viewModel.validLocationList.asLiveData().observe(this) { list ->
            val data = list ?: return@observe
            AsyncHelper.runOnIO {
                widgetUpdateGateway.updateNotificationIfNecessary(
                    this,
                    data.locationList
                )
            }
            refreshBackgroundViews(
                resetBackground = false,
                locationList = data.locationList,
            )
        }
        viewModel.permissionsRequest.asLiveData().observe(this) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || it == null
                || it.permissionList.isEmpty()
                || !it.consume()) {
                return@observe
            }

            var needShowDialog = false
            for (permission in it.permissionList) {
                if (isLocationPermission(permission)) {
                    needShowDialog = true
                    break
                }
            }
            if (needShowDialog && !viewModel.statementManager.isLocationPermissionDeclared) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.feedback_location_permissions_title)
                    .setMessage(R.string.feedback_location_permissions_statement)
                    .setPositiveButton(R.string.next) { _, _ ->
                        viewModel.statementManager.setLocationPermissionDeclared(this)

                        val request = viewModel.permissionsRequest.value
                        if (request != null
                            && request.permissionList.isNotEmpty()
                            && request.target != null) {
                            permissionsResultLauncher.launch(
                                request.permissionList.toTypedArray()
                            )
                        }
                    }
                    .setCancelable(false)
                    .show()
            } else {
                permissionsResultLauncher.launch(it.permissionList.toTypedArray())
            }
        }
        viewModel.mainMessage.asLiveData().observe(this) {
            it?. let { msg ->
                when (msg) {
                    MainMessage.LOCATION_FAILED -> {
                        SnackbarHelper.showSnackbar(
                            getString(R.string.feedback_location_failed),
                            getString(R.string.help)
                        ) {
                            LocationHelpDialog.show(this)
                        }
                    }
                    MainMessage.WEATHER_REQ_FAILED -> {
                        SnackbarHelper.showSnackbar(
                            getString(R.string.feedback_get_weather_failed)
                        )
                    }
                }
            }
        }
    }

    private fun isLocationPermission(
        permission: String
    ) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION
                || isEssentialLocationPermission(permission)
    } else {
        isEssentialLocationPermission(permission)
    }

    private fun isEssentialLocationPermission(permission: String): Boolean {
        return permission == Manifest.permission.ACCESS_COARSE_LOCATION
                || permission == Manifest.permission.ACCESS_FINE_LOCATION
    }

    private val isLocationPermissionsGranted: Boolean
        get() = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    val isDaylight
        get() = viewModel.currentLocation.value?.daylight

    private val isWideLayout: Boolean
        get() = resources.configuration.screenWidthDp >= WIDE_LAYOUT_MIN_DP

    private fun consumeIntentAction(intent: Intent) {
        val action = intent.action
        if (TextUtils.isEmpty(action)) {
            return
        }
        val formattedId = intent.getStringExtra(KEY_MAIN_ACTIVITY_LOCATION_FORMATTED_ID)
        if (ACTION_SHOW_ALERTS == action) {
            IntentHelper.startAlertActivity(this, formattedId)
            return
        }
        if (ACTION_SHOW_DAILY_FORECAST == action) {
            val index = intent.getIntExtra(KEY_DAILY_INDEX, 0)
            IntentHelper.startDailyWeatherActivity(this, formattedId, index)
            return
        }
        if (ACTION_MANAGEMENT == action) {
            setManagementFragmentVisibility(true)
        }
    }

    private fun updateSystemBarStyle() {
        if (isWideLayout || !managementVisible) {
            homeHost?.setSystemBarStyle()
            return
        }
        DisplayUtils.setSystemBarStyle(
            this,
            window,
            false,
            !DisplayUtils.isDarkMode(this),
            true,
            !DisplayUtils.isDarkMode(this)
        )
    }

    private fun updateDayNightColors() {
        fitHorizontalSystemBarRootLayout.setBackgroundColor(
            MainThemeColorProvider.getColor(
                lightTheme =  !DisplayUtils.isDarkMode(this),
                id = android.R.attr.colorBackground
            )
        )
    }

    fun setManagementFragmentVisibility(visible: Boolean) {
        if (managementVisible == visible) {
            return
        }
        managementVisible = visible
        updateSystemBarStyle()
    }

    private fun refreshBackgroundViews(resetBackground: Boolean, locationList: List<Location>?) {
        if (resetBackground) {
            AsyncHelper.delayRunOnIO({
                widgetUpdateGateway.resetAllBackgroundTask(
                    this, false
                )
            }, 1000)
        }
        locationList?.let {
            if (it.isNotEmpty()) {
                AsyncHelper.delayRunOnIO({
                    widgetUpdateGateway.updateWidgetsAndNotifications(this, it)
                }, 1000)
                widgetUpdateGateway.refreshShortcuts(this, it)
            }
        }
    }
}
