package wangdaye.com.geometricweather.theme.weatherView.materialWeatherView

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.Size
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider
import wangdaye.com.geometricweather.theme.weatherView.WeatherView
import wangdaye.com.geometricweather.theme.weatherView.WeatherView.WeatherKindRule
import kotlin.math.min

class MaterialWeatherView(context: Context) : ViewGroup(context), WeatherView {

    private var mCurrentView: MaterialPainterView? = null
    private var mPreviousView: MaterialPainterView? = null
    private var mSwitchAnimator: Animator? = null

    @WeatherKindRule
    override var weatherKind: Int = 0
        private set
    private var mDaytime: Boolean = false

    private var mFirstCardMarginTop: Int = 0

    private var mGravitySensorEnabled: Boolean = true
    private var mDrawable: Boolean = false

    abstract class WeatherAnimationImplementor {
        abstract fun updateData(
            @Size(2) canvasSizes: IntArray,
            interval: Long,
            rotation2D: Float,
            rotation3D: Float
        )

        abstract fun draw(
            @Size(2) canvasSizes: IntArray,
            canvas: Canvas,
            scrollRate: Float,
            rotation2D: Float,
            rotation3D: Float
        )
    }

    abstract class RotateController {
        abstract fun updateRotation(rotation: Double, interval: Double)
        abstract fun getRotation(): Double
    }

    init {
        setWeather(WeatherView.WEATHER_KING_NULL, true, null)
        mGravitySensorEnabled = true
        mDrawable = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        mFirstCardMarginTop = (resources.displayMetrics.heightPixels * 0.66).toInt()

        for (index in 0 until childCount) {
            val child = getChildAt(index)
            child.measure(
                MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
            )
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            child.layout(
                0,
                0,
                child.measuredWidth,
                child.measuredHeight
            )
        }
    }

    override fun setWeather(
        @WeatherKindRule weatherKind: Int,
        daytime: Boolean,
        provider: ResourceProvider?
    ) {
        if (this.weatherKind == weatherKind && mDaytime == daytime) {
            return
        }

        this.weatherKind = weatherKind
        mDaytime = daytime

        mSwitchAnimator?.cancel()
        mSwitchAnimator = null

        mCurrentView?.drawable = false

        val prev = mPreviousView
        mPreviousView = mCurrentView
        mCurrentView = prev
        if (mCurrentView == null) {
            mCurrentView = MaterialPainterView(
                context,
                weatherKind,
                daytime,
                mDrawable,
                mPreviousView?.scrollRate ?: 0f,
                mGravitySensorEnabled
            )
            addView(mCurrentView)
        } else {
            mCurrentView!!.update(weatherKind, daytime, mGravitySensorEnabled)
            mCurrentView!!.drawable = mDrawable
        }

        if (mPreviousView == null) {
            mCurrentView!!.alpha = 1f
            return
        }

        val set = AnimatorSet()
        set.duration = SWITCH_ANIMATION_DURATION
        set.interpolator = AccelerateDecelerateInterpolator()
        set.playTogether(
            ObjectAnimator.ofFloat(
                mCurrentView,
                "alpha",
                0f, 1f
            ),
            ObjectAnimator.ofFloat(
                mPreviousView,
                "alpha",
                mPreviousView!!.alpha, 0f
            )
        )

        mSwitchAnimator = set
        mSwitchAnimator!!.start()
    }

    override fun onClick() {
        // do nothing.
    }

    override fun onScroll(scrollY: Int) {
        val scrollRate = min(1.0, 1.0 * scrollY / mFirstCardMarginTop).toFloat()

        mCurrentView?.scrollRate = scrollRate
        mPreviousView?.scrollRate = scrollRate
    }

    override fun setDrawable(drawable: Boolean) {
        if (mDrawable == drawable) {
            return
        }
        mDrawable = drawable

        mCurrentView?.drawable = drawable
        mPreviousView?.drawable = drawable
    }

    override fun setGravitySensorEnabled(enabled: Boolean) {
        mGravitySensorEnabled = enabled
    }

    companion object {
        private const val SWITCH_ANIMATION_DURATION = 300L
    }
}
