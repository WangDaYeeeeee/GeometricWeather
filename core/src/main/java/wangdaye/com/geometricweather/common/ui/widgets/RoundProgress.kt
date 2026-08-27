package wangdaye.com.geometricweather.common.ui.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import wangdaye.com.geometricweather.common.utils.DisplayUtils

class RoundProgress @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var mProgressPaint: Paint
    private val mBackgroundRectF = RectF()
    private val mProgressRectF = RectF()

    var progress: Float = 0f
        set(value) {
            field = if (value > max) max else value
            invalidate()
        }

    var max: Float = 100f
        set(value) {
            if (value > 0) {
                field = value
                invalidate()
            }
        }

    @ColorInt
    private var mProgressColor: Int = Color.BLACK

    @ColorInt
    private var mBackgroundColor: Int = Color.GRAY

    init {
        initPaint()
    }

    private fun initPaint() {
        mProgressPaint = Paint()
        mProgressPaint.isAntiAlias = true
        mProgressPaint.style = Paint.Style.FILL
        mProgressPaint.strokeCap = Paint.Cap.ROUND
    }

    fun setProgressColor(@ColorInt progressColor: Int) {
        mProgressColor = progressColor
        invalidate()
    }

    fun setProgressBackgroundColor(@ColorInt backgroundColor: Int) {
        mBackgroundColor = backgroundColor
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val padding = DisplayUtils.dpToPx(context, 2f).toInt()
        mBackgroundRectF.set(
            padding.toFloat(),
            padding.toFloat(),
            (measuredWidth - padding).toFloat(),
            (measuredHeight - padding).toFloat()
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val radius = mBackgroundRectF.height() / 2f
        mProgressPaint.color = mBackgroundColor
        canvas.drawRoundRect(mBackgroundRectF, radius, radius, mProgressPaint)

        mProgressRectF.set(
            mBackgroundRectF.left,
            mBackgroundRectF.top,
            mBackgroundRectF.left + mBackgroundRectF.width() * progress / max,
            mBackgroundRectF.bottom
        )
        mProgressPaint.color = mProgressColor
        if (mProgressRectF.width() < 2 * radius) {
            canvas.drawCircle(
                mProgressRectF.left + radius,
                mProgressRectF.top + radius,
                radius,
                mProgressPaint
            )
        } else {
            canvas.drawRoundRect(mProgressRectF, radius, radius, mProgressPaint)
        }
    }
}
