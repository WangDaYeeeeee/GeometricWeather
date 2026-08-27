package wangdaye.com.geometricweather.remoteviews.trend

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.ui.widgets.trend.chart.PolylineAndHistogramView
import wangdaye.com.geometricweather.common.utils.DisplayUtils

class WidgetItemView : ViewGroup {

    private lateinit var mTrend: PolylineAndHistogramView
    private lateinit var mTitleTextPaint: Paint
    private lateinit var mSubtitleTextPaint: Paint

    private var mWidth = 0f

    private var mTitleText: String? = null
    private var mSubtitleText: String? = null

    private var mTopIconDrawable: Drawable? = null
    private var mBottomIconDrawable: Drawable? = null

    @ColorInt
    private var mContentColor = 0
    @ColorInt
    private var mSubtitleColor = 0

    private var mTitleTextBaseLine = 0f
    private var mSubtitleTextBaseLine = 0f
    private var mTopIconLeft = 0f
    private var mTopIconTop = 0f
    private var mTrendViewTop = 0f
    private var mBottomIconLeft = 0f
    private var mBottomIconTop = 0f
    private var mIconSize = 0

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        super(context, attrs, defStyleAttr) {
        initialize()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initialize()
    }

    private fun initialize() {
        setWillNotDraw(false)

        mTrend = PolylineAndHistogramView(context)
        addView(mTrend)

        mTitleTextPaint = Paint()
        mTitleTextPaint.isAntiAlias = true
        mTitleTextPaint.textAlign = Paint.Align.CENTER
        mTitleTextPaint.typeface =
            DisplayUtils.getTypefaceFromTextAppearance(context, R.style.title_text)
        mTitleTextPaint.textSize =
            context.resources.getDimensionPixelSize(R.dimen.title_text_size).toFloat()

        mSubtitleTextPaint = Paint()
        mSubtitleTextPaint.isAntiAlias = true
        mSubtitleTextPaint.textAlign = Paint.Align.CENTER
        mSubtitleTextPaint.typeface =
            DisplayUtils.getTypefaceFromTextAppearance(context, R.style.content_text)
        mSubtitleTextPaint.textSize =
            context.resources.getDimensionPixelSize(R.dimen.content_text_size).toFloat()

        setColor(true)

        mIconSize = DisplayUtils.dpToPx(context, ICON_SIZE_DIP.toFloat()).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = 0f

        val textMargin = DisplayUtils.dpToPx(context, TEXT_MARGIN_DIP.toFloat())
        val iconMargin = DisplayUtils.dpToPx(context, ICON_MARGIN_DIP.toFloat())

        if (mTitleText != null) {
            val fontMetrics = mTitleTextPaint.fontMetrics
            height += DisplayUtils.dpToPx(context, MARGIN_VERTICAL_DIP.toFloat())
            mTitleTextBaseLine = height - fontMetrics.top
            height += fontMetrics.bottom - fontMetrics.top
            height += textMargin
        }

        if (mSubtitleText != null) {
            val fontMetrics = mSubtitleTextPaint.fontMetrics
            height += textMargin
            mSubtitleTextBaseLine = height - fontMetrics.top
            height += fontMetrics.bottom - fontMetrics.top
            height += textMargin
        }

        if (mTopIconDrawable != null) {
            height += iconMargin
            mTopIconLeft = (mWidth - mIconSize) / 2f
            mTopIconTop = height
            height += mIconSize
            height += iconMargin
        }

        mTrendViewTop = height
        val trendViewHeight = if (mBottomIconDrawable == null) {
            DisplayUtils.dpToPx(context, TREND_VIEW_HEIGHT_DIP_1X.toFloat()).toInt()
        } else {
            DisplayUtils.dpToPx(context, TREND_VIEW_HEIGHT_DIP_2X.toFloat()).toInt()
        }
        mTrend.measure(
            MeasureSpec.makeMeasureSpec(mWidth.toInt(), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(trendViewHeight, MeasureSpec.EXACTLY)
        )
        height += mTrend.measuredHeight

        if (mBottomIconDrawable != null) {
            height += iconMargin
            mBottomIconLeft = (mWidth - mIconSize) / 2f
            mBottomIconTop = height
            height += mIconSize
        }

        height += DisplayUtils.dpToPx(context, MARGIN_VERTICAL_DIP.toFloat()).toInt()

        setMeasuredDimension(mWidth.toInt(), height.toInt())
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mTrend.layout(
            0,
            mTrendViewTop.toInt(),
            mTrend.measuredWidth,
            (mTrendViewTop + mTrend.measuredHeight).toInt()
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (mTitleText != null) {
            mTitleTextPaint.color = mContentColor
            canvas.drawText(mTitleText!!, measuredWidth / 2f, mTitleTextBaseLine, mTitleTextPaint)
        }

        if (mSubtitleText != null) {
            mSubtitleTextPaint.color = mSubtitleColor
            canvas.drawText(mSubtitleText!!, measuredWidth / 2f, mSubtitleTextBaseLine, mSubtitleTextPaint)
        }

        mTopIconDrawable?.let { d ->
            val restoreCount = canvas.save()
            canvas.translate(mTopIconLeft, mTopIconTop)
            d.draw(canvas)
            canvas.restoreToCount(restoreCount)
        }

        mBottomIconDrawable?.let { d ->
            val restoreCount = canvas.save()
            canvas.translate(mBottomIconLeft, mBottomIconTop)
            d.draw(canvas)
            canvas.restoreToCount(restoreCount)
        }
    }

    fun setColor(daytime: Boolean) {
        if (daytime) {
            mContentColor = ContextCompat.getColor(context, R.color.colorTextDark2nd)
            mSubtitleColor = ContextCompat.getColor(context, R.color.colorTextGrey2nd)
        } else {
            mContentColor = ContextCompat.getColor(context, R.color.colorTextLight2nd)
            mSubtitleColor = ContextCompat.getColor(context, R.color.colorTextGrey)
        }
    }

    fun setSize(width: Float) {
        mWidth = width
    }

    fun setTitleText(titleText: String?) {
        mTitleText = titleText
    }

    fun setSubtitleText(subtitleText: String?) {
        mSubtitleText = subtitleText
    }

    fun setTopIconDrawable(d: Drawable?) {
        mTopIconDrawable = d
        if (d != null) {
            d.setVisible(true, true)
            d.callback = this
            d.setBounds(0, 0, mIconSize, mIconSize)
        }
    }

    fun setBottomIconDrawable(d: Drawable?) {
        mBottomIconDrawable = d
        if (d != null) {
            d.setVisible(true, true)
            d.callback = this
            d.setBounds(0, 0, mIconSize, mIconSize)
        }
    }

    fun getTrendItemView(): PolylineAndHistogramView = mTrend

    fun getIconSize(): Int = mIconSize

    companion object {
        const val ICON_SIZE_DIP = 32
        const val TREND_VIEW_HEIGHT_DIP_1X = 96
        const val TREND_VIEW_HEIGHT_DIP_2X = 108
        const val TEXT_MARGIN_DIP = 2
        const val ICON_MARGIN_DIP = 4
        const val MARGIN_VERTICAL_DIP = 8
    }
}
