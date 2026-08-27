package wangdaye.com.geometricweather.common.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.ViewCompat
import androidx.dynamicanimation.animation.FloatValueHolder
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce

class SwipeSwitchLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), NestedScrollingParent2, NestedScrollingParent3 {

    private var mTarget: View? = null
    private var mResetAnimation: SpringAnimation? = null
    private var mSwitchListener: OnSwitchListener? = null
    private var mPageSwipeListener: OnPagerSwipeListener? = null

    private var mTotalCount = 1
    private var mPosition = 0

    private var mSwipeDistance = 0
    private var mSwipeTrigger = 500
    private var mNestedScrollingDistance = 0f
    private var mNestedScrollingTrigger = 300f

    private var mLastX = 0f
    private var mLastY = 0f
    private var mTouchSlop = 0

    private var mIsBeingTouched = false
    private var mIsBeingDragged = false
    private var mIsHorizontalDragged = false
    private var mIsBeingNestedScrolling = false

    interface OnSwitchListener {
        fun onSwiped(swipeDirection: Int, progress: Float)
        fun onSwitched(swipeDirection: Int)
    }

    interface OnPagerSwipeListener {
        fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
        fun onPageSelected(position: Int)
    }

    init {
        mTarget = null
        mSwipeDistance = 0
        mSwipeTrigger = 500
        mNestedScrollingDistance = 0f
        mNestedScrollingTrigger = 300f
        mTouchSlop = ViewConfiguration.get(getContext()).scaledTouchSlop
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mSwipeTrigger = measuredWidth / 5
        mNestedScrollingTrigger = mSwipeTrigger.toFloat()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!isEnabled || (ev.action != MotionEvent.ACTION_DOWN && mIsBeingNestedScrolling)) {
            return false
        }

        if (mTarget == null && childCount > 0) {
            mTarget = getChildAt(0)
        }
        if (mTarget == null) {
            return false
        }

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                cancelResetAnimation()
                mIsBeingTouched = true
                mIsBeingDragged = false
                mIsHorizontalDragged = false
                mLastX = ev.x
                mLastY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (!mIsBeingTouched) {
                    mIsBeingTouched = true
                    mLastX = ev.x
                    mLastY = ev.y
                }

                val x = ev.x
                val y = ev.y

                if (!mIsBeingDragged && !mIsHorizontalDragged) {
                    if (Math.abs(x - mLastX) > mTouchSlop || Math.abs(y - mLastY) > mTouchSlop) {
                        mIsBeingDragged = true
                        if (Math.abs(x - mLastX) > Math.abs(y - mLastY)) {
                            mLastX += if (x > mLastX) mTouchSlop.toFloat() else -mTouchSlop.toFloat()
                            mIsHorizontalDragged = true
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mIsBeingTouched = false
                mIsBeingDragged = false
                mIsHorizontalDragged = false
            }
        }

        return mIsBeingDragged && mIsHorizontalDragged
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (!isEnabled || mIsBeingNestedScrolling) {
            return false
        }

        if (mTarget == null && childCount > 0) {
            mTarget = getChildAt(0)
        }
        if (mTarget == null) {
            return false
        }

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                cancelResetAnimation()
                mIsBeingTouched = true
                mIsBeingDragged = false
                mIsHorizontalDragged = false
                mLastX = ev.x
                mLastY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (mIsBeingDragged && mIsHorizontalDragged) {
                    mSwipeDistance += (ev.x - mLastX).toInt()
                    setTranslation(mSwipeTrigger, SWIPE_RATIO)
                }
                mLastX = ev.x
                mLastY = ev.y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mIsBeingTouched = false
                release(mSwipeTrigger)
            }
        }

        return true
    }

    fun reset() {
        cancelResetAnimation()
        mIsBeingDragged = false
        mIsHorizontalDragged = false
        mSwipeDistance = 0
        mNestedScrollingDistance = 0f
        setTranslation(mSwipeTrigger, SWIPE_RATIO)
    }

    private fun cancelResetAnimation() {
        mResetAnimation?.cancel()
        mResetAnimation = null
    }

    private fun setTranslation(triggerDistance: Int, translateRatio: Float) {
        var realDistance = mSwipeDistance.toFloat()
        realDistance = Math.min(realDistance, triggerDistance.toFloat())
        realDistance = Math.max(realDistance, -triggerDistance.toFloat())

        val swipeDirection = if (mSwipeDistance < 0) SWIPE_DIRECTION_LEFT else SWIPE_DIRECTION_RIGHT
        val progress = Math.abs(realDistance) / triggerDistance

        if (mTarget != null) {
            mTarget!!.alpha = 1 - progress
            mTarget!!.translationX =
                (swipeDirection * translateRatio * triggerDistance * Math.log10(
                    1 + 9.0 * Math.abs(mSwipeDistance) / triggerDistance
                )).toFloat()
        }

        mSwitchListener?.onSwiped(swipeDirection, progress)
        if (mPageSwipeListener != null) {
            if (mSwipeDistance > 0) {
                mPageSwipeListener!!.onPageScrolled(
                    mPosition - 1,
                    1 - Math.min(1f, 1f * mSwipeDistance / triggerDistance),
                    Math.max(0, triggerDistance - mSwipeDistance)
                )
            } else {
                mPageSwipeListener!!.onPageScrolled(
                    mPosition,
                    Math.min(1f, -1f * mSwipeDistance / triggerDistance),
                    Math.min(-mSwipeDistance, triggerDistance)
                )
            }
        }
    }

    private fun release(triggerDistance: Int) {
        val swipeDirection = if (mSwipeDistance < 0) SWIPE_DIRECTION_LEFT else SWIPE_DIRECTION_RIGHT
        if (Math.abs(mSwipeDistance) > Math.abs(triggerDistance)) {
            setPosition(swipeDirection)
            mSwitchListener?.onSwitched(swipeDirection)
            mPageSwipeListener?.onPageSelected(mPosition)
        } else {
            if (mTarget == null) {
                reset()
                return
            }

            mResetAnimation = SpringAnimation(FloatValueHolder(mSwipeDistance.toFloat()))
            mResetAnimation!!.spring = SpringForce(0f)
                .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
            mResetAnimation!!.addUpdateListener { _, value, _ ->
                mSwipeDistance = value.toInt()
                setTranslation(mSwipeTrigger, SWIPE_RATIO)
            }
            mResetAnimation!!.start()
        }
    }

    fun setData(currentIndex: Int, pageCount: Int) {
        if (currentIndex < 0 || currentIndex >= pageCount) {
            throw RuntimeException("Invalid current index.")
        }
        mPosition = currentIndex
        mTotalCount = pageCount
    }

    private fun setPosition(swipeDirection: Int) {
        when (swipeDirection) {
            SWIPE_DIRECTION_LEFT -> mPosition++
            SWIPE_DIRECTION_RIGHT -> mPosition--
        }
        if (mPosition < 0) {
            mPosition = mTotalCount - 1
        } else if (mPosition > mTotalCount - 1) {
            mPosition = 0
        }
    }

    val totalCount: Int
        get() = mTotalCount

    val position: Int
        get() = mPosition

    fun setOnSwitchListener(l: OnSwitchListener?) {
        mSwitchListener = l
    }

    fun setOnPageSwipeListener(l: OnPagerSwipeListener?) {
        mPageSwipeListener = l
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        if (mTarget == null && childCount > 0) {
            mTarget = getChildAt(0)
        }
        return axes and ViewCompat.SCROLL_AXIS_HORIZONTAL != 0 &&
            mSwitchListener != null &&
            type == ViewCompat.TYPE_TOUCH &&
            isEnabled &&
            mTarget != null
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        if (!mIsBeingNestedScrolling) {
            mIsBeingNestedScrolling = true
            mNestedScrollingDistance = if ((!target.canScrollHorizontally(-1) && !target.canScrollHorizontally(1)) ||
                mSwipeDistance != 0
            ) {
                mNestedScrollingTrigger
            } else {
                0f
            }
        }
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        mIsBeingNestedScrolling = false
        release(mSwipeTrigger)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (mSwipeDistance != 0) {
            consumed[0] = if ((mSwipeDistance > 0 && mSwipeDistance - dx < 0) ||
                (mSwipeDistance < 0 && mSwipeDistance - dx > 0)
            ) {
                mSwipeDistance
            } else {
                dx
            }
            innerNestedScroll(consumed[0])
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        innerNestedScroll(dxUnconsumed)
        consumed[0] += dxUnconsumed
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        innerNestedScroll(dxUnconsumed)
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean = false

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean = false

    private fun innerNestedScroll(dxUnconsumed: Int) {
        if (Math.abs(mNestedScrollingDistance) >= mNestedScrollingTrigger) {
            mSwipeDistance -= dxUnconsumed
        } else {
            mNestedScrollingDistance -= dxUnconsumed
            mSwipeDistance = (mSwipeDistance - dxUnconsumed / 10f).toInt()
            if (Math.abs(mNestedScrollingDistance) >= mNestedScrollingTrigger) {
                performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            }
        }
        setTranslation(mSwipeTrigger, NESTED_SCROLLING_RATIO)
    }

    companion object {
        private const val SWIPE_RATIO = 0.4f
        private const val NESTED_SCROLLING_RATIO = SWIPE_RATIO

        const val SWIPE_DIRECTION_LEFT = -1
        const val SWIPE_DIRECTION_RIGHT = 1
    }
}
