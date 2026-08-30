package wangdaye.com.geometricweather.common.ui.decotarions

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView
import wangdaye.com.geometricweather.core.R

class GridMarginsDecoration : RecyclerView.ItemDecoration {

    @Px
    private val mMarginsVertical: Float

    @Px
    private val mMarginsHorizontal: Float

    constructor(context: Context, recyclerView: RecyclerView) : this(
        context.resources.getDimensionPixelSize(R.dimen.little_margin).toFloat(),
        recyclerView
    )

    constructor(@Px margins: Float, recyclerView: RecyclerView) : this(
        margins,
        margins,
        recyclerView
    )

    constructor(
        @Px marginsVertical: Float,
        @Px marginsHorizontal: Float,
        recyclerView: RecyclerView
    ) {
        mMarginsVertical = marginsVertical
        mMarginsHorizontal = marginsHorizontal
        recyclerView.clipToPadding = false
        recyclerView.setPadding(
            marginsHorizontal.toInt() / 2,
            marginsVertical.toInt() / 2,
            marginsHorizontal.toInt() / 2,
            marginsVertical.toInt() / 2
        )
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(
            (mMarginsHorizontal / 2).toInt(),
            (mMarginsVertical / 2).toInt(),
            (mMarginsHorizontal / 2).toInt(),
            (mMarginsVertical / 2).toInt()
        )
    }
}
