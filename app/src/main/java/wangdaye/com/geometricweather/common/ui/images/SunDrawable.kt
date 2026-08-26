package wangdaye.com.geometricweather.common.ui.images

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Size
import kotlin.math.min
import kotlin.math.sin

class SunDrawable : Drawable() {

    private val mPaint = Paint()
    @ColorInt
    private val mCoreColor = Color.rgb(254, 214, 117)
    @Size(2)
    @ColorInt
    private val mHaloColors = intArrayOf(
        Color.rgb(249, 183, 93),
        Color.rgb(252, 198, 101)
    )
    private var mAlpha = 1f
    private var mBoundsRect: Rect = bounds
    private var mRadius = 0f
    private var mCX = 0f
    private var mCY = 0f

    init {
        mPaint.isAntiAlias = true
        ensurePosition(mBoundsRect)
    }

    private fun ensurePosition(bounds: Rect) {
        val boundSize = min(bounds.width(), bounds.height()).toFloat()
        mRadius = (sin(Math.PI / 4) * boundSize / 2).toFloat() - 2
        mCX = (1.0 * bounds.width() / 2 + bounds.left).toFloat()
        mCY = (1.0 * bounds.height() / 2 + bounds.top).toFloat()
    }

    override fun onBoundsChange(bounds: Rect) {
        mBoundsRect = bounds
        ensurePosition(bounds)
    }

    override fun draw(canvas: Canvas) {
        mPaint.alpha = (mAlpha * 255).toInt()
        mPaint.color = mHaloColors[0]
        canvas.drawRect(mCX - mRadius, mCY - mRadius, mCX + mRadius, mCY + mRadius, mPaint)
        mPaint.color = mHaloColors[0]
        val restoreCount = canvas.save()
        canvas.rotate(45f, mCX, mCY)
        canvas.drawRect(mCX - mRadius, mCY - mRadius, mCX + mRadius, mCY + mRadius, mPaint)
        canvas.restoreToCount(restoreCount)
        mPaint.color = mCoreColor
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
        return mBoundsRect.width()
    }

    override fun getIntrinsicHeight(): Int {
        return mBoundsRect.height()
    }
}
