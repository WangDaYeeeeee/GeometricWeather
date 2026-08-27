package wangdaye.com.geometricweather.common.ui.widgets.trend

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.ui.widgets.horizontal.HorizontalRecyclerView
import wangdaye.com.geometricweather.common.ui.widgets.trend.item.AbsTrendItemView
import wangdaye.com.geometricweather.common.utils.DisplayUtils

class TrendRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : HorizontalRecyclerView(context, attrs, defStyle) {

    private val mPaint: Paint
    @ColorInt
    private var mLineColor: Int = 0

    private var mDrawingBoundaryTop: Int
    private var mDrawingBoundaryBottom: Int

    private var mKeyLineList: List<KeyLine>?
    private var mKeyLineVisibility = true

    private var mHighestData: Float? = null
    private var mLowestData: Float? = null

    private val mTextSize: Int
    private val mTextMargin: Int
    private val mLineWidth: Int

    class KeyLine(
        @JvmField var value: Float,
        @JvmField var contentLeft: String,
        @JvmField var contentRight: String,
        @JvmField var contentPosition: ContentPosition
    ) {
        enum class ContentPosition { ABOVE_LINE, BELOW_LINE }
    }

    init {
        setWillNotDraw(false)

        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.typeface =
            DisplayUtils.getTypefaceFromTextAppearance(getContext(), R.style.subtitle_text)

        mTextSize = DisplayUtils.dpToPx(getContext(), TEXT_SIZE_DIP.toFloat()).toInt()
        mTextMargin = DisplayUtils.dpToPx(getContext(), TEXT_MARGIN_DIP.toFloat()).toInt()
        mLineWidth = DisplayUtils.dpToPx(getContext(), LINE_WIDTH_DIP.toFloat()).toInt()

        mDrawingBoundaryTop = -1
        mDrawingBoundaryBottom = -1

        setLineColor(Color.GRAY)

        mKeyLineList = ArrayList()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawKeyLines(canvas)
    }

    private fun drawKeyLines(canvas: Canvas) {
        if (!mKeyLineVisibility ||
            mKeyLineList == null ||
            mKeyLineList!!.isEmpty() ||
            mHighestData == null ||
            mLowestData == null
        ) {
            return
        }

        if (childCount > 0) {
            mDrawingBoundaryTop = (getChildAt(0) as AbsTrendItemView).getChartTop()
            mDrawingBoundaryBottom = (getChildAt(0) as AbsTrendItemView).getChartBottom()
        }
        if (mDrawingBoundaryTop < 0 || mDrawingBoundaryBottom < 0) {
            return
        }

        val dataRange = mHighestData!! - mLowestData!!
        val boundaryRange = (mDrawingBoundaryBottom - mDrawingBoundaryTop).toFloat()
        for (line in mKeyLineList!!) {
            if (line.value > mHighestData!! || line.value < mLowestData!!) {
                continue
            }

            val y = (mDrawingBoundaryBottom -
                (line.value - mLowestData!!) / dataRange * boundaryRange).toInt()

            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = mLineWidth.toFloat()
            mPaint.color = mLineColor
            canvas.drawLine(0f, y.toFloat(), measuredWidth.toFloat(), y.toFloat(), mPaint)

            mPaint.style = Paint.Style.FILL
            mPaint.textSize = mTextSize.toFloat()
            mPaint.color = if (DisplayUtils.isDarkMode(getContext())) {
                ContextCompat.getColor(getContext(), R.color.colorTextGrey)
            } else {
                ContextCompat.getColor(getContext(), R.color.colorTextGrey2nd)
            }
            when (line.contentPosition) {
                KeyLine.ContentPosition.ABOVE_LINE -> {
                    mPaint.textAlign = Paint.Align.LEFT
                    canvas.drawText(
                        line.contentLeft,
                        (2 * mTextMargin).toFloat(),
                        y - mPaint.fontMetrics.bottom - mTextMargin,
                        mPaint
                    )
                    mPaint.textAlign = Paint.Align.RIGHT
                    canvas.drawText(
                        line.contentRight,
                        (measuredWidth - 2 * mTextMargin).toFloat(),
                        y - mPaint.fontMetrics.bottom - mTextMargin,
                        mPaint
                    )
                }
                KeyLine.ContentPosition.BELOW_LINE -> {
                    mPaint.textAlign = Paint.Align.LEFT
                    canvas.drawText(
                        line.contentLeft,
                        (2 * mTextMargin).toFloat(),
                        y - mPaint.fontMetrics.top + mTextMargin,
                        mPaint
                    )
                    mPaint.textAlign = Paint.Align.RIGHT
                    canvas.drawText(
                        line.contentRight,
                        (measuredWidth - 2 * mTextMargin).toFloat(),
                        y - mPaint.fontMetrics.top + mTextMargin,
                        mPaint
                    )
                }
            }
        }
    }

    fun setData(keyLineList: List<KeyLine>?, highestData: Float, lowestData: Float) {
        mKeyLineList = keyLineList
        mHighestData = highestData
        mLowestData = lowestData
        invalidate()
    }

    fun setKeyLineVisibility(visibility: Boolean) {
        mKeyLineVisibility = visibility
        invalidate()
    }

    fun setLineColor(@ColorInt lineColor: Int) {
        mLineColor = lineColor
        invalidate()
    }

    companion object {
        private const val LINE_WIDTH_DIP = 1
        private const val TEXT_SIZE_DIP = 12
        private const val TEXT_MARGIN_DIP = 2
        const val ITEM_MARGIN_BOTTOM_DIP = 16
    }
}
