package wangdaye.com.geometricweather.theme.weatherView.materialWeatherView.implementor

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.ColorInt
import androidx.annotation.Size
import wangdaye.com.geometricweather.theme.weatherView.materialWeatherView.MaterialWeatherView

class SunImplementor(
    @Size(2) canvasSizes: IntArray?
) : MaterialWeatherView.WeatherAnimationImplementor() {

    private val mPaint = Paint()
    private val mAngles = FloatArray(3)
    private val mUnitSizes = FloatArray(3)

    init {
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
        mPaint.color = Color.rgb(253, 84, 17)
        mUnitSizes[0] = (0.5 * 0.47 * canvasSizes!![0]).toFloat()
        mUnitSizes[1] = (1.7794 * mUnitSizes[0]).toFloat()
        mUnitSizes[2] = (3.0594 * mUnitSizes[0]).toFloat()
    }

    override fun updateData(
        @Size(2) canvasSizes: IntArray,
        interval: Long,
        rotation2D: Float,
        rotation3D: Float
    ) {
        for (i in mAngles.indices) {
            mAngles[i] = ((mAngles[i] + (90.0 / (3000 + 1000 * i) * interval)) % 90).toFloat()
        }
    }

    override fun draw(
        @Size(2) canvasSizes: IntArray,
        canvas: Canvas,
        scrollRate: Float,
        rotation2D: Float,
        rotation3D: Float
    ) {
        if (scrollRate < 1) {
            val deltaX = (Math.sin(rotation2D * Math.PI / 180.0) * 0.3 * canvasSizes[0]).toFloat()
            val deltaY = (Math.sin(rotation3D * Math.PI / 180.0) * -0.3 * canvasSizes[0]).toFloat()

            canvas.translate(
                canvasSizes[0] + deltaX,
                (0.0333 * canvasSizes[0] + deltaY).toFloat()
            )

            mPaint.alpha = ((1 - scrollRate) * 255 * 0.40).toInt()
            canvas.rotate(mAngles[0])
            for (i in 0 until 4) {
                canvas.drawRect(-mUnitSizes[0], -mUnitSizes[0], mUnitSizes[0], mUnitSizes[0], mPaint)
                canvas.rotate(22.5f)
            }
            canvas.rotate(-90 - mAngles[0])

            mPaint.alpha = ((1 - scrollRate) * 255 * 0.16).toInt()
            canvas.rotate(mAngles[1])
            for (i in 0 until 4) {
                canvas.drawRect(-mUnitSizes[1], -mUnitSizes[1], mUnitSizes[1], mUnitSizes[1], mPaint)
                canvas.rotate(22.5f)
            }
            canvas.rotate(-90 - mAngles[1])

            mPaint.alpha = ((1 - scrollRate) * 255 * 0.08).toInt()
            canvas.rotate(mAngles[2])
            for (i in 0 until 4) {
                canvas.drawRect(-mUnitSizes[2], -mUnitSizes[2], mUnitSizes[2], mUnitSizes[2], mPaint)
                canvas.rotate(22.5f)
            }
            canvas.rotate(-90 - mAngles[2])
        }
    }

    companion object {
        @JvmStatic
        @ColorInt
        fun getThemeColor(): Int {
            return Color.rgb(253, 188, 76)
        }
    }
}
