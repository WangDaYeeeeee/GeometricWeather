package wangdaye.com.geometricweather.main.adapters.main

import android.animation.Animator
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.appearance.CardDisplay
import wangdaye.com.geometricweather.main.adapters.main.holder.AbstractMainCardViewHolder
import wangdaye.com.geometricweather.main.adapters.main.holder.AbstractMainViewHolder
import wangdaye.com.geometricweather.main.adapters.main.holder.AirQualityViewHolder
import wangdaye.com.geometricweather.main.adapters.main.holder.AllergenViewHolder
import wangdaye.com.geometricweather.main.adapters.main.holder.AstroViewHolder
import wangdaye.com.geometricweather.main.adapters.main.holder.DailyViewHolder
import wangdaye.com.geometricweather.main.adapters.main.holder.DetailsViewHolder
import wangdaye.com.geometricweather.main.adapters.main.holder.FooterViewHolder
import wangdaye.com.geometricweather.main.adapters.main.holder.HeaderViewHolder
import wangdaye.com.geometricweather.main.adapters.main.holder.HourlyViewHolder
import wangdaye.com.geometricweather.settings.SettingsManager
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider
import wangdaye.com.geometricweather.theme.weatherView.WeatherView

class MainAdapter(
    activity: GeoActivity,
    host: RecyclerView,
    weatherView: WeatherView,
    location: Location?,
    provider: ResourceProvider,
    listAnimationEnabled: Boolean,
    itemAnimationEnabled: Boolean
) : RecyclerView.Adapter<AbstractMainViewHolder>() {

    private lateinit var activity: GeoActivity
    private lateinit var host: RecyclerView
    private lateinit var weatherView: WeatherView
    private var location: Location? = null
    private lateinit var provider: ResourceProvider

    private var viewTypeList: MutableList<Int> = ArrayList()
    private var firstCardPosition: Int? = null
    private var pendingAnimatorList: MutableList<Animator> = ArrayList()
    private var headerCurrentTemperatureTextHeight = -1
    private var listAnimationEnabled = false
    private var itemAnimationEnabled = false

    init {
        update(activity, host, weatherView, location, provider, listAnimationEnabled, itemAnimationEnabled)
    }

    fun update(
        activity: GeoActivity,
        host: RecyclerView,
        weatherView: WeatherView,
        location: Location?,
        provider: ResourceProvider,
        listAnimationEnabled: Boolean,
        itemAnimationEnabled: Boolean
    ) {
        this.activity = activity
        this.host = host
        this.weatherView = weatherView
        this.location = location
        this.provider = provider

        viewTypeList = ArrayList()
        firstCardPosition = null
        pendingAnimatorList = ArrayList()
        headerCurrentTemperatureTextHeight = -1
        this.listAnimationEnabled = listAnimationEnabled
        this.itemAnimationEnabled = itemAnimationEnabled

        if (location?.weather != null) {
            val weather = location.weather!!
            val cardDisplayList = SettingsManager.getInstance(activity).cardDisplayList
            viewTypeList.add(ViewType.HEADER)
            for (c in cardDisplayList) {
                if (c == CardDisplay.CARD_AIR_QUALITY && !weather.current.airQuality.isValid) {
                    continue
                }
                if (c == CardDisplay.CARD_ALLERGEN && !weather.dailyForecast[0].pollen.isValid) {
                    continue
                }
                if (c == CardDisplay.CARD_SUNRISE_SUNSET &&
                    (weather.dailyForecast.isEmpty() || !weather.dailyForecast[0].sun().isValid)
                ) {
                    continue
                }
                viewTypeList.add(getViewType(c))
            }
            viewTypeList.add(ViewType.FOOTER)

            ensureFirstCard()
        }
    }

    fun setNullWeather() {
        viewTypeList = ArrayList()
        ensureFirstCard()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractMainViewHolder {
        return when (viewType) {
            ViewType.HEADER -> HeaderViewHolder(parent, weatherView)
            ViewType.DAILY -> DailyViewHolder(parent)
            ViewType.HOURLY -> HourlyViewHolder(parent)
            ViewType.AIR_QUALITY -> AirQualityViewHolder(parent)
            ViewType.ALLERGEN -> AllergenViewHolder(parent)
            ViewType.ASTRO -> AstroViewHolder(parent)
            ViewType.DETAILS -> DetailsViewHolder(parent)
            else -> FooterViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: AbstractMainViewHolder, position: Int) {
        val loc = location!!
        if (holder is AbstractMainCardViewHolder) {
            holder.onBindView(
                activity,
                loc,
                provider,
                listAnimationEnabled,
                itemAnimationEnabled,
                firstCardPosition != null && firstCardPosition == position
            )
        } else {
            holder.onBindView(activity, loc, provider, listAnimationEnabled, itemAnimationEnabled)
        }
        host.post { holder.checkEnterScreen(host, pendingAnimatorList, listAnimationEnabled) }
    }

    override fun onViewRecycled(holder: AbstractMainViewHolder) {
        holder.onRecycleView()
    }

    override fun getItemCount(): Int = viewTypeList.size

    override fun getItemViewType(position: Int): Int = viewTypeList[position]

    private fun ensureFirstCard() {
        firstCardPosition = null
        for (i in 0 until itemCount) {
            val type = getItemViewType(i)
            if (type == ViewType.DAILY ||
                type == ViewType.HOURLY ||
                type == ViewType.AIR_QUALITY ||
                type == ViewType.ALLERGEN ||
                type == ViewType.ASTRO ||
                type == ViewType.DETAILS
            ) {
                firstCardPosition = i
                return
            }
        }
    }

    fun getCurrentTemperatureTextHeight(): Int {
        if (headerCurrentTemperatureTextHeight <= 0 && itemCount > 0) {
            val holder = host.findViewHolderForAdapterPosition(0) as AbstractMainViewHolder?
            if (holder is HeaderViewHolder) {
                headerCurrentTemperatureTextHeight = holder.currentTemperatureHeight
            }
        }
        return headerCurrentTemperatureTextHeight
    }

    fun onScroll() {
        for (i in 0 until itemCount) {
            val holder = host.findViewHolderForAdapterPosition(i) as AbstractMainViewHolder?
            holder?.checkEnterScreen(host, pendingAnimatorList, listAnimationEnabled)
        }
    }

    companion object {
        private fun getViewType(cardDisplay: CardDisplay): Int {
            return when (cardDisplay) {
                CardDisplay.CARD_DAILY_OVERVIEW -> ViewType.DAILY
                CardDisplay.CARD_HOURLY_OVERVIEW -> ViewType.HOURLY
                CardDisplay.CARD_AIR_QUALITY -> ViewType.AIR_QUALITY
                CardDisplay.CARD_ALLERGEN -> ViewType.ALLERGEN
                CardDisplay.CARD_SUNRISE_SUNSET -> ViewType.ASTRO
                else -> ViewType.DETAILS
            }
        }
    }
}
