package wangdaye.com.geometricweather.main.widgets

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider

class TrendRecyclerViewScrollBar : RecyclerView.ItemDecoration() {

    private var paint: Paint? = null
    private var scrollBarWidth = 0
    private var scrollBarHeight = 0
    private var themeChanged = false
    @ColorInt
    private var endPointsColor = 0
    @ColorInt
    private var centerColor = 0

    fun resetColor(location: Location) {
        themeChanged = true
        endPointsColor = MainThemeColorProvider.getColor(location, R.attr.colorMainCardBackground)
        centerColor = DisplayUtils.blendColor(
            ColorUtils.setAlphaComponent(
                MainThemeColorProvider.getColor(location, R.attr.colorPrimary),
                (0.05 * 255).toInt()
            ),
            MainThemeColorProvider.getColor(location, R.attr.colorMainCardBackground)
        )
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (paint == null && parent.childCount > 0) {
            paint = Paint().apply { isAntiAlias = true }
            scrollBarWidth = parent.getChildAt(0).measuredWidth
            scrollBarHeight = parent.getChildAt(0).measuredHeight
        }

        val barPaint = paint ?: return
        if (consumedThemeChanged()) {
            barPaint.shader = LinearGradient(
                0f,
                0f,
                0f,
                scrollBarHeight / 2f,
                endPointsColor,
                centerColor,
                Shader.TileMode.MIRROR
            )
        }

        val extent = parent.computeHorizontalScrollExtent()
        val range = parent.computeHorizontalScrollRange()
        val offset = parent.computeHorizontalScrollOffset()

        val offsetPercent = 1f * offset / (range - extent)

        val scrollBarOffsetX = offsetPercent * (parent.measuredWidth - scrollBarWidth)
        c.drawRect(
            scrollBarOffsetX,
            0f,
            scrollBarWidth + scrollBarOffsetX,
            scrollBarHeight.toFloat(),
            barPaint
        )
    }

    private fun consumedThemeChanged(): Boolean {
        return if (themeChanged) {
            themeChanged = false
            true
        } else {
            false
        }
    }
}
