package wangdaye.com.geometricweather.common.ui.widgets

import android.graphics.Shader
import java.util.Arrays

class DayNightShaderWrapper {

    var shader: Shader? = null
        private set
    var targetWidth: Int = 0
        private set
    var targetHeight: Int = 0
        private set
    var isLightTheme: Boolean = false
        private set
    private var mColors: IntArray = IntArray(0)

    constructor(targetWidth: Int, targetHeight: Int) : this(
        null, targetWidth, targetHeight, true, IntArray(0)
    )

    constructor(
        shader: Shader?,
        targetWidth: Int,
        targetHeight: Int,
        lightTheme: Boolean,
        colors: IntArray
    ) {
        setShader(shader, targetWidth, targetHeight, lightTheme, colors)
    }

    fun isDifferent(
        targetWidth: Int,
        targetHeight: Int,
        lightTheme: Boolean,
        colors: IntArray
    ): Boolean {
        if (shader == null ||
            this.targetWidth != targetWidth ||
            this.targetHeight != targetHeight ||
            isLightTheme != lightTheme ||
            mColors.size != colors.size
        ) {
            return true
        }
        for (i in colors.indices) {
            if (mColors[i] != colors[i]) {
                return true
            }
        }
        return false
    }

    fun setShader(
        shader: Shader?,
        targetWidth: Int,
        targetHeight: Int,
        lightTheme: Boolean,
        colors: IntArray
    ) {
        this.shader = shader
        this.targetWidth = targetWidth
        this.targetHeight = targetHeight
        isLightTheme = lightTheme
        mColors = Arrays.copyOf(colors, colors.size)
    }
}
