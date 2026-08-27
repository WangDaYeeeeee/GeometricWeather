package wangdaye.com.geometricweather.common.ui.widgets.astro

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import wangdaye.com.geometricweather.common.utils.DisplayUtils

class MoonPhaseView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var mPaint: Paint
    private var mForegroundRectF = RectF()
    private var mBackgroundRectF = RectF()

    private var mSurfaceAngle = 0f
    @ColorInt
    private var mLightColor = 0
    @ColorInt
    private var mDarkColor = 0
    @ColorInt
    private var mStrokeColor = 0

    private var LINE_WIDTH = 1f

    init {
        initialize()
        initPaint()
    }

    private fun initialize() {
        setColor(Color.WHITE, Color.BLACK, Color.GRAY)
        setSurfaceAngle(0f)
        mForegroundRectF = RectF()
        mBackgroundRectF = RectF()
        LINE_WIDTH = DisplayUtils.dpToPx(context, LINE_WIDTH.toInt().toFloat())
    }

    private fun initPaint() {
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.strokeCap = Paint.Cap.ROUND
    }

    fun setColor(
        @ColorInt lightColor: Int,
        @ColorInt darkColor: Int,
        @ColorInt strokeColor: Int
    ) {
        mLightColor = lightColor
        mDarkColor = darkColor
        mStrokeColor = strokeColor
    }

    fun setSurfaceAngle(surfaceAngle: Float) {
        mSurfaceAngle = surfaceAngle
        if (mSurfaceAngle >= 360) {
            mSurfaceAngle %= 360
        }
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        val padding = DisplayUtils.dpToPx(context, 4f).toInt()
        mBackgroundRectF.set(
            padding.toFloat(),
            padding.toFloat(),
            (measuredWidth - padding).toFloat(),
            (measuredHeight - padding).toFloat()
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mPaint.style = Paint.Style.FILL
        when {
            mSurfaceAngle == 0f -> drawDarkCircle(canvas)
            mSurfaceAngle < 90 -> {
                drawLightCircle(canvas)
                mPaint.color = mDarkColor
                canvas.drawArc(mBackgroundRectF, 90f, 180f, true, mPaint)
                val halfWidth = (mBackgroundRectF.width() / 2 * Math.cos(Math.toRadians(mSurfaceAngle.toDouble()))).toFloat()
                mForegroundRectF.set(
                    mBackgroundRectF.centerX() - halfWidth,
                    mBackgroundRectF.top,
                    mBackgroundRectF.centerX() + halfWidth,
                    mBackgroundRectF.bottom
                )
                canvas.drawArc(mForegroundRectF, 270f, 180f, true, mPaint)
            }
            mSurfaceAngle == 90f -> {
                drawDarkCircle(canvas)
                mPaint.color = mLightColor
                canvas.drawArc(mBackgroundRectF, 270f, 180f, true, mPaint)
            }
            mSurfaceAngle < 180 -> {
                drawDarkCircle(canvas)
                mPaint.color = mLightColor
                canvas.drawArc(mBackgroundRectF, 270f, 180f, true, mPaint)
                val halfWidth = (mBackgroundRectF.width() / 2 * Math.sin(Math.toRadians((mSurfaceAngle - 90).toDouble()))).toFloat()
                mForegroundRectF.set(
                    mBackgroundRectF.centerX() - halfWidth,
                    mBackgroundRectF.top,
                    mBackgroundRectF.centerX() + halfWidth,
                    mBackgroundRectF.bottom
                )
                canvas.drawArc(mForegroundRectF, 90f, 180f, true, mPaint)
            }
            mSurfaceAngle == 180f -> drawLightCircle(canvas)
            mSurfaceAngle < 270 -> {
                drawDarkCircle(canvas)
                mPaint.color = mLightColor
                canvas.drawArc(mBackgroundRectF, 90f, 180f, true, mPaint)
                val halfWidth = (mBackgroundRectF.width() / 2 * Math.cos(Math.toRadians((mSurfaceAngle - 180).toDouble()))).toFloat()
                mForegroundRectF.set(
                    mBackgroundRectF.centerX() - halfWidth,
                    mBackgroundRectF.top,
                    mBackgroundRectF.centerX() + halfWidth,
                    mBackgroundRectF.bottom
                )
                canvas.drawArc(mForegroundRectF, 270f, 180f, true, mPaint)
            }
            mSurfaceAngle == 270f -> {
                drawDarkCircle(canvas)
                mPaint.color = mLightColor
                canvas.drawArc(mBackgroundRectF, 90f, 180f, true, mPaint)
            }
            else -> {
                drawLightCircle(canvas)
                mPaint.color = mDarkColor
                canvas.drawArc(mBackgroundRectF, 270f, 180f, true, mPaint)
                val halfWidth = (mBackgroundRectF.width() / 2 * Math.cos(Math.toRadians((360 - mSurfaceAngle).toDouble()))).toFloat()
                mForegroundRectF.set(
                    mBackgroundRectF.centerX() - halfWidth,
                    mBackgroundRectF.top,
                    mBackgroundRectF.centerX() + halfWidth,
                    mBackgroundRectF.bottom
                )
                canvas.drawArc(mForegroundRectF, 90f, 180f, true, mPaint)
            }
        }

        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = LINE_WIDTH
        if (mSurfaceAngle < 90 || 270 < mSurfaceAngle) {
            mPaint.color = mDarkColor
            canvas.drawLine(
                mBackgroundRectF.centerX(), mBackgroundRectF.top,
                mBackgroundRectF.centerX(), mBackgroundRectF.bottom,
                mPaint
            )
        } else if (90 < mSurfaceAngle && mSurfaceAngle < 270) {
            mPaint.color = mLightColor
            canvas.drawLine(
                mBackgroundRectF.centerX(), mBackgroundRectF.top,
                mBackgroundRectF.centerX(), mBackgroundRectF.bottom,
                mPaint
            )
        }
        mPaint.color = mStrokeColor
        canvas.drawCircle(
            mBackgroundRectF.centerX(),
            mBackgroundRectF.centerY(),
            mBackgroundRectF.width() / 2,
            mPaint
        )
    }

    private fun drawLightCircle(canvas: Canvas) {
        mPaint.color = mLightColor
        canvas.drawCircle(
            mBackgroundRectF.centerX(),
            mBackgroundRectF.centerY(),
            mBackgroundRectF.width() / 2,
            mPaint
        )
    }

    private fun drawDarkCircle(canvas: Canvas) {
        mPaint.color = mDarkColor
        canvas.drawCircle(
            mBackgroundRectF.centerX(),
            mBackgroundRectF.centerY(),
            mBackgroundRectF.width() / 2,
            mPaint
        )
    }
}
