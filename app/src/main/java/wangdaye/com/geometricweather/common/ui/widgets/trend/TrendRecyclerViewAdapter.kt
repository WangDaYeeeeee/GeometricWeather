package wangdaye.com.geometricweather.common.ui.widgets.trend

import androidx.recyclerview.widget.RecyclerView
import wangdaye.com.geometricweather.common.basic.models.Location

abstract class TrendRecyclerViewAdapter<VH : RecyclerView.ViewHolder>(
    private var mLocation: Location
) : RecyclerView.Adapter<VH>() {

    fun getLocation(): Location = mLocation

    fun setLocation(location: Location) {
        mLocation = location
        notifyDataSetChanged()
    }
}
