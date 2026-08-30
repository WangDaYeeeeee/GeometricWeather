package wangdaye.com.geometricweather.main.adapters.main.holder

import android.animation.AnimatorSet
import android.animation.FloatEvaluator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.weather.Weather
import wangdaye.com.geometricweather.common.ui.widgets.astro.MoonPhaseView
import wangdaye.com.geometricweather.common.ui.widgets.astro.SunMoonView
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider
import wangdaye.com.geometricweather.theme.ThemeManager
import wangdaye.com.geometricweather.theme.weatherThemeDelegate
import wangdaye.com.geometricweather.theme.resource.ResourceHelper
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider
import wangdaye.com.geometricweather.theme.weatherView.WeatherViewController
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.max
import kotlin.math.min

class AstroViewHolder(
    parent: ViewGroup
) : AbstractMainCardViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.container_main_sun_moon, parent, false)
) {
    private val title: TextView = itemView.findViewById(R.id.container_main_sun_moon_title)
    private val phaseText: TextView = itemView.findViewById(R.id.container_main_sun_moon_phaseText)
    private val phaseView: MoonPhaseView = itemView.findViewById(R.id.container_main_sun_moon_phaseView)
    private val sunMoonView: SunMoonView = itemView.findViewById(R.id.container_main_sun_moon_controlView)
    private val sunContainer: RelativeLayout = itemView.findViewById(R.id.container_main_sun_moon_sunContainer)
    private val sunTxt: TextView = itemView.findViewById(R.id.container_main_sun_moon_sunrise_sunset)
    private val moonContainer: RelativeLayout = itemView.findViewById(R.id.container_main_sun_moon_moonContainer)
    private val moonTxt: TextView = itemView.findViewById(R.id.container_main_sun_moon_moonrise_moonset)

    private var weather: Weather? = null
    private lateinit var timeZone: TimeZone
    private var startTimes = LongArray(2)
    private var endTimes = LongArray(2)
    private var currentTimes = LongArray(2)
    private var animCurrentTimes = LongArray(2)
    private var phaseAngle = 0
    private val attachAnimatorSets = arrayOfNulls<AnimatorSet>(3)

    private class LongEvaluator : TypeEvaluator<Long> {
        override fun evaluate(fraction: Float, startValue: Long, endValue: Long): Long {
            return startValue + ((endValue - startValue) * fraction).toLong()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindView(
        activity: GeoActivity,
        location: Location,
        provider: ResourceProvider,
        listAnimationEnabled: Boolean,
        itemAnimationEnabled: Boolean,
        firstCard: Boolean
    ) {
        super.onBindView(activity, location, provider, listAnimationEnabled, itemAnimationEnabled, firstCard)

        weather = location.weather
        timeZone = location.timeZone
        val boundWeather = weather!!

        val themeColors = ThemeManager
            .getInstance(context)
            .weatherThemeDelegate
            .getThemeColors(
                context,
                WeatherViewController.getWeatherKind(location.weather),
                location.isDaylight
            )
        title.setTextColor(themeColors[0])

        val talkBackBuilder = StringBuilder(title.text)

        ensureTime(boundWeather)
        ensurePhaseAngle(boundWeather)

        if (!boundWeather.dailyForecast[0].moonPhase.isValid) {
            phaseText.visibility = View.GONE
            phaseView.visibility = View.GONE
        } else {
            phaseText.visibility = View.VISIBLE
            phaseView.visibility = View.VISIBLE

            phaseText.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorBodyText))
            phaseView.setColor(
                ContextCompat.getColor(context, R.color.colorTextLight2nd),
                ContextCompat.getColor(context, R.color.colorTextDark2nd),
                MainThemeColorProvider.getColor(location, R.attr.colorBodyText)
            )

            phaseText.text = boundWeather.dailyForecast[0].moonPhase.getMoonPhase(context)
            talkBackBuilder.append(", ").append(phaseText.text)
        }

        sunMoonView.setSunDrawable(ResourceHelper.getSunDrawable(provider))
        sunMoonView.setMoonDrawable(ResourceHelper.getMoonDrawable(provider))

        if (MainThemeColorProvider.isLightTheme(context, location)) {
            sunMoonView.setColors(
                themeColors[0],
                ColorUtils.setAlphaComponent(themeColors[1], (0.66 * 255).toInt()),
                ColorUtils.setAlphaComponent(themeColors[1], (0.33 * 255).toInt()),
                MainThemeColorProvider.getColor(location, R.attr.colorMainCardBackground),
                true
            )
        } else {
            sunMoonView.setColors(
                themeColors[2],
                ColorUtils.setAlphaComponent(themeColors[2], (0.5 * 255).toInt()),
                ColorUtils.setAlphaComponent(themeColors[2], (0.2 * 255).toInt()),
                MainThemeColorProvider.getColor(location, R.attr.colorMainCardBackground),
                false
            )
        }

        if (itemAnimationEnabled) {
            sunMoonView.setTime(startTimes, endTimes, startTimes)
            sunMoonView.setDayIndicatorRotation(0f)
            sunMoonView.setNightIndicatorRotation(0f)
            phaseView.setSurfaceAngle(0f)
        } else {
            sunMoonView.post { sunMoonView.setTime(startTimes, endTimes, currentTimes) }
            sunMoonView.setDayIndicatorRotation(0f)
            sunMoonView.setNightIndicatorRotation(0f)
            phaseView.setSurfaceAngle(phaseAngle.toFloat())
        }

        if (boundWeather.dailyForecast[0].sun().isValid) {
            val sunriseTime = boundWeather.dailyForecast[0].sun().getRiseTime(context, timeZone)
            val sunsetTime = boundWeather.dailyForecast[0].sun().getSetTime(context, timeZone)

            sunContainer.visibility = View.VISIBLE
            sunTxt.text = sunriseTime + "↑" + "\n" + sunsetTime + "↓"

            talkBackBuilder
                .append(", ")
                .append(activity.getString(R.string.content_des_sunrise).replace("$", sunriseTime!!))
                .append(", ")
                .append(activity.getString(R.string.content_des_sunset).replace("$", sunsetTime!!))
        } else {
            sunContainer.visibility = View.GONE
        }
        if (boundWeather.dailyForecast[0].moon().isValid) {
            val moonriseTime = boundWeather.dailyForecast[0].moon().getRiseTime(context, timeZone)
            val moonsetTime = boundWeather.dailyForecast[0].moon().getSetTime(context, timeZone)

            moonContainer.visibility = View.VISIBLE
            moonTxt.text = moonriseTime + "↑" + "\n" + moonsetTime + "↓"

            talkBackBuilder
                .append(", ")
                .append(activity.getString(R.string.content_des_moonrise).replace("$", moonriseTime!!))
                .append(", ")
                .append(activity.getString(R.string.content_des_moonset).replace("$", moonsetTime!!))
        } else {
            moonContainer.visibility = View.GONE
        }

        itemView.contentDescription = talkBackBuilder.toString()
    }

    @SuppressLint("Recycle")
    override fun onEnterScreen() {
        val boundWeather = weather
        if (itemAnimationEnabled && boundWeather != null) {
            val timeDay = ValueAnimator.ofObject(LongEvaluator(), startTimes[0], currentTimes[0])
            timeDay.addUpdateListener { animation ->
                animCurrentTimes[0] = animation.animatedValue as Long
                sunMoonView.setTime(startTimes, endTimes, animCurrentTimes)
            }

            val totalRotationDay = 360.0 * 7 * (currentTimes[0] - startTimes[0]) / (endTimes[0] - startTimes[0])
            val rotateDay = ValueAnimator.ofObject(
                FloatEvaluator(), 0, (totalRotationDay - totalRotationDay % 360).toInt()
            )
            rotateDay.addUpdateListener { animation ->
                sunMoonView.setDayIndicatorRotation(animation.animatedValue as Float)
            }

            attachAnimatorSets[0] = AnimatorSet()
            attachAnimatorSets[0]!!.playTogether(timeDay, rotateDay)
            attachAnimatorSets[0]!!.interpolator = OvershootInterpolator(1f)
            attachAnimatorSets[0]!!.duration = getPathAnimatorDuration(0)
            attachAnimatorSets[0]!!.start()

            val timeNight = ValueAnimator.ofObject(LongEvaluator(), startTimes[1], currentTimes[1])
            timeNight.addUpdateListener { animation ->
                animCurrentTimes[1] = animation.animatedValue as Long
                sunMoonView.setTime(startTimes, endTimes, animCurrentTimes)
            }

            val totalRotationNight = 360.0 * 4 * (currentTimes[1] - startTimes[1]) / (endTimes[1] - startTimes[1])
            val rotateNight = ValueAnimator.ofObject(
                FloatEvaluator(), 0, (totalRotationNight - totalRotationNight % 360).toInt()
            )
            rotateNight.addUpdateListener { animation ->
                sunMoonView.setNightIndicatorRotation(-1 * (animation.animatedValue as Float))
            }

            attachAnimatorSets[1] = AnimatorSet()
            attachAnimatorSets[1]!!.playTogether(timeNight, rotateNight)
            attachAnimatorSets[1]!!.interpolator = OvershootInterpolator(1f)
            attachAnimatorSets[1]!!.duration = getPathAnimatorDuration(1)
            attachAnimatorSets[1]!!.start()

            if (phaseAngle > 0) {
                val moonAngle = ValueAnimator.ofObject(FloatEvaluator(), 0, phaseAngle)
                moonAngle.addUpdateListener { animation ->
                    phaseView.setSurfaceAngle(animation.animatedValue as Float)
                }

                attachAnimatorSets[2] = AnimatorSet()
                attachAnimatorSets[2]!!.playTogether(moonAngle)
                attachAnimatorSets[2]!!.interpolator = DecelerateInterpolator()
                attachAnimatorSets[2]!!.duration = getPhaseAnimatorDuration()
                attachAnimatorSets[2]!!.start()
            }
        }
    }

    override fun onRecycleView() {
        super.onRecycleView()
        for (i in attachAnimatorSets.indices) {
            if (attachAnimatorSets[i] != null && attachAnimatorSets[i]!!.isRunning) {
                attachAnimatorSets[i]!!.cancel()
            }
            attachAnimatorSets[i] = null
        }
    }

    private fun ensureTime(weather: Weather) {
        val today = weather.dailyForecast[0]

        val calendar = Calendar.getInstance()
        calendar.timeZone = timeZone
        val currentTime = calendar.time.time

        startTimes = LongArray(2)
        endTimes = LongArray(2)
        currentTimes = longArrayOf(currentTime, currentTime)

        if (today.sun().riseDate == null || today.sun().setDate == null) {
            startTimes[0] = currentTime + 1
            endTimes[0] = currentTime + 1
        } else {
            startTimes[0] = today.sun().riseDate!!.time
            endTimes[0] = today.sun().setDate!!.time
        }

        if (today.moon().riseDate == null || today.moon().setDate == null) {
            startTimes[1] = currentTime + 1
            endTimes[1] = currentTime + 1
        } else {
            startTimes[1] = today.moon().riseDate!!.time
            endTimes[1] = today.moon().setDate!!.time
        }

        animCurrentTimes = longArrayOf(currentTimes[0], currentTimes[1])
    }

    private fun ensurePhaseAngle(weather: Weather) {
        val angle = weather.dailyForecast[0].moonPhase.angle
        phaseAngle = angle ?: 0
    }

    private fun getPathAnimatorDuration(index: Int): Long {
        val duration = max(
            1000 + 3000.0
                * (currentTimes[index] - startTimes[index])
                / (endTimes[index] - startTimes[index]),
            0.0
        ).toLong()
        return min(duration, 4000)
    }

    private fun getPhaseAnimatorDuration(): Long {
        val duration = max(0.0, phaseAngle / 360.0 * 1000 + 1000).toLong()
        return min(duration, 2000)
    }
}
