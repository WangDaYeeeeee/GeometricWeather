package wangdaye.com.geometricweather.common.ui.widgets.horizontal

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy.SOURCE
import java.util.Locale

class ScrollEventAdapter(viewPager: HorizontalViewPager2) : RecyclerView.OnScrollListener() {

    @Retention(SOURCE)
    @IntDef(
        STATE_IDLE, STATE_IN_PROGRESS_MANUAL_DRAG, STATE_IN_PROGRESS_SMOOTH_SCROLL,
        STATE_IN_PROGRESS_IMMEDIATE_SCROLL, STATE_IN_PROGRESS_FAKE_DRAG
    )
    private annotation class AdapterState

    private var mCallback: HorizontalViewPager2.OnPageChangeCallback? = null
    private val mViewPager: HorizontalViewPager2 = viewPager
    private val mRecyclerView: RecyclerView = mViewPager.mRecyclerView
    private val mLayoutManager: LinearLayoutManager = mRecyclerView.layoutManager as LinearLayoutManager

    @AdapterState
    private var mAdapterState = 0
    @HorizontalViewPager2.ScrollState
    private var mScrollState = 0
    private val mScrollValues = ScrollEventValues()
    private var mDragStartPosition = 0
    private var mTarget = 0
    private var mDispatchSelected = false
    private var mScrollHappened = false
    private var mDataSetChangeHappened = false
    private var mFakeDragging = false

    init {
        resetState()
    }

    private fun resetState() {
        mAdapterState = STATE_IDLE
        mScrollState = HorizontalViewPager2.SCROLL_STATE_IDLE
        mScrollValues.reset()
        mDragStartPosition = NO_POSITION
        mTarget = NO_POSITION
        mDispatchSelected = false
        mScrollHappened = false
        mFakeDragging = false
        mDataSetChangeHappened = false
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if ((mAdapterState != STATE_IN_PROGRESS_MANUAL_DRAG
                || mScrollState != HorizontalViewPager2.SCROLL_STATE_DRAGGING)
            && newState == RecyclerView.SCROLL_STATE_DRAGGING
        ) {
            startDrag(false)
            return
        }

        if (isInAnyDraggingState() && newState == RecyclerView.SCROLL_STATE_SETTLING) {
            if (mScrollHappened) {
                dispatchStateChanged(HorizontalViewPager2.SCROLL_STATE_SETTLING)
                mDispatchSelected = true
            }
            return
        }

        if (isInAnyDraggingState() && newState == RecyclerView.SCROLL_STATE_IDLE) {
            var dispatchIdle = false
            updateScrollEventValues()
            if (!mScrollHappened) {
                if (mScrollValues.mPosition != RecyclerView.NO_POSITION) {
                    dispatchScrolled(mScrollValues.mPosition, 0f, 0)
                }
                dispatchIdle = true
            } else if (mScrollValues.mOffsetPx == 0) {
                dispatchIdle = true
                if (mDragStartPosition != mScrollValues.mPosition) {
                    dispatchSelected(mScrollValues.mPosition)
                }
            }
            if (dispatchIdle) {
                dispatchStateChanged(HorizontalViewPager2.SCROLL_STATE_IDLE)
                resetState()
            }
        }

        if (mAdapterState == STATE_IN_PROGRESS_SMOOTH_SCROLL
            && newState == RecyclerView.SCROLL_STATE_IDLE && mDataSetChangeHappened
        ) {
            updateScrollEventValues()
            if (mScrollValues.mOffsetPx == 0) {
                if (mTarget != mScrollValues.mPosition) {
                    dispatchSelected(
                        if (mScrollValues.mPosition == NO_POSITION) 0 else mScrollValues.mPosition
                    )
                }
                dispatchStateChanged(HorizontalViewPager2.SCROLL_STATE_IDLE)
                resetState()
            }
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        mScrollHappened = true
        updateScrollEventValues()

        if (mDispatchSelected) {
            mDispatchSelected = false
            val scrollingForward = dy > 0 || (dy == 0 && (dx < 0 == mViewPager.isRtl()))
            mTarget = if (scrollingForward && mScrollValues.mOffsetPx != 0) {
                mScrollValues.mPosition + 1
            } else {
                mScrollValues.mPosition
            }
            if (mDragStartPosition != mTarget) {
                dispatchSelected(mTarget)
            }
        } else if (mAdapterState == STATE_IDLE) {
            val position = mScrollValues.mPosition
            dispatchSelected(if (position == NO_POSITION) 0 else position)
        }

        dispatchScrolled(
            if (mScrollValues.mPosition == NO_POSITION) 0 else mScrollValues.mPosition,
            mScrollValues.mOffset, mScrollValues.mOffsetPx
        )

        if ((mScrollValues.mPosition == mTarget || mTarget == NO_POSITION)
            && mScrollValues.mOffsetPx == 0 && mScrollState != HorizontalViewPager2.SCROLL_STATE_DRAGGING
        ) {
            dispatchStateChanged(HorizontalViewPager2.SCROLL_STATE_IDLE)
            resetState()
        }
    }

    private fun updateScrollEventValues() {
        val values = mScrollValues

        values.mPosition = mLayoutManager.findFirstVisibleItemPosition()
        if (values.mPosition == RecyclerView.NO_POSITION) {
            values.reset()
            return
        }
        val firstVisibleView = mLayoutManager.findViewByPosition(values.mPosition)
        if (firstVisibleView == null) {
            values.reset()
            return
        }

        var leftDecorations = mLayoutManager.getLeftDecorationWidth(firstVisibleView)
        var rightDecorations = mLayoutManager.getRightDecorationWidth(firstVisibleView)
        var topDecorations = mLayoutManager.getTopDecorationHeight(firstVisibleView)
        var bottomDecorations = mLayoutManager.getBottomDecorationHeight(firstVisibleView)

        val params = firstVisibleView.layoutParams
        if (params is ViewGroup.MarginLayoutParams) {
            leftDecorations += params.leftMargin
            rightDecorations += params.rightMargin
            topDecorations += params.topMargin
            bottomDecorations += params.bottomMargin
        }

        val decoratedHeight = firstVisibleView.height + topDecorations + bottomDecorations
        val decoratedWidth = firstVisibleView.width + leftDecorations + rightDecorations

        val isHorizontal = mLayoutManager.orientation == HorizontalViewPager2.ORIENTATION_HORIZONTAL
        val start: Int
        val sizePx: Int
        if (isHorizontal) {
            sizePx = decoratedWidth
            start = firstVisibleView.left - leftDecorations - mRecyclerView.paddingLeft
            val startAdjusted = if (mViewPager.isRtl()) -start else start
            values.mOffsetPx = -startAdjusted
        } else {
            sizePx = decoratedHeight
            start = firstVisibleView.top - topDecorations - mRecyclerView.paddingTop
            values.mOffsetPx = -start
        }

        if (values.mOffsetPx < 0) {
            if (AnimateLayoutChangeDetector(mLayoutManager).mayHaveInterferingAnimations()) {
                throw IllegalStateException(
                    "Page(s) contain a ViewGroup with a " +
                        "LayoutTransition (or animateLayoutChanges=\"true\"), which interferes " +
                        "with the scrolling animation. Make sure to call getLayoutTransition()" +
                        ".setAnimateParentHierarchy(false) on all ViewGroups with a " +
                        "LayoutTransition before an animation is started."
                )
            }
            throw IllegalStateException(
                String.format(
                    Locale.US, "Page can only be offset by a " +
                        "positive amount, not by %d", values.mOffsetPx
                )
            )
        }
        values.mOffset = if (sizePx == 0) 0f else values.mOffsetPx.toFloat() / sizePx
    }

    private fun startDrag(isFakeDrag: Boolean) {
        mFakeDragging = isFakeDrag
        mAdapterState = if (isFakeDrag) STATE_IN_PROGRESS_FAKE_DRAG else STATE_IN_PROGRESS_MANUAL_DRAG
        if (mTarget != NO_POSITION) {
            mDragStartPosition = mTarget
            mTarget = NO_POSITION
        } else if (mDragStartPosition == NO_POSITION) {
            mDragStartPosition = getPosition()
        }
        dispatchStateChanged(HorizontalViewPager2.SCROLL_STATE_DRAGGING)
    }

    fun notifyDataSetChangeHappened() {
        mDataSetChangeHappened = true
    }

    fun notifyProgrammaticScroll(target: Int, smooth: Boolean) {
        mAdapterState = if (smooth) STATE_IN_PROGRESS_SMOOTH_SCROLL else STATE_IN_PROGRESS_IMMEDIATE_SCROLL
        mFakeDragging = false
        val hasNewTarget = mTarget != target
        mTarget = target
        dispatchStateChanged(HorizontalViewPager2.SCROLL_STATE_SETTLING)
        if (hasNewTarget) {
            dispatchSelected(target)
        }
    }

    fun notifyBeginFakeDrag() {
        mAdapterState = STATE_IN_PROGRESS_FAKE_DRAG
        startDrag(true)
    }

    fun notifyEndFakeDrag() {
        if (isDragging() && !mFakeDragging) {
            return
        }
        mFakeDragging = false
        updateScrollEventValues()
        if (mScrollValues.mOffsetPx == 0) {
            if (mScrollValues.mPosition != mDragStartPosition) {
                dispatchSelected(mScrollValues.mPosition)
            }
            dispatchStateChanged(HorizontalViewPager2.SCROLL_STATE_IDLE)
            resetState()
        } else {
            dispatchStateChanged(HorizontalViewPager2.SCROLL_STATE_SETTLING)
        }
    }

    fun setOnPageChangeCallback(callback: HorizontalViewPager2.OnPageChangeCallback?) {
        mCallback = callback
    }

    fun getScrollState(): Int {
        return mScrollState
    }

    fun isIdle(): Boolean {
        return mScrollState == HorizontalViewPager2.SCROLL_STATE_IDLE
    }

    fun isDragging(): Boolean {
        return mScrollState == HorizontalViewPager2.SCROLL_STATE_DRAGGING
    }

    fun isFakeDragging(): Boolean {
        return mFakeDragging
    }

    private fun isInAnyDraggingState(): Boolean {
        return mAdapterState == STATE_IN_PROGRESS_MANUAL_DRAG ||
            mAdapterState == STATE_IN_PROGRESS_FAKE_DRAG
    }

    fun getRelativeScrollPosition(): Double {
        updateScrollEventValues()
        return mScrollValues.mPosition + mScrollValues.mOffset.toDouble()
    }

    private fun dispatchStateChanged(@HorizontalViewPager2.ScrollState state: Int) {
        if (mAdapterState == STATE_IN_PROGRESS_IMMEDIATE_SCROLL
            && mScrollState == HorizontalViewPager2.SCROLL_STATE_IDLE
        ) {
            return
        }
        if (mScrollState == state) {
            return
        }

        mScrollState = state
        mCallback?.onPageScrollStateChanged(state)
    }

    private fun dispatchSelected(target: Int) {
        mCallback?.onPageSelected(target)
    }

    private fun dispatchScrolled(position: Int, offset: Float, offsetPx: Int) {
        mCallback?.onPageScrolled(position, offset, offsetPx)
    }

    private fun getPosition(): Int {
        return mLayoutManager.findFirstVisibleItemPosition()
    }

    private class ScrollEventValues {
        var mPosition = 0
        var mOffset = 0f
        var mOffsetPx = 0

        fun reset() {
            mPosition = RecyclerView.NO_POSITION
            mOffset = 0f
            mOffsetPx = 0
        }
    }

    companion object {
        private const val STATE_IDLE = 0
        private const val STATE_IN_PROGRESS_MANUAL_DRAG = 1
        private const val STATE_IN_PROGRESS_SMOOTH_SCROLL = 2
        private const val STATE_IN_PROGRESS_IMMEDIATE_SCROLL = 3
        private const val STATE_IN_PROGRESS_FAKE_DRAG = 4
        private const val NO_POSITION = -1
    }
}
