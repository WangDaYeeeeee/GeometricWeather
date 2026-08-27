package wangdaye.com.geometricweather.common.ui.decotarions

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView
import wangdaye.com.geometricweather.common.utils.DisplayUtils

class ListDecoration(context: Context, @ColorInt color: Int) : RecyclerView.ItemDecoration() {

    private val mPaint: Paint = Paint()

    @Px
    private val mDividerDistance: Int = DisplayUtils.dpToPx(context, 1f).toInt()

    init {
        mPaint.color = color
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mDividerDistance.toFloat()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            c.drawLine(
                child.left.toFloat(),
                child.bottom + mDividerDistance / 2f,
                child.right.toFloat(),
                child.bottom + mDividerDistance / 2f,
                mPaint
            )
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(0, 0, 0, mDividerDistance)
    }

    fun setColor(@ColorInt color: Int) {
        mPaint.color = color
    }

    @ColorInt
    fun getColor(): Int {
        return mPaint.color
    }
}
