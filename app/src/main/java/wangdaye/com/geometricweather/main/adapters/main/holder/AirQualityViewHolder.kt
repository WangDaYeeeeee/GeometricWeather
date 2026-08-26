package wangdaye.com.geometricweather.main.adapters.main.holder

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.ui.widgets.ArcProgress
import wangdaye.com.geometricweather.main.adapters.AqiAdapter
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider
import wangdaye.com.geometricweather.theme.ThemeManager
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider
import wangdaye.com.geometricweather.theme.weatherView.WeatherViewController

class AirQualityViewHolder(
    parent: ViewGroup
) : AbstractMainCardViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.container_main_aqi, parent, false)
) {
    private val title: TextView = itemView.findViewById(R.id.container_main_aqi_title)
    private val progress: ArcProgress = itemView.findViewById(R.id.container_main_aqi_progress)
    private val recyclerView: RecyclerView = itemView.findViewById(R.id.container_main_aqi_recyclerView)
    private var adapter: AqiAdapter? = null
    private var aqiIndex = 0
    private var enable = false
    private var attachAnimatorSet: AnimatorSet? = null

    @SuppressLint("DefaultLocale")
    override fun onBindView(
        activity: GeoActivity,
        location: Location,
        provider: ResourceProvider,
        listAnimationEnabled: Boolean,
        itemAnimationEnabled: Boolean,
        firstCard: Boolean
    ) {
        super.onBindView(activity, location, provider, listAnimationEnabled, itemAnimationEnabled, firstCard)

        val weather = location.weather!!
        aqiIndex = weather.current.airQuality.aqiIndex ?: 0
        enable = true

        title.setTextColor(
            ThemeManager
                .getInstance(context)
                .weatherThemeDelegate
                .getThemeColors(
                    context,
                    WeatherViewController.getWeatherKind(weather),
                    location.isDaylight
                )[0]
        )

        if (itemAnimationEnabled) {
            progress.progress = 0f
            progress.setText(String.format("%d", 0))
            progress.setProgressColor(
                ContextCompat.getColor(context, R.color.colorLevel_1),
                MainThemeColorProvider.isLightTheme(context, location)
            )
            progress.setArcBackgroundColor(MainThemeColorProvider.getColor(location, R.attr.colorOutline))
        } else {
            val aqiColor = weather.current.airQuality.getAqiColor(progress.context)
            progress.progress = aqiIndex.toFloat()
            progress.setText(String.format("%d", aqiIndex))
            progress.setProgressColor(
                aqiColor,
                MainThemeColorProvider.isLightTheme(context, location)
            )
            progress.setArcBackgroundColor(
                ColorUtils.setAlphaComponent(aqiColor, (255 * 0.1).toInt())
            )
        }

        progress.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorTitleText))
        progress.setBottomText(weather.current.airQuality.aqiText)
        progress.setBottomTextColor(MainThemeColorProvider.getColor(location, R.attr.colorBodyText))
        progress.contentDescription = aqiIndex.toString() + ", " + weather.current.airQuality.aqiText

        adapter = AqiAdapter(context, location, itemAnimationEnabled)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    @SuppressLint("DefaultLocale")
    override fun onEnterScreen() {
        val boundLocation = location
        if (itemAnimationEnabled && enable && boundLocation?.weather != null) {
            val weather = boundLocation.weather!!
            val aqiColor = weather.current.airQuality.getAqiColor(progress.context)

            val progressColor = ValueAnimator.ofObject(
                ArgbEvaluator(),
                ContextCompat.getColor(context, R.color.colorLevel_1),
                aqiColor
            )
            progressColor.addUpdateListener { animation ->
                progress.setProgressColor(
                    animation.animatedValue as Int,
                    MainThemeColorProvider.isLightTheme(context, boundLocation)
                )
            }

            val backgroundColor = ValueAnimator.ofObject(
                ArgbEvaluator(),
                MainThemeColorProvider.getColor(
                    boundLocation.isDaylight,
                    R.attr.colorOutline
                ),
                ColorUtils.setAlphaComponent(aqiColor, (255 * 0.1).toInt())
            )
            backgroundColor.addUpdateListener { animation ->
                progress.setArcBackgroundColor(animation.animatedValue as Int)
            }

            val aqiNumber = ValueAnimator.ofObject(FloatEvaluator(), 0, aqiIndex)
            aqiNumber.addUpdateListener { animation ->
                progress.progress = animation.animatedValue as Float
                progress.setText(String.format("%d", progress.progress.toInt()))
            }

            attachAnimatorSet = AnimatorSet()
            attachAnimatorSet!!.playTogether(progressColor, backgroundColor, aqiNumber)
            attachAnimatorSet!!.interpolator = DecelerateInterpolator()
            attachAnimatorSet!!.duration = (1500 + aqiIndex / 400f * 1500).toLong()
            attachAnimatorSet!!.start()

            adapter?.executeAnimation()
        }
    }

    override fun onRecycleView() {
        super.onRecycleView()
        if (attachAnimatorSet != null && attachAnimatorSet!!.isRunning) {
            attachAnimatorSet!!.cancel()
        }
        attachAnimatorSet = null
        adapter?.cancelAnimation()
    }
}
