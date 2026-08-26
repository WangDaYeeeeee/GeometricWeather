package wangdaye.com.geometricweather.main.layouts

import android.view.ViewGroup
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

class MainLayoutManager : RecyclerView.LayoutManager() {

    @Px
    var scrollOffset = 0
        private set
    @Px
    private var measuredHeight = 0
    private var dataSetChanged = true

    override fun onDetachedFromWindow(view: RecyclerView, recycler: RecyclerView.Recycler) {
        super.onDetachedFromWindow(view, recycler)
        removeAndRecycleAllViews(recycler)
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onAdapterChanged(oldAdapter: RecyclerView.Adapter<*>?, newAdapter: RecyclerView.Adapter<*>?) {
        super.onAdapterChanged(oldAdapter, newAdapter)
        dataSetChanged = true
    }

    override fun onItemsChanged(recyclerView: RecyclerView) {
        super.onItemsChanged(recyclerView)
        dataSetChanged = true
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (dataSetChanged) {
            removeAndRecycleAllViews(recycler)
        } else {
            detachAndScrapAttachedViews(recycler)
        }

        if (state.itemCount == 0 || state.isPreLayout) {
            return
        }
        if (itemCount == 0) {
            return
        }

        var y = 0
        if (!clipToPadding) {
            y += paddingTop
        }

        for (i in 0 until itemCount) {
            val child = recycler.getViewForPosition(i)
            addView(child)

            measureChildWithMargins(child, 0, 0)
            val childHeight = getDecoratedMeasuredHeight(child)
            val params = child.layoutParams as ViewGroup.MarginLayoutParams
            layoutDecoratedWithMargins(
                child,
                paddingLeft,
                y,
                width - paddingRight,
                y + childHeight + params.topMargin + params.bottomMargin
            )

            y += childHeight + params.topMargin + params.bottomMargin
        }

        if (!clipToPadding) {
            y += paddingBottom
        }

        measuredHeight = y

        if (dataSetChanged) {
            scrollOffset = 0
            dataSetChanged = false
        } else {
            val oldOffset = scrollOffset
            scrollOffset = 0
            scrollVerticallyBy(oldOffset, recycler, state)
        }
    }

    override fun canScrollVertically(): Boolean = true

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        if (childCount == 0 || dy == 0) {
            return 0
        }

        var consumed = dy
        if (scrollOffset + consumed + height > measuredHeight) {
            consumed = measuredHeight - scrollOffset - height
        } else if (scrollOffset + consumed < 0) {
            consumed = -scrollOffset
        }
        scrollOffset += consumed

        offsetChildrenVertical(-consumed)
        return consumed
    }

    override fun computeVerticalScrollOffset(state: RecyclerView.State): Int = scrollOffset

    override fun computeVerticalScrollRange(state: RecyclerView.State): Int = measuredHeight

    override fun computeVerticalScrollExtent(state: RecyclerView.State): Int = height
}
