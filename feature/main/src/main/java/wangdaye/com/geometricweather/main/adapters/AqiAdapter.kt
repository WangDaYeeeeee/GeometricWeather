package wangdaye.com.geometricweather.main.adapters

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.FloatEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.unit.AirQualityCOUnit
import wangdaye.com.geometricweather.common.basic.models.options.unit.AirQualityUnit
import wangdaye.com.geometricweather.common.ui.widgets.RoundProgress
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider

class AqiAdapter(
    context: Context,
    location: Location,
    executeAnimation: Boolean
) : RecyclerView.Adapter<AqiAdapter.ViewHolder>() {

    private val lightTheme = location.isDaylight
    private val itemList = ArrayList<AqiItem>()
    private val holderList = ArrayList<ViewHolder>()

    class AqiItem(
        @ColorInt val color: Int,
        val progress: Float,
        val max: Float,
        val title: String,
        val content: String,
        val talkBack: String,
        val executeAnimation: Boolean
    )

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var item: AqiItem? = null
        private var lightTheme: Boolean? = null
        private var executeAnimation = false
        private var attachAnimatorSet: AnimatorSet? = null
        private val title: TextView = itemView.findViewById(R.id.item_aqi_title)
        private val content: TextView = itemView.findViewById(R.id.item_aqi_content)
        private val progress: RoundProgress = itemView.findViewById(R.id.item_aqi_progress)

        fun onBindView(lightTheme: Boolean, item: AqiItem) {
            val context = itemView.context
            this.item = item
            this.lightTheme = lightTheme
            executeAnimation = item.executeAnimation
            itemView.contentDescription = item.talkBack
            title.text = item.title
            title.setTextColor(MainThemeColorProvider.getColor(lightTheme, R.attr.colorTitleText))
            content.text = item.content
            content.setTextColor(MainThemeColorProvider.getColor(lightTheme, R.attr.colorBodyText))
            if (executeAnimation) {
                progress.progress = 0f
                progress.setProgressColor(ContextCompat.getColor(context, R.color.colorLevel_1))
                progress.setProgressBackgroundColor(MainThemeColorProvider.getColor(lightTheme, R.attr.colorOutline))
            } else {
                progress.progress = (100.0 * item.progress / item.max).toInt().toFloat()
                progress.setProgressColor(item.color)
                progress.setProgressBackgroundColor(
                    ColorUtils.setAlphaComponent(item.color, (255 * 0.1).toInt())
                )
            }
        }

        fun executeAnimation() {
            val bound = item
            if (executeAnimation && bound != null) {
                executeAnimation = false
                val progressColor = ValueAnimator.ofObject(
                    ArgbEvaluator(),
                    ContextCompat.getColor(itemView.context, R.color.colorLevel_1),
                    bound.color
                )
                progressColor.addUpdateListener { animation ->
                    progress.setProgressColor(animation.animatedValue as Int)
                }
                val backgroundColor = ValueAnimator.ofObject(
                    ArgbEvaluator(),
                    MainThemeColorProvider.getColor(lightTheme!!, R.attr.colorOutline),
                    ColorUtils.setAlphaComponent(bound.color, (255 * 0.1).toInt())
                )
                backgroundColor.addUpdateListener { animation ->
                    progress.setProgressBackgroundColor(animation.animatedValue as Int)
                }
                val aqiNumber = ValueAnimator.ofObject(FloatEvaluator(), 0, bound.progress)
                aqiNumber.addUpdateListener { animation ->
                    progress.progress = 100.0f * (animation.animatedValue as Float) / bound.max
                }
                attachAnimatorSet = AnimatorSet()
                attachAnimatorSet!!.playTogether(progressColor, backgroundColor, aqiNumber)
                attachAnimatorSet!!.interpolator = DecelerateInterpolator(3f)
                attachAnimatorSet!!.duration = (bound.progress / bound.max * 5000).toLong()
                attachAnimatorSet!!.start()
            }
        }

        fun cancelAnimation() {
            if (attachAnimatorSet != null && attachAnimatorSet!!.isRunning) {
                attachAnimatorSet!!.cancel()
            }
            attachAnimatorSet = null
        }
    }

    init {
        if (location.weather?.current?.airQuality?.isValid == true) {
            val airQuality = location.weather!!.current.airQuality
            airQuality.pm25?.let { pm25 ->
                itemList.add(
                    AqiItem(
                        airQuality.getPm25Color(context),
                        pm25,
                        250f,
                        "PM2.5",
                        AirQualityUnit.MUGPCUM.getValueText(context, pm25),
                        context.getString(R.string.content_des_pm25) + ", " +
                            AirQualityUnit.MUGPCUM.getValueVoice(context, pm25),
                        executeAnimation
                    )
                )
            }
            airQuality.pm10?.let { pm10 ->
                itemList.add(
                    AqiItem(
                        airQuality.getPm10Color(context),
                        pm10,
                        420f,
                        "PM10",
                        AirQualityUnit.MUGPCUM.getValueText(context, pm10),
                        context.getString(R.string.content_des_pm10) + ", " +
                            AirQualityUnit.MUGPCUM.getValueVoice(context, pm10),
                        executeAnimation
                    )
                )
            }
            airQuality.so2?.let { so2 ->
                itemList.add(
                    AqiItem(
                        airQuality.getSo2Color(context),
                        so2,
                        1600f,
                        "SO₂",
                        AirQualityUnit.MUGPCUM.getValueText(context, so2),
                        context.getString(R.string.content_des_so2) + ", " +
                            AirQualityUnit.MUGPCUM.getValueVoice(context, so2),
                        executeAnimation
                    )
                )
            }
            airQuality.no2?.let { no2 ->
                itemList.add(
                    AqiItem(
                        airQuality.getNo2Color(context),
                        no2,
                        565f,
                        "NO₂",
                        AirQualityUnit.MUGPCUM.getValueText(context, no2),
                        context.getString(R.string.content_des_no2) + ", " +
                            AirQualityUnit.MUGPCUM.getValueVoice(context, no2),
                        executeAnimation
                    )
                )
            }
            airQuality.o3?.let { o3 ->
                itemList.add(
                    AqiItem(
                        airQuality.getO3Color(context),
                        o3,
                        800f,
                        "O₃",
                        AirQualityUnit.MUGPCUM.getValueText(context, o3),
                        context.getString(R.string.content_des_o3) + ", " +
                            AirQualityUnit.MUGPCUM.getValueVoice(context, o3),
                        executeAnimation
                    )
                )
            }
            airQuality.co?.let { co ->
                itemList.add(
                    AqiItem(
                        airQuality.getCOColor(context),
                        co,
                        90f,
                        "CO",
                        AirQualityCOUnit.MGPCUM.getValueText(context, co),
                        context.getString(R.string.content_des_co) + ", " +
                            AirQualityCOUnit.MGPCUM.getValueVoice(context, co),
                        executeAnimation
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_aqi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindView(lightTheme, itemList[position])
        if (itemList[position].executeAnimation) {
            holderList.add(holder)
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun executeAnimation() {
        for (holder in holderList) {
            holder.executeAnimation()
        }
    }

    fun cancelAnimation() {
        for (holder in holderList) {
            holder.cancelAnimation()
        }
        holderList.clear()
    }
}
