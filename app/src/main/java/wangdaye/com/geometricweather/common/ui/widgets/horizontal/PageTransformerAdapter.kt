package wangdaye.com.geometricweather.common.ui.widgets.horizontal

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import java.util.Locale

internal class PageTransformerAdapter(
    private val mLayoutManager: LinearLayoutManager
) : HorizontalViewPager2.OnPageChangeCallback() {

    private var mPageTransformer: HorizontalViewPager2.PageTransformer? = null

    fun getPageTransformer(): HorizontalViewPager2.PageTransformer? {
        return mPageTransformer
    }

    fun setPageTransformer(transformer: HorizontalViewPager2.PageTransformer?) {
        mPageTransformer = transformer
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        val transformer = mPageTransformer ?: return

        val transformOffset = -positionOffset
        for (i in 0 until mLayoutManager.childCount) {
            val view = mLayoutManager.getChildAt(i)
                ?: throw IllegalStateException(
                    String.format(
                        Locale.US,
                        "LayoutManager returned a null child at pos %d/%d while transforming pages",
                        i, mLayoutManager.childCount
                    )
                )
            val currPos = mLayoutManager.getPosition(view)
            val viewOffset = transformOffset + (currPos - position)
            transformer.transformPage(view, viewOffset)
        }
    }

    override fun onPageSelected(position: Int) {}

    override fun onPageScrollStateChanged(state: Int) {}
}
