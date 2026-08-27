package wangdaye.com.geometricweather.common.ui.widgets

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.animation.Interpolator
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import java.util.Arrays

class InkPageIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle),
    SwipeSwitchLayout.OnPagerSwipeListener,
    View.OnAttachStateChangeListener {

    private var mDotDiameter: Int
    private var mGap: Int
    private var mAnimDuration: Long
    private var mUnselectedColour: Int
    private var mSelectedColour: Int

    private var mDotRadius: Float
    private var mHalfDotRadius: Float
    private var mAnimHalfDuration: Long
    private var mDotTopY = 0f
    private var mDotCenterY = 0f
    private var mDotBottomY = 0f

    private var mSwitchView: SwipeSwitchLayout? = null

    private var mPageCount = 0
    private var mCurrentPage = 0
    private var mPreviousPage = 0
    private var mSelectedDotX = 0f
    private var mSelectedDotInPosition = false
    private var mDotCenterX: FloatArray? = null
    private lateinit var mJoiningFractions: FloatArray
    private var mRetreatingJoinX1 = 0f
    private var mRetreatingJoinX2 = 0f
    private lateinit var mDotRevealFractions: FloatArray
    private var mIsAttachedToWindow = false
    private var mPageChanging = false
    private var mShowing: Boolean

    private val mUnselectedPaint: Paint
    private val mSelectedPaint: Paint
    private val mTextPaint: Paint
    private var mCombinedUnselectedPath: Path
    private val mUnselectedDotPath: Path
    private val mUnselectedDotLeftPath: Path
    private val mUnselectedDotRightPath: Path
    private val mRectF: RectF

    private var mMoveAnimation: ValueAnimator? = null
    private var mJoiningAnimationSet: AnimatorSet? = null
    private var mRetreatAnimation: PendingRetreatAnimator? = null
    private lateinit var mRevealAnimations: Array<PendingRevealAnimator>
    private val mInterpolator: Interpolator
    private val mShowAnimator: ObjectAnimator
    private val mDismissAnimator: ObjectAnimator

    var endX1 = 0f
    var endY1 = 0f
    var endX2 = 0f
    var endY2 = 0f
    var controlX1 = 0f
    var controlY1 = 0f
    var controlX2 = 0f
    var controlY2 = 0f

    init {
        val density = context.resources.displayMetrics.density.toInt()

        val a = getContext().obtainStyledAttributes(
            attrs, R.styleable.InkPageIndicator, defStyle, 0
        )

        mDotDiameter = a.getDimensionPixelSize(
            R.styleable.InkPageIndicator_dotDiameter,
            DEFAULT_DOT_SIZE * density
        )
        mDotRadius = mDotDiameter / 2f
        mHalfDotRadius = mDotRadius / 2f
        mGap = a.getDimensionPixelSize(
            R.styleable.InkPageIndicator_dotGap,
            DEFAULT_GAP * density
        )
        mAnimDuration = a.getInteger(
            R.styleable.InkPageIndicator_animationDuration,
            DEFAULT_ANIM_DURATION
        ).toLong()
        mAnimHalfDuration = mAnimDuration / 2
        mUnselectedColour = a.getColor(
            R.styleable.InkPageIndicator_pageIndicatorColor,
            DEFAULT_UNSELECTED_COLOUR
        )
        mSelectedColour = a.getColor(
            R.styleable.InkPageIndicator_currentPageIndicatorColor,
            DEFAULT_SELECTED_COLOUR
        )

        a.recycle()

        mUnselectedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mUnselectedPaint.color = mUnselectedColour
        mSelectedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mSelectedPaint.color = mSelectedColour
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint.color = mSelectedColour
        mTextPaint.typeface =
            DisplayUtils.getTypefaceFromTextAppearance(getContext(), R.style.subtitle_text)
        mInterpolator = FastOutSlowInInterpolator()

        mCombinedUnselectedPath = Path()
        mUnselectedDotPath = Path()
        mUnselectedDotLeftPath = Path()
        mUnselectedDotRightPath = Path()
        mRectF = RectF()

        addOnAttachStateChangeListener(this)

        mShowing = false
        alpha = 0f

        mShowAnimator = ObjectAnimator.ofFloat(
            this, "alpha", 0f, MAX_ALPHA
        ).setDuration(100)

        mDismissAnimator = ObjectAnimator.ofFloat(
            this, "alpha", MAX_ALPHA, 0f
        ).setDuration(200)
        mDismissAnimator.startDelay = 600
    }

    fun setSwitchView(switchView: SwipeSwitchLayout) {
        mSwitchView = switchView
        switchView.setOnPageSwipeListener(this)
        setPageCount(switchView.totalCount)
        setCurrentPageImmediate()
    }

    fun setDisplayState(show: Boolean) {
        if (mShowing == show) {
            return
        }

        mShowing = show

        mDismissAnimator.cancel()
        if (show) {
            mShowAnimator.cancel()
            if (alpha != MAX_ALPHA) {
                mShowAnimator.setFloatValues(alpha, 0.7f)
                mShowAnimator.start()
            }
        } else {
            mDismissAnimator.start()
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (mIsAttachedToWindow) {
            if (position < 0 || position > mPageCount - 1) {
                return
            }

            var fraction = positionOffset
            val currentPosition = if (mPageChanging) mPreviousPage else mCurrentPage
            var leftDotPosition = position
            if (currentPosition != position) {
                fraction = 1f - positionOffset

                if (fraction == 1f) {
                    leftDotPosition = Math.min(currentPosition, position)
                }
            }
            setJoiningFraction(leftDotPosition, fraction)
        }
    }

    override fun onPageSelected(position: Int) {
        if (mIsAttachedToWindow) {
            setSelectedPage(position)
        } else {
            setCurrentPageImmediate()
        }
    }

    private fun setPageCount(pages: Int) {
        mPageCount = pages
        resetState()
        requestLayout()
    }

    fun setCurrentIndicatorColor(@ColorInt color: Int) {
        mSelectedPaint.color = color
        mTextPaint.color = color
        invalidate()
    }

    fun setIndicatorColor(@ColorInt color: Int) {
        mUnselectedPaint.color = color
        invalidate()
    }

    private fun calculateDotPositions(width: Int, height: Int) {
        val left = paddingLeft
        val top = paddingTop
        val right = width - paddingRight

        val requiredWidth = getRequiredWidth()
        val startLeft = left + ((right - left - requiredWidth) / 2f) + mDotRadius

        mDotCenterX = FloatArray(mPageCount)
        for (i in 0 until mPageCount) {
            mDotCenterX!![i] = startLeft + i * (mDotDiameter + mGap)
        }
        mDotTopY = top.toFloat()
        mDotCenterY = top + mDotRadius
        mDotBottomY = (top + mDotDiameter).toFloat()

        setCurrentPageImmediate()
    }

    private fun setCurrentPageImmediate() {
        mCurrentPage = mSwitchView?.position ?: 0
        val dotCenterX = mDotCenterX
        val moveAnimation = mMoveAnimation
        if (dotCenterX != null && dotCenterX.isNotEmpty() && (moveAnimation == null || !moveAnimation.isStarted)) {
            mSelectedDotX = dotCenterX[mCurrentPage]
        }
    }

    private fun resetState() {
        mJoiningFractions = FloatArray(mPageCount - 1)
        Arrays.fill(mJoiningFractions, 0f)
        mDotRevealFractions = FloatArray(mPageCount)
        Arrays.fill(mDotRevealFractions, 0f)
        mRetreatingJoinX1 = INVALID_FRACTION
        mRetreatingJoinX2 = INVALID_FRACTION
        mSelectedDotInPosition = true
        if (measuredHeight != 0 || measuredWidth != 0) {
            calculateDotPositions(measuredWidth, measuredHeight)
        }
    }

    override fun fitSystemWindows(insets: Rect): Boolean {
        setPadding(0, 0, 0, insets.bottom)
        return false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = getDesiredHeight()
        val height = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(heightMeasureSpec)
            MeasureSpec.AT_MOST -> Math.min(desiredHeight, MeasureSpec.getSize(heightMeasureSpec))
            else -> desiredHeight
        }

        val desiredWidth = getDesiredWidth()
        val width = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(widthMeasureSpec)
            MeasureSpec.AT_MOST -> Math.min(desiredWidth, MeasureSpec.getSize(widthMeasureSpec))
            else -> desiredWidth
        }
        setMeasuredDimension(width, height)
        calculateDotPositions(width, height)
    }

    private fun getDesiredHeight(): Int {
        return paddingTop + mDotDiameter + paddingBottom
    }

    private fun getRequiredWidth(): Int {
        return mPageCount * mDotDiameter + (mPageCount - 1) * mGap
    }

    private fun getDesiredWidth(): Int {
        return paddingLeft + getRequiredWidth() + paddingRight
    }

    override fun onViewAttachedToWindow(view: View) {
        mIsAttachedToWindow = true
    }

    override fun onViewDetachedFromWindow(view: View) {
        mIsAttachedToWindow = false
    }

    override fun onDraw(canvas: Canvas) {
        if (mSwitchView == null || mPageCount == 0) return
        if (mPageCount > 7) {
            val cx = measuredWidth / 2
            val cy = measuredHeight / 2

            mTextPaint.textAlign = Paint.Align.CENTER
            mTextPaint.textSize = (mDotDiameter + mGap / 2).toFloat()
            val fontMetrics = mTextPaint.fontMetrics
            val baseLineY = (cy - fontMetrics.top - fontMetrics.bottom).toInt()
            canvas.drawText((mCurrentPage + 1).toString() + "/" + mPageCount, cx.toFloat(), baseLineY.toFloat(), mTextPaint)

            return
        }

        drawUnselected(canvas)
        drawSelected(canvas)
    }

    private fun drawUnselected(canvas: Canvas) {
        mCombinedUnselectedPath.rewind()

        for (page in 0 until mPageCount) {
            val nextXIndex = if (page == mPageCount - 1) page else page + 1
            val unselectedPath = getUnselectedPath(
                page,
                mDotCenterX!![page],
                mDotCenterX!![nextXIndex],
                if (page == mPageCount - 1) INVALID_FRACTION else mJoiningFractions[page],
                mDotRevealFractions[page]
            )
            unselectedPath.addPath(mCombinedUnselectedPath)
            mCombinedUnselectedPath.addPath(unselectedPath)
        }
        if (mRetreatingJoinX1 != INVALID_FRACTION) {
            val retreatingJoinPath = getRetreatingJoinPath()
            mCombinedUnselectedPath.addPath(retreatingJoinPath)
        }

        canvas.drawPath(mCombinedUnselectedPath, mUnselectedPaint)
    }

    private fun getUnselectedPath(
        page: Int,
        centerX: Float,
        nextCenterX: Float,
        joiningFraction: Float,
        dotRevealFraction: Float
    ): Path {
        mUnselectedDotPath.rewind()

        if ((joiningFraction == 0f || joiningFraction == INVALID_FRACTION) &&
            dotRevealFraction == 0f &&
            !(page == mCurrentPage && mSelectedDotInPosition)
        ) {
            mUnselectedDotPath.addCircle(mDotCenterX!![page], mDotCenterY, mDotRadius, Path.Direction.CW)
        }

        if (joiningFraction > 0f && joiningFraction <= 0.5f &&
            mRetreatingJoinX1 == INVALID_FRACTION
        ) {
            mUnselectedDotLeftPath.rewind()
            mUnselectedDotLeftPath.moveTo(centerX, mDotBottomY)
            mRectF.set(centerX - mDotRadius, mDotTopY, centerX + mDotRadius, mDotBottomY)
            mUnselectedDotLeftPath.arcTo(mRectF, 90f, 180f, true)

            endX1 = centerX + mDotRadius + (joiningFraction * mGap)
            endY1 = mDotCenterY
            controlX1 = centerX + mHalfDotRadius
            controlY1 = mDotTopY
            controlX2 = endX1
            controlY2 = endY1 - mHalfDotRadius
            mUnselectedDotLeftPath.cubicTo(
                controlX1, controlY1,
                controlX2, controlY2,
                endX1, endY1
            )

            endX2 = centerX
            endY2 = mDotBottomY
            controlX1 = endX1
            controlY1 = endY1 + mHalfDotRadius
            controlX2 = centerX + mHalfDotRadius
            controlY2 = mDotBottomY
            mUnselectedDotLeftPath.cubicTo(
                controlX1, controlY1,
                controlX2, controlY2,
                endX2, endY2
            )

            mUnselectedDotPath.addPath(mUnselectedDotLeftPath)

            mUnselectedDotRightPath.rewind()
            mUnselectedDotRightPath.moveTo(nextCenterX, mDotBottomY)
            mRectF.set(nextCenterX - mDotRadius, mDotTopY, nextCenterX + mDotRadius, mDotBottomY)
            mUnselectedDotRightPath.arcTo(mRectF, 90f, -180f, true)

            endX1 = nextCenterX - mDotRadius - (joiningFraction * mGap)
            endY1 = mDotCenterY
            controlX1 = nextCenterX - mHalfDotRadius
            controlY1 = mDotTopY
            controlX2 = endX1
            controlY2 = endY1 - mHalfDotRadius
            mUnselectedDotRightPath.cubicTo(
                controlX1, controlY1,
                controlX2, controlY2,
                endX1, endY1
            )

            endX2 = nextCenterX
            endY2 = mDotBottomY
            controlX1 = endX1
            controlY1 = endY1 + mHalfDotRadius
            controlX2 = endX2 - mHalfDotRadius
            controlY2 = mDotBottomY
            mUnselectedDotRightPath.cubicTo(
                controlX1, controlY1,
                controlX2, controlY2,
                endX2, endY2
            )
            mUnselectedDotPath.addPath(mUnselectedDotRightPath)
        }

        if (joiningFraction > 0.5f && joiningFraction < 1f &&
            mRetreatingJoinX1 == INVALID_FRACTION
        ) {
            val adjustedFraction = (joiningFraction - 0.2f) * 1.25f

            mUnselectedDotPath.moveTo(centerX, mDotBottomY)
            mRectF.set(centerX - mDotRadius, mDotTopY, centerX + mDotRadius, mDotBottomY)
            mUnselectedDotPath.arcTo(mRectF, 90f, 180f, true)

            endX1 = centerX + mDotRadius + (mGap / 2)
            endY1 = mDotCenterY - (adjustedFraction * mDotRadius)
            controlX1 = endX1 - (adjustedFraction * mDotRadius)
            controlY1 = mDotTopY
            controlX2 = endX1 - ((1 - adjustedFraction) * mDotRadius)
            controlY2 = endY1
            mUnselectedDotPath.cubicTo(
                controlX1, controlY1,
                controlX2, controlY2,
                endX1, endY1
            )

            endX2 = nextCenterX
            endY2 = mDotTopY
            controlX1 = endX1 + ((1 - adjustedFraction) * mDotRadius)
            controlY1 = endY1
            controlX2 = endX1 + (adjustedFraction * mDotRadius)
            controlY2 = mDotTopY
            mUnselectedDotPath.cubicTo(
                controlX1, controlY1,
                controlX2, controlY2,
                endX2, endY2
            )

            mRectF.set(nextCenterX - mDotRadius, mDotTopY, nextCenterX + mDotRadius, mDotBottomY)
            mUnselectedDotPath.arcTo(mRectF, 270f, 180f, true)

            endY1 = mDotCenterY + (adjustedFraction * mDotRadius)
            controlX1 = endX1 + (adjustedFraction * mDotRadius)
            controlY1 = mDotBottomY
            controlX2 = endX1 + ((1 - adjustedFraction) * mDotRadius)
            controlY2 = endY1
            mUnselectedDotPath.cubicTo(
                controlX1, controlY1,
                controlX2, controlY2,
                endX1, endY1
            )

            endX2 = centerX
            endY2 = mDotBottomY
            controlX1 = endX1 - ((1 - adjustedFraction) * mDotRadius)
            controlY1 = endY1
            controlX2 = endX1 - (adjustedFraction * mDotRadius)
            controlY2 = endY2
            mUnselectedDotPath.cubicTo(
                controlX1, controlY1,
                controlX2, controlY2,
                endX2, endY2
            )
        }
        if (joiningFraction == 1f && mRetreatingJoinX1 == INVALID_FRACTION) {
            mRectF.set(centerX - mDotRadius, mDotTopY, nextCenterX + mDotRadius, mDotBottomY)
            mUnselectedDotPath.addRoundRect(mRectF, mDotRadius, mDotRadius, Path.Direction.CW)
        }

        if (dotRevealFraction > MINIMAL_REVEAL) {
            mUnselectedDotPath.addCircle(
                centerX, mDotCenterY, dotRevealFraction * mDotRadius,
                Path.Direction.CW
            )
        }

        return mUnselectedDotPath
    }

    private fun getRetreatingJoinPath(): Path {
        mUnselectedDotPath.rewind()
        mRectF.set(mRetreatingJoinX1, mDotTopY, mRetreatingJoinX2, mDotBottomY)
        mUnselectedDotPath.addRoundRect(mRectF, mDotRadius, mDotRadius, Path.Direction.CW)
        return mUnselectedDotPath
    }

    private fun drawSelected(canvas: Canvas) {
        canvas.drawCircle(mSelectedDotX, mDotCenterY, mDotRadius, mSelectedPaint)
    }

    private fun setSelectedPage(now: Int) {
        if (now == mCurrentPage) return

        mPageChanging = true
        mPreviousPage = mCurrentPage
        mCurrentPage = now
        val steps = Math.abs(now - mPreviousPage)

        if (steps > 1) {
            if (now > mPreviousPage) {
                for (i in 0 until steps) {
                    setJoiningFraction(mPreviousPage + i, 1f)
                }
            } else {
                var i = -1
                while (i > -steps) {
                    setJoiningFraction(mPreviousPage + i, 1f)
                    i--
                }
            }
        }

        mMoveAnimation = createMoveSelectedAnimator(mDotCenterX!![now], mPreviousPage, now, steps)
        mMoveAnimation!!.start()
    }

    private fun createMoveSelectedAnimator(
        moveTo: Float, was: Int, now: Int, steps: Int
    ): ValueAnimator {
        val moveSelected = ValueAnimator.ofFloat(mSelectedDotX, moveTo)

        mRetreatAnimation = PendingRetreatAnimator(
            was, now, steps,
            if (now > was) {
                RightwardStartPredicate(moveTo - ((moveTo - mSelectedDotX) * 0.25f))
            } else {
                LeftwardStartPredicate(moveTo + ((mSelectedDotX - moveTo) * 0.25f))
            }
        )
        mRetreatAnimation!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                resetState()
                mPageChanging = false
            }
        })
        moveSelected.addUpdateListener { valueAnimator ->
            mSelectedDotX = valueAnimator.animatedValue as Float
            mRetreatAnimation!!.startIfNecessary(mSelectedDotX)
            ViewCompat.postInvalidateOnAnimation(this)
        }
        moveSelected.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                mSelectedDotInPosition = false
            }

            override fun onAnimationEnd(animation: Animator) {
                mSelectedDotInPosition = true
            }
        })
        moveSelected.startDelay = if (mSelectedDotInPosition) mAnimDuration / 4L else 0L
        moveSelected.duration = mAnimDuration * 3L / 4L
        moveSelected.interpolator = mInterpolator
        return moveSelected
    }

    private fun setJoiningFraction(leftDot: Int, fraction: Float) {
        if (leftDot < mJoiningFractions.size) {
            mJoiningFractions[leftDot] = fraction
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    private fun clearJoiningFractions() {
        Arrays.fill(mJoiningFractions, 0f)
        ViewCompat.postInvalidateOnAnimation(this)
    }

    private fun setDotRevealFraction(dot: Int, fraction: Float) {
        if (dot < mDotRevealFractions.size) {
            mDotRevealFractions[dot] = fraction
        }
        ViewCompat.postInvalidateOnAnimation(this)
    }

    private fun cancelJoiningAnimations() {
        val joiningAnimationSet = mJoiningAnimationSet
        if (joiningAnimationSet != null && joiningAnimationSet.isRunning) {
            joiningAnimationSet.cancel()
        }
    }

    abstract inner class PendingStartAnimator(protected var predicate: StartPredicate) : ValueAnimator() {
        protected var hasStarted: Boolean = false

        fun startIfNecessary(currentValue: Float) {
            if (!hasStarted && predicate.shouldStart(currentValue)) {
                start()
                hasStarted = true
            }
        }
    }

    inner class PendingRetreatAnimator(
        was: Int,
        now: Int,
        steps: Int,
        predicate: StartPredicate
    ) : PendingStartAnimator(predicate) {

        init {
            duration = mAnimHalfDuration
            interpolator = mInterpolator

            val initialX1 = if (now > was) {
                Math.min(mDotCenterX!![was], mSelectedDotX) - mDotRadius
            } else {
                mDotCenterX!![now] - mDotRadius
            }
            val finalX1 = mDotCenterX!![now] - mDotRadius
            val initialX2 = if (now > was) {
                mDotCenterX!![now] + mDotRadius
            } else {
                Math.max(mDotCenterX!![was], mSelectedDotX) + mDotRadius
            }
            val finalX2 = mDotCenterX!![now] + mDotRadius

            mRevealAnimations = arrayOfNulls<PendingRevealAnimator>(steps) as Array<PendingRevealAnimator>
            val dotsToHide = IntArray(steps)
            if (initialX1 != finalX1) {
                setFloatValues(initialX1, finalX1)
                for (i in 0 until steps) {
                    mRevealAnimations[i] = PendingRevealAnimator(
                        was + i,
                        RightwardStartPredicate(mDotCenterX!![was + i])
                    )
                    dotsToHide[i] = was + i
                }
                addUpdateListener { valueAnimator ->
                    mRetreatingJoinX1 = valueAnimator.animatedValue as Float
                    ViewCompat.postInvalidateOnAnimation(this@InkPageIndicator)
                    for (pendingReveal in mRevealAnimations) {
                        pendingReveal.startIfNecessary(mRetreatingJoinX1)
                    }
                }
            } else {
                setFloatValues(initialX2, finalX2)
                for (i in 0 until steps) {
                    mRevealAnimations[i] = PendingRevealAnimator(
                        was - i,
                        LeftwardStartPredicate(mDotCenterX!![was - i])
                    )
                    dotsToHide[i] = was - i
                }
                addUpdateListener { valueAnimator ->
                    mRetreatingJoinX2 = valueAnimator.animatedValue as Float
                    ViewCompat.postInvalidateOnAnimation(this@InkPageIndicator)
                    for (pendingReveal in mRevealAnimations) {
                        pendingReveal.startIfNecessary(mRetreatingJoinX2)
                    }
                }
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    cancelJoiningAnimations()
                    clearJoiningFractions()
                    for (dot in dotsToHide) {
                        setDotRevealFraction(dot, MINIMAL_REVEAL)
                    }
                    mRetreatingJoinX1 = initialX1
                    mRetreatingJoinX2 = initialX2
                    ViewCompat.postInvalidateOnAnimation(this@InkPageIndicator)
                }

                override fun onAnimationEnd(animation: Animator) {
                    mRetreatingJoinX1 = INVALID_FRACTION
                    mRetreatingJoinX2 = INVALID_FRACTION
                    ViewCompat.postInvalidateOnAnimation(this@InkPageIndicator)
                }
            })
        }
    }

    inner class PendingRevealAnimator(
        private val mDot: Int,
        predicate: StartPredicate
    ) : PendingStartAnimator(predicate) {

        init {
            setFloatValues(MINIMAL_REVEAL, 1f)
            duration = mAnimHalfDuration
            interpolator = mInterpolator
            addUpdateListener { valueAnimator ->
                setDotRevealFraction(mDot, valueAnimator.animatedValue as Float)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    setDotRevealFraction(mDot, 0f)
                    ViewCompat.postInvalidateOnAnimation(this@InkPageIndicator)
                }
            })
        }
    }

    abstract inner class StartPredicate(protected var thresholdValue: Float) {
        abstract fun shouldStart(currentValue: Float): Boolean
    }

    inner class RightwardStartPredicate(thresholdValue: Float) : StartPredicate(thresholdValue) {
        override fun shouldStart(currentValue: Float): Boolean {
            return currentValue > thresholdValue
        }
    }

    inner class LeftwardStartPredicate(thresholdValue: Float) : StartPredicate(thresholdValue) {
        override fun shouldStart(currentValue: Float): Boolean {
            return currentValue < thresholdValue
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        mCurrentPage = savedState.currentPage
        requestLayout()
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        savedState.currentPage = mCurrentPage
        return savedState
    }

    class SavedState : BaseSavedState {
        var currentPage: Int = 0

        constructor(superState: Parcelable?) : super(superState)

        private constructor(`in`: Parcel) : super(`in`) {
            currentPage = `in`.readInt()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeInt(currentPage)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_DOT_SIZE = 8
        private const val DEFAULT_GAP = 12
        private const val DEFAULT_ANIM_DURATION = 400
        private const val DEFAULT_UNSELECTED_COLOUR = 0x80ffffff.toInt()
        private const val DEFAULT_SELECTED_COLOUR = -0x1

        private const val INVALID_FRACTION = -1f
        private const val MINIMAL_REVEAL = 0.00001f
        private const val MAX_ALPHA = 0.7f
    }
}
