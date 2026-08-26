package wangdaye.com.geometricweather.common.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

abstract class SyncListAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    list: List<T>,
    private val mCallback: DiffUtil.ItemCallback<T>
) : RecyclerView.Adapter<VH>() {

    private var mModelList: List<T> = list

    fun submitList(newList: List<T>) {
        if (newList === mModelList) {
            return
        }

        val oldList = currentList

        if (oldList.isEmpty() && newList.isEmpty()) {
            return
        }

        if (oldList.isEmpty()) {
            val insertedCount = newList.size
            mModelList = newList
            notifyItemRangeInserted(0, insertedCount)
            return
        }

        if (newList.isEmpty()) {
            val removedCount = oldList.size
            mModelList = newList
            notifyItemRangeRemoved(0, removedCount)
            return
        }

        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldList.size

            override fun getNewListSize(): Int = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return mCallback.areItemsTheSame(
                    oldList[oldItemPosition],
                    newList[newItemPosition]
                )
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return mCallback.areContentsTheSame(
                    oldList[oldItemPosition],
                    newList[newItemPosition]
                )
            }
        }, true)

        mModelList = newList
        result.dispatchUpdatesTo(this)
    }

    fun submitMove(from: Int, to: Int) {
        Collections.swap(mModelList, from, to)
        notifyItemMoved(from, to)
    }

    val currentList: List<T>
        get() = Collections.unmodifiableList(mModelList)

    fun getItem(position: Int): T {
        return mModelList[position]
    }

    override fun getItemCount(): Int {
        return mModelList.size
    }
}
