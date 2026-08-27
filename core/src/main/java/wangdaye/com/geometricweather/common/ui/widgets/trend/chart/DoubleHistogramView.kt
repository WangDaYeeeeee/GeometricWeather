package wangdaye.com.geometricweather.common.ui.widgets.trend.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.Size
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.utils.DisplayUtils

class DoubleHistogramView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbsChartItemView(context, attrs, defStyleAttr) {

    private lateinit var mPaint: Paint
    private var mHighHistogramValue: Float? = null
    private var mLowHistogramValue: Float? = null
    private var mHighHistogramValueStr: String? = null
    private var mLowHistogramValueStr: String? = null
    private var mHighestHistogramValue: Float? = null
    private var mHighHistogramY = 0
    private var mLowHistogramY = 0
    private var mMargins = 0
    private var mMarginCenter = 0
    private var mHistogramWidth = 0
    private var mHistogramTextSize = 0
    private var mChartLineWith = 0
    private var mTextMargin = 0
    private lateinit var mLineColors: IntArray
    private var mTextColor = 0
    private var mTextShadowColor = 0
    @Size(2)
    private lateinit var mHistogramAlphas: FloatArray

    init {
        initialize()
    }

    private fun initialize() {
        mLineColors = intArrayOf(Color.BLACK, Color.DKGRAY, Color.LTGRAY)
        setTextColors(Color.BLACK)
        mMargins = DisplayUtils.dpToPx(context, MARGIN_DIP).toInt()
        mMarginCenter = DisplayUtils.dpToPx(context, MARGIN_CENTER_DIP).toInt()
        mHistogramWidth = DisplayUtils.dpToPx(context, HISTOGRAM_WIDTH_DIP).toInt()
        mHistogramTextSize = DisplayUtils.dpToPx(context, HISTOGRAM_TEXT_SIZE_DIP).toInt()
        mChartLineWith = DisplayUtils.dpToPx(context, CHART_LINE_SIZE_DIP).toInt()
        mTextMargin = DisplayUtils.dpToPx(context, TEXT_MARGIN_DIP).toInt()
        mPaint = Paint()
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.isAntiAlias = true
        mPaint.isFilterBitmap = true
        mPaint.typeface = DisplayUtils.getTypefaceFromTextAppearance(context, R.style.title_text)
        mHistogramAlphas = floatArrayOf(1f, 1f)
    }

    override fun getMarginTop(): Int = mMargins

    override fun getMarginBottom(): Int = mMargins

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        computeCoordinates()
        drawTimeLine(canvas)
        if (mHighestHistogramValue != null) {
            if (mHighHistogramValue != null && mHighHistogramValue != 0f && mHighHistogramValueStr != null) {
                drawHighHistogram(canvas)
            }
            if (mLowHistogramValue != null && mLowHistogramValue != 0f && mLowHistogramValueStr != null) {
                drawLowHistogram(canvas)
            }
        }
    }

    private fun drawTimeLine(canvas: Canvas) {
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mChartLineWith.toFloat()
        mPaint.color = mLineColors[2]
        canvas.drawLine(
            measuredWidth / 2f, mMargins.toFloat(),
            measuredWidth / 2f, (measuredHeight - mMargins).toFloat(),
            mPaint
        )
    }

    private fun drawHighHistogram(canvas: Canvas) {
        val cx = measuredWidth / 2f
        val cy = measuredHeight / 2f - mMarginCenter / 2f
        mPaint.style = Paint.Style.FILL
        mPaint.color = mLineColors[0]
        mPaint.alpha = (255 * mHistogramAlphas[0]).toInt()
        canvas.drawRoundRect(
            RectF(cx - mHistogramWidth / 2f, mHighHistogramY.toFloat(), cx + mHistogramWidth / 2f, cy),
            mHistogramWidth / 2f, mHistogramWidth / 2f,
            mPaint
        )
        mPaint.color = mTextColor
        mPaint.alpha = 255
        mPaint.style = Paint.Style.FILL
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.textSize = mHistogramTextSize.toFloat()
        mPaint.setShadowLayer(2f, 0f, 1f, mTextShadowColor)
        canvas.drawText(
            mHighHistogramValueStr!!, cx, mHighHistogramY - mPaint.fontMetrics.bottom - mTextMargin, mPaint
        )
        mPaint.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT)
    }

    private fun drawLowHistogram(canvas: Canvas) {
        val cx = measuredWidth / 2f
        val cy = measuredHeight / 2f + mMarginCenter / 2f
        mPaint.style = Paint.Style.FILL
        mPaint.color = mLineColors[1]
        mPaint.alpha = (255 * mHistogramAlphas[1]).toInt()
        canvas.drawRoundRect(
            RectF(cx - mHistogramWidth / 2f, cy, cx + mHistogramWidth / 2f, mLowHistogramY.toFloat()),
            mHistogramWidth / 2f, mHistogramWidth / 2f,
            mPaint
        )
        mPaint.color = mTextColor
        mPaint.alpha = 255
        mPaint.style = Paint.Style.FILL
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.textSize = mHistogramTextSize.toFloat()
        mPaint.setShadowLayer(2f, 0f, 1f, mTextShadowColor)
        canvas.drawText(
            mLowHistogramValueStr!!, cx, mLowHistogramY - mPaint.fontMetrics.top + mTextMargin, mPaint
        )
        mPaint.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT)
    }

    fun setData(
        highHistogramValues: Float?,
        lowHistogramValues: Float?,
        highHistogramValueStr: String?,
        lowHistogramValueStr: String?,
        highestHistogramValue: Float?
    ) {
        mHighHistogramValue = highHistogramValues
        mLowHistogramValue = lowHistogramValues
        mHighHistogramValueStr = highHistogramValueStr
        mLowHistogramValueStr = lowHistogramValueStr
        mHighestHistogramValue = highestHistogramValue
        invalidate()
    }

    fun setLineColors(
        @ColorInt colorHigh: Int,
        @ColorInt colorLow: Int,
        @ColorInt colorSubLine: Int
    ) {
        mLineColors[0] = colorHigh
        mLineColors[1] = colorLow
        mLineColors[2] = colorSubLine
        invalidate()
    }

    fun setTextColors(@ColorInt textColor: Int) {
        mTextColor = textColor
        mTextShadowColor = Color.argb((255 * 0.2).toInt(), 0, 0, 0)
        invalidate()
    }

    fun setHistogramAlphas(highAlpha: Float, lowAlpha: Float) {
        mHistogramAlphas = floatArrayOf(highAlpha, lowAlpha)
    }

    private fun computeCoordinates() {
        val canvasHeight = (measuredHeight - mMargins * 2 - mMarginCenter) / 2f
        val cy = measuredHeight / 2f
        if (mHighestHistogramValue != null) {
            if (mHighHistogramValue != null) {
                mHighHistogramY = (cy - mMarginCenter / 2f - canvasHeight * mHighHistogramValue!! / mHighestHistogramValue!!).toInt()
            }
            if (mLowHistogramValue != null) {
                mLowHistogramY = (cy + mMarginCenter / 2f + canvasHeight * mLowHistogramValue!! / mHighestHistogramValue!!).toInt()
            }
        }
    }

    companion object {
        private const val MARGIN_DIP = 24f
        private const val MARGIN_CENTER_DIP = 4f
        private const val HISTOGRAM_WIDTH_DIP = 8f
        private const val HISTOGRAM_TEXT_SIZE_DIP = 14f
        private const val CHART_LINE_SIZE_DIP = 1f
        private const val TEXT_MARGIN_DIP = 2f
    }
}
