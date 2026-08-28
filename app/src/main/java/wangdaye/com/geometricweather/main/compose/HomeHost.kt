package wangdaye.com.geometricweather.main.compose

import android.animation.Animator
import android.annotation.SuppressLint
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.snackbar.SnackbarContainer
import wangdaye.com.geometricweather.common.ui.widgets.SwipeSwitchLayout
import wangdaye.com.geometricweather.common.bus.EventBus
import wangdaye.com.geometricweather.databinding.FragmentHomeBinding
import wangdaye.com.geometricweather.main.MainActivityViewModel
import wangdaye.com.geometricweather.main.adapters.main.MainAdapter
import wangdaye.com.geometricweather.main.fragments.ModifyMainSystemBarMessage
import wangdaye.com.geometricweather.main.layouts.MainLayoutManager
import wangdaye.com.geometricweather.main.utils.MainModuleUtils
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.ThemeManager
import wangdaye.com.geometricweather.theme.weatherThemeDelegate
import wangdaye.com.geometricweather.theme.resource.ResourcesProviderFactory
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider
import wangdaye.com.geometricweather.theme.weatherView.WeatherView
import wangdaye.com.geometricweather.theme.weatherView.WeatherViewController

/**
 * View-backed Home content hosted from Compose via [AndroidView].
 * Keeps [WeatherView], [MainAdapter] / [MainLayoutManager], and [SwipeSwitchLayout]
 * as real Android Views so the weather animation is not rewritten to Canvas.
 */
class HomeHost(
    private val activity: GeoActivity,
    private val viewModel: MainActivityViewModel,
    var onManageIconClicked: () -> Unit,
    var onSettingsIconClicked: () -> Unit,
) {

    val binding: FragmentHomeBinding = FragmentHomeBinding.inflate(activity.layoutInflater)
    val weatherView: WeatherView = ThemeManager
        .getInstance(activity)
        .weatherThemeDelegate
        .getWeatherView(activity)
    val root: View
        get() = binding.root

    val snackbarContainer: SnackbarContainer
        get() = SnackbarContainer(activity, binding.root as ViewGroup, true)

    private var adapter: MainAdapter? = null
    private var scrollListener: OnScrollListener? = null
    private var recyclerViewAnimator: Animator? = null
    private var resourceProvider: ResourceProvider? = null
    private val previewOffset = MutableStateFlow(0)
    private var weatherDrawableOverride: Boolean? = null
    private var collectJob: Job? = null
    private var lastUiMode: Int = activity.resources.configuration.uiMode
    private var released = false

    init {
        binding.root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        (binding.switchLayout.parent as CoordinatorLayout).addView(
            weatherView as View,
            0,
            CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        initView()
    }

    fun startCollecting(owner: LifecycleOwner) {
        if (collectJob != null) {
            return
        }
        collectJob = owner.lifecycleScope.launch {
            owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.currentLocation.collectLatest { holder ->
                        holder?.let { updateViews(it.location) }
                    }
                }
                launch {
                    viewModel.loading.collectLatest { setRefreshing(it) }
                }
                launch {
                    viewModel.indicator.collectLatest { data ->
                        data ?: return@collectLatest
                        binding.switchLayout.isEnabled = data.total > 1
                        if (binding.switchLayout.totalCount != data.total
                            || binding.switchLayout.position != data.index) {
                            binding.switchLayout.setData(data.index, data.total)
                            binding.indicator.setSwitchView(binding.switchLayout)
                        }
                        binding.indicator.visibility =
                            if (data.total > 1) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    previewOffset.collectLatest {
                        binding.root.post {
                            if (!released) {
                                updatePreviewSubviews()
                            }
                        }
                    }
                }
            }
        }
    }

    fun onActivityResume() {
        applyWeatherDrawable()
    }

    fun onActivityPause() {
        weatherView.setDrawable(false)
    }

    fun setWeatherDrawableEnabled(enabled: Boolean) {
        weatherDrawableOverride = enabled
        applyWeatherDrawable()
    }

    fun applyWeatherDrawable() {
        weatherView.setDrawable(weatherDrawableOverride ?: true)
    }

    fun setSystemBarStyle() {
        ThemeManager
            .getInstance(activity)
            .weatherThemeDelegate
            .setSystemBarStyle(
                activity,
                activity.window,
                statusShader = scrollListener?.topOverlap == true,
                lightStatus = false,
                navigationShader = true,
                lightNavigation = false
            )
    }

    fun onConfigurationChangedIfNeeded() {
        val uiMode = activity.resources.configuration.uiMode
        if (uiMode == lastUiMode) {
            return
        }
        lastUiMode = uiMode
        updateDayNightColors()
        updateViews()
    }

    @JvmOverloads
    fun updateViews(location: Location? = viewModel.currentLocation.value?.location) {
        if (location == null || released) {
            return
        }
        ensureResourceProvider()
        updateContentViews(location = location)
        binding.root.post {
            if (!released) {
                updatePreviewSubviews()
            }
        }
    }

    fun release() {
        released = true
        collectJob?.cancel()
        collectJob = null
        adapter = null
        binding.recyclerView.clearOnScrollListeners()
        scrollListener = null
        weatherView.setDrawable(false)
    }

    @SuppressLint("ClickableViewAccessibility", "NonConstantResourceId", "NotifyDataSetChanged")
    private fun initView() {
        ensureResourceProvider()

        weatherView.setGravitySensorEnabled(
            SettingsManager.getInstance(activity).isGravitySensorEnabled
        )

        binding.toolbar.setNavigationOnClickListener { onManageIconClicked() }
        binding.toolbar.inflateMenu(R.menu.activity_main)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_manage -> onManageIconClicked()
                R.id.action_settings -> onSettingsIconClicked()
            }
            true
        }

        binding.switchLayout.setOnSwitchListener(switchListener)
        binding.switchLayout.reset()
        binding.indicator.setSwitchView(binding.switchLayout)
        binding.indicator.setCurrentIndicatorColor(Color.WHITE)
        binding.indicator.setIndicatorColor(
            ColorUtils.setAlphaComponent(Color.WHITE, (0.5 * 255).toInt())
        )

        binding.refreshLayout.setOnRefreshListener {
            viewModel.updateWithUpdatingChecking(
                triggeredByUser = true,
                checkPermissions = true
            )
        }

        val listAnimationEnabled = SettingsManager
            .getInstance(activity)
            .isListAnimationEnabled
        val itemAnimationEnabled = SettingsManager
            .getInstance(activity)
            .isItemAnimationEnabled
        adapter = MainAdapter(
            activity,
            binding.recyclerView,
            weatherView,
            null,
            resourceProvider!!,
            listAnimationEnabled,
            itemAnimationEnabled
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = MainLayoutManager()
        binding.recyclerView.addOnScrollListener(OnScrollListener().also { scrollListener = it })
        binding.recyclerView.setOnTouchListener(indicatorStateListener)
    }

    private fun updateDayNightColors() {
        val location = viewModel.currentLocation.value?.location ?: return
        binding.refreshLayout.setProgressBackgroundColorSchemeColor(
            MainThemeColorProvider.getColor(
                location = location,
                id = R.attr.colorSurface
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility", "NotifyDataSetChanged")
    private fun updateContentViews(location: Location) {
        if (recyclerViewAnimator != null) {
            recyclerViewAnimator!!.cancel()
            recyclerViewAnimator = null
        }

        updateDayNightColors()
        binding.switchLayout.reset()

        if (location.weather == null) {
            adapter!!.setNullWeather()
            adapter!!.notifyDataSetChanged()
            binding.recyclerView.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN
                    && !binding.refreshLayout.isRefreshing) {
                    viewModel.updateWithUpdatingChecking(
                        triggeredByUser = true,
                        checkPermissions = true
                    )
                }
                false
            }
            return
        }

        binding.recyclerView.setOnTouchListener(null)

        val listAnimationEnabled = SettingsManager
            .getInstance(activity)
            .isListAnimationEnabled
        val itemAnimationEnabled = SettingsManager
            .getInstance(activity)
            .isItemAnimationEnabled
        adapter!!.update(
            activity,
            binding.recyclerView,
            weatherView,
            location,
            resourceProvider!!,
            listAnimationEnabled,
            itemAnimationEnabled
        )
        adapter!!.notifyDataSetChanged()

        scrollListener!!.postReset(binding.recyclerView)

        if (!listAnimationEnabled) {
            binding.recyclerView.alpha = 0f
            recyclerViewAnimator = MainModuleUtils.getEnterAnimator(
                binding.recyclerView,
                0
            )
            recyclerViewAnimator!!.startDelay = 150
            recyclerViewAnimator!!.start()
        }
    }

    private fun ensureResourceProvider() {
        val iconProvider = SettingsManager
            .getInstance(activity)
            .iconProvider
        if (resourceProvider == null
            || resourceProvider!!.packageName != iconProvider) {
            resourceProvider = ResourcesProviderFactory.getNewInstance()
        }
    }

    private fun updatePreviewSubviews() {
        val provider = resourceProvider ?: return
        if (viewModel.validLocationList.value?.locationList.isNullOrEmpty()) {
            return
        }
        val location = viewModel.getValidLocation(previewOffset.value)
        val daylight = location.isDaylight

        binding.toolbar.title = location.getCityName(activity)
        WeatherViewController.setWeatherCode(
            weatherView,
            location.weather,
            daylight,
            provider
        )
        binding.refreshLayout.setColorSchemeColors(
            ThemeManager
                .getInstance(activity)
                .weatherThemeDelegate
                .getThemeColors(
                    activity,
                    WeatherViewController.getWeatherKind(location.weather),
                    daylight
                )[0]
        )
    }

    private fun setRefreshing(b: Boolean) {
        binding.refreshLayout.post {
            if (!released) {
                binding.refreshLayout.isRefreshing = b
            }
        }
    }

    private fun setPreviewOffset(value: Int) {
        if (previewOffset.value != value) {
            previewOffset.value = value
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private val indicatorStateListener = View.OnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_MOVE ->
                binding.indicator.setDisplayState(true)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                binding.indicator.setDisplayState(false)
        }
        false
    }

    private val switchListener: SwipeSwitchLayout.OnSwitchListener =
        object : SwipeSwitchLayout.OnSwitchListener {

            override fun onSwiped(swipeDirection: Int, progress: Float) {
                binding.indicator.setDisplayState(progress != 0f)

                if (progress >= 1) {
                    setPreviewOffset(
                        if (swipeDirection == SwipeSwitchLayout.SWIPE_DIRECTION_LEFT) 1 else -1
                    )
                } else {
                    setPreviewOffset(0)
                }
            }

            override fun onSwitched(swipeDirection: Int) {
                binding.indicator.setDisplayState(false)
                viewModel.offsetLocation(
                    if (swipeDirection == SwipeSwitchLayout.SWIPE_DIRECTION_LEFT) 1 else -1
                )
                setPreviewOffset(0)
            }
        }

    private inner class OnScrollListener : RecyclerView.OnScrollListener() {

        private var mTopChanged: Boolean? = null
        var topOverlap = false
        private var mFirstCardMarginTop = 0
        private var mScrollY = 0
        private var mLastAppBarTranslationY = 0f

        fun postReset(recyclerView: RecyclerView) {
            recyclerView.post {
                if (released) {
                    return@post
                }
                mTopChanged = null
                topOverlap = false
                mFirstCardMarginTop = 0
                mScrollY = 0
                mLastAppBarTranslationY = 0f
                onScrolled(recyclerView, 0, 0)
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            mFirstCardMarginTop = if (recyclerView.childCount > 0) {
                recyclerView.getChildAt(0).measuredHeight
            } else {
                -1
            }

            mScrollY = recyclerView.computeVerticalScrollOffset()
            mLastAppBarTranslationY = binding.appBar.translationY
            weatherView.onScroll(mScrollY)

            adapter?.onScroll()

            if (adapter != null && mFirstCardMarginTop > 0) {
                if (mFirstCardMarginTop >= binding.appBar.measuredHeight
                    + adapter!!.currentTemperatureTextHeight) {
                    when {
                        mScrollY < (mFirstCardMarginTop
                                - binding.appBar.measuredHeight
                                - adapter!!.currentTemperatureTextHeight) -> {
                            binding.appBar.translationY = 0f
                        }
                        mScrollY > mFirstCardMarginTop - binding.appBar.y -> {
                            binding.appBar.translationY = -binding.appBar.measuredHeight.toFloat()
                        }
                        else -> {
                            binding.appBar.translationY = (
                                    mFirstCardMarginTop
                                            - adapter!!.currentTemperatureTextHeight
                                            - mScrollY
                                            - binding.appBar.measuredHeight
                                    ).toFloat()
                        }
                    }
                } else {
                    binding.appBar.translationY = -mScrollY.toFloat()
                }
            }

            if (mFirstCardMarginTop <= 0) {
                mTopChanged = true
                topOverlap = false
            } else {
                mTopChanged = (binding.appBar.translationY != 0f) != (mLastAppBarTranslationY != 0f)
                topOverlap = binding.appBar.translationY != 0f
            }
            if (mTopChanged == true) {
                EventBus.instance
                    .with(ModifyMainSystemBarMessage::class.java)
                    .postValue(ModifyMainSystemBarMessage())
            }
        }
    }
}
