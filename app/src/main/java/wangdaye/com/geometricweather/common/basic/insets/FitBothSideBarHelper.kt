package wangdaye.com.geometricweather.common.basic.insets

import android.graphics.Rect
import android.view.View
import android.view.WindowInsets
import androidx.core.view.WindowInsetsCompat

class FitBothSideBarHelper @JvmOverloads constructor(
    private val mTarget: View,
    private var mFitSide: Int = FitBothSideBarView.SIDE_TOP or FitBothSideBarView.SIDE_BOTTOM,
    private var mFitTopSideEnabled: Boolean = true,
    private var mFitBottomSideEnabled: Boolean = true
) {

    fun interface InsetsConsumer {
        fun consume()
    }

    private var mWindowInsets: Rect = Rect(0, 0, 0, 0)

    companion object {
        private val sRootInsetsCache = ThreadLocal<Rect>()

        @JvmStatic
        fun setRootInsetsCache(rootInsets: Rect) {
            sRootInsetsCache.set(rootInsets)
        }
    }

    fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        return onApplyWindowInsets(insets) { mTarget.requestLayout() }
    }

    fun onApplyWindowInsets(insets: WindowInsets, consumer: InsetsConsumer): WindowInsets {
        val compat = WindowInsetsCompat.toWindowInsetsCompat(insets)
        val systemInsets = compat.getInsets(WindowInsetsCompat.Type.systemBars())
        mWindowInsets = Rect(
            systemInsets.left,
            systemInsets.top,
            systemInsets.right,
            systemInsets.bottom
        )
        consumer.consume()
        return insets
    }

    fun fitSystemWindows(r: Rect): Boolean {
        return fitSystemWindows(r) { mTarget.requestLayout() }
    }

    fun fitSystemWindows(r: Rect, consumer: InsetsConsumer): Boolean {
        mWindowInsets = r
        consumer.consume()
        return false
    }

    fun getWindowInsets(): Rect {
        val cached = sRootInsetsCache.get()
        return cached ?: mWindowInsets
    }

    fun left(): Int {
        return getWindowInsets().left
    }

    fun top(): Int {
        return if (mFitSide and FitBothSideBarView.SIDE_TOP != 0 && mFitTopSideEnabled) {
            getWindowInsets().top
        } else {
            0
        }
    }

    fun right(): Int {
        return getWindowInsets().right
    }

    fun bottom(): Int {
        return if (mFitSide and FitBothSideBarView.SIDE_BOTTOM != 0 && mFitBottomSideEnabled) {
            getWindowInsets().bottom
        } else {
            0
        }
    }

    fun addFitSide(@FitBothSideBarView.FitSide side: Int) {
        if (mFitSide and side != 0) {
            mFitSide = mFitSide or side
            mTarget.requestLayout()
        }
    }

    fun removeFitSide(@FitBothSideBarView.FitSide side: Int) {
        if (mFitSide and side != 0) {
            mFitSide = mFitSide xor side
            mTarget.requestLayout()
        }
    }

    fun setFitSystemBarEnabled(top: Boolean, bottom: Boolean) {
        if (mFitTopSideEnabled != top || mFitBottomSideEnabled != bottom) {
            mFitTopSideEnabled = top
            mFitBottomSideEnabled = bottom
            mTarget.requestLayout()
        }
    }
}
