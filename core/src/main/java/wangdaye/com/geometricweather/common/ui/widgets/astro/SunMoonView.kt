package wangdaye.com.geometricweather.common.ui.widgets.astro

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PathEffect
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Xfermode
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Size
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import wangdaye.com.geometricweather.common.ui.widgets.DayNightShaderWrapper
import wangdaye.com.geometricweather.common.utils.DisplayUtils

class SunMoonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    @Size(2)
    private lateinit var mIconDrawables: Array<Drawable?>

    private lateinit var mPaint: Paint
    private lateinit var mClearXfermode: Xfermode
    private lateinit var mX1ShaderWrapper: DayNightShaderWrapper
    private lateinit var mX2ShaderWrapper: DayNightShaderWrapper
    private lateinit var mEffect: PathEffect
    private lateinit var mRectF: RectF

    @Size(2)
    private lateinit var mIconRotations: FloatArray
    @Size(2)
    private lateinit var mIconAlphas: FloatArray
    @Size(2)
    private lateinit var mIconPositions: Array<FloatArray>

    @Size(2)
    private lateinit var mStartTimes: LongArray
    @Size(2)
    private lateinit var mCurrentTimes: LongArray
    @Size(2)
    private lateinit var mEndTimes: LongArray
    @Size(2)
    private lateinit var mProgresses: LongArray
    @Size(2)
    private lateinit var mMaxes: LongArray

    @Size(3)
    private lateinit var mLineColors: IntArray
    @Size(2)
    private lateinit var mX1ShaderColors: IntArray
    @Size(2)
    private lateinit var mX2ShaderColors: IntArray
    @ColorInt
    private var mRootColor = 0

    private var mLineSize = 0f
    private var mDottedLineSize = 0f
    private var mMargin = 0f

    var iconSize: Int = 0

    init {
        initialize()
    }

    private fun initialize() {
        mIconDrawables = arrayOfNulls(2)

        mIconRotations = floatArrayOf(0f, 0f)
        mIconAlphas = floatArrayOf(0f, 0f)
        mIconPositions = arrayOf(floatArrayOf(0f, 0f), floatArrayOf(0f, 0f))

        mStartTimes = longArrayOf(1, 1)
        mEndTimes = longArrayOf(1, 1)
        mCurrentTimes = longArrayOf(0, 0)
        mProgresses = longArrayOf(-1, -1)
        mMaxes = longArrayOf(100, 100)

        mLineColors = intArrayOf(Color.BLACK, Color.GRAY, Color.LTGRAY)

        mX1ShaderColors = intArrayOf(Color.GRAY, Color.WHITE)
        mX2ShaderColors = intArrayOf(Color.BLACK, Color.WHITE)
        mRootColor = Color.WHITE

        mLineSize = DisplayUtils.dpToPx(getContext(), LINE_SIZE_DIP)
        mDottedLineSize = DisplayUtils.dpToPx(getContext(), DOTTED_LINE_SIZE_DIP)
        mMargin = DisplayUtils.dpToPx(getContext(), MARGIN_DIP)

        iconSize = DisplayUtils.dpToPx(getContext(), ICON_SIZE_DIP).toInt()

        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.strokeCap = Paint.Cap.ROUND

        mClearXfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        mX1ShaderWrapper = DayNightShaderWrapper(measuredWidth, measuredHeight)
        mX2ShaderWrapper = DayNightShaderWrapper(measuredWidth, measuredHeight)

        mEffect = DashPathEffect(
            floatArrayOf(
                DisplayUtils.dpToPx(getContext(), 3f),
                2 * DisplayUtils.dpToPx(getContext(), 3f)
            ),
            0f
        )
        mRectF = RectF()
    }

    fun setTime(
        @Size(2) startTimes: LongArray,
        @Size(2) endTimes: LongArray,
        @Size(2) currentTimes: LongArray
    ) {
        mStartTimes = startTimes
        mEndTimes = endTimes
        mCurrentTimes = currentTimes

        setIndicatorPosition(0)
        setIndicatorPosition(1)

        ViewCompat.postInvalidateOnAnimation(this)
    }

    fun setColors(
        @ColorInt sunLineColor: Int,
        @ColorInt moonLineColor: Int,
        @ColorInt backgroundLineColor: Int,
        @ColorInt rootColor: Int,
        lightTheme: Boolean
    ) {
        mLineColors = intArrayOf(sunLineColor, moonLineColor, backgroundLineColor)
        ensureShader(rootColor, sunLineColor, moonLineColor, lightTheme)
        ViewCompat.postInvalidateOnAnimation(this)
    }

    fun setDayIndicatorRotation(rotation: Float) {
        mIconRotations[0] = rotation
        ViewCompat.postInvalidateOnAnimation(this)
    }

    fun setNightIndicatorRotation(rotation: Float) {
        mIconRotations[1] = rotation
        ViewCompat.postInvalidateOnAnimation(this)
    }

    private fun ensureShader(
        @ColorInt rootColor: Int,
        @ColorInt sunLineColor: Int,
        @ColorInt moonLineColor: Int,
        lightTheme: Boolean
    ) {
        val lineShadowShader = if (lightTheme) {
            ColorUtils.setAlphaComponent(sunLineColor, (255 * SHADOW_ALPHA_FACTOR_LIGHT).toInt())
        } else {
            ColorUtils.setAlphaComponent(moonLineColor, (255 * SHADOW_ALPHA_FACTOR_DARK).toInt())
        }

        mX1ShaderColors[0] = DisplayUtils.blendColor(lineShadowShader, rootColor)
        mX1ShaderColors[1] = rootColor

        mX2ShaderColors[0] = DisplayUtils.blendColor(lineShadowShader, mX1ShaderColors[0])
        mX2ShaderColors[1] = rootColor

        mRootColor = rootColor

        if (mX1ShaderWrapper.isDifferent(
                measuredWidth, measuredHeight, lightTheme, mX1ShaderColors
            )
        ) {
            mX1ShaderWrapper.setShader(
                LinearGradient(
                    0f, mRectF.top,
                    0f, measuredHeight - mMargin,
                    mX1ShaderColors[0], mX1ShaderColors[1],
                    Shader.TileMode.CLAMP
                ),
                measuredWidth, measuredHeight,
                lightTheme,
                mX1ShaderColors
            )
        }
        if (mX2ShaderWrapper.isDifferent(
                measuredWidth, measuredHeight, lightTheme, mX2ShaderColors
            )
        ) {
            mX2ShaderWrapper.setShader(
                LinearGradient(
                    0f, mRectF.top,
                    0f, measuredHeight - mMargin,
                    mX2ShaderColors[0], mX2ShaderColors[1],
                    Shader.TileMode.CLAMP
                ),
                measuredWidth, measuredHeight,
                lightTheme,
                mX2ShaderColors
            )
        }
    }

    private fun ensureProgress(index: Int) {
        mMaxes[index] = mEndTimes[index] - mStartTimes[index]
        mProgresses[index] = mCurrentTimes[index] - mStartTimes[index]
        mProgresses[index] = mProgresses[index].coerceAtLeast(0)
        mProgresses[index] = mProgresses[index].coerceAtMost(mMaxes[index])
    }

    private fun setIndicatorPosition(index: Int) {
        ensureProgress(index)
        val startAngle = 270 - ARC_ANGLE / 2f
        val progressSweepAngle = (1.0 * mProgresses[index] / mMaxes[index] * ARC_ANGLE).toFloat()
        val progressEndAngle = startAngle + progressSweepAngle
        val deltaAngle = progressEndAngle - 180
        val deltaWidth = Math.abs(mRectF.width() / 2 * Math.cos(Math.toRadians(deltaAngle.toDouble()))).toFloat()
        val deltaHeight = Math.abs(mRectF.width() / 2 * Math.sin(Math.toRadians(deltaAngle.toDouble()))).toFloat()

        if (progressSweepAngle == 0f && mIconAlphas[index] != 0f) {
            mIconAlphas[index] = 0f
        } else if (progressSweepAngle != 0f && mIconAlphas[index] == 0f) {
            mIconAlphas[index] = 1f
        }

        if (mIconDrawables[index] != null) {
            if (progressEndAngle < 270) {
                mIconPositions[index][0] = mRectF.centerX() - deltaWidth - iconSize / 2f
            } else {
                mIconPositions[index][0] = mRectF.centerX() + deltaWidth - iconSize / 2f
            }
            mIconPositions[index][1] = mRectF.centerY() - deltaHeight - iconSize / 2f
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = (MeasureSpec.getSize(widthMeasureSpec) - 2 * mMargin).toInt()
        val deltaRadians = Math.toRadians((180 - ARC_ANGLE) / 2.0)
        val radius = (width / 2 / Math.cos(deltaRadians)).toInt()
        val height = (radius - width / 2 * Math.tan(deltaRadians)).toInt()
        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec((width + 2 * mMargin).toInt(), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec((height + 2 * mMargin).toInt(), MeasureSpec.EXACTLY)
        )

        val centerX = measuredWidth / 2
        val centerY = (mMargin + radius).toInt()
        mRectF.set(
            (centerX - radius).toFloat(),
            (centerY - radius).toFloat(),
            (centerX + radius).toFloat(),
            (centerY + radius).toFloat()
        )

        ensureShader(mRootColor, mLineColors[0], mLineColors[1], mX1ShaderWrapper.isLightTheme)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val startAngle = 270 - ARC_ANGLE / 2f
        val progressSweepAngleDay = (1.0 * mProgresses[0] / mMaxes[0] * ARC_ANGLE).toFloat()
        val progressEndAngleDay = startAngle + progressSweepAngleDay
        val progressSweepAngleNight = (1.0 * mProgresses[1] / mMaxes[1] * ARC_ANGLE).toFloat()
        val progressEndAngleNight = startAngle + progressSweepAngleNight
        if (progressEndAngleDay == progressEndAngleNight) {
            drawShadow(canvas, 0, progressEndAngleDay, mX2ShaderWrapper.shader)
        } else if (progressEndAngleDay > progressEndAngleNight) {
            drawShadow(canvas, 0, progressEndAngleDay, mX1ShaderWrapper.shader)
            drawShadow(canvas, 1, progressEndAngleNight, mX2ShaderWrapper.shader)
        } else {
            drawShadow(canvas, 1, progressEndAngleNight, mX1ShaderWrapper.shader)
            drawShadow(canvas, 0, progressEndAngleDay, mX2ShaderWrapper.shader)
        }

        mPaint.color = mLineColors[2]
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mDottedLineSize
        mPaint.pathEffect = mEffect
        canvas.drawArc(mRectF, startAngle, ARC_ANGLE.toFloat(), false, mPaint)
        canvas.drawLine(
            mMargin,
            measuredHeight - mMargin,
            measuredWidth - mMargin,
            measuredHeight - mMargin,
            mPaint
        )

        drawPathLine(canvas, 1, startAngle, (1.0 * mProgresses[1] / mMaxes[1] * ARC_ANGLE).toFloat())
        drawPathLine(canvas, 0, startAngle, (1.0 * mProgresses[0] / mMaxes[0] * ARC_ANGLE).toFloat())

        for (i in 1 downTo 0) {
            if (mIconDrawables[i] == null || mProgresses[i] <= 0) {
                continue
            }
            val restoreCount = canvas.save()
            canvas.translate(mIconPositions[i][0], mIconPositions[i][1])
            canvas.rotate(mIconRotations[i], iconSize / 2f, iconSize / 2f)
            mIconDrawables[i]!!.draw(canvas)
            canvas.restoreToCount(restoreCount)
        }
    }

    private fun drawShadow(canvas: Canvas, index: Int, progressEndAngle: Float, shader: Shader?) {
        if (mProgresses[index] > 0) {
            val layerId = canvas.saveLayer(
                mRectF.left, mRectF.top, mRectF.right, mRectF.top + mRectF.height() / 2,
                null, Canvas.ALL_SAVE_FLAG
            )

            mPaint.style = Paint.Style.FILL
            mPaint.shader = shader
            canvas.drawArc(
                mRectF,
                270 - ARC_ANGLE / 2f,
                ARC_ANGLE.toFloat(),
                false,
                mPaint
            )
            mPaint.shader = null

            mPaint.xfermode = mClearXfermode
            canvas.drawRect(
                (
                    mRectF.centerX() + mRectF.width() / 2 *
                        Math.cos((360 - progressEndAngle) * Math.PI / 180)
                    ).toFloat(),
                mRectF.top,
                mRectF.right,
                mRectF.top + mRectF.height() / 2,
                mPaint
            )
            mPaint.xfermode = null

            canvas.restoreToCount(layerId)
        }
    }

    private fun drawPathLine(canvas: Canvas, index: Int, startAngle: Float, progressSweepAngle: Float) {
        if (mProgresses[index] > 0) {
            mPaint.color = mLineColors[index]
            mPaint.strokeWidth = mLineSize
            mPaint.pathEffect = null
            canvas.drawArc(mRectF, startAngle, progressSweepAngle, false, mPaint)
        }
    }

    fun setSunDrawable(d: Drawable?) {
        if (d != null) {
            mIconDrawables[0] = d
            mIconDrawables[0]!!.setBounds(0, 0, iconSize, iconSize)
        }
    }

    fun setMoonDrawable(d: Drawable?) {
        if (d != null) {
            mIconDrawables[1] = d
            mIconDrawables[1]!!.setBounds(0, 0, iconSize, iconSize)
        }
    }

    companion object {
        private const val ICON_SIZE_DIP = 24f
        private const val LINE_SIZE_DIP = 5f
        private const val DOTTED_LINE_SIZE_DIP = 1f
        private const val MARGIN_DIP = 16f

        private const val ARC_ANGLE = 135

        private const val SHADOW_ALPHA_FACTOR_LIGHT = 0.1f
        private const val SHADOW_ALPHA_FACTOR_DARK = 0.2f
    }
}
