package wangdaye.com.geometricweather.common.ui.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.annotation.FloatRange
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.utils.DisplayUtils

class DrawerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var mDrawer: View? = null
    private var mContent: View? = null

    private var mUnfold: Boolean

    @FloatRange(from = 0.0, to = 1.0)
    private var mProgress: Float

    private var mProgressAnimator: ValueAnimator? = null

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DrawerLayout, defStyleAttr, 0)
        mUnfold = a.getBoolean(R.styleable.DrawerLayout_unfold, false)
        mProgress = if (mUnfold) 1f else 0f
        a.recycle()
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        )
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (childCount > 0) {
            mDrawer = getChildAt(0)
        }
        if (childCount > 1) {
            mContent = getChildAt(1)
        }

        var lp: LayoutParams
        if (mDrawer != null) {
            lp = mDrawer!!.layoutParams
            var width = lp.width
            if (width == LayoutParams.WRAP_CONTENT) {
                width = measuredWidth - DisplayUtils.getTabletListAdaptiveWidth(context, measuredWidth)
                if (width == 0) {
                    width = LayoutParams.MATCH_PARENT
                } else {
                    val minDrawerWidth = DisplayUtils.dpToPx(context, MIN_DRAWER_WIDTH_DP.toFloat()).toInt()
                    val maxDrawerWidth = DisplayUtils.dpToPx(context, MAX_DRAWER_WIDTH_DP.toFloat()).toInt()
                    width = Math.max(width, minDrawerWidth)
                    width = Math.min(width, maxDrawerWidth)
                }
            }
            mDrawer!!.measure(
                getChildMeasureSpec(widthMeasureSpec, 0, width),
                getChildMeasureSpec(heightMeasureSpec, 0, lp.height)
            )

            if (mContent != null) {
                lp = mContent!!.layoutParams
                if (mDrawer!!.measuredWidth == measuredWidth) {
                    mContent!!.measure(
                        getChildMeasureSpec(widthMeasureSpec, 0, lp.width),
                        getChildMeasureSpec(heightMeasureSpec, 0, lp.height)
                    )
                } else {
                    val widthUsed = (mDrawer!!.measuredWidth * mProgress).toInt()
                    mContent!!.measure(
                        getChildMeasureSpec(widthMeasureSpec, widthUsed, lp.width),
                        getChildMeasureSpec(heightMeasureSpec, 0, lp.height)
                    )
                }
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount > 0) {
            mDrawer = getChildAt(0)
        }
        if (childCount > 1) {
            mContent = getChildAt(1)
        }

        if (DisplayUtils.isRtl(context)) {
            if (mDrawer != null) {
                mDrawer!!.layout(
                    (measuredWidth - mDrawer!!.measuredWidth * mProgress).toInt(),
                    0,
                    (measuredWidth + mDrawer!!.measuredWidth * (1 - mProgress)).toInt(),
                    mDrawer!!.measuredHeight
                )
            }
            if (mContent != null) {
                mContent!!.layout(
                    0,
                    0,
                    mDrawer!!.left,
                    mContent!!.measuredHeight
                )
            }
        } else {
            if (mDrawer != null) {
                mDrawer!!.layout(
                    (mDrawer!!.measuredWidth * (mProgress - 1)).toInt(),
                    0,
                    (mDrawer!!.measuredWidth * mProgress).toInt(),
                    mDrawer!!.measuredHeight
                )
            }
            if (mContent != null) {
                mContent!!.layout(
                    mDrawer!!.right,
                    0,
                    mDrawer!!.right + mContent!!.measuredWidth,
                    mContent!!.measuredHeight
                )
            }
        }
    }

    fun isUnfold(): Boolean = mUnfold

    fun setUnfold(unfold: Boolean) {
        if (mUnfold == unfold) {
            return
        }

        mUnfold = unfold

        mProgressAnimator?.cancel()
        mProgressAnimator = null

        mProgressAnimator = generateProgressAnimator(mProgress, if (unfold) 1f else 0f)
        mProgressAnimator!!.start()
    }

    private fun generateProgressAnimator(from: Float, to: Float): ValueAnimator {
        val a = ValueAnimator.ofFloat(from, to)
        a.addUpdateListener { animation -> setProgress(animation.animatedValue as Float) }
        a.duration = (Math.abs(from - to) * 450).toLong()
        a.interpolator = DecelerateInterpolator(2f)
        return a
    }

    private fun setProgress(progress: Float) {
        mProgress = progress
        requestLayout()
    }

    companion object {
        private const val MIN_DRAWER_WIDTH_DP = 280
        private const val MAX_DRAWER_WIDTH_DP = 320
    }
}
