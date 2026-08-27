package wangdaye.com.geometricweather.remoteviews.trend

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.options.unit.TemperatureUnit
import wangdaye.com.geometricweather.common.basic.models.weather.Temperature
import wangdaye.com.geometricweather.common.utils.DisplayUtils

class TrendLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private val mPaint = Paint()

    private var mHistoryTemps: IntArray? = null
    private var mHistoryTempYs: IntArray? = null

    private var mHighestTemp = 0
    private var mLowestTemp = 0
    private var mTemperatureUnit: TemperatureUnit = TemperatureUnit.C

    @ColorInt
    private var mLineColor = 0
    @ColorInt
    private var mTextColor = 0

    private var TREND_ITEM_HEIGHT = 0f
    private var BOTTOM_MARGIN = 0f
    private var TREND_MARGIN_TOP = 24f
    private var TREND_MARGIN_BOTTOM = 36f
    private var CHART_LINE_SIZE = 1f
    private var TEXT_SIZE = 12f
    private var MARGIN_TEXT = 2f

    init {
        setWillNotDraw(false)

        mPaint.isAntiAlias = true
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.typeface = DisplayUtils.getTypefaceFromTextAppearance(getContext(), R.style.subtitle_text)
        mPaint.textSize = TEXT_SIZE

        setColor(true)

        TREND_MARGIN_TOP = DisplayUtils.dpToPx(getContext(), TREND_MARGIN_TOP)
        TREND_MARGIN_BOTTOM = DisplayUtils.dpToPx(getContext(), TREND_MARGIN_BOTTOM)
        TEXT_SIZE = DisplayUtils.dpToPx(getContext(), TEXT_SIZE)
        CHART_LINE_SIZE = DisplayUtils.dpToPx(getContext(), CHART_LINE_SIZE)
        MARGIN_TEXT = DisplayUtils.dpToPx(getContext(), MARGIN_TEXT)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mHistoryTemps == null) {
            return
        }

        computeCoordinates()

        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = CHART_LINE_SIZE
        mPaint.color = mLineColor
        canvas.drawLine(
            0f, mHistoryTempYs!![0].toFloat(),
            measuredWidth.toFloat(), mHistoryTempYs!![0].toFloat(),
            mPaint
        )
        canvas.drawLine(
            0f, mHistoryTempYs!![1].toFloat(),
            measuredWidth.toFloat(), mHistoryTempYs!![1].toFloat(),
            mPaint
        )

        mPaint.style = Paint.Style.FILL
        mPaint.textSize = TEXT_SIZE
        mPaint.textAlign = Paint.Align.LEFT
        mPaint.color = mTextColor
        canvas.drawText(
            Temperature.getShortTemperature(getContext(), mHistoryTemps!![0], mTemperatureUnit).orEmpty(),
            2 * MARGIN_TEXT,
            mHistoryTempYs!![0] - mPaint.fontMetrics.bottom - MARGIN_TEXT,
            mPaint
        )
        canvas.drawText(
            Temperature.getShortTemperature(getContext(), mHistoryTemps!![1], mTemperatureUnit).orEmpty(),
            2 * MARGIN_TEXT,
            mHistoryTempYs!![1] - mPaint.fontMetrics.top + MARGIN_TEXT,
            mPaint
        )

        mPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText(
            getContext().getString(R.string.yesterday),
            measuredWidth - 2 * MARGIN_TEXT,
            mHistoryTempYs!![0] - mPaint.fontMetrics.bottom - MARGIN_TEXT,
            mPaint
        )
        canvas.drawText(
            getContext().getString(R.string.yesterday),
            measuredWidth - 2 * MARGIN_TEXT,
            mHistoryTempYs!![1] - mPaint.fontMetrics.top + MARGIN_TEXT,
            mPaint
        )
    }

    fun setColor(lightTheme: Boolean) {
        if (lightTheme) {
            mLineColor = ColorUtils.setAlphaComponent(Color.BLACK, (255 * 0.05).toInt())
            mTextColor = ContextCompat.getColor(getContext(), R.color.colorTextGrey2nd)
        } else {
            mLineColor = ColorUtils.setAlphaComponent(Color.WHITE, (255 * 0.1).toInt())
            mTextColor = ContextCompat.getColor(getContext(), R.color.colorTextGrey)
        }
    }

    fun setData(
        historyTemps: IntArray?,
        highestTemp: Int,
        lowestTemp: Int,
        unit: TemperatureUnit,
        daily: Boolean
    ) {
        mHistoryTemps = historyTemps
        mHighestTemp = highestTemp
        mLowestTemp = lowestTemp
        mTemperatureUnit = unit
        if (daily) {
            TREND_ITEM_HEIGHT = DisplayUtils.dpToPx(
                getContext(), WidgetItemView.TREND_VIEW_HEIGHT_DIP_2X.toFloat()
            )
            BOTTOM_MARGIN = DisplayUtils.dpToPx(
                getContext(),
                (
                    WidgetItemView.ICON_SIZE_DIP +
                        WidgetItemView.ICON_MARGIN_DIP +
                        WidgetItemView.MARGIN_VERTICAL_DIP
                    ).toFloat()
            )
        } else {
            TREND_ITEM_HEIGHT = DisplayUtils.dpToPx(
                getContext(), WidgetItemView.TREND_VIEW_HEIGHT_DIP_1X.toFloat()
            )
            BOTTOM_MARGIN = DisplayUtils.dpToPx(
                getContext(), WidgetItemView.MARGIN_VERTICAL_DIP.toFloat()
            )
        }
        invalidate()
    }

    private fun computeCoordinates() {
        mHistoryTempYs = intArrayOf(
            computeSingleCoordinate(mHistoryTemps!![0].toFloat(), mHighestTemp.toFloat(), mLowestTemp.toFloat()),
            computeSingleCoordinate(mHistoryTemps!![1].toFloat(), mHighestTemp.toFloat(), mLowestTemp.toFloat())
        )
    }

    private fun computeSingleCoordinate(value: Float, max: Float, min: Float): Int {
        val canvasHeight = TREND_ITEM_HEIGHT - TREND_MARGIN_TOP - TREND_MARGIN_BOTTOM
        return (
            measuredHeight - BOTTOM_MARGIN - TREND_MARGIN_BOTTOM
                - canvasHeight * (value - min) / (max - min)
            ).toInt()
    }
}
