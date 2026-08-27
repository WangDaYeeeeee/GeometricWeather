package wangdaye.com.geometricweather.theme.weatherView.materialWeatherView.implementor

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.ColorInt
import androidx.annotation.Size
import wangdaye.com.geometricweather.theme.weatherView.materialWeatherView.MaterialWeatherView
import java.util.Random

class MeteorShowerImplementor(
    @Size(2) canvasSizes: IntArray
) : MaterialWeatherView.WeatherAnimationImplementor() {

    private val mPaint = Paint()
    private val mMeteors: Array<Meteor>
    private val mStars: Array<Star>
    private var mLastRotation3D = INITIAL_ROTATION_3D

    private class Meteor(
        viewWidth: Int,
        viewHeight: Int,
        @ColorInt color: Int,
        scale: Float
    ) {
        var x = 0f
        var y = 0f
        var width: Float = (viewWidth * 0.0088 * scale).toFloat()
        var height = 0f

        var rectF = RectF()
        var speed: Float = viewWidth / 200f

        @ColorInt
        var color: Int = color
        var scale: Float = scale

        private val mViewWidth = viewWidth
        private val mViewHeight = viewHeight
        private val mCanvasSize = Math.pow((viewWidth * viewWidth + viewHeight * viewHeight).toDouble(), 0.5).toInt()

        private val MAX_HEIGHT: Float = (1.1 * viewWidth / Math.cos(60.0 * Math.PI / 180.0)).toFloat()
        private val MIN_HEIGHT: Float = (MAX_HEIGHT * 0.7).toFloat()

        init {
            init(true)
        }

        private fun init(firstTime: Boolean) {
            val r = Random()
            x = r.nextInt(mCanvasSize).toFloat()
            y = if (firstTime) {
                r.nextInt(mCanvasSize) - MAX_HEIGHT - mCanvasSize
            } else {
                -MAX_HEIGHT
            }
            height = MIN_HEIGHT + r.nextFloat() * (MAX_HEIGHT - MIN_HEIGHT)
            buildRectF()
        }

        private fun buildRectF() {
            val x = (this.x - (mCanvasSize - mViewWidth) * 0.5).toFloat()
            val y = (this.y - (mCanvasSize - mViewHeight) * 0.5).toFloat()
            rectF.set(x, y, x + width, y + height)
        }

        fun move(interval: Long, deltaRotation3D: Float) {
            x -= (speed * interval * 5
                * Math.sin(deltaRotation3D * Math.PI / 180.0) * Math.cos(60 * Math.PI / 180.0)).toFloat()
            y += (speed * interval
                * (Math.pow(scale.toDouble(), 0.5)
                - 5 * Math.sin(deltaRotation3D * Math.PI / 180.0) * Math.sin(60 * Math.PI / 180.0))).toFloat()

            if (y >= mCanvasSize) {
                init(false)
            } else {
                buildRectF()
            }
        }
    }

    private class Star(
        centerX: Float,
        centerY: Float,
        radius: Float,
        @ColorInt color: Int,
        duration: Long
    ) {
        var centerX: Float = centerX
        var centerY: Float = centerY
        var radius: Float = (radius * (0.7 + 0.3 * Random().nextFloat())).toFloat()

        @ColorInt
        var color: Int = color
        var alpha = 0f

        var duration: Long = duration
        var progress: Long = 0

        init {
            computeAlpha(duration, progress)
        }

        fun shine(interval: Long) {
            progress = (progress + interval) % duration
            computeAlpha(duration, progress)
        }

        private fun computeAlpha(duration: Long, progress: Long) {
            alpha = if (progress < 0.5 * duration) {
                (progress / 0.5 / duration).toFloat()
            } else {
                (1 - (progress - 0.5 * duration) / 0.5 / duration).toFloat()
            }
            alpha = alpha * 0.66f + 0.33f
        }
    }

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.isAntiAlias = true

        val random = Random()
        val viewWidth = canvasSizes[0]
        val viewHeight = canvasSizes[1]
        val colors = intArrayOf(
            Color.rgb(210, 247, 255),
            Color.rgb(208, 233, 255),
            Color.rgb(175, 201, 228),
            Color.rgb(164, 194, 220),
            Color.rgb(97, 171, 220),
            Color.rgb(74, 141, 193),
            Color.rgb(54, 66, 119),
            Color.rgb(34, 48, 74),
            Color.rgb(236, 234, 213),
            Color.rgb(240, 220, 151)
        )
        mMeteors = Array(15) {
            Meteor(
                viewWidth, viewHeight,
                colors[random.nextInt(colors.size)], random.nextFloat()
            )
        }
        val canvasSize = Math.pow(
            Math.pow(viewWidth.toDouble(), 2.0) + Math.pow(viewHeight.toDouble(), 2.0),
            0.5
        ).toInt()
        val width = (1.0 * canvasSize).toInt()
        val height = ((canvasSize - viewHeight) * 0.5 + viewWidth * 1.1111).toInt()
        val radius = (0.00125 * canvasSize * (0.5 + random.nextFloat())).toFloat()
        mStars = Array(50) { i ->
            val x = (random.nextInt(width) - 0.5 * (canvasSize - viewWidth)).toInt()
            val y = (random.nextInt(height) - 0.5 * (canvasSize - viewHeight)).toInt()
            val duration = (2500 + random.nextFloat() * 2500).toLong()
            Star(
                x.toFloat(),
                y.toFloat(),
                radius,
                colors[i % colors.size],
                duration
            )
        }
    }

    override fun updateData(
        @Size(2) canvasSizes: IntArray,
        interval: Long,
        rotation2D: Float,
        rotation3D: Float
    ) {
        for (m in mMeteors) {
            m.move(interval, if (mLastRotation3D == INITIAL_ROTATION_3D) 0f else rotation3D - mLastRotation3D)
        }
        for (s in mStars) {
            s.shine(interval)
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
            canvas.rotate(
                rotation2D,
                canvasSizes[0] * 0.5f,
                canvasSizes[1] * 0.5f
            )
            for (s in mStars) {
                mPaint.color = s.color
                mPaint.alpha = ((1 - scrollRate) * s.alpha * 255).toInt()
                mPaint.strokeWidth = s.radius * 2
                canvas.drawPoint(s.centerX, s.centerY, mPaint)
            }

            canvas.rotate(
                60f,
                canvasSizes[0] * 0.5f,
                canvasSizes[1] * 0.5f
            )
            for (m in mMeteors) {
                mPaint.color = m.color
                mPaint.strokeWidth = m.rectF.width()
                mPaint.alpha = ((1 - scrollRate) * 255).toInt()
                canvas.drawLine(
                    m.rectF.centerX(), m.rectF.top,
                    m.rectF.centerX(), m.rectF.bottom,
                    mPaint
                )
            }
        }
    }

    companion object {
        private const val INITIAL_ROTATION_3D = 1000f

        @JvmStatic
        @ColorInt
        fun getThemeColor(): Int {
            return Color.rgb(20, 28, 44)
        }
    }
}
