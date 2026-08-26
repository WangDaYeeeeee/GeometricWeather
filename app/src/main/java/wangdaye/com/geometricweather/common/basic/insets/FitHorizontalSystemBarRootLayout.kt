package wangdaye.com.geometricweather.common.basic.insets

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FitHorizontalSystemBarRootLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var mFitKeyboardExpanded = false

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        val compat = WindowInsetsCompat.toWindowInsetsCompat(insets)
        val systemInsets = compat.getInsets(WindowInsetsCompat.Type.systemBars())
        val r = Rect(
            systemInsets.left,
            systemInsets.top,
            systemInsets.right,
            systemInsets.bottom
        )
        FitBothSideBarHelper.setRootInsetsCache(
            Rect(0, r.top, 0, if (mFitKeyboardExpanded) 0 else r.bottom)
        )
        setPadding(r.left, 0, r.right, 0)
        return insets
    }

    fun setFitKeyboardExpanded(fit: Boolean) {
        mFitKeyboardExpanded = fit
        ViewCompat.requestApplyInsets(this)
        requestLayout()
    }
}
