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
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.ui.widgets.trend.TrendRecyclerView
import wangdaye.com.geometricweather.common.ui.widgets.trend.chart.AbsChartItemView
import wangdaye.com.geometricweather.common.utils.DisplayUtils

class DailyTrendItemView : AbsTrendItemView {

    private var mChartItem: AbsChartItemView? = null
    private lateinit var mWeekTextPaint: Paint
    private lateinit var mDateTextPaint: Paint
    private var mClickListener: OnClickListener? = null
    private var mWeekText: String? = null
    private var mDateText: String? = null
    private var mDayIconDrawable: Drawable? = null
    private var mNightIconDrawable: Drawable? = null
    @ColorInt
    private var mContentColor = 0
    @ColorInt
    private var mSubTitleColor = 0
    private var mWeekTextBaseLine = 0f
    private var mDateTextBaseLine = 0f
    private var mDayIconLeft = 0f
    private var mDayIconTop = 0f
    private var mTrendViewTop = 0f
    private var mNightIconLeft = 0f
    private var mNightIconTop = 0f
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
        mWeekTextPaint = Paint()
        mWeekTextPaint.isAntiAlias = true
        mWeekTextPaint.textAlign = Paint.Align.CENTER
        mWeekTextPaint.typeface = DisplayUtils.getTypefaceFromTextAppearance(context, R.style.title_text)
        mWeekTextPaint.textSize = context.resources.getDimensionPixelSize(R.dimen.title_text_size).toFloat()
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
        var fontMetrics = mWeekTextPaint.fontMetrics
        y += textMargin
        mWeekTextBaseLine = y - fontMetrics.top
        y += fontMetrics.bottom - fontMetrics.top
        y += textMargin
        fontMetrics = mDateTextPaint.fontMetrics
        y += textMargin
        mDateTextBaseLine = y - fontMetrics.top
        y += fontMetrics.bottom - fontMetrics.top
        y += textMargin
        if (mDayIconDrawable != null) {
            y += iconMargin
            mDayIconLeft = (width - mIconSize) / 2f
            mDayIconTop = y
            y += mIconSize
            y += iconMargin
        }
        var consumedHeight = y
        val marginBottom = DisplayUtils.dpToPx(context, TrendRecyclerView.ITEM_MARGIN_BOTTOM_DIP.toFloat())
        consumedHeight += marginBottom
        if (mNightIconDrawable != null) {
            mNightIconLeft = (width - mIconSize) / 2f
            mNightIconTop = height - marginBottom - iconMargin - mIconSize
            consumedHeight += mIconSize + 2 * iconMargin
        }
        if (mChartItem != null) {
            mChartItem!!.measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec((height - consumedHeight).toInt(), MeasureSpec.EXACTLY)
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
        if (mWeekText != null) {
            mWeekTextPaint.color = mContentColor
            canvas.drawText(mWeekText!!, measuredWidth / 2f, mWeekTextBaseLine, mWeekTextPaint)
        }
        if (mDateText != null) {
            mDateTextPaint.color = mSubTitleColor
            canvas.drawText(mDateText!!, measuredWidth / 2f, mDateTextBaseLine, mDateTextPaint)
        }
        if (mDayIconDrawable != null) {
            val restoreCount = canvas.save()
            canvas.translate(mDayIconLeft, mDayIconTop)
            mDayIconDrawable!!.draw(canvas)
            canvas.restoreToCount(restoreCount)
        }
        if (mNightIconDrawable != null) {
            val restoreCount = canvas.save()
            canvas.translate(mNightIconLeft, mNightIconTop)
            mNightIconDrawable!!.draw(canvas)
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

    fun setWeekText(weekText: String?) { mWeekText = weekText; invalidate() }
    fun setDateText(dateText: String?) { mDateText = dateText; invalidate() }
    fun setTextColor(@ColorInt contentColor: Int, @ColorInt subTitleColor: Int) {
        mContentColor = contentColor; mSubTitleColor = subTitleColor; invalidate()
    }

    fun setDayIconDrawable(d: Drawable?) {
        val nullDrawable = mDayIconDrawable == null
        mDayIconDrawable = d
        if (d != null) {
            d.setVisible(true, true)
            d.callback = this
            d.setBounds(0, 0, mIconSize, mIconSize)
        }
        if (nullDrawable != (d == null)) requestLayout() else invalidate()
    }

    fun setNightIconDrawable(d: Drawable?) {
        val nullDrawable = mNightIconDrawable == null
        mNightIconDrawable = d
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
