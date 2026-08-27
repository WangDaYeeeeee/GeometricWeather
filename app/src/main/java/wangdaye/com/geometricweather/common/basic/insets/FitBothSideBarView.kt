package wangdaye.com.geometricweather.common.basic.insets

import androidx.annotation.IntDef

interface FitBothSideBarView {

    companion object {
        const val SIDE_TOP = 1
        const val SIDE_BOTTOM = 1 shl 1
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(SIDE_TOP, SIDE_BOTTOM)
    annotation class FitSide

    fun addFitSide(@FitSide side: Int)

    fun removeFitSide(@FitSide side: Int)

    fun setFitSystemBarEnabled(top: Boolean, bottom: Boolean)

    fun getTopWindowInset(): Int

    fun getBottomWindowInset(): Int
}
