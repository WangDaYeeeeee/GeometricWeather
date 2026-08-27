package wangdaye.com.geometricweather.common.ui.widgets.horizontal

import android.os.SystemClock
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView

internal class FakeDrag(
    private val mViewPager: HorizontalViewPager2,
    private val mScrollEventAdapter: ScrollEventAdapter,
    private val mRecyclerView: RecyclerView
) {
    private var mVelocityTracker: VelocityTracker? = null
    private var mMaximumVelocity = 0
    private var mRequestedDragDistance = 0f
    private var mActualDraggedDistance = 0
    private var mFakeDragBeginTime: Long = 0

    fun isFakeDragging(): Boolean {
        return mScrollEventAdapter.isFakeDragging()
    }

    @UiThread
    fun beginFakeDrag(): Boolean {
        if (mScrollEventAdapter.isDragging()) {
            return false
        }
        mActualDraggedDistance = 0
        mRequestedDragDistance = 0f
        mFakeDragBeginTime = SystemClock.uptimeMillis()
        beginFakeVelocityTracker()

        mScrollEventAdapter.notifyBeginFakeDrag()
        if (!mScrollEventAdapter.isIdle()) {
            mRecyclerView.stopScroll()
        }
        addFakeMotionEvent(mFakeDragBeginTime, MotionEvent.ACTION_DOWN, 0f, 0f)
        return true
    }

    @UiThread
    fun fakeDragBy(offsetPxFloat: Float): Boolean {
        if (!mScrollEventAdapter.isFakeDragging()) {
            return false
        }
        mRequestedDragDistance -= offsetPxFloat
        val offsetPx = Math.round(mRequestedDragDistance - mActualDraggedDistance)
        mActualDraggedDistance += offsetPx
        val time = SystemClock.uptimeMillis()

        val isHorizontal = mViewPager.orientation == HorizontalViewPager2.ORIENTATION_HORIZONTAL
        val offsetX = if (isHorizontal) offsetPx else 0
        val offsetY = if (isHorizontal) 0 else offsetPx
        val x = if (isHorizontal) mRequestedDragDistance else 0f
        val y = if (isHorizontal) 0f else mRequestedDragDistance

        mRecyclerView.scrollBy(offsetX, offsetY)
        addFakeMotionEvent(time, MotionEvent.ACTION_MOVE, x, y)
        return true
    }

    @UiThread
    fun endFakeDrag(): Boolean {
        if (!mScrollEventAdapter.isFakeDragging()) {
            return false
        }

        mScrollEventAdapter.notifyEndFakeDrag()

        val pixelsPerSecond = 1000
        val velocityTracker = mVelocityTracker!!
        velocityTracker.computeCurrentVelocity(pixelsPerSecond, mMaximumVelocity.toFloat())
        val xVelocity = velocityTracker.xVelocity.toInt()
        val yVelocity = velocityTracker.yVelocity.toInt()
        if (!mRecyclerView.fling(xVelocity, yVelocity)) {
            mViewPager.snapToPage()
        }
        return true
    }

    private fun beginFakeVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
            val configuration = ViewConfiguration.get(mViewPager.context)
            mMaximumVelocity = configuration.scaledMaximumFlingVelocity
        } else {
            mVelocityTracker!!.clear()
        }
    }

    private fun addFakeMotionEvent(time: Long, action: Int, x: Float, y: Float) {
        val ev = MotionEvent.obtain(mFakeDragBeginTime, time, action, x, y, 0)
        mVelocityTracker!!.addMovement(ev)
        ev.recycle()
    }
}
