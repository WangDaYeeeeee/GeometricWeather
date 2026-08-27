package wangdaye.com.geometricweather.common.ui.widgets

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Size
import androidx.core.graphics.ColorUtils
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.utils.DisplayUtils

class ArcProgress @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var mProgressPaint: Paint
    private lateinit var mShadowPaint: Paint
    private lateinit var mCenterTextPaint: Paint
    private lateinit var mBottomTextPaint: Paint

    private val mShaderWrapper: DayNightShaderWrapper

    private val mRectF = RectF()
    private var mArcBottomHeight = 0f

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

    private var mArcAngle = 0f
    private var mProgressWidth = 0f

    @ColorInt
    private var mProgressColor: Int = 0

    @ColorInt
    private var mShadowColor: Int = 0

    @ColorInt
    private var mShaderColor: Int = 0

    @ColorInt
    private var mBackgroundColor: Int = 0

    private var mText: String? = null
    private var mTextSize = 0f

    @ColorInt
    private var mTextColor: Int = 0

    @Size(2)
    private val mShaderColors: IntArray

    private var mBottomText: String? = null
    private var mBottomTextSize = 0f

    @ColorInt
    private var mBottomTextColor: Int = 0

    init {
        val attributes = context.theme
            .obtainStyledAttributes(attrs, R.styleable.ArcProgress, defStyleAttr, 0)
        initialize(attributes)
        attributes.recycle()

        initPaint()

        mShaderColors = intArrayOf(Color.BLACK, Color.WHITE)
        mShaderWrapper = DayNightShaderWrapper(
            null, measuredWidth, measuredHeight, true, mShaderColors
        )
    }

    private fun initialize(attributes: TypedArray) {
        progress = attributes.getInt(R.styleable.ArcProgress_progress, 0).toFloat()
        max = attributes.getInt(R.styleable.ArcProgress_max, 100).toFloat()
        mArcAngle = attributes.getFloat(R.styleable.ArcProgress_arc_angle, 360 * 0.8f)
        mProgressWidth = attributes.getDimension(
            R.styleable.ArcProgress_progress_width, DisplayUtils.dpToPx(context, 8f)
        )
        mProgressColor = attributes.getColor(R.styleable.ArcProgress_progress_color, Color.BLACK)
        mShadowColor = Color.argb((0.2 * 255).toInt(), 0, 0, 0)
        mShaderColor = Color.argb((0.2 * 255).toInt(), 0, 0, 0)
        mBackgroundColor = attributes.getColor(
            R.styleable.ArcProgress_background_color, Color.GRAY
        )

        mText = attributes.getString(R.styleable.ArcProgress_text)
        mTextSize = attributes.getDimension(
            R.styleable.ArcProgress_text_size, DisplayUtils.dpToPx(context, 36f)
        )
        mTextColor = attributes.getColor(R.styleable.ArcProgress_text_color, Color.DKGRAY)

        mBottomText = attributes.getString(R.styleable.ArcProgress_bottom_text)
        mBottomTextSize = attributes.getDimension(
            R.styleable.ArcProgress_bottom_text_size, DisplayUtils.dpToPx(context, 14f)
        )
        mBottomTextColor = attributes.getColor(R.styleable.ArcProgress_bottom_text_color, Color.DKGRAY)
    }

    private fun initPaint() {
        mProgressPaint = Paint()
        mProgressPaint.isAntiAlias = true
        mProgressPaint.strokeWidth = mProgressWidth
        mProgressPaint.style = Paint.Style.STROKE
        mProgressPaint.strokeCap = Paint.Cap.ROUND

        mShadowPaint = Paint()
        mShadowPaint.isAntiAlias = true
        mShadowPaint.style = Paint.Style.FILL

        mCenterTextPaint = TextPaint()
        mCenterTextPaint.textSize = mTextSize
        mCenterTextPaint.isAntiAlias = true
        mCenterTextPaint.typeface =
            DisplayUtils.getTypefaceFromTextAppearance(context, R.style.large_title_text)

        mBottomTextPaint = TextPaint()
        mBottomTextPaint.set(mCenterTextPaint)
        mBottomTextPaint.typeface =
            DisplayUtils.getTypefaceFromTextAppearance(context, R.style.content_text)
    }

    fun setProgressColor(lightTheme: Boolean) {
        setProgressColor(mProgressColor, lightTheme)
    }

    fun setProgressColor(@ColorInt progressColor: Int, lightTheme: Boolean) {
        mProgressColor = progressColor
        mShadowColor = getDarkerColor(progressColor)
        mShaderColor = ColorUtils.setAlphaComponent(
            progressColor,
            (255 * (if (lightTheme) SHADOW_ALPHA_FACTOR_LIGHT else SHADOW_ALPHA_FACTOR_DARK)).toInt()
        )
        invalidate()
    }

    private fun getDarkerColor(@ColorInt color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[1] = hsv[1] + 0.15f
        hsv[2] = hsv[2] - 0.15f
        return Color.HSVToColor(hsv)
    }

    fun setArcBackgroundColor(@ColorInt backgroundColor: Int) {
        mBackgroundColor = backgroundColor
        invalidate()
    }

    fun setText(text: String?) {
        mText = text
        invalidate()
    }

    fun setTextColor(@ColorInt textColor: Int) {
        mTextColor = textColor
        invalidate()
    }

    fun setBottomText(bottomText: String?) {
        mBottomText = bottomText
        invalidate()
    }

    fun setBottomTextColor(@ColorInt bottomTextColor: Int) {
        mBottomTextColor = bottomTextColor
        invalidate()
    }

    private fun ensureShadowShader() {
        mShaderColors[0] = mShaderColor
        mShaderColors[1] = Color.TRANSPARENT

        if (mShaderWrapper.isDifferent(measuredWidth, measuredHeight, false, mShaderColors)) {
            mShaderWrapper.setShader(
                LinearGradient(
                    0f, mRectF.top,
                    0f, mRectF.bottom,
                    mShaderColors[0], mShaderColors[1],
                    Shader.TileMode.CLAMP
                ),
                measuredWidth, measuredHeight,
                false,
                mShaderColors
            )
        }
    }

    override fun getSuggestedMinimumHeight(): Int {
        return DisplayUtils.dpToPx(context, 100f).toInt()
    }

    override fun getSuggestedMinimumWidth(): Int {
        return DisplayUtils.dpToPx(context, 100f).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val arcPadding = DisplayUtils.dpToPx(context, 4f).toInt()
        mRectF.set(
            mProgressWidth / 2f + arcPadding,
            mProgressWidth / 2f + arcPadding,
            width - mProgressWidth / 2f - arcPadding,
            MeasureSpec.getSize(heightMeasureSpec) - mProgressWidth / 2f - arcPadding
        )
        val radius = (width - 2 * arcPadding) / 2f
        val angle = (360 - mArcAngle) / 2f
        mArcBottomHeight = radius * (1 - Math.cos(angle / 180 * Math.PI)).toFloat()

        ensureShadowShader()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val startAngle = 270 - mArcAngle / 2f
        val progressSweepAngle = (1.0 * progress / max * mArcAngle).toFloat()
        val progressEndAngle = startAngle + progressSweepAngle
        val deltaAngle =
            (mProgressWidth / 2 / Math.PI / (mRectF.width() / 2) * 180).toFloat()

        if (progress > 0) {
            ensureShadowShader()
            mShadowPaint.shader = mShaderWrapper.shader
            if (progressEndAngle + deltaAngle >= 360) {
                canvas.drawCircle(
                    mRectF.centerX(),
                    mRectF.centerY(),
                    mRectF.width() / 2,
                    mShadowPaint
                )
            } else if (progressEndAngle + deltaAngle > 180) {
                canvas.drawArc(
                    mRectF,
                    360 - progressEndAngle - deltaAngle,
                    360 - 2 * (360 - progressEndAngle - deltaAngle),
                    false,
                    mShadowPaint
                )
            }
        }

        mProgressPaint.color = mBackgroundColor
        canvas.drawArc(mRectF, startAngle, mArcAngle, false, mProgressPaint)
        if (progress > 0) {
            mProgressPaint.color = mProgressColor
            canvas.drawArc(mRectF, startAngle, progressSweepAngle, false, mProgressPaint)
        }

        if (!TextUtils.isEmpty(mText)) {
            mCenterTextPaint.color = mTextColor
            mCenterTextPaint.textSize = mTextSize
            val textHeight = mCenterTextPaint.descent() + mCenterTextPaint.ascent()
            val textBaseline = (height - textHeight) / 2.0f
            canvas.drawText(
                mText!!,
                (width - mCenterTextPaint.measureText(mText)) / 2.0f,
                textBaseline,
                mCenterTextPaint
            )
        }

        if (mArcBottomHeight == 0f) {
            val radius = width / 2f
            val angle = (360 - mArcAngle) / 2f
            mArcBottomHeight = radius * (1 - Math.cos(angle / 180 * Math.PI)).toFloat()
        }

        if (!TextUtils.isEmpty(mBottomText)) {
            mBottomTextPaint.color = mBottomTextColor
            mBottomTextPaint.textSize = mBottomTextSize
            val bottomTextBaseline = height +
                (mBottomTextPaint.descent() + mBottomTextPaint.ascent()) / 2 -
                mProgressWidth * 0.33f
            canvas.drawText(
                mBottomText!!,
                (width - mBottomTextPaint.measureText(mBottomText)) / 2.0f,
                bottomTextBaseline,
                mBottomTextPaint
            )
        }
    }

    companion object {
        private const val SHADOW_ALPHA_FACTOR_LIGHT = 0.1f
        private const val SHADOW_ALPHA_FACTOR_DARK = 0.1f
    }
}
