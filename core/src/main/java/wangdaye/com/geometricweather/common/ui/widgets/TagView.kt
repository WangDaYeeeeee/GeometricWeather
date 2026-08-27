package wangdaye.com.geometricweather.common.ui.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import wangdaye.com.geometricweather.core.R

class TagView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private val mOutline = RectF()
    private val mPaint = Paint()

    var isChecked: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    @ColorInt
    var checkedBackgroundColor: Int = Color.WHITE
        set(value) {
            field = value
            invalidate()
        }

    @ColorInt
    var uncheckedBackgroundColor: Int = Color.LTGRAY
        set(value) {
            field = value
            invalidate()
        }

    init {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL

        val a = context.obtainStyledAttributes(attrs, R.styleable.TagView, defStyleAttr, 0)
        isChecked = a.getBoolean(R.styleable.TagView_checked, false)
        checkedBackgroundColor = a.getColor(R.styleable.TagView_checked_background_color, Color.WHITE)
        uncheckedBackgroundColor = a.getColor(R.styleable.TagView_unchecked_background_color, Color.LTGRAY)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        mOutline.set(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())

        clipToOutline = true
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, viewOutline: Outline) {
                viewOutline.setRoundRect(
                    mOutline.left.toInt(),
                    mOutline.top.toInt(),
                    mOutline.right.toInt(),
                    mOutline.bottom.toInt(),
                    mOutline.height() / 2
                )
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        mPaint.color = if (isChecked) checkedBackgroundColor else uncheckedBackgroundColor
        canvas.drawRoundRect(
            mOutline, mOutline.height() / 2, mOutline.height() / 2, mPaint
        )
        super.onDraw(canvas)
    }
}
