package wangdaye.com.geometricweather.common.ui.widgets.trend.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.Size
import androidx.core.graphics.ColorUtils
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.ui.widgets.DayNightShaderWrapper
import wangdaye.com.geometricweather.common.utils.DisplayUtils

class PolylineAndHistogramView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbsChartItemView(context, attrs, defStyleAttr) {

    private lateinit var mPaint: Paint
    private lateinit var mPath: Path
    private lateinit var mShaderWrapper: DayNightShaderWrapper

    @Size(3)
    private var mHighPolylineValues: Array<Float?>? = arrayOfNulls(3)
    @Size(3)
    private var mLowPolylineValues: Array<Float?>? = arrayOfNulls(3)
    private var mHighPolylineValueStr: String? = null
    private var mLowPolylineValueStr: String? = null
    private var mHighestPolylineValue: Float? = null
    private var mLowestPolylineValue: Float? = null

    private var mHistogramValue: Float? = null
    private var mHistogramValueStr: String? = null
    private var mHighestHistogramValue: Float? = null
    private var mLowestHistogramValue: Float? = null

    private val mHighPolylineY = IntArray(3)
    private val mLowPolylineY = IntArray(3)
    private var mHistogramY = 0

    private var mMarginTop = 0
    private var mMarginBottom = 0
    private var mPolylineWidth = 0
    private var mPolylineTextSize = 0
    private var mHistogramWidth = 0
    private var mHistogramTextSize = 0
    private var mChartLineWith = 0
    private var mTextMargin = 0

    private lateinit var mLineColors: IntArray
    private lateinit var mShadowColors: IntArray
    private var mHighTextColor = 0
    private var mLowTextColor = 0
    private var mTextShadowColor = 0
    private var mHistogramTextColor = 0

    private var mHistogramAlpha = 0f

    init {
        initialize()
    }

    private fun initialize() {
        mLineColors = intArrayOf(Color.BLACK, Color.DKGRAY, Color.LTGRAY)
        mShadowColors = intArrayOf(Color.BLACK, Color.WHITE)

        setTextColors(Color.BLACK, Color.DKGRAY, Color.GRAY)
        setHistogramAlpha(0.33f)

        mMarginTop = DisplayUtils.dpToPx(context, MARGIN_TOP_DIP).toInt()
        mMarginBottom = DisplayUtils.dpToPx(context, MARGIN_BOTTOM_DIP).toInt()
        mPolylineTextSize = DisplayUtils.dpToPx(context, POLYLINE_TEXT_SIZE_DIP).toInt()
        mHistogramTextSize = DisplayUtils.dpToPx(context, HISTOGRAM_TEXT_SIZE_DIP).toInt()
        mPolylineWidth = DisplayUtils.dpToPx(context, POLYLINE_SIZE_DIP).toInt()
        mHistogramWidth = DisplayUtils.dpToPx(context, HISTOGRAM_WIDTH_DIP).toInt()
        mChartLineWith = DisplayUtils.dpToPx(context, CHART_LINE_SIZE_DIP).toInt()
        mTextMargin = DisplayUtils.dpToPx(context, TEXT_MARGIN_DIP).toInt()

        mPaint = Paint()
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.isAntiAlias = true
        mPaint.isFilterBitmap = true
        mPaint.typeface = DisplayUtils.getTypefaceFromTextAppearance(context, R.style.title_text)

        mPath = Path()
        mShaderWrapper = DayNightShaderWrapper(measuredWidth, measuredHeight)
        setShadowColors(Color.BLACK, Color.GRAY, true)
    }

    override fun getMarginTop(): Int = mMarginTop

    override fun getMarginBottom(): Int = mMarginBottom

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        ensureShader(mShaderWrapper.isLightTheme)
        computeCoordinates()

        drawTimeLine(canvas)

        val histogramValue = mHistogramValue
        if (histogramValue != null &&
            (histogramValue != 0f || (mHighestPolylineValue == null && mLowestPolylineValue == null)) &&
            mHistogramValueStr != null &&
            mHighestHistogramValue != null &&
            mLowestHistogramValue != null
        ) {
            drawHistogram(canvas)
        }
        if (mHighestPolylineValue != null && mLowestPolylineValue != null) {
            if (mHighPolylineValues != null && mHighPolylineValueStr != null) {
                drawHighPolyLine(canvas)
            }
            if (mLowPolylineValues != null && mLowPolylineValueStr != null) {
                drawLowPolyline(canvas)
            }
        }
    }

    private fun drawTimeLine(canvas: Canvas) {
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mChartLineWith.toFloat()
        mPaint.color = mLineColors[2]

        canvas.drawLine(
            measuredWidth / 2f, mMarginTop.toFloat(),
            measuredWidth / 2f, (measuredHeight - mMarginBottom).toFloat(),
            mPaint
        )
    }

    private fun drawHighPolyLine(canvas: Canvas) {
        val highPolylineValues = mHighPolylineValues ?: return
        val highPolylineValueStr = mHighPolylineValueStr ?: return
        if (highPolylineValues[0] != null && highPolylineValues[2] != null) {
            mPaint.color = Color.BLACK
            mPaint.shader = mShaderWrapper.shader
            mPaint.style = Paint.Style.FILL

            mPath.reset()
            mPath.moveTo(getRTLCompactX(0f), mHighPolylineY[0].toFloat())
            mPath.lineTo(getRTLCompactX((measuredWidth / 2.0).toFloat()), mHighPolylineY[1].toFloat())
            mPath.lineTo(getRTLCompactX(measuredWidth.toFloat()), mHighPolylineY[2].toFloat())
            mPath.lineTo(getRTLCompactX(measuredWidth.toFloat()), (measuredHeight - mMarginBottom).toFloat())
            mPath.lineTo(getRTLCompactX(0f), (measuredHeight - mMarginBottom).toFloat())
            mPath.close()
            canvas.drawPath(mPath, mPaint)

            mPaint.shader = null
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = mPolylineWidth.toFloat()
            mPaint.color = mLineColors[0]

            mPath.reset()
            mPath.moveTo(getRTLCompactX(0f), mHighPolylineY[0].toFloat())
            mPath.lineTo(getRTLCompactX((measuredWidth / 2.0).toFloat()), mHighPolylineY[1].toFloat())
            mPath.lineTo(getRTLCompactX(measuredWidth.toFloat()), mHighPolylineY[2].toFloat())
            canvas.drawPath(mPath, mPaint)
        } else if (highPolylineValues[0] == null) {
            mPaint.color = Color.BLACK
            mPaint.shader = mShaderWrapper.shader
            mPaint.style = Paint.Style.FILL

            mPath.reset()
            mPath.moveTo(getRTLCompactX((measuredWidth / 2.0).toFloat()), mHighPolylineY[1].toFloat())
            mPath.lineTo(getRTLCompactX(measuredWidth.toFloat()), mHighPolylineY[2].toFloat())
            mPath.lineTo(getRTLCompactX(measuredWidth.toFloat()), (measuredHeight - mMarginBottom).toFloat())
            mPath.lineTo(
                getRTLCompactX((measuredWidth / 2.0).toFloat()),
                (measuredHeight - mMarginBottom).toFloat()
            )
            mPath.close()
            canvas.drawPath(mPath, mPaint)

            mPaint.shader = null
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = mPolylineWidth.toFloat()
            mPaint.color = mLineColors[0]

            mPath.reset()
            mPath.moveTo(getRTLCompactX((measuredWidth / 2.0).toFloat()), mHighPolylineY[1].toFloat())
            mPath.lineTo(getRTLCompactX(measuredWidth.toFloat()), mHighPolylineY[2].toFloat())
            canvas.drawPath(mPath, mPaint)
        } else {
            mPaint.color = Color.BLACK
            mPaint.shader = mShaderWrapper.shader
            mPaint.style = Paint.Style.FILL

            mPath.reset()
            mPath.moveTo(getRTLCompactX(0f), mHighPolylineY[0].toFloat())
            mPath.lineTo(getRTLCompactX((measuredWidth / 2.0).toFloat()), mHighPolylineY[1].toFloat())
            mPath.lineTo(
                getRTLCompactX((measuredWidth / 2.0).toFloat()),
                (measuredHeight - mMarginBottom).toFloat()
            )
            mPath.lineTo(getRTLCompactX(0f), (measuredHeight - mMarginBottom).toFloat())
            mPath.close()
            canvas.drawPath(mPath, mPaint)

            mPaint.shader = null
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = mPolylineWidth.toFloat()
            mPaint.color = mLineColors[0]

            mPath.reset()
            mPath.moveTo(getRTLCompactX(0f), mHighPolylineY[0].toFloat())
            mPath.lineTo(getRTLCompactX((measuredWidth / 2.0).toFloat()), mHighPolylineY[1].toFloat())
            canvas.drawPath(mPath, mPaint)
        }

        mPaint.color = mHighTextColor
        mPaint.style = Paint.Style.FILL
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.textSize = mPolylineTextSize.toFloat()
        mPaint.setShadowLayer(2f, 0f, 1f, mTextShadowColor)
        canvas.drawText(
            highPolylineValueStr,
            getRTLCompactX((measuredWidth / 2.0).toFloat()),
            mHighPolylineY[1] - mPaint.fontMetrics.bottom - mTextMargin,
            mPaint
        )
        mPaint.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT)
    }

    private fun drawLowPolyline(canvas: Canvas) {
        val lowPolylineValues = mLowPolylineValues ?: return
        val lowPolylineValueStr = mLowPolylineValueStr ?: return
        if (lowPolylineValues[0] != null && lowPolylineValues[2] != null) {
            mPaint.shader = null
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = mPolylineWidth.toFloat()
            mPaint.color = mLineColors[1]

            mPath.reset()
            mPath.moveTo(getRTLCompactX(0f), mLowPolylineY[0].toFloat())
            mPath.lineTo(getRTLCompactX((measuredWidth / 2.0).toFloat()), mLowPolylineY[1].toFloat())
            mPath.lineTo(getRTLCompactX(measuredWidth.toFloat()), mLowPolylineY[2].toFloat())
            canvas.drawPath(mPath, mPaint)
        } else if (lowPolylineValues[0] == null) {
            mPaint.shader = null
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = mPolylineWidth.toFloat()
            mPaint.color = mLineColors[1]

            mPath.reset()
            mPath.moveTo(getRTLCompactX((measuredWidth / 2.0).toFloat()), mLowPolylineY[1].toFloat())
            mPath.lineTo(getRTLCompactX(measuredWidth.toFloat()), mLowPolylineY[2].toFloat())
            canvas.drawPath(mPath, mPaint)
        } else {
            mPaint.shader = null
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = mPolylineWidth.toFloat()
            mPaint.color = mLineColors[1]

            mPath.reset()
            mPath.moveTo(getRTLCompactX(0f), mLowPolylineY[0].toFloat())
            mPath.lineTo(getRTLCompactX((measuredWidth / 2.0).toFloat()), mLowPolylineY[1].toFloat())
            canvas.drawPath(mPath, mPaint)
        }

        mPaint.color = mLowTextColor
        mPaint.style = Paint.Style.FILL
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.textSize = mPolylineTextSize.toFloat()
        mPaint.setShadowLayer(2f, 0f, 1f, mTextShadowColor)
        canvas.drawText(
            lowPolylineValueStr,
            getRTLCompactX((measuredWidth / 2.0).toFloat()),
            mLowPolylineY[1] - mPaint.fontMetrics.top + mTextMargin,
            mPaint
        )
        mPaint.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT)
    }

    private fun drawHistogram(canvas: Canvas) {
        val histogramValueStr = mHistogramValueStr ?: return

        mPaint.color = mLineColors[1]
        mPaint.alpha = (255 * mHistogramAlpha).toInt()
        mPaint.style = Paint.Style.FILL

        canvas.drawRoundRect(
            RectF(
                (measuredWidth / 2.0 - mHistogramWidth).toFloat(),
                mHistogramY.toFloat(),
                (measuredWidth / 2.0 + mHistogramWidth).toFloat(),
                (measuredHeight - mMarginBottom).toFloat()
            ),
            mHistogramWidth.toFloat(), mHistogramWidth.toFloat(),
            mPaint
        )

        mPaint.color = mHistogramTextColor
        mPaint.alpha = 255
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.textSize = mHistogramTextSize.toFloat()
        canvas.drawText(
            histogramValueStr,
            (measuredWidth / 2.0).toFloat(),
            (
                measuredHeight -
                    mMarginBottom -
                    mPaint.fontMetrics.top +
                    2.0 * mTextMargin +
                    mPolylineTextSize
                ).toFloat(),
            mPaint
        )

        mPaint.alpha = 255
    }

    fun setData(
        @Size(3) highPolylineValues: Array<Float?>?,
        @Size(3) lowPolylineValues: Array<Float?>?,
        highPolylineValueStr: String?,
        lowPolylineValueStr: String?,
        highestPolylineValue: Float?,
        lowestPolylineValue: Float?,
        histogramValue: Float?,
        histogramValueStr: String?,
        highestHistogramValue: Float?,
        lowestHistogramValue: Float?
    ) {
        mHighPolylineValues = highPolylineValues
        mLowPolylineValues = lowPolylineValues
        mHighPolylineValueStr = highPolylineValueStr
        mLowPolylineValueStr = lowPolylineValueStr
        mHighestPolylineValue = highestPolylineValue
        mLowestPolylineValue = lowestPolylineValue
        mHistogramValue = histogramValue
        mHistogramValueStr = histogramValueStr
        mHighestHistogramValue = highestHistogramValue
        mLowestHistogramValue = lowestHistogramValue
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

    fun setShadowColors(@ColorInt colorHigh: Int, @ColorInt colorLow: Int, lightTheme: Boolean) {
        mShadowColors[0] = if (lightTheme) {
            ColorUtils.setAlphaComponent(colorHigh, (255 * SHADOW_ALPHA_FACTOR_LIGHT).toInt())
        } else {
            ColorUtils.setAlphaComponent(colorLow, (255 * SHADOW_ALPHA_FACTOR_DARK).toInt())
        }
        mShadowColors[1] = Color.TRANSPARENT

        ensureShader(lightTheme)
        invalidate()
    }

    fun setTextColors(
        @ColorInt highTextColor: Int,
        @ColorInt lowTextColor: Int,
        @ColorInt histogramTextColor: Int
    ) {
        mHighTextColor = highTextColor
        mLowTextColor = lowTextColor
        mTextShadowColor = Color.argb((255 * 0.2).toInt(), 0, 0, 0)
        mHistogramTextColor = histogramTextColor
        invalidate()
    }

    fun setHistogramAlpha(@FloatRange(from = 0.0, to = 1.0) histogramAlpha: Float) {
        mHistogramAlpha = histogramAlpha
        invalidate()
    }

    private fun ensureShader(lightTheme: Boolean) {
        if (mShaderWrapper.isDifferent(
                measuredWidth, measuredHeight, lightTheme, mShadowColors
            )
        ) {
            mShaderWrapper.setShader(
                LinearGradient(
                    0f, mMarginTop.toFloat(),
                    0f, (measuredHeight - mMarginBottom).toFloat(),
                    mShadowColors[0], mShadowColors[1],
                    Shader.TileMode.CLAMP
                ),
                measuredWidth, measuredHeight,
                lightTheme,
                mShadowColors
            )
        }
    }

    private fun computeCoordinates() {
        val canvasHeight = (measuredHeight - mMarginTop - mMarginBottom).toFloat()
        val highestPolylineValue = mHighestPolylineValue
        val lowestPolylineValue = mLowestPolylineValue
        if (highestPolylineValue != null && lowestPolylineValue != null) {
            val highPolylineValues = mHighPolylineValues
            if (highPolylineValues != null) {
                for (i in highPolylineValues.indices) {
                    val value = highPolylineValues[i]
                    mHighPolylineY[i] = if (value == null) {
                        0
                    } else {
                        computeSingleCoordinate(
                            canvasHeight, value, highestPolylineValue, lowestPolylineValue
                        )
                    }
                }
            }
            val lowPolylineValues = mLowPolylineValues
            if (lowPolylineValues != null) {
                for (i in lowPolylineValues.indices) {
                    val value = lowPolylineValues[i]
                    mLowPolylineY[i] = if (value == null) {
                        0
                    } else {
                        computeSingleCoordinate(
                            canvasHeight, value, highestPolylineValue, lowestPolylineValue
                        )
                    }
                }
            }
        }

        val histogramValue = mHistogramValue
        val highestHistogramValue = mHighestHistogramValue
        val lowestHistogramValue = mLowestHistogramValue
        if (histogramValue != null && highestHistogramValue != null && lowestHistogramValue != null) {
            mHistogramY = computeSingleCoordinate(
                canvasHeight, histogramValue, highestHistogramValue, lowestHistogramValue
            )
        }
    }

    private fun computeSingleCoordinate(canvasHeight: Float, value: Float, max: Float, min: Float): Int {
        return (
            measuredHeight -
                mMarginBottom -
                canvasHeight * (value - min) / (max - min)
            ).toInt()
    }

    private fun getRTLCompactX(x: Float): Float {
        return if (layoutDirection == LAYOUT_DIRECTION_RTL) (measuredWidth - x) else x
    }

    companion object {
        private const val MARGIN_TOP_DIP = 24f
        private const val MARGIN_BOTTOM_DIP = 36f
        private const val POLYLINE_SIZE_DIP = 5f
        private const val POLYLINE_TEXT_SIZE_DIP = 14f
        private const val HISTOGRAM_WIDTH_DIP = 4.5f
        private const val HISTOGRAM_TEXT_SIZE_DIP = 12f
        private const val CHART_LINE_SIZE_DIP = 1f
        private const val TEXT_MARGIN_DIP = 2f

        private const val SHADOW_ALPHA_FACTOR_LIGHT = 0.15f
        private const val SHADOW_ALPHA_FACTOR_DARK = 0.3f
    }
}
