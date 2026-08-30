package wangdaye.com.geometricweather.theme.weatherView.materialWeatherView.implementor

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.ColorInt
import androidx.annotation.Size
import wangdaye.com.geometricweather.theme.weatherView.materialWeatherView.MaterialWeatherView
import java.util.Random

class WindImplementor(
    @Size(2) canvasSizes: IntArray,
    daylight: Boolean
) : MaterialWeatherView.WeatherAnimationImplementor() {

    private val mPaint = Paint()
    private val mWinds: Array<Wind>
    private var mLastRotation3D = INITIAL_ROTATION_3D

    private class Wind(
        viewWidth: Int,
        viewHeight: Int,
        @ColorInt color: Int,
        scale: Float
    ) {
        var x = 0f
        var y = 0f
        var width = 0f
        var height = 0f

        var rectF = RectF()
        var speed: Float

        @ColorInt
        var color: Int = color
        var scale: Float = scale

        private val mViewWidth = viewWidth
        private val mViewHeight = viewHeight
        private val mCanvasSize = Math.pow(
            (viewWidth * viewWidth + viewHeight * viewHeight).toDouble(),
            0.5
        ).toInt()

        private val MAX_WIDTH: Float
        private val MIN_WIDTH: Float
        private val MAX_HEIGHT: Float
        private val MIN_HEIGHT: Float

        init {
            this.speed = (
                mCanvasSize / (
                    1000.0 * (0.5 + Random().nextDouble())
                    ) * 6.0
                ).toFloat()
            this.MAX_HEIGHT = 0.007f * mCanvasSize
            this.MIN_HEIGHT = 0.005f * mCanvasSize
            this.MAX_WIDTH = this.MAX_HEIGHT * 10
            this.MIN_WIDTH = this.MIN_HEIGHT * 6
            init(true)
        }

        private fun init(firstTime: Boolean) {
            val r = Random()
            y = r.nextInt(mCanvasSize).toFloat()
            x = if (firstTime) {
                r.nextInt((mCanvasSize - MAX_HEIGHT).toInt()) - mCanvasSize.toFloat()
            } else {
                -MAX_HEIGHT
            }
            width = MIN_WIDTH + r.nextFloat() * (MAX_WIDTH - MIN_WIDTH)
            height = MIN_HEIGHT + r.nextFloat() * (MAX_HEIGHT - MIN_HEIGHT)
            buildRectF()
        }

        private fun buildRectF() {
            val x = (this.x - (mCanvasSize - mViewWidth) * 0.5).toFloat()
            val y = (this.y - (mCanvasSize - mViewHeight) * 0.5).toFloat()
            rectF.set(x, y, x + width * scale, y + height * scale)
        }

        fun move(interval: Long, deltaRotation3D: Float) {
            x += (speed * interval
                * (Math.pow(scale.toDouble(), 1.5)
                + 5 * Math.sin(deltaRotation3D * Math.PI / 180.0) * Math.cos(16 * Math.PI / 180.0))).toFloat()
            y -= (speed * interval
                * 5 * Math.sin(deltaRotation3D * Math.PI / 180.0) * Math.sin(16 * Math.PI / 180.0)).toFloat()

            if (x >= mCanvasSize) {
                init(false)
            } else {
                buildRectF()
            }
        }
    }

    init {
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
        mPaint.alpha = ((if (daylight) 1f else 0.33f) * 255).toInt()

        val colors = if (daylight) {
            intArrayOf(
                Color.rgb(240, 200, 148),
                Color.rgb(237, 178, 100),
                Color.rgb(209, 142, 54),
            )
        } else {
            intArrayOf(
                Color.rgb(240, 200, 148),
                Color.rgb(237, 178, 100),
                Color.rgb(209, 142, 54),
            )
        }
        val scales = floatArrayOf(0.6f, 0.8f, 1f)

        mWinds = Array(WIND_COUNT) { i ->
            Wind(
                canvasSizes[0],
                canvasSizes[1],
                colors[i * 3 / WIND_COUNT],
                scales[i * 3 / WIND_COUNT]
            )
        }
    }

    override fun updateData(
        @Size(2) canvasSizes: IntArray,
        interval: Long,
        rotation2D: Float,
        rotation3D: Float
    ) {
        for (w in mWinds) {
            w.move(interval, if (mLastRotation3D == INITIAL_ROTATION_3D) 0f else rotation3D - mLastRotation3D)
        }
        mLastRotation3D = rotation3D
    }

    override fun draw(
        @Size(2) canvasSizes: IntArray,
        canvas: Canvas,
        scrollRate: Float,
        rotation2D: Float,
        rotation3D: Float
    ) {
        if (scrollRate < 1) {
            val rot = rotation2D - 16
            canvas.rotate(
                rot,
                canvasSizes[0] * 0.5f,
                canvasSizes[1] * 0.5f
            )

            for (w in mWinds) {
                mPaint.color = w.color
                mPaint.alpha = ((1 - scrollRate) * 255).toInt()
                canvas.drawRect(w.rectF, mPaint)
            }
        }
    }

    companion object {
        private const val INITIAL_ROTATION_3D = 1000f
        private const val WIND_COUNT = 160

        @JvmStatic
        @ColorInt
        fun getThemeColor(daylight: Boolean): Int {
            return if (daylight) 0xFFeacda3.toInt() else 0xFF958675.toInt()
        }
    }
}
