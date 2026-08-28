package wangdaye.com.geometricweather.common.ui.images.pixel

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt

class PixelSunDrawable : Drawable() {

    private val mPaint: Paint = Paint().apply { isAntiAlias = true }

    @ColorInt
    private val mColor: Int = Color.rgb(255, 215, 5)

    private var mAlpha = 1f
    private var mBounds: Rect = bounds

    private var mRadius = 0f
    private var mCX = 0f
    private var mCY = 0f

    init {
        ensurePosition(mBounds)
    }

    private fun ensurePosition(bounds: Rect) {
        val boundSize = Math.min(bounds.width(), bounds.height()).toFloat()
        mRadius = ((Math.sin(Math.PI / 4) * boundSize / 2 + boundSize / 2) / 2 - 2).toFloat()
        mCX = (1.0 * bounds.width() / 2 + bounds.left).toFloat()
        mCY = (1.0 * bounds.height() / 2 + bounds.top).toFloat()
    }

    override fun onBoundsChange(bounds: Rect) {
        mBounds = bounds
        ensurePosition(bounds)
    }

    override fun draw(canvas: Canvas) {
        mPaint.alpha = (mAlpha * 255).toInt()

        mPaint.color = mColor
        canvas.drawCircle(mCX, mCY, mRadius, mPaint)
    }

    override fun setAlpha(alpha: Int) {
        mAlpha = alpha.toFloat()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun getIntrinsicWidth(): Int {
        return mBounds.width()
    }

    override fun getIntrinsicHeight(): Int {
        return mBounds.height()
    }
}
