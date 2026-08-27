package wangdaye.com.geometricweather.common.ui.widgets.insets

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.basic.insets.FitBothSideBarHelper
import wangdaye.com.geometricweather.common.basic.insets.FitBothSideBarView
import wangdaye.com.geometricweather.common.basic.insets.FitBothSideBarView.FitSide

class FitSystemBarSwipeRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs), FitBothSideBarView {

    private val mHelper = FitBothSideBarHelper(this, FitBothSideBarView.SIDE_TOP)

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        return mHelper.onApplyWindowInsets(insets) { fitSystemBar() }
    }

    private fun fitSystemBar() {
        val startPosition =
            mHelper.top() + resources.getDimensionPixelSize(R.dimen.normal_margin)
        val endPosition = (startPosition + 64 * resources.displayMetrics.density).toInt()

        if (startPosition != progressViewStartOffset || endPosition != progressViewEndOffset) {
            setProgressViewOffset(false, startPosition, endPosition)
        }
    }

    override fun addFitSide(@FitSide side: Int) {
        // do nothing.
    }

    override fun removeFitSide(@FitSide side: Int) {
        // do nothing.
    }

    override fun setFitSystemBarEnabled(top: Boolean, bottom: Boolean) {
        mHelper.setFitSystemBarEnabled(top, bottom)
    }

    override fun getTopWindowInset(): Int {
        return mHelper.top()
    }

    override fun getBottomWindowInset(): Int {
        return 0
    }
}
