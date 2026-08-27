package wangdaye.com.geometricweather.common.ui.widgets.horizontal

import android.animation.LayoutTransition
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.LinearLayoutManager
import java.util.Arrays

internal class AnimateLayoutChangeDetector(llm: LinearLayoutManager) {

    private val mLayoutManager: LinearLayoutManager = llm

    fun mayHaveInterferingAnimations(): Boolean {
        return (!arePagesLaidOutContiguously() || mLayoutManager.childCount <= 1) &&
            hasRunningChangingLayoutTransition()
    }

    private fun arePagesLaidOutContiguously(): Boolean {
        val childCount = mLayoutManager.childCount
        if (childCount == 0) {
            return true
        }

        val isHorizontal = mLayoutManager.orientation == HorizontalViewPager2.ORIENTATION_HORIZONTAL
        val bounds = Array(childCount) { IntArray(2) }
        for (i in 0 until childCount) {
            val view = mLayoutManager.getChildAt(i)
                ?: throw IllegalStateException("null view contained in the view hierarchy")
            val layoutParams = view.layoutParams
            val margin = if (layoutParams is ViewGroup.MarginLayoutParams) {
                layoutParams
            } else {
                ZERO_MARGIN_LAYOUT_PARAMS
            }
            bounds[i][0] = if (isHorizontal) {
                view.left - margin.leftMargin
            } else {
                view.top - margin.topMargin
            }
            bounds[i][1] = if (isHorizontal) {
                view.right + margin.rightMargin
            } else {
                view.bottom + margin.bottomMargin
            }
        }

        Arrays.sort(bounds) { lhs, rhs -> lhs[0] - rhs[0] }

        for (i in 1 until childCount) {
            if (bounds[i - 1][1] != bounds[i][0]) {
                return false
            }
        }

        val pageSize = bounds[0][1] - bounds[0][0]
        if (bounds[0][0] > 0 || bounds[childCount - 1][1] < pageSize) {
            return false
        }
        return true
    }

    private fun hasRunningChangingLayoutTransition(): Boolean {
        val childCount = mLayoutManager.childCount
        for (i in 0 until childCount) {
            if (hasRunningChangingLayoutTransition(mLayoutManager.getChildAt(i))) {
                return true
            }
        }
        return false
    }

    companion object {
        private val ZERO_MARGIN_LAYOUT_PARAMS: ViewGroup.MarginLayoutParams =
            ViewGroup.MarginLayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
                setMargins(0, 0, 0, 0)
            }

        private fun hasRunningChangingLayoutTransition(view: View?): Boolean {
            if (view is ViewGroup) {
                val layoutTransition: LayoutTransition? = view.layoutTransition
                if (layoutTransition != null && layoutTransition.isChangingLayout) {
                    return true
                }
                val childCount = view.childCount
                for (i in 0 until childCount) {
                    if (hasRunningChangingLayoutTransition(view.getChildAt(i))) {
                        return true
                    }
                }
            }
            return false
        }
    }
}
