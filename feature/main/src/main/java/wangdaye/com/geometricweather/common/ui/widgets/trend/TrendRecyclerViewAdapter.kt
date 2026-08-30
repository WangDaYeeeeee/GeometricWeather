package wangdaye.com.geometricweather.common.ui.widgets.trend

import androidx.recyclerview.widget.RecyclerView
import wangdaye.com.geometricweather.common.basic.models.Location

abstract class TrendRecyclerViewAdapter<VH : RecyclerView.ViewHolder>(
    location: Location
) : RecyclerView.Adapter<VH>() {

    var location: Location = location
        set(value) {
            field = value
            notifyDataSetChanged()
        }
}
