package wangdaye.com.geometricweather.remoteviews.config

import android.Manifest
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.RemoteViews
import android.widget.Switch
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.xw.repo.BubbleSeekBar
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.background.polling.PollingManager
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.ui.widgets.insets.FitSystemBarNestedScrollView
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import wangdaye.com.geometricweather.common.utils.helpers.SnackbarHelper
import wangdaye.com.geometricweather.db.DatabaseHelper
import wangdaye.com.geometricweather.settings.ConfigStore
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.weather.WeatherHelper
import javax.inject.Inject

abstract class AbstractWidgetConfigActivity : GeoActivity(), WeatherHelper.OnRequestWeatherListener {

    protected lateinit var mTopContainer: FrameLayout
    protected lateinit var mWallpaper: ImageView
    protected lateinit var mWidgetContainer: FrameLayout

    protected lateinit var mScrollView: NestedScrollView
    lateinit var mViewTypeContainer: RelativeLayout
    lateinit var mCardStyleContainer: RelativeLayout
    lateinit var mCardAlphaContainer: RelativeLayout
    lateinit var mHideSubtitleContainer: RelativeLayout
    lateinit var mSubtitleDataContainer: RelativeLayout
    lateinit var mTextColorContainer: RelativeLayout
    lateinit var mTextSizeContainer: RelativeLayout
    lateinit var mClockFontContainer: RelativeLayout
    lateinit var mHideLunarContainer: RelativeLayout
    lateinit var mAlignEndContainer: RelativeLayout

    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var mBottomSheetScrollView: FitSystemBarNestedScrollView
    private lateinit var mSubtitleInputLayout: TextInputLayout
    private lateinit var mSubtitleEditText: TextInputEditText

    lateinit var locationNow: Location

    @Inject lateinit var weatherHelper: WeatherHelper
    protected var destroyed = false

    lateinit var viewTypeValueNow: String
    lateinit var viewTypes: Array<String>
    lateinit var viewTypeValues: Array<String>

    lateinit var cardStyleValueNow: String
    lateinit var cardStyles: Array<String>
    lateinit var cardStyleValues: Array<String>

    var cardAlpha = 0
    var hideSubtitle = false

    lateinit var subtitleDataValueNow: String
    lateinit var subtitleData: Array<String>
    lateinit var subtitleDataValues: Array<String>

    lateinit var textColorValueNow: String
    lateinit var textColors: Array<String>
    lateinit var textColorValues: Array<String>

    var textSize = 0

    lateinit var clockFontValueNow: String
    lateinit var clockFonts: Array<String>
    lateinit var clockFontValues: Array<String>

    var hideLunar = false
    var alignEnd = false

    private var mLastBackPressedTime = -1L
    private var mPendingWallpaperBind = false
    private var mPendingWallpaperUpdate = false

    private val storagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                if (mPendingWallpaperBind) {
                    mPendingWallpaperBind = false
                    bindWallpaperInternal()
                }
                if (mPendingWallpaperUpdate) {
                    mPendingWallpaperUpdate = false
                    updateHostView()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_config)

        initData()
        readConfig()
        initView()
        updateHostView()

        if (locationNow.isCurrentPosition) {
            if (locationNow.isUsable) {
                weatherHelper.requestWeather(this, locationNow, this)
            } else {
                weatherHelper.requestWeather(
                    this,
                    Location.buildDefaultLocation(
                        SettingsManager.getInstance(this).weatherSource
                    ),
                    this
                )
            }
        } else {
            weatherHelper.requestWeather(this, locationNow, this)
        }
    }

    override fun onBackPressed() {
        if (mBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            setBottomSheetState(true)
            return
        }

        val time = System.currentTimeMillis()
        if (time - mLastBackPressedTime < 2000) {
            super.onBackPressed()
            return
        }

        mLastBackPressedTime = time
        SnackbarHelper.showSnackbar(getString(R.string.feedback_click_again_to_exit))
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        if (name == "ImageView") {
            return ImageView(context, attrs)
        }
        return super.onCreateView(parent, name, context, attrs)
    }

    override fun onCreateView(
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        if (name == "ImageView") {
            return ImageView(context, attrs)
        }
        return super.onCreateView(name, context, attrs)
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyed = true
        weatherHelper.cancel()
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        // do nothing.
    }

    @CallSuper
    open fun initData() {
        locationNow = DatabaseHelper.getInstance(this).readLocationList()[0]
        locationNow = Location.copy(
            locationNow,
            DatabaseHelper.getInstance(this).readWeather(locationNow)
        )

        destroyed = false

        val res = resources

        viewTypeValueNow = "rectangle"
        viewTypes = res.getStringArray(R.array.widget_styles)
        viewTypeValues = res.getStringArray(R.array.widget_style_values)

        cardStyleValueNow = "none"
        cardStyles = res.getStringArray(R.array.widget_card_styles)
        cardStyleValues = res.getStringArray(R.array.widget_card_style_values)

        cardAlpha = 100
        hideSubtitle = false

        subtitleDataValueNow = "time"
        val data = res.getStringArray(R.array.subtitle_data)
        val dataValues = res.getStringArray(R.array.subtitle_data_values)
        if (SettingsManager.getInstance(this).language.isChinese) {
            subtitleData = arrayOf(data[0], data[1], data[2], data[3], data[4], data[5])
            subtitleDataValues = arrayOf(
                dataValues[0], dataValues[1], dataValues[2], dataValues[3], dataValues[4], dataValues[5]
            )
        } else {
            subtitleData = arrayOf(data[0], data[1], data[2], data[3], data[5])
            subtitleDataValues = arrayOf(
                dataValues[0], dataValues[1], dataValues[2], dataValues[3], dataValues[5]
            )
        }

        textColorValueNow = "light"
        textColors = res.getStringArray(R.array.widget_text_colors)
        textColorValues = res.getStringArray(R.array.widget_text_color_values)

        textSize = 100

        clockFontValueNow = "light"
        clockFonts = res.getStringArray(R.array.clock_font)
        clockFontValues = res.getStringArray(R.array.clock_font_values)

        hideLunar = false
        alignEnd = false
    }

    private fun readConfig() {
        val config = ConfigStore.getInstance(this, getConfigStoreName())
        viewTypeValueNow = config.getString(getString(R.string.key_view_type), viewTypeValueNow)!!
        cardStyleValueNow = config.getString(getString(R.string.key_card_style), cardStyleValueNow)!!
        cardAlpha = config.getInt(getString(R.string.key_card_alpha), cardAlpha)
        hideSubtitle = config.getBoolean(getString(R.string.key_hide_subtitle), hideSubtitle)
        subtitleDataValueNow = config.getString(getString(R.string.key_subtitle_data), subtitleDataValueNow)!!
        textColorValueNow = config.getString(getString(R.string.key_text_color), textColorValueNow)!!
        textSize = config.getInt(getString(R.string.key_text_size), textSize)
        clockFontValueNow = config.getString(getString(R.string.key_clock_font), clockFontValueNow)!!
        hideLunar = config.getBoolean(getString(R.string.key_hide_lunar), hideLunar)
        alignEnd = config.getBoolean(getString(R.string.key_align_end), alignEnd)
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @CallSuper
    open fun initView() {
        mWallpaper = findViewById(R.id.activity_widget_config_wall)
        bindWallpaper(true)

        mWidgetContainer = findViewById(R.id.activity_widget_config_widgetContainer)

        mTopContainer = findViewById(R.id.activity_widget_config_top)
        val screenWidth = resources.displayMetrics.widthPixels
        val adaptiveWidth = DisplayUtils.getTabletListAdaptiveWidth(this, screenWidth)
        val paddingHorizontal = (screenWidth - adaptiveWidth) / 2
        mTopContainer.setOnApplyWindowInsetsListener { _, insets ->
            val compat = WindowInsetsCompat.toWindowInsetsCompat(insets)
            val systemInsets = compat.getInsets(WindowInsetsCompat.Type.systemBars())
            mWidgetContainer.setPadding(
                paddingHorizontal, systemInsets.top,
                paddingHorizontal, 0
            )
            insets
        }

        mScrollView = findViewById(R.id.activity_widget_config_scrollView)

        mViewTypeContainer = findViewById(R.id.activity_widget_config_viewStyleContainer)
        mViewTypeContainer.visibility = View.GONE
        val viewTypeSpinner: AppCompatSpinner = findViewById(R.id.activity_widget_config_styleSpinner)
        viewTypeSpinner.onItemSelectedListener = ViewTypeSpinnerSelectedListener()
        viewTypeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, viewTypes)
        viewTypeSpinner.setSelection(indexValue(viewTypeValues, viewTypeValueNow), true)

        mCardStyleContainer = findViewById(R.id.activity_widget_config_showCardContainer)
        mCardStyleContainer.visibility = View.GONE
        val cardStyleSpinner: AppCompatSpinner = findViewById(R.id.activity_widget_config_showCardSpinner)
        cardStyleSpinner.onItemSelectedListener = CardStyleSpinnerSelectedListener()
        cardStyleSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cardStyles)
        cardStyleSpinner.setSelection(indexValue(cardStyleValues, cardStyleValueNow), true)

        mCardAlphaContainer = findViewById(R.id.activity_widget_config_cardAlphaContainer)
        mCardAlphaContainer.visibility = View.GONE
        val cardAlphaSeekBar: BubbleSeekBar = findViewById(R.id.activity_widget_config_cardAlphaSeekBar)
        cardAlphaSeekBar.setCustomSectionTextArray { _, array ->
            array.clear()
            array.put(0, "0%")
            array.put(1, "20%")
            array.put(2, "40%")
            array.put(3, "60%")
            array.put(4, "80%")
            array.put(5, "100%")
            array
        }
        cardAlphaSeekBar.setOnProgressChangedListener(CardAlphaChangedListener())
        cardAlphaSeekBar.setProgress(cardAlpha.toFloat())

        mHideSubtitleContainer = findViewById(R.id.activity_widget_config_hideSubtitleContainer)
        mHideSubtitleContainer.visibility = View.GONE
        val hideSubtitleSwitch: Switch = findViewById(R.id.activity_widget_config_hideSubtitleSwitch)
        hideSubtitleSwitch.setOnCheckedChangeListener(HideSubtitleSwitchCheckListener())
        hideSubtitleSwitch.isChecked = hideSubtitle

        mSubtitleDataContainer = findViewById(R.id.activity_widget_config_subtitleDataContainer)
        mSubtitleDataContainer.visibility = View.GONE
        val subtitleDataSpinner: AppCompatSpinner = findViewById(R.id.activity_widget_config_subtitleDataSpinner)
        subtitleDataSpinner.onItemSelectedListener = SubtitleDataSpinnerSelectedListener()
        subtitleDataSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, subtitleData)
        subtitleDataSpinner.setSelection(
            indexValue(subtitleDataValues, if (isCustomSubtitle()) "custom" else subtitleDataValueNow),
            true
        )

        mTextColorContainer = findViewById(R.id.activity_widget_config_blackTextContainer)
        mTextColorContainer.visibility = View.GONE
        val textStyleSpinner: AppCompatSpinner = findViewById(R.id.activity_widget_config_blackTextSpinner)
        textStyleSpinner.onItemSelectedListener = TextColorSpinnerSelectedListener()
        textStyleSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, textColors)
        textStyleSpinner.setSelection(indexValue(textColorValues, textColorValueNow), true)

        mTextSizeContainer = findViewById(R.id.activity_widget_config_textSizeContainer)
        mTextSizeContainer.visibility = View.GONE
        val textSizeSeekBar: BubbleSeekBar = findViewById(R.id.activity_widget_config_textSizeSeekBar)
        textSizeSeekBar.setCustomSectionTextArray { _, array ->
            array.clear()
            array.put(0, "0%")
            array.put(1, "100%")
            array.put(2, "200%")
            array.put(3, "300%")
            array
        }
        textSizeSeekBar.setOnProgressChangedListener(TextSizeChangedListener())
        textSizeSeekBar.setProgress(textSize.toFloat())

        mClockFontContainer = findViewById(R.id.activity_widget_config_clockFontContainer)
        mClockFontContainer.visibility = View.GONE
        val clockFontSpinner: AppCompatSpinner = findViewById(R.id.activity_widget_config_clockFontSpinner)
        clockFontSpinner.onItemSelectedListener = ClockFontSpinnerSelectedListener()
        clockFontSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, clockFonts)
        clockFontSpinner.setSelection(indexValue(clockFontValues, cardStyleValueNow), true)

        mHideLunarContainer = findViewById(R.id.activity_widget_config_hideLunarContainer)
        mHideLunarContainer.visibility = View.GONE
        val hideLunarSwitch: Switch = findViewById(R.id.activity_widget_config_hideLunarSwitch)
        hideLunarSwitch.setOnCheckedChangeListener(HideLunarSwitchCheckListener())
        hideLunarSwitch.isChecked = hideLunar

        mAlignEndContainer = findViewById(R.id.activity_widget_config_alignEndContainer)
        mAlignEndContainer.visibility = View.GONE
        val alignEndSwitch: Switch = findViewById(R.id.activity_widget_config_alignEndSwitch)
        alignEndSwitch.setOnCheckedChangeListener(AlignEndSwitchCheckListener())
        alignEndSwitch.isChecked = alignEnd

        val doneButton: Button = findViewById(R.id.activity_widget_config_doneButton)
        doneButton.setOnClickListener {
            ConfigStore.getInstance(this, getConfigStoreName())
                .edit()
                .putString(getString(R.string.key_view_type), viewTypeValueNow)
                .putString(getString(R.string.key_card_style), cardStyleValueNow)
                .putInt(getString(R.string.key_card_alpha), cardAlpha)
                .putBoolean(getString(R.string.key_hide_subtitle), hideSubtitle)
                .putString(getString(R.string.key_subtitle_data), subtitleDataValueNow)
                .putString(getString(R.string.key_text_color), textColorValueNow)
                .putInt(getString(R.string.key_text_size), textSize)
                .putString(getString(R.string.key_clock_font), clockFontValueNow)
                .putBoolean(getString(R.string.key_hide_lunar), hideLunar)
                .putBoolean(getString(R.string.key_align_end), alignEnd)
                .apply()

            val extras = intent.extras
            var appWidgetId = 0
            if (extras != null) {
                appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
            }

            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(RESULT_OK, resultValue)

            PollingManager.resetNormalBackgroundTask(this, true)
            finish()
        }

        mBottomSheetScrollView = findViewById(R.id.activity_widget_config_custom_scrollView)

        mSubtitleInputLayout = findViewById(R.id.activity_widget_config_subtitle_inputLayout)

        mSubtitleEditText = findViewById(R.id.activity_widget_config_subtitle_inputter)
        mSubtitleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                subtitleDataValueNow = editable?.toString() ?: ""
                updateHostView()
            }
        })
        if (isCustomSubtitle()) {
            mSubtitleEditText.setText(subtitleDataValueNow)
        } else {
            mSubtitleEditText.setText("")
        }

        val subtitleCustomKeywords: TextView = findViewById(R.id.activity_widget_config_custom_subtitle_keywords)
        subtitleCustomKeywords.text = getSubtitleCustomKeywords()

        val scrollContainer: LinearLayout = findViewById(R.id.activity_widget_config_scrollContainer)
        scrollContainer.post {
            scrollContainer.setPaddingRelative(
                0, 0, 0, mSubtitleInputLayout.measuredHeight
            )
        }

        val bottomSheet: AppBarLayout = findViewById(R.id.activity_widget_config_custom_subtitle)
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheet.post {
            mBottomSheetBehavior.peekHeight =
                mSubtitleInputLayout.measuredHeight + mBottomSheetScrollView.bottomWindowInset
            setBottomSheetState(isCustomSubtitle())
        }
    }

    fun updateHostView() {
        mWidgetContainer.removeAllViews()
        val view = getRemoteViews().apply(applicationContext, mWidgetContainer)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mWidgetContainer.addView(view, params)
    }

    private fun setBottomSheetState(visible: Boolean) {
        if (visible) {
            mBottomSheetBehavior.isHideable = false
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            mBottomSheetBehavior.isHideable = true
            mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    abstract fun getRemoteViews(): RemoteViews

    fun getLocationNow(): Location = locationNow

    abstract fun getConfigStoreName(): String

    private fun indexValue(values: Array<String>, current: String?): Int {
        for (i in values.indices) {
            if (values[i] == current) {
                return i
            }
        }
        return 0
    }

    private fun isCustomSubtitle(): Boolean {
        for (v in subtitleDataValues) {
            if (v != "custom" && v == subtitleDataValueNow) {
                return false
            }
        }
        return true
    }

    private fun getSubtitleCustomKeywords(): String {
        return getString(R.string.feedback_custom_subtitle_keyword_cw) +
            getString(R.string.feedback_custom_subtitle_keyword_ct) +
            getString(R.string.feedback_custom_subtitle_keyword_ctd) +
            getString(R.string.feedback_custom_subtitle_keyword_at) +
            getString(R.string.feedback_custom_subtitle_keyword_atd) +
            getString(R.string.feedback_custom_subtitle_keyword_cpb) +
            getString(R.string.feedback_custom_subtitle_keyword_cp) +
            getString(R.string.feedback_custom_subtitle_keyword_cwd) +
            getString(R.string.feedback_custom_subtitle_keyword_cuv) +
            getString(R.string.feedback_custom_subtitle_keyword_ch) +
            getString(R.string.feedback_custom_subtitle_keyword_cps) +
            getString(R.string.feedback_custom_subtitle_keyword_cv) +
            getString(R.string.feedback_custom_subtitle_keyword_cdp) +
            getString(R.string.feedback_custom_subtitle_keyword_al) +
            getString(R.string.feedback_custom_subtitle_keyword_als) +
            "\n" +
            getString(R.string.feedback_custom_subtitle_keyword_l) +
            getString(R.string.feedback_custom_subtitle_keyword_lat) +
            getString(R.string.feedback_custom_subtitle_keyword_lon) +
            getString(R.string.feedback_custom_subtitle_keyword_ut) +
            getString(R.string.feedback_custom_subtitle_keyword_d) +
            getString(R.string.feedback_custom_subtitle_keyword_lc) +
            getString(R.string.feedback_custom_subtitle_keyword_w) +
            getString(R.string.feedback_custom_subtitle_keyword_ws) +
            getString(R.string.feedback_custom_subtitle_keyword_dd) +
            getString(R.string.feedback_custom_subtitle_keyword_hd) +
            getString(R.string.feedback_custom_subtitle_keyword_enter) +
            "\n" +
            getString(R.string.feedback_custom_subtitle_keyword_xdw) +
            "\n" +
            getString(R.string.feedback_custom_subtitle_keyword_xnw) +
            "\n" +
            getString(R.string.feedback_custom_subtitle_keyword_xdt) +
            "\n" +
            getString(R.string.feedback_custom_subtitle_keyword_xnt) +
            "\n" +
            getString(R.string.feedback_custom_subtitle_keyword_xdtd) +
            "\n" +
            getString(R.string.feedback_custom_subtitle_keyword_xntd) +
            "\n" +
            getString(R.string.feedback_custom_subtitle_keyword_xdp) +
            "\n" +
            getString(R.string.feedback_custom_subtitle_keyword_xnp) +
            "\n" +
            getString(R.string.feedback_custom_subtitle_keyword_xdwd) +
            "\n" +
            getString(R.string.feedback_custom_subtitle_keyword_xnwd) +
            "\n" +
            getString(R.string.feedback_custom_subtitle_keyword_xsr) +
            "\n" +
            getString(R.string.feedback_custom_subtitle_keyword_xss) +
            "\n" +
            getString(R.string.feedback_custom_subtitle_keyword_xmr) +
            "\n" +
            getString(R.string.feedback_custom_subtitle_keyword_xms) +
            "\n" +
            getString(R.string.feedback_custom_subtitle_keyword_xmp)
    }

    protected fun isHideLunarContainerVisible(): Int {
        return if (SettingsManager.getInstance(this).language.isChinese) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun requestWeatherSuccess(requestLocation: Location) {
        if (destroyed) {
            return
        }
        locationNow = requestLocation
        if (requestLocation.weather == null) {
            requestWeatherFailed(requestLocation)
        } else {
            updateHostView()
        }
    }

    override fun requestWeatherFailed(requestLocation: Location) {
        if (destroyed) {
            return
        }
        locationNow = requestLocation
        updateHostView()
        SnackbarHelper.showSnackbar(getString(R.string.feedback_get_weather_failed))
    }

    @SuppressLint("MissingPermission")
    private fun bindWallpaper(checkPermissions: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bindWallpaperInternal()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkPermissions) {
            if (!checkStoragePermission()) {
                return
            }
        }
        bindWallpaperInternal()
    }

    private fun bindWallpaperInternal() {
        try {
            val manager = WallpaperManager.getInstance(this)
            val drawable = manager.drawable
            if (drawable != null) {
                mWallpaper.setImageDrawable(drawable)
            }
        } catch (ignore: Exception) {
            // do nothing.
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun checkStoragePermission(): Boolean {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            mPendingWallpaperBind = true
            storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            return false
        }
        return true
    }

    private inner class HideSubtitleSwitchCheckListener : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            hideSubtitle = isChecked
            updateHostView()
        }
    }

    private inner class HideLunarSwitchCheckListener : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            hideLunar = isChecked
            updateHostView()
        }
    }

    private inner class AlignEndSwitchCheckListener : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            alignEnd = isChecked
            updateHostView()
        }
    }

    private inner class ViewTypeSpinnerSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
            if (viewTypeValueNow != viewTypeValues[i]) {
                viewTypeValueNow = viewTypeValues[i]
                updateHostView()
            }
        }

        override fun onNothingSelected(adapterView: AdapterView<*>?) {}
    }

    private inner class CardStyleSpinnerSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
            if (cardStyleValueNow != cardStyleValues[i]) {
                cardStyleValueNow = cardStyleValues[i]
                updateHostView()
            }
        }

        override fun onNothingSelected(adapterView: AdapterView<*>?) {}
    }

    private inner class SubtitleDataSpinnerSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
            setBottomSheetState(subtitleDataValues[i] == "custom")
            if (subtitleDataValueNow != subtitleDataValues[i]) {
                if (subtitleDataValues[i] == "custom") {
                    subtitleDataValueNow = mSubtitleEditText.text?.toString() ?: ""
                } else {
                    subtitleDataValueNow = subtitleDataValues[i]
                }
                updateHostView()
            }
        }

        override fun onNothingSelected(adapterView: AdapterView<*>?) {}
    }

    private inner class TextColorSpinnerSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
            if (textColorValueNow != textColorValues[i]) {
                textColorValueNow = textColorValues[i]
                if (textColorValueNow != "auto") {
                    updateHostView()
                    return
                }
                var hasPermission = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hasPermission = checkStoragePermission()
                }
                if (hasPermission) {
                    updateHostView()
                }
            }
        }

        override fun onNothingSelected(adapterView: AdapterView<*>?) {}
    }

    private inner class ClockFontSpinnerSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
            if (clockFontValueNow != clockFontValues[i]) {
                clockFontValueNow = clockFontValues[i]
                updateHostView()
            }
        }

        override fun onNothingSelected(adapterView: AdapterView<*>?) {}
    }

    private inner class CardAlphaChangedListener : BubbleSeekBar.OnProgressChangedListenerAdapter() {
        override fun getProgressOnActionUp(bubbleSeekBar: BubbleSeekBar, progress: Int, progressFloat: Float) {
            if (cardAlpha != progress) {
                cardAlpha = progress
                updateHostView()
            }
        }
    }

    private inner class TextSizeChangedListener : BubbleSeekBar.OnProgressChangedListenerAdapter() {
        override fun getProgressOnActionUp(bubbleSeekBar: BubbleSeekBar, progress: Int, progressFloat: Float) {
            if (textSize != progress) {
                textSize = progress
                updateHostView()
            }
        }
    }
}
