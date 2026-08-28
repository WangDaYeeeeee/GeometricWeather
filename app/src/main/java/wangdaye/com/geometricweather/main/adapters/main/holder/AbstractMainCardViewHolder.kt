package wangdaye.com.geometricweather.main.adapters.main.holder

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.CallSuper
import androidx.cardview.widget.CardView
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.main.adapters.main.FirstCardHeaderController
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider
import wangdaye.com.geometricweather.theme.ThemeManager
import wangdaye.com.geometricweather.theme.weatherThemeDelegate
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider

abstract class AbstractMainCardViewHolder(
    view: View
) : AbstractMainViewHolder(view) {

    private var firstCardHeaderController: FirstCardHeaderController? = null
    protected var location: Location? = null

    @CallSuper
    open fun onBindView(
        activity: GeoActivity,
        location: Location,
        provider: ResourceProvider,
        listAnimationEnabled: Boolean,
        itemAnimationEnabled: Boolean,
        firstCard: Boolean
    ) {
        super.onBindView(activity, location, provider, listAnimationEnabled, itemAnimationEnabled)
        this.location = location

        val delegate = ThemeManager
            .getInstance(activity)
            .weatherThemeDelegate

        val card = itemView as CardView
        card.radius = delegate.getHomeCardRadius(activity)
        card.cardElevation = delegate.getHomeCardElevation(activity)
        card.setCardBackgroundColor(
            MainThemeColorProvider.getColor(location, R.attr.colorMainCardBackground)
        )

        val params = card.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(
            delegate.getHomeCardMargins(context),
            0,
            delegate.getHomeCardMargins(context),
            delegate.getHomeCardMargins(context)
        )
        card.layoutParams = params

        if (firstCard) {
            firstCardHeaderController = FirstCardHeaderController(activity, location)
            firstCardHeaderController!!.bind(card.getChildAt(0) as LinearLayout)
        }
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated method.")
    override fun onBindView(
        context: Context,
        location: Location,
        provider: ResourceProvider,
        listAnimationEnabled: Boolean,
        itemAnimationEnabled: Boolean
    ) {
        throw RuntimeException("Deprecated method.")
    }

    override fun onRecycleView() {
        super.onRecycleView()
        firstCardHeaderController?.unbind()
        firstCardHeaderController = null
    }
}
