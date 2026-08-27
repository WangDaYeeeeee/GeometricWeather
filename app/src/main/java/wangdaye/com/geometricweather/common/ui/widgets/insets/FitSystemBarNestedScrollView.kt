package wangdaye.com.geometricweather.common.ui.widgets.insets

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.insets.FitBothSideBarHelper
import wangdaye.com.geometricweather.common.basic.insets.FitBothSideBarView
import wangdaye.com.geometricweather.common.basic.insets.FitBothSideBarView.FitSide

class FitSystemBarNestedScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr), FitBothSideBarView {

    private val mHelper: FitBothSideBarHelper

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.FitSystemBarNestedScrollView, defStyleAttr, 0
        )
        val fitSide = a.getInt(
            R.styleable.FitSystemBarNestedScrollView_sv_side,
            FitBothSideBarView.SIDE_TOP or FitBothSideBarView.SIDE_BOTTOM
        )
        a.recycle()
        mHelper = FitBothSideBarHelper(this, fitSide)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        return mHelper.onApplyWindowInsets(insets)
    }

    override fun fitSystemWindows(insets: Rect): Boolean {
        return mHelper.fitSystemWindows(insets)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setPadding(0, mHelper.top(), 0, mHelper.bottom())
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun addFitSide(@FitSide side: Int) {
        mHelper.addFitSide(side)
    }

    override fun removeFitSide(@FitSide side: Int) {
        mHelper.removeFitSide(side)
    }

    override fun setFitSystemBarEnabled(top: Boolean, bottom: Boolean) {
        mHelper.setFitSystemBarEnabled(top, bottom)
    }

    override fun getTopWindowInset(): Int {
        return mHelper.top()
    }

    override fun getBottomWindowInset(): Int {
        return mHelper.bottom()
    }
}
