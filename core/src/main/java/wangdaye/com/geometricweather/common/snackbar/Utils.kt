package wangdaye.com.geometricweather.common.snackbar

import android.animation.Animator
import android.animation.AnimatorSet
import android.graphics.Rect
import android.view.View
import android.view.animation.Interpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import wangdaye.com.geometricweather.common.basic.insets.FitBothSideBarHelper
import wangdaye.com.geometricweather.common.basic.insets.FitBothSideBarView
import wangdaye.com.geometricweather.common.utils.DisplayUtils

internal object Utils {

    @JvmField
    val FAST_OUT_SLOW_IN_INTERPOLATOR: Interpolator = FastOutSlowInInterpolator()

    fun getEnterAnimator(view: View, cardStyle: Boolean): Animator {
        view.translationY = view.height.toFloat()
        view.scaleX = if (cardStyle) 1.1f else 1f
        view.scaleY = if (cardStyle) 1.1f else 1f

        val animators = DisplayUtils.getFloatingOvershotEnterAnimators(view)
        if (!cardStyle) {
            animators[0].interpolator = DisplayUtils.FLOATING_DECELERATE_INTERPOLATOR
        }

        val set = AnimatorSet()
        set.playTogether(animators[0], animators[1], animators[2])
        return set
    }

    fun consumeInsets(view: View, insets: Rect) {
        val fitInsetsHelper = FitBothSideBarHelper(
            view, FitBothSideBarView.SIDE_BOTTOM
        )
        fitInsetsHelper.fitSystemWindows(insets) {
            insets.set(fitInsetsHelper.getWindowInsets())
            view.requestLayout()
        }
    }
}
