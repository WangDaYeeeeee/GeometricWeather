package wangdaye.com.geometricweather.common.ui.widgets.horizontal

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.accessibility.AccessibilityViewCommand
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.StatefulAdapter
import kotlin.annotation.AnnotationRetention.SOURCE

class HorizontalViewPager2 : ViewGroup {

    @RestrictTo(LIBRARY_GROUP_PREFIX)
    @Retention(SOURCE)
    @IntDef(ORIENTATION_HORIZONTAL, ORIENTATION_VERTICAL)
    annotation class Orientation

    @RestrictTo(LIBRARY_GROUP_PREFIX)
    @Retention(SOURCE)
    @IntDef(SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING)
    annotation class ScrollState

    @Suppress("WeakerAccess")
    @RestrictTo(LIBRARY_GROUP_PREFIX)
    @Retention(SOURCE)
    @IntDef(OFFSCREEN_PAGE_LIMIT_DEFAULT)
    @IntRange(from = 1)
    annotation class OffscreenPageLimit

    private val mTmpContainerRect = Rect()
    private val mTmpChildRect = Rect()

    private val mExternalPageChangeCallbacks = CompositeOnPageChangeCallback(3)

    internal var mCurrentItem = 0
    var mCurrentItemDirty = false
    private val mCurrentItemDataSetChangeObserver: RecyclerView.AdapterDataObserver =
        object : DataSetChangeObserver() {
            override fun onChanged() {
                mCurrentItemDirty = true
                mScrollEventAdapter.notifyDataSetChangeHappened()
            }
        }

    private lateinit var mLayoutManager: LinearLayoutManager
    private var mPendingCurrentItem = RecyclerView.NO_POSITION
    private var mPendingAdapterState: Parcelable? = null
    internal lateinit var mRecyclerView: RecyclerView
    private lateinit var mPagerSnapHelper: PagerSnapHelper
    lateinit var mScrollEventAdapter: ScrollEventAdapter
    private lateinit var mPageChangeEventDispatcher: CompositeOnPageChangeCallback
    private lateinit var mFakeDragger: FakeDrag
    private lateinit var mPageTransformerAdapter: PageTransformerAdapter
    private var mSavedItemAnimator: RecyclerView.ItemAnimator? = null
    private var mSavedItemAnimatorPresent = false
    private var mUserInputEnabled = true
    @OffscreenPageLimit
    private var mOffscreenPageLimit = OFFSCREEN_PAGE_LIMIT_DEFAULT
    lateinit var mAccessibilityProvider: AccessibilityProvider

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        super(context, attrs, defStyleAttr) {
        initialize(context)
    }

    @RequiresApi(21)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initialize(context)
    }

    private fun initialize(context: Context) {
        mAccessibilityProvider = if (sFeatureEnhancedA11yEnabled) {
            PageAwareAccessibilityProvider()
        } else {
            BasicAccessibilityProvider()
        }

        mRecyclerView = RecyclerViewImpl(context)
        mRecyclerView.id = ViewCompat.generateViewId()
        mRecyclerView.descendantFocusability = FOCUS_BEFORE_DESCENDANTS

        mLayoutManager = LinearLayoutManagerImpl(context)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING)
        orientation = ORIENTATION_HORIZONTAL

        mRecyclerView.layoutParams = ViewGroup.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        )
        mRecyclerView.addOnChildAttachStateChangeListener(enforceChildFillListener())

        mScrollEventAdapter = ScrollEventAdapter(this)
        mFakeDragger = FakeDrag(this, mScrollEventAdapter, mRecyclerView)
        mPagerSnapHelper = PagerSnapHelperImpl()
        mPagerSnapHelper.attachToRecyclerView(mRecyclerView)
        mRecyclerView.addOnScrollListener(mScrollEventAdapter)

        mPageChangeEventDispatcher = CompositeOnPageChangeCallback(3)
        mScrollEventAdapter.setOnPageChangeCallback(mPageChangeEventDispatcher)

        val currentItemUpdater = object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (mCurrentItem != position) {
                    mCurrentItem = position
                    mAccessibilityProvider.onSetNewCurrentItem()
                }
            }

            override fun onPageScrollStateChanged(newState: Int) {
                if (newState == SCROLL_STATE_IDLE) {
                    updateCurrentItem()
                }
            }
        }

        val focusClearer = object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                clearFocus()
                if (hasFocus()) {
                    mRecyclerView.requestFocus(View.FOCUS_FORWARD)
                }
            }
        }

        mPageChangeEventDispatcher.addOnPageChangeCallback(currentItemUpdater)
        mPageChangeEventDispatcher.addOnPageChangeCallback(focusClearer)
        mAccessibilityProvider.onInitialize(mPageChangeEventDispatcher, mRecyclerView)
        mPageChangeEventDispatcher.addOnPageChangeCallback(mExternalPageChangeCallbacks)

        mPageTransformerAdapter = PageTransformerAdapter(mLayoutManager)
        mPageChangeEventDispatcher.addOnPageChangeCallback(mPageTransformerAdapter)

        attachViewToParent(mRecyclerView, 0, mRecyclerView.layoutParams)
    }

    private fun enforceChildFillListener(): RecyclerView.OnChildAttachStateChangeListener {
        return object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                val layoutParams = view.layoutParams as RecyclerView.LayoutParams
                if (layoutParams.width != LayoutParams.MATCH_PARENT
                    || layoutParams.height != LayoutParams.MATCH_PARENT
                ) {
                    throw IllegalStateException(
                        "Pages must fill the whole ViewPager2 (use match_parent)"
                    )
                }
            }

            override fun onChildViewDetachedFromWindow(view: View) {}
        }
    }

    @RequiresApi(23)
    override fun getAccessibilityClassName(): CharSequence {
        if (mAccessibilityProvider.handlesGetAccessibilityClassName()) {
            return mAccessibilityProvider.onGetAccessibilityClassName()
        }
        return super.getAccessibilityClassName()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)

        ss.mRecyclerViewId = mRecyclerView.id
        ss.mCurrentItem = if (mPendingCurrentItem == RecyclerView.NO_POSITION) {
            mCurrentItem
        } else {
            mPendingCurrentItem
        }

        if (mPendingAdapterState != null) {
            ss.mAdapterState = mPendingAdapterState
        } else {
            val adapter = mRecyclerView.adapter
            if (adapter is StatefulAdapter) {
                ss.mAdapterState = adapter.saveState()
            }
        }
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        mPendingCurrentItem = state.mCurrentItem
        mPendingAdapterState = state.mAdapterState
    }

    private fun restorePendingState() {
        if (mPendingCurrentItem == RecyclerView.NO_POSITION) {
            return
        }
        val adapter = adapter ?: return
        if (mPendingAdapterState != null) {
            if (adapter is StatefulAdapter) {
                adapter.restoreState(mPendingAdapterState!!)
            }
            mPendingAdapterState = null
        }
        mCurrentItem = Math.max(0, Math.min(mPendingCurrentItem, adapter.itemCount - 1))
        mPendingCurrentItem = RecyclerView.NO_POSITION
        mRecyclerView.scrollToPosition(mCurrentItem)
        mAccessibilityProvider.onRestorePendingState()
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        val state = container[id]
        if (state is SavedState) {
            val previousRvId = state.mRecyclerViewId
            val currentRvId = mRecyclerView.id
            container.put(currentRvId, container[previousRvId])
            container.remove(previousRvId)
        }
        super.dispatchRestoreInstanceState(container)
        restorePendingState()
    }

    class SavedState : BaseSavedState {
        var mRecyclerViewId = 0
        var mCurrentItem = 0
        var mAdapterState: Parcelable? = null

        @RequiresApi(24)
        constructor(source: Parcel, loader: ClassLoader?) : super(source, loader) {
            readValues(source, loader)
        }

        constructor(source: Parcel) : super(source) {
            readValues(source, null)
        }

        constructor(superState: Parcelable?) : super(superState)

        private fun readValues(source: Parcel, loader: ClassLoader?) {
            mRecyclerViewId = source.readInt()
            mCurrentItem = source.readInt()
            mAdapterState = source.readParcelable(loader)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(mRecyclerViewId)
            out.writeInt(mCurrentItem)
            out.writeParcelable(mAdapterState, flags)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.ClassLoaderCreator<SavedState> =
                object : Parcelable.ClassLoaderCreator<SavedState> {
                    override fun createFromParcel(source: Parcel, loader: ClassLoader?): SavedState {
                        return if (Build.VERSION.SDK_INT >= 24) {
                            SavedState(source, loader)
                        } else {
                            SavedState(source)
                        }
                    }

                    override fun createFromParcel(source: Parcel): SavedState {
                        return createFromParcel(source, null)
                    }

                    override fun newArray(size: Int): Array<SavedState?> {
                        return arrayOfNulls(size)
                    }
                }
        }
    }

    var adapter: RecyclerView.Adapter<*>?
        get() = mRecyclerView.adapter
        set(adapter) {
            val currentAdapter = mRecyclerView.adapter
            mAccessibilityProvider.onDetachAdapter(currentAdapter)
            unregisterCurrentItemDataSetTracker(currentAdapter)
            mRecyclerView.adapter = adapter
            mCurrentItem = 0
            restorePendingState()
            mAccessibilityProvider.onAttachAdapter(adapter)
            registerCurrentItemDataSetTracker(adapter)
        }

    private fun registerCurrentItemDataSetTracker(adapter: RecyclerView.Adapter<*>?) {
        adapter?.registerAdapterDataObserver(mCurrentItemDataSetChangeObserver)
    }

    private fun unregisterCurrentItemDataSetTracker(adapter: RecyclerView.Adapter<*>?) {
        adapter?.unregisterAdapterDataObserver(mCurrentItemDataSetChangeObserver)
    }

    override fun onViewAdded(child: View?) {
        throw IllegalStateException(
            javaClass.simpleName + " does not support direct child views"
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(mRecyclerView, widthMeasureSpec, heightMeasureSpec)
        var width = mRecyclerView.measuredWidth
        var height = mRecyclerView.measuredHeight
        val childState = mRecyclerView.measuredState

        width += paddingLeft + paddingRight
        height += paddingTop + paddingBottom

        width = Math.max(width, suggestedMinimumWidth)
        height = Math.max(height, suggestedMinimumHeight)

        setMeasuredDimension(
            resolveSizeAndState(width, widthMeasureSpec, childState),
            resolveSizeAndState(
                height, heightMeasureSpec,
                childState shl MEASURED_HEIGHT_STATE_SHIFT
            )
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = mRecyclerView.measuredWidth
        val height = mRecyclerView.measuredHeight

        mTmpContainerRect.left = paddingLeft
        mTmpContainerRect.right = r - l - paddingRight
        mTmpContainerRect.top = paddingTop
        mTmpContainerRect.bottom = b - t - paddingBottom

        Gravity.apply(Gravity.TOP or Gravity.START, width, height, mTmpContainerRect, mTmpChildRect)
        mRecyclerView.layout(
            mTmpChildRect.left, mTmpChildRect.top, mTmpChildRect.right,
            mTmpChildRect.bottom
        )

        if (mCurrentItemDirty) {
            updateCurrentItem()
        }
    }

    fun updateCurrentItem() {
        val snapView = mPagerSnapHelper.findSnapView(mLayoutManager) ?: return
        val snapPosition = mLayoutManager.getPosition(snapView)

        if (snapPosition != mCurrentItem && scrollState == SCROLL_STATE_IDLE) {
            mPageChangeEventDispatcher.onPageSelected(snapPosition)
        }
        mCurrentItemDirty = false
    }

    fun getPageSize(): Int {
        val rv = mRecyclerView
        return if (orientation == ORIENTATION_HORIZONTAL) {
            rv.width - rv.paddingLeft - rv.paddingRight
        } else {
            rv.height - rv.paddingTop - rv.paddingBottom
        }
    }

    @get:Orientation
    @set:Orientation
    var orientation: Int
        get() = mLayoutManager.orientation
        set(orientation) {
            mLayoutManager.orientation = orientation
            mAccessibilityProvider.onSetOrientation()
        }

    fun isRtl(): Boolean {
        return mLayoutManager.layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL
    }

    fun setCurrentItem(item: Int) {
        setCurrentItem(item, true)
    }

    fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        if (isFakeDragging()) {
            throw IllegalStateException(
                "Cannot change current item when ViewPager2 is fake dragging"
            )
        }
        setCurrentItemInternal(item, smoothScroll)
    }

    fun setCurrentItemInternal(item: Int, smoothScroll: Boolean) {
        var target = item
        val adapter = adapter
        if (adapter == null) {
            if (mPendingCurrentItem != RecyclerView.NO_POSITION) {
                mPendingCurrentItem = Math.max(target, 0)
            }
            return
        }
        if (adapter.itemCount <= 0) {
            return
        }
        target = Math.max(target, 0)
        target = Math.min(target, adapter.itemCount - 1)

        if (target == mCurrentItem && mScrollEventAdapter.isIdle()) {
            return
        }
        if (target == mCurrentItem && smoothScroll) {
            return
        }

        var previousItem = mCurrentItem.toDouble()
        mCurrentItem = target
        mAccessibilityProvider.onSetNewCurrentItem()

        if (!mScrollEventAdapter.isIdle()) {
            previousItem = mScrollEventAdapter.getRelativeScrollPosition()
        }

        mScrollEventAdapter.notifyProgrammaticScroll(target, smoothScroll)
        if (!smoothScroll) {
            mRecyclerView.scrollToPosition(target)
            return
        }

        if (Math.abs(target - previousItem) > 3) {
            mRecyclerView.scrollToPosition(if (target > previousItem) target - 3 else target + 3)
            mRecyclerView.post(SmoothScrollToPosition(target, mRecyclerView))
        } else {
            mRecyclerView.smoothScrollToPosition(target)
        }
    }

    fun getCurrentItem(): Int = mCurrentItem

    @get:ScrollState
    val scrollState: Int
        get() = mScrollEventAdapter.getScrollState()

    fun beginFakeDrag(): Boolean = mFakeDragger.beginFakeDrag()

    @SuppressLint("SupportAnnotationUsage")
    fun fakeDragBy(@Px offsetPxFloat: Float): Boolean = mFakeDragger.fakeDragBy(offsetPxFloat)

    fun endFakeDrag(): Boolean = mFakeDragger.endFakeDrag()

    fun isFakeDragging(): Boolean = mFakeDragger.isFakeDragging()

    fun snapToPage() {
        val view = mPagerSnapHelper.findSnapView(mLayoutManager) ?: return
        val snapDistance = mPagerSnapHelper.calculateDistanceToFinalSnap(mLayoutManager, view)
        if (snapDistance != null && (snapDistance[0] != 0 || snapDistance[1] != 0)) {
            mRecyclerView.smoothScrollBy(snapDistance[0], snapDistance[1])
        }
    }

    var isUserInputEnabled: Boolean
        get() = mUserInputEnabled
        set(enabled) {
            mUserInputEnabled = enabled
            mAccessibilityProvider.onSetUserInputEnabled()
        }

    fun setOffscreenPageLimit(@OffscreenPageLimit limit: Int) {
        if (limit < 1 && limit != OFFSCREEN_PAGE_LIMIT_DEFAULT) {
            throw IllegalArgumentException(
                "Offscreen page limit must be OFFSCREEN_PAGE_LIMIT_DEFAULT or a number > 0"
            )
        }
        mOffscreenPageLimit = limit
        mRecyclerView.requestLayout()
    }

    @OffscreenPageLimit
    fun getOffscreenPageLimit(): Int = mOffscreenPageLimit

    override fun canScrollHorizontally(direction: Int): Boolean {
        return mRecyclerView.canScrollHorizontally(direction)
    }

    override fun canScrollVertically(direction: Int): Boolean {
        return mRecyclerView.canScrollVertically(direction)
    }

    fun registerOnPageChangeCallback(callback: OnPageChangeCallback) {
        mExternalPageChangeCallbacks.addOnPageChangeCallback(callback)
    }

    fun unregisterOnPageChangeCallback(callback: OnPageChangeCallback) {
        mExternalPageChangeCallbacks.removeOnPageChangeCallback(callback)
    }

    fun setPageTransformer(transformer: PageTransformer?) {
        if (transformer != null) {
            if (!mSavedItemAnimatorPresent) {
                mSavedItemAnimator = mRecyclerView.itemAnimator
                mSavedItemAnimatorPresent = true
            }
            mRecyclerView.itemAnimator = null
        } else {
            if (mSavedItemAnimatorPresent) {
                mRecyclerView.itemAnimator = mSavedItemAnimator
                mSavedItemAnimator = null
                mSavedItemAnimatorPresent = false
            }
        }

        if (transformer === mPageTransformerAdapter.getPageTransformer()) {
            return
        }
        mPageTransformerAdapter.setPageTransformer(transformer)
        requestTransform()
    }

    fun requestTransform() {
        if (mPageTransformerAdapter.getPageTransformer() == null) {
            return
        }
        val relativePosition = mScrollEventAdapter.getRelativeScrollPosition()
        val position = relativePosition.toInt()
        val positionOffset = (relativePosition - position).toFloat()
        val positionOffsetPx = Math.round(getPageSize() * positionOffset)
        mPageTransformerAdapter.onPageScrolled(position, positionOffset, positionOffsetPx)
    }

    @RequiresApi(17)
    override fun setLayoutDirection(layoutDirection: Int) {
        super.setLayoutDirection(layoutDirection)
        mAccessibilityProvider.onSetLayoutDirection()
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        mAccessibilityProvider.onInitializeAccessibilityNodeInfo(info)
    }

    @RequiresApi(16)
    override fun performAccessibilityAction(action: Int, arguments: Bundle?): Boolean {
        if (mAccessibilityProvider.handlesPerformAccessibilityAction(action, arguments)) {
            return mAccessibilityProvider.onPerformAccessibilityAction(action, arguments)
        }
        return super.performAccessibilityAction(action, arguments)
    }

    private inner class RecyclerViewImpl(context: Context) : HorizontalRecyclerView(context) {
        @RequiresApi(23)
        override fun getAccessibilityClassName(): CharSequence {
            if (mAccessibilityProvider.handlesRvGetAccessibilityClassName()) {
                return mAccessibilityProvider.onRvGetAccessibilityClassName()
            }
            return super.getAccessibilityClassName()
        }

        override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
            super.onInitializeAccessibilityEvent(event)
            event.fromIndex = mCurrentItem
            event.toIndex = mCurrentItem
            mAccessibilityProvider.onRvInitializeAccessibilityEvent(event)
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouchEvent(event: MotionEvent): Boolean {
            return isUserInputEnabled && super.onTouchEvent(event)
        }

        override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
            return isUserInputEnabled && super.onInterceptTouchEvent(ev)
        }
    }

    private inner class LinearLayoutManagerImpl(context: Context) : LinearLayoutManager(context) {
        override fun performAccessibilityAction(
            recycler: RecyclerView.Recycler,
            state: RecyclerView.State,
            action: Int,
            args: Bundle?
        ): Boolean {
            if (mAccessibilityProvider.handlesLmPerformAccessibilityAction(action)) {
                return mAccessibilityProvider.onLmPerformAccessibilityAction(action)
            }
            return super.performAccessibilityAction(recycler, state, action, args)
        }

        override fun onInitializeAccessibilityNodeInfo(
            recycler: RecyclerView.Recycler,
            state: RecyclerView.State,
            info: AccessibilityNodeInfoCompat
        ) {
            super.onInitializeAccessibilityNodeInfo(recycler, state, info)
            mAccessibilityProvider.onLmInitializeAccessibilityNodeInfo(info)
        }

        override fun calculateExtraLayoutSpace(state: RecyclerView.State, extraLayoutSpace: IntArray) {
            val pageLimit = getOffscreenPageLimit()
            if (pageLimit == OFFSCREEN_PAGE_LIMIT_DEFAULT) {
                super.calculateExtraLayoutSpace(state, extraLayoutSpace)
                return
            }
            val offscreenSpace = getPageSize() * pageLimit
            extraLayoutSpace[0] = offscreenSpace
            extraLayoutSpace[1] = offscreenSpace
        }

        override fun requestChildRectangleOnScreen(
            parent: RecyclerView,
            child: View,
            rect: Rect,
            immediate: Boolean,
            focusedChildVisible: Boolean
        ): Boolean {
            return false
        }
    }

    private inner class PagerSnapHelperImpl : PagerSnapHelper() {
        override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
            return if (isFakeDragging()) null else super.findSnapView(layoutManager)
        }
    }

    private class SmoothScrollToPosition(
        private val mPosition: Int,
        private val mRecyclerView: RecyclerView
    ) : Runnable {
        override fun run() {
            mRecyclerView.smoothScrollToPosition(mPosition)
        }
    }

    abstract class OnPageChangeCallback {
        open fun onPageScrolled(position: Int, positionOffset: Float, @Px positionOffsetPixels: Int) {}
        open fun onPageSelected(position: Int) {}
        open fun onPageScrollStateChanged(@ScrollState state: Int) {}
    }

    fun interface PageTransformer {
        fun transformPage(page: View, position: Float)
    }

    fun addItemDecoration(decor: RecyclerView.ItemDecoration) {
        mRecyclerView.addItemDecoration(decor)
    }

    fun addItemDecoration(decor: RecyclerView.ItemDecoration, index: Int) {
        mRecyclerView.addItemDecoration(decor, index)
    }

    fun getItemDecorationAt(index: Int): RecyclerView.ItemDecoration {
        return mRecyclerView.getItemDecorationAt(index)
    }

    fun getItemDecorationCount(): Int = mRecyclerView.itemDecorationCount

    fun invalidateItemDecorations() {
        mRecyclerView.invalidateItemDecorations()
    }

    fun removeItemDecorationAt(index: Int) {
        mRecyclerView.removeItemDecorationAt(index)
    }

    fun removeItemDecoration(decor: RecyclerView.ItemDecoration) {
        mRecyclerView.removeItemDecoration(decor)
    }

    @Suppress("ClassCanBeStatic")
    abstract inner class AccessibilityProvider {
        open fun onInitialize(
            pageChangeEventDispatcher: CompositeOnPageChangeCallback,
            recyclerView: RecyclerView
        ) {
        }

        open fun handlesGetAccessibilityClassName(): Boolean = false

        open fun onGetAccessibilityClassName(): String {
            throw IllegalStateException("Not implemented.")
        }

        open fun onRestorePendingState() {}
        open fun onAttachAdapter(newAdapter: RecyclerView.Adapter<*>?) {}
        open fun onDetachAdapter(oldAdapter: RecyclerView.Adapter<*>?) {}
        open fun onSetOrientation() {}
        open fun onSetNewCurrentItem() {}
        open fun onSetUserInputEnabled() {}
        open fun onSetLayoutDirection() {}
        open fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {}
        open fun handlesPerformAccessibilityAction(action: Int, arguments: Bundle?): Boolean = false
        open fun onPerformAccessibilityAction(action: Int, arguments: Bundle?): Boolean {
            throw IllegalStateException("Not implemented.")
        }

        open fun onRvInitializeAccessibilityEvent(event: AccessibilityEvent) {}
        open fun handlesLmPerformAccessibilityAction(action: Int): Boolean = false
        open fun onLmPerformAccessibilityAction(action: Int): Boolean {
            throw IllegalStateException("Not implemented.")
        }

        open fun onLmInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfoCompat) {}
        open fun handlesRvGetAccessibilityClassName(): Boolean = false
        open fun onRvGetAccessibilityClassName(): CharSequence {
            throw IllegalStateException("Not implemented.")
        }
    }

    inner class BasicAccessibilityProvider : AccessibilityProvider() {
        override fun handlesLmPerformAccessibilityAction(action: Int): Boolean {
            return (action == AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD
                || action == AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD)
                && !isUserInputEnabled
        }

        override fun onLmPerformAccessibilityAction(action: Int): Boolean {
            if (!handlesLmPerformAccessibilityAction(action)) {
                throw IllegalStateException()
            }
            return false
        }

        override fun onLmInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfoCompat) {
            if (!isUserInputEnabled) {
                info.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_BACKWARD)
                info.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_FORWARD)
                info.isScrollable = false
            }
        }

        override fun handlesRvGetAccessibilityClassName(): Boolean = true

        override fun onRvGetAccessibilityClassName(): CharSequence {
            if (!handlesRvGetAccessibilityClassName()) {
                throw IllegalStateException()
            }
            return "androidx.viewpager.widget.ViewPager"
        }
    }

    inner class PageAwareAccessibilityProvider : AccessibilityProvider() {
        private val mActionPageForward = AccessibilityViewCommand { view, _ ->
            val viewPager = view as HorizontalViewPager2
            setCurrentItemFromAccessibilityCommand(viewPager.getCurrentItem() + 1)
            true
        }

        private val mActionPageBackward = AccessibilityViewCommand { view, _ ->
            val viewPager = view as HorizontalViewPager2
            setCurrentItemFromAccessibilityCommand(viewPager.getCurrentItem() - 1)
            true
        }

        private lateinit var mAdapterDataObserver: RecyclerView.AdapterDataObserver

        override fun onInitialize(
            pageChangeEventDispatcher: CompositeOnPageChangeCallback,
            recyclerView: RecyclerView
        ) {
            ViewCompat.setImportantForAccessibility(
                recyclerView,
                ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO
            )

            mAdapterDataObserver = object : DataSetChangeObserver() {
                override fun onChanged() {
                    updatePageAccessibilityActions()
                }
            }

            if (ViewCompat.getImportantForAccessibility(this@HorizontalViewPager2)
                == ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_AUTO
            ) {
                ViewCompat.setImportantForAccessibility(
                    this@HorizontalViewPager2,
                    ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES
                )
            }
        }

        override fun handlesGetAccessibilityClassName(): Boolean = true

        override fun onGetAccessibilityClassName(): String {
            if (!handlesGetAccessibilityClassName()) {
                throw IllegalStateException()
            }
            return "androidx.viewpager.widget.ViewPager"
        }

        override fun onRestorePendingState() {
            updatePageAccessibilityActions()
        }

        override fun onAttachAdapter(newAdapter: RecyclerView.Adapter<*>?) {
            updatePageAccessibilityActions()
            newAdapter?.registerAdapterDataObserver(mAdapterDataObserver)
        }

        override fun onDetachAdapter(oldAdapter: RecyclerView.Adapter<*>?) {
            oldAdapter?.unregisterAdapterDataObserver(mAdapterDataObserver)
        }

        override fun onSetOrientation() {
            updatePageAccessibilityActions()
        }

        override fun onSetNewCurrentItem() {
            updatePageAccessibilityActions()
        }

        override fun onSetUserInputEnabled() {
            updatePageAccessibilityActions()
            if (Build.VERSION.SDK_INT < 21) {
                sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)
            }
        }

        override fun onSetLayoutDirection() {
            updatePageAccessibilityActions()
        }

        override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
            addCollectionInfo(info)
            if (Build.VERSION.SDK_INT >= 16) {
                addScrollActions(info)
            }
        }

        override fun handlesPerformAccessibilityAction(action: Int, arguments: Bundle?): Boolean {
            return action == AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD
                || action == AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD
        }

        override fun onPerformAccessibilityAction(action: Int, arguments: Bundle?): Boolean {
            if (!handlesPerformAccessibilityAction(action, arguments)) {
                throw IllegalStateException()
            }
            val nextItem = if (action == AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD) {
                getCurrentItem() - 1
            } else {
                getCurrentItem() + 1
            }
            setCurrentItemFromAccessibilityCommand(nextItem)
            return true
        }

        override fun onRvInitializeAccessibilityEvent(event: AccessibilityEvent) {
            event.setSource(this@HorizontalViewPager2)
            event.className = onGetAccessibilityClassName()
        }

        fun setCurrentItemFromAccessibilityCommand(item: Int) {
            if (isUserInputEnabled) {
                setCurrentItemInternal(item, true)
            }
        }

        fun updatePageAccessibilityActions() {
            val viewPager = this@HorizontalViewPager2

            @SuppressLint("InlinedApi")
            val actionIdPageLeft = android.R.id.accessibilityActionPageLeft
            @SuppressLint("InlinedApi")
            val actionIdPageRight = android.R.id.accessibilityActionPageRight
            @SuppressLint("InlinedApi")
            val actionIdPageUp = android.R.id.accessibilityActionPageUp
            @SuppressLint("InlinedApi")
            val actionIdPageDown = android.R.id.accessibilityActionPageDown

            ViewCompat.removeAccessibilityAction(viewPager, actionIdPageLeft)
            ViewCompat.removeAccessibilityAction(viewPager, actionIdPageRight)
            ViewCompat.removeAccessibilityAction(viewPager, actionIdPageUp)
            ViewCompat.removeAccessibilityAction(viewPager, actionIdPageDown)

            val adapter = adapter ?: return
            val itemCount = adapter.itemCount
            if (itemCount == 0) {
                return
            }
            if (!isUserInputEnabled) {
                return
            }

            if (orientation == ORIENTATION_HORIZONTAL) {
                val isLayoutRtl = isRtl()
                val actionIdPageForward = if (isLayoutRtl) actionIdPageLeft else actionIdPageRight
                val actionIdPageBackward = if (isLayoutRtl) actionIdPageRight else actionIdPageLeft

                if (mCurrentItem < itemCount - 1) {
                    ViewCompat.replaceAccessibilityAction(
                        viewPager,
                        AccessibilityNodeInfoCompat.AccessibilityActionCompat(actionIdPageForward, null),
                        null,
                        mActionPageForward
                    )
                }
                if (mCurrentItem > 0) {
                    ViewCompat.replaceAccessibilityAction(
                        viewPager,
                        AccessibilityNodeInfoCompat.AccessibilityActionCompat(actionIdPageBackward, null),
                        null,
                        mActionPageBackward
                    )
                }
            } else {
                if (mCurrentItem < itemCount - 1) {
                    ViewCompat.replaceAccessibilityAction(
                        viewPager,
                        AccessibilityNodeInfoCompat.AccessibilityActionCompat(actionIdPageDown, null),
                        null,
                        mActionPageForward
                    )
                }
                if (mCurrentItem > 0) {
                    ViewCompat.replaceAccessibilityAction(
                        viewPager,
                        AccessibilityNodeInfoCompat.AccessibilityActionCompat(actionIdPageUp, null),
                        null,
                        mActionPageBackward
                    )
                }
            }
        }

        private fun addCollectionInfo(info: AccessibilityNodeInfo) {
            var rowCount = 0
            var colCount = 0
            val adapter = adapter
            if (adapter != null) {
                if (orientation == ORIENTATION_VERTICAL) {
                    rowCount = adapter.itemCount
                } else {
                    colCount = adapter.itemCount
                }
            }
            val nodeInfoCompat = AccessibilityNodeInfoCompat.wrap(info)
            val collectionInfo = AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(
                rowCount, colCount,
                false,
                AccessibilityNodeInfoCompat.CollectionInfoCompat.SELECTION_MODE_NONE
            )
            nodeInfoCompat.setCollectionInfo(collectionInfo)
        }

        private fun addScrollActions(info: AccessibilityNodeInfo) {
            val adapter = adapter ?: return
            val itemCount = adapter.itemCount
            if (itemCount == 0 || !isUserInputEnabled) {
                return
            }
            if (mCurrentItem > 0) {
                info.addAction(AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD)
            }
            if (mCurrentItem < itemCount - 1) {
                info.addAction(AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD)
            }
            info.isScrollable = true
        }
    }

    private abstract class DataSetChangeObserver : RecyclerView.AdapterDataObserver() {
        abstract override fun onChanged()

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            onChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            onChanged()
        }
    }

    companion object {
        const val ORIENTATION_HORIZONTAL = RecyclerView.HORIZONTAL
        const val ORIENTATION_VERTICAL = RecyclerView.VERTICAL
        const val SCROLL_STATE_IDLE = 0
        const val SCROLL_STATE_DRAGGING = 1
        const val SCROLL_STATE_SETTLING = 2
        const val OFFSCREEN_PAGE_LIMIT_DEFAULT = -1

        @JvmField
        var sFeatureEnhancedA11yEnabled = true
    }
}
