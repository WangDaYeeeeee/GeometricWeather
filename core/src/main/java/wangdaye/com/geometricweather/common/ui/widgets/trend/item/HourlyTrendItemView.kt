package wangdaye.com.geometricweather.common.ui.widgets.trend.item

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.ui.widgets.trend.TrendRecyclerView
import wangdaye.com.geometricweather.common.ui.widgets.trend.chart.AbsChartItemView
import wangdaye.com.geometricweather.common.utils.DisplayUtils

class HourlyTrendItemView : AbsTrendItemView {

    private var mChartItem: AbsChartItemView? = null
    private lateinit var mHourTextPaint: Paint
    private lateinit var mDateTextPaint: Paint
    private var mClickListener: OnClickListener? = null
    private var mHourText: String? = null
    private var mDayText: String? = null
    private var mIconDrawable: Drawable? = null
    @ColorInt
    private var mContentColor = 0
    @ColorInt
    private var mSubTitleColor = 0
    private var mDayTextBaseLine = 0f
    private var mHourTextBaseLine = 0f
    private var mIconLeft = 0f
    private var mIconTop = 0f
    private var mTrendViewTop = 0f
    private var mIconSize = 0
    private var mChartTop = 0
    private var mChartBottom = 0

    constructor(context: Context) : super(context) { initialize() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initialize() }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initialize() }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) { initialize() }

    private fun initialize() {
        setWillNotDraw(false)
        mHourTextPaint = Paint()
        mHourTextPaint.isAntiAlias = true
        mHourTextPaint.textAlign = Paint.Align.CENTER
        mHourTextPaint.typeface = DisplayUtils.getTypefaceFromTextAppearance(context, R.style.title_text)
        mHourTextPaint.textSize = context.resources.getDimensionPixelSize(R.dimen.title_text_size).toFloat()
        mDateTextPaint = Paint()
        mDateTextPaint.isAntiAlias = true
        mDateTextPaint.textAlign = Paint.Align.CENTER
        mDateTextPaint.typeface = DisplayUtils.getTypefaceFromTextAppearance(context, R.style.content_text)
        mDateTextPaint.textSize = context.resources.getDimensionPixelSize(R.dimen.content_text_size).toFloat()
        setTextColor(Color.BLACK, Color.GRAY)
        mIconSize = DisplayUtils.dpToPx(context, ICON_SIZE_DIP.toFloat()).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        var y = 0f
        val textMargin = DisplayUtils.dpToPx(context, TEXT_MARGIN_DIP.toFloat())
        val iconMargin = DisplayUtils.dpToPx(context, ICON_MARGIN_DIP.toFloat())
        var fontMetrics = mHourTextPaint.fontMetrics
        y += textMargin
        mHourTextBaseLine = y - fontMetrics.top
        y += fontMetrics.bottom - fontMetrics.top
        y += textMargin
        fontMetrics = mDateTextPaint.fontMetrics
        y += textMargin
        mDayTextBaseLine = y - fontMetrics.top
        y += fontMetrics.bottom - fontMetrics.top
        y += textMargin
        if (mIconDrawable != null) {
            y += iconMargin
            mIconLeft = (width - mIconSize) / 2f
            mIconTop = y
            y += mIconSize
            y += iconMargin
        }
        val marginBottom = DisplayUtils.dpToPx(context, TrendRecyclerView.ITEM_MARGIN_BOTTOM_DIP.toFloat())
        if (mChartItem != null) {
            mChartItem!!.measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec((height - marginBottom - y).toInt(), MeasureSpec.EXACTLY)
            )
        }
        mTrendViewTop = y
        mChartTop = (mTrendViewTop + mChartItem!!.getMarginTop()).toInt()
        mChartBottom = (mTrendViewTop + mChartItem!!.measuredHeight - mChartItem!!.getMarginBottom()).toInt()
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (mChartItem != null) {
            mChartItem!!.layout(0, mTrendViewTop.toInt(), mChartItem!!.measuredWidth, (mTrendViewTop + mChartItem!!.measuredHeight).toInt())
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (mHourText != null) {
            mHourTextPaint.color = mContentColor
            canvas.drawText(mHourText!!, measuredWidth / 2f, mHourTextBaseLine, mHourTextPaint)
        }
        if (mDayText != null) {
            mDateTextPaint.color = mSubTitleColor
            canvas.drawText(mDayText!!, measuredWidth / 2f, mDayTextBaseLine, mDateTextPaint)
        }
        if (mIconDrawable != null) {
            val restoreCount = canvas.save()
            canvas.translate(mIconLeft, mIconTop)
            mIconDrawable!!.draw(canvas)
            canvas.restoreToCount(restoreCount)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            mClickListener?.onClick(this)
        }
        return super.onTouchEvent(event)
    }

    fun setDayText(dayText: String?) { mDayText = dayText; invalidate() }
    fun setHourText(hourText: String?) { mHourText = hourText; invalidate() }
    fun setTextColor(@ColorInt contentColor: Int, @ColorInt subTitleColor: Int) {
        mContentColor = contentColor; mSubTitleColor = subTitleColor; invalidate()
    }

    fun setIconDrawable(d: Drawable?) {
        val nullDrawable = mIconDrawable == null
        mIconDrawable = d
        if (d != null) {
            d.setVisible(true, true)
            d.callback = this
            d.setBounds(0, 0, mIconSize, mIconSize)
        }
        if (nullDrawable != (d == null)) requestLayout() else invalidate()
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mClickListener = l
        super.setOnClickListener { }
    }

    override fun setChartItemView(t: AbsChartItemView) {
        mChartItem = t
        removeAllViews()
        addView(mChartItem)
        requestLayout()
    }

    override fun getChartItemView(): AbsChartItemView? = mChartItem
    override fun getChartTop(): Int = mChartTop
    override fun getChartBottom(): Int = mChartBottom

    companion object {
        private const val ICON_SIZE_DIP = 32
        private const val TEXT_MARGIN_DIP = 2
        private const val ICON_MARGIN_DIP = 8
    }
}
