package wangdaye.com.geometricweather.common.ui.widgets.slidingItem

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.ImageViewCompat
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import kotlin.math.abs
import kotlin.math.pow

class SlidingItemContainerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val mIcon: AppCompatImageView
    private var mChild: View? = null
    private var mSwipeX = 0f
    private var mUpdateFlag: Boolean

    @DrawableRes
    var iconResStart: Int = 0
        set(value) {
            field = value
            mUpdateFlag = true
        }
    @DrawableRes
    var iconResEnd: Int = 0
        set(value) {
            field = value
            mUpdateFlag = true
        }
    @ColorInt
    var tintColorStart: Int = Color.WHITE
        set(value) {
            field = value
            mUpdateFlag = true
        }
    @ColorInt
    var tintColorEnd: Int = Color.WHITE
        set(value) {
            field = value
            mUpdateFlag = true
        }
    @ColorInt
    var backgroundColorStart: Int = Color.DKGRAY
        set(value) {
            field = value
            mUpdateFlag = true
        }
    @ColorInt
    var backgroundColorEnd: Int = Color.DKGRAY
        set(value) {
            field = value
            mUpdateFlag = true
        }

    init {
        val iconSize = DisplayUtils.dpToPx(context, 56f).toInt()
        val iconPadding = DisplayUtils.dpToPx(context, 16f).toInt()
        mIcon = AppCompatImageView(context)
        mIcon.setPadding(iconPadding, iconPadding, iconPadding, iconPadding)
        ImageViewCompat.setImageTintList(mIcon, ColorStateList.valueOf(Color.WHITE))
        addView(mIcon, LayoutParams(iconSize, iconSize, Gravity.CENTER_VERTICAL))
        setBackgroundColor(Color.GRAY)

        val a = context.obtainStyledAttributes(
            attrs, R.styleable.SlidingItemContainerLayout, defStyleAttr, 0
        )
        iconResStart = a.getResourceId(R.styleable.SlidingItemContainerLayout_iconResStart, 0)
        iconResEnd = a.getResourceId(R.styleable.SlidingItemContainerLayout_iconResEnd, 0)
        backgroundColorStart = a.getColor(
            R.styleable.SlidingItemContainerLayout_backgroundColorStart, Color.DKGRAY
        )
        backgroundColorEnd = a.getColor(
            R.styleable.SlidingItemContainerLayout_backgroundColorEnd, Color.DKGRAY
        )
        tintColorStart = a.getColor(R.styleable.SlidingItemContainerLayout_tintColorStart, Color.WHITE)
        tintColorEnd = a.getColor(R.styleable.SlidingItemContainerLayout_tintColorEnd, Color.WHITE)
        a.recycle()
        mUpdateFlag = true
    }

    fun swipe(totalX: Float) {
        if (mSwipeX == totalX) {
            return
        }
        if (mChild == null) {
            for (i in 0 until childCount) {
                val v = getChildAt(i)
                if (v !== mIcon) {
                    mChild = v
                    break
                }
            }
        }
        val child = mChild ?: return
        child.translationX = totalX
        var progress = abs(totalX / measuredWidth)
        progress = (1.0f - (1.0f - progress).toDouble().pow(4.0)).toFloat()
        if (totalX != 0f) {
            if (totalX * mSwipeX <= 0 || mUpdateFlag) {
                mUpdateFlag = false
                if (DisplayUtils.isRtl(context)) {
                    mIcon.setImageResource(if (totalX < 0) iconResStart else iconResEnd)
                    mIcon.imageTintList = ColorStateList.valueOf(
                        if (totalX < 0) tintColorStart else tintColorEnd
                    )
                    setBackgroundColor(if (totalX < 0) backgroundColorStart else backgroundColorEnd)
                } else {
                    mIcon.setImageResource(if (totalX > 0) iconResStart else iconResEnd)
                    mIcon.imageTintList = ColorStateList.valueOf(
                        if (totalX > 0) tintColorStart else tintColorEnd
                    )
                    setBackgroundColor(if (totalX > 0) backgroundColorStart else backgroundColorEnd)
                }
            }
            if (totalX > 0) {
                mIcon.translationX = (
                    0.5 * -mIcon.measuredWidth + 0.75 * mIcon.measuredWidth * progress
                ).toFloat()
            } else {
                mIcon.translationX = (
                    measuredWidth - 0.5 * mIcon.measuredWidth - 0.75 * mIcon.measuredWidth * progress
                ).toFloat()
            }
        } else {
            setBackgroundColor(Color.GRAY)
        }
        mSwipeX = totalX
    }
}
