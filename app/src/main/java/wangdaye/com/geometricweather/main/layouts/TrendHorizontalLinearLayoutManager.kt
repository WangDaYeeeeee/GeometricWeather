package wangdaye.com.geometricweather.main.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import wangdaye.com.geometricweather.common.ui.widgets.trend.TrendLayoutManager
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import kotlin.math.abs

class TrendHorizontalLinearLayoutManager @JvmOverloads constructor(
    private val context: Context,
    private val fillCount: Int = 0
) : TrendLayoutManager(context) {

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val consumed = super.scrollHorizontallyBy(dx, recycler, state)
        return if (consumed == 0) {
            0
        } else if (abs(consumed) < abs(dx)) {
            dx
        } else {
            consumed
        }
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return if (fillCount > 0) {
            val minWidth = DisplayUtils.dpToPx(context, MIN_ITEM_WIDTH.toFloat()).toInt()
            val minHeight = DisplayUtils.dpToPx(context, MIN_ITEM_HEIGHT.toFloat()).toInt()
            RecyclerView.LayoutParams(
                minWidth.coerceAtLeast(width / fillCount),
                if (height > minHeight) ViewGroup.LayoutParams.MATCH_PARENT else minHeight
            )
        } else {
            RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun generateLayoutParams(c: Context, attrs: AttributeSet): RecyclerView.LayoutParams {
        return generateDefaultLayoutParams()
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams): RecyclerView.LayoutParams {
        return generateDefaultLayoutParams()
    }

    companion object {
        private const val MIN_ITEM_WIDTH = 56
        private const val MIN_ITEM_HEIGHT = 144
    }
}
