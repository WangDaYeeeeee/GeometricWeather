package wangdaye.com.geometricweather.common.ui.widgets.trend.item

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import wangdaye.com.geometricweather.common.ui.widgets.trend.chart.AbsChartItemView

abstract class AbsTrendItemView : ViewGroup {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    abstract fun setChartItemView(view: AbsChartItemView)

    abstract fun getChartItemView(): AbsChartItemView?

    abstract fun getChartTop(): Int

    abstract fun getChartBottom(): Int
}
