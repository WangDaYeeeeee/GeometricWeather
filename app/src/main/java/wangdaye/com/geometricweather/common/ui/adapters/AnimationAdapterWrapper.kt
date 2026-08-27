package wangdaye.com.geometricweather.common.ui.adapters

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class AnimationAdapterWrapper<A : RecyclerView.Adapter<VH>, VH : RecyclerView.ViewHolder>
@JvmOverloads constructor(
    private val mInner: A,
    private var mFirstOnly: Boolean = true
) : RecyclerView.Adapter<VH>() {

    private val mAnimatorSet: MutableMap<Int, Animator> = HashMap()
    private var mLastPosition = -1

    init {
        super.setHasStableIds(mInner.hasStableIds())
    }

    protected abstract fun getAnimator(view: View, pendingCount: Int): Animator?

    protected abstract fun setInitState(view: View)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return mInner.onCreateViewHolder(parent, viewType)
    }

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.registerAdapterDataObserver(observer)
        mInner.registerAdapterDataObserver(observer)
    }

    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.unregisterAdapterDataObserver(observer)
        mInner.unregisterAdapterDataObserver(observer)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mInner.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mInner.onDetachedFromRecyclerView(recyclerView)
    }

    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        mInner.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: VH) {
        super.onViewDetachedFromWindow(holder)
        mInner.onViewDetachedFromWindow(holder)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        mInner.onBindViewHolder(holder, position)

        if (!mFirstOnly || position > mLastPosition) {
            clear(holder.itemView, position)

            val a = getAnimator(holder.itemView, mAnimatorSet.size)
            if (a != null) {
                setInitState(holder.itemView)

                a.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        mAnimatorSet.remove(position)
                    }
                })
                a.start()

                mAnimatorSet[position] = a
                mLastPosition = position
                return
            }
        }
        clear(holder.itemView, position)
    }

    private fun clear(view: View, position: Int) {
        val a = mAnimatorSet[position]
        if (a != null) {
            a.cancel()
            mAnimatorSet.remove(position)
        }

        view.alpha = 1f

        view.rotation = 0f
        view.rotationX = 0f
        view.rotationY = 0f

        view.scaleX = 1f
        view.scaleY = 1f
        view.translationX = 0f
        view.translationY = 0f
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.translationZ = 0f
        }
    }

    override fun onViewRecycled(holder: VH) {
        super.onViewRecycled(holder)
        mInner.onViewRecycled(holder)
        clear(holder.itemView, holder.adapterPosition)
    }

    override fun getItemCount(): Int {
        return mInner.itemCount
    }

    override fun getItemViewType(position: Int): Int {
        return mInner.getItemViewType(position)
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(hasStableIds)
        mInner.setHasStableIds(hasStableIds)
    }

    override fun getItemId(position: Int): Long {
        return mInner.getItemId(position)
    }

    fun getWrappedAdapter(): A = mInner

    fun setLastPosition(lastPosition: Int) {
        mLastPosition = lastPosition
    }

    fun setFirstOnly(firstOnly: Boolean) {
        mFirstOnly = firstOnly
    }
}
