package wangdaye.com.geometricweather.common.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.ui.widgets.TagView

class TagAdapter(
    tagList: List<Tag>,
    @ColorInt private val mCheckedTitleColor: Int,
    @ColorInt private val mUncheckedTitleColor: Int,
    @ColorInt private val mCheckedBackgroundColor: Int,
    @ColorInt private val mUncheckedBackgroundColor: Int,
    private val mListener: OnTagCheckedListener?,
    private var mCheckedIndex: Int = UNCHECKABLE_INDEX
) : RecyclerView.Adapter<TagAdapter.ViewHolder>() {

    private val mTagList: MutableList<Tag> =
        if (tagList is MutableList) tagList else tagList.toMutableList()

    companion object {
        const val UNCHECKABLE_INDEX = -1
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val mTagView: TagView = itemView.findViewById(R.id.item_tag)

        init {
            mTagView.setOnClickListener {
                var consumed = false
                if (mListener != null) {
                    consumed = mListener.onItemChecked(
                        !mTagView.isChecked,
                        mCheckedIndex,
                        adapterPosition
                    )
                }
                if (!consumed && mCheckedIndex != adapterPosition) {
                    val i = mCheckedIndex
                    mCheckedIndex = adapterPosition
                    notifyItemChanged(i)
                    notifyItemChanged(mCheckedIndex)
                }
            }
        }

        fun onBindView(tag: Tag, checked: Boolean) {
            mTagView.setText(tag.getName())

            mTagView.setCheckedBackgroundColor(mCheckedBackgroundColor)
            mTagView.setUncheckedBackgroundColor(mUncheckedBackgroundColor)

            setChecked(checked)
        }

        fun setChecked(checked: Boolean) {
            mTagView.setTextColor(if (checked) mCheckedTitleColor else mUncheckedTitleColor)
            mTagView.isChecked = checked
        }
    }

    fun interface OnTagCheckedListener {
        fun onItemChecked(checked: Boolean, oldPosition: Int, newPosition: Int): Boolean
    }

    fun interface Tag {
        fun getName(): String
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_tag, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindView(mTagList[position], position == mCheckedIndex)
    }

    override fun getItemCount(): Int {
        return mTagList.size
    }

    fun insertItem(tag: Tag) {
        mTagList.add(tag)
        notifyItemInserted(mTagList.size - 1)
    }

    fun removeItem(position: Int): Tag {
        val tag = mTagList.removeAt(position)
        notifyItemRemoved(position)
        return tag
    }
}
