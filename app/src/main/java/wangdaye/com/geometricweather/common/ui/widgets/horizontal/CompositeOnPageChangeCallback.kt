package wangdaye.com.geometricweather.common.ui.widgets.horizontal

import androidx.annotation.Px
import java.util.concurrent.ConcurrentModificationException

internal class CompositeOnPageChangeCallback(initialCapacity: Int) :
    HorizontalViewPager2.OnPageChangeCallback() {

    private val mCallbacks: MutableList<HorizontalViewPager2.OnPageChangeCallback> =
        ArrayList(initialCapacity)

    fun addOnPageChangeCallback(callback: HorizontalViewPager2.OnPageChangeCallback) {
        mCallbacks.add(callback)
    }

    fun removeOnPageChangeCallback(callback: HorizontalViewPager2.OnPageChangeCallback) {
        mCallbacks.remove(callback)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, @Px positionOffsetPixels: Int) {
        try {
            for (callback in mCallbacks) {
                callback.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        } catch (ex: ConcurrentModificationException) {
            throwCallbackListModifiedWhileInUse(ex)
        }
    }

    override fun onPageSelected(position: Int) {
        try {
            for (callback in mCallbacks) {
                callback.onPageSelected(position)
            }
        } catch (ex: ConcurrentModificationException) {
            throwCallbackListModifiedWhileInUse(ex)
        }
    }

    override fun onPageScrollStateChanged(@HorizontalViewPager2.ScrollState state: Int) {
        try {
            for (callback in mCallbacks) {
                callback.onPageScrollStateChanged(state)
            }
        } catch (ex: ConcurrentModificationException) {
            throwCallbackListModifiedWhileInUse(ex)
        }
    }

    private fun throwCallbackListModifiedWhileInUse(parent: ConcurrentModificationException) {
        throw IllegalStateException(
            "Adding and removing callbacks during dispatch to callbacks is not supported",
            parent
        )
    }
}
