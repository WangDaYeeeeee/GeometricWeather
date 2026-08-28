package wangdaye.com.geometricweather.common.basic

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import wangdaye.com.geometricweather.common.basic.insets.FitHorizontalSystemBarRootLayout
import wangdaye.com.geometricweather.common.snackbar.SnackbarContainer
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import wangdaye.com.geometricweather.common.utils.LanguageUtils

abstract class GeoActivity : AppCompatActivity() {

    lateinit var fitHorizontalSystemBarRootLayout: FitHorizontalSystemBarRootLayout

    private class KeyboardResizeBugWorkaround private constructor(
        activity: GeoActivity
    ) {
        private val root = activity.fitHorizontalSystemBarRootLayout
        private val rootParams: ViewGroup.LayoutParams
        private var usableHeightPrevious = 0

        private fun possiblyResizeChildOfContent() {
            val usableHeightNow = computeUsableHeight()

            if (usableHeightNow != usableHeightPrevious) {
                val screenHeight = root.rootView.height
                val keyboardExpanded: Boolean

                if (screenHeight - usableHeightNow > screenHeight / 5) {
                    keyboardExpanded = true
                    rootParams.height = usableHeightNow
                } else {
                    keyboardExpanded = false
                    rootParams.height = screenHeight
                }

                usableHeightPrevious = usableHeightNow
                root.setFitKeyboardExpanded(keyboardExpanded)
            }
        }

        private fun computeUsableHeight(): Int {
            val r = Rect()
            DisplayUtils.getVisibleDisplayFrame(root, r)
            return r.bottom
        }

        companion object {
            fun assistActivity(activity: GeoActivity) {
                KeyboardResizeBugWorkaround(activity)
            }
        }

        init {
            root.viewTreeObserver.addOnGlobalLayoutListener { possiblyResizeChildOfContent() }
            rootParams = root.layoutParams
        }
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fitHorizontalSystemBarRootLayout = FitHorizontalSystemBarRootLayout(this)

        GeoApp.activityHost.addActivity(this)

        LanguageUtils.setLanguage(
            this,
            GeoApp.languageLocale(this)
        )

        DisplayUtils.setSystemBarStyle(
            this,
            window,
            false,
            !DisplayUtils.isDarkMode(this),
            true,
            !DisplayUtils.isDarkMode(this)
        )
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        val decorView = window.decorView as ViewGroup
        val decorChild = decorView.getChildAt(0) as ViewGroup

        decorView.removeView(decorChild)
        decorView.addView(fitHorizontalSystemBarRootLayout)

        fitHorizontalSystemBarRootLayout.removeAllViews()
        fitHorizontalSystemBarRootLayout.addView(decorChild)

        KeyboardResizeBugWorkaround.assistActivity(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        GeoApp.activityHost.setTopActivity(this)
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        GeoApp.activityHost.setTopActivity(this)
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        GeoApp.activityHost.checkToCleanTopActivity(this)
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        GeoApp.activityHost.removeActivity(this)
    }

    open val snackbarContainer: SnackbarContainer?
        get() = SnackbarContainer(
            this,
            findViewById<ViewGroup>(android.R.id.content).getChildAt(0) as ViewGroup,
            true
        )

    fun provideSnackbarContainer(): SnackbarContainer? = snackbarContainer

    val isActivityCreated: Boolean
        get() = lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)
    val isActivityStarted: Boolean
        get() = lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    val isActivityResumed: Boolean
        get() = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
}
