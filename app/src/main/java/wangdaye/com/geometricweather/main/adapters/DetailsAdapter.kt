package wangdaye.com.geometricweather.main.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import wangdaye.com.geometricweather.R
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.options.unit.CloudCoverUnit
import wangdaye.com.geometricweather.common.basic.models.options.unit.RelativeHumidityUnit
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider
import wangdaye.com.geometricweather.settings.SettingsManager

class DetailsAdapter(
    context: Context,
    location: Location
) : RecyclerView.Adapter<DetailsAdapter.ViewHolder>() {

    private val lightTheme = MainThemeColorProvider.isLightTheme(context, location)
    private val indexList = ArrayList<Index>()

    class Index(
        @DrawableRes val iconId: Int,
        val title: String,
        val content: String,
        val talkBack: String = "$title, $content"
    )

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: AppCompatImageView = itemView.findViewById(R.id.item_details_icon)
        private val title: TextView = itemView.findViewById(R.id.item_details_title)
        private val content: TextView = itemView.findViewById(R.id.item_details_content)

        fun onBindView(lightTheme: Boolean, index: Index) {
            itemView.contentDescription = index.talkBack
            icon.setImageResource(index.iconId)
            title.text = index.title
            content.text = index.content
            ImageViewCompat.setImageTintList(
                icon,
                ColorStateList.valueOf(
                    MainThemeColorProvider.getColor(lightTheme, R.attr.colorTitleText)
                )
            )
            title.setTextColor(MainThemeColorProvider.getColor(lightTheme, R.attr.colorTitleText))
            content.setTextColor(MainThemeColorProvider.getColor(lightTheme, R.attr.colorBodyText))
        }
    }

    init {
        val settings = SettingsManager.getInstance(context)
        val speedUnit = settings.speedUnit
        val weather = location.weather!!

        val windTitle = context.getString(R.string.live) + " : " +
            weather.current.wind.getWindDescription(context, speedUnit)
        val windContent = context.getString(R.string.daytime) + " : " +
            weather.dailyForecast[0].day().wind.getWindDescription(context, speedUnit) + "\n" +
            context.getString(R.string.nighttime) + " : " +
            weather.dailyForecast[0].night().wind.getWindDescription(context, speedUnit)
        indexList.add(
            Index(
                R.drawable.ic_wind,
                windTitle,
                windContent,
                context.getString(R.string.wind) + ", " + windTitle + ", " + windContent.replace("\n", ", ")
            )
        )

        if (weather.current.relativeHumidity != null) {
            indexList.add(
                Index(
                    R.drawable.ic_water_percent,
                    context.getString(R.string.humidity),
                    RelativeHumidityUnit.PERCENT.getValueText(
                        context,
                        weather.current.relativeHumidity!!.toInt()
                    )
                )
            )
        }
        if (weather.current.uv.isValid) {
            indexList.add(
                Index(
                    R.drawable.ic_uv,
                    context.getString(R.string.uv_index),
                    weather.current.uv.uvDescription
                )
            )
        }
        if (weather.current.pressure != null) {
            indexList.add(
                Index(
                    R.drawable.ic_gauge,
                    context.getString(R.string.pressure),
                    settings.pressureUnit.getValueText(context, weather.current.pressure!!),
                    context.getString(R.string.pressure) + ", " +
                        settings.pressureUnit.getValueVoice(context, weather.current.pressure!!)
                )
            )
        }
        if (weather.current.visibility != null) {
            indexList.add(
                Index(
                    R.drawable.ic_eye,
                    context.getString(R.string.visibility),
                    settings.distanceUnit.getValueText(context, weather.current.visibility!!),
                    context.getString(R.string.visibility) + ", " +
                        settings.distanceUnit.getValueVoice(context, weather.current.visibility!!)
                )
            )
        }
        if (weather.current.dewPoint != null) {
            indexList.add(
                Index(
                    R.drawable.ic_water,
                    context.getString(R.string.dew_point),
                    settings.temperatureUnit.getValueText(context, weather.current.dewPoint!!)
                )
            )
        }
        if (weather.current.cloudCover != null) {
            indexList.add(
                Index(
                    R.drawable.ic_cloud,
                    context.getString(R.string.cloud_cover),
                    CloudCoverUnit.PERCENT.getValueText(context, weather.current.cloudCover!!)
                )
            )
        }
        if (weather.current.ceiling != null) {
            indexList.add(
                Index(
                    R.drawable.ic_top,
                    context.getString(R.string.ceiling),
                    settings.distanceUnit.getValueText(context, weather.current.ceiling!!),
                    context.getString(R.string.ceiling) + ", " +
                        settings.distanceUnit.getValueVoice(context, weather.current.ceiling!!)
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_details, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindView(lightTheme, indexList[position])
    }

    override fun getItemCount(): Int = indexList.size
}
