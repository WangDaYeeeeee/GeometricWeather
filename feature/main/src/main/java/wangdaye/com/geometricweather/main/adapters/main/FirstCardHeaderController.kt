package wangdaye.com.geometricweather.main.adapters.main

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextClock
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.ImageViewCompat
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.basic.GeoActivity
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.basic.models.weather.Base
import wangdaye.com.geometricweather.common.utils.helpers.IntentHelper
import wangdaye.com.geometricweather.common.utils.helpers.startAboutActivity
import wangdaye.com.geometricweather.common.utils.helpers.startAlertActivity
import wangdaye.com.geometricweather.common.utils.helpers.startAllergenActivity
import wangdaye.com.geometricweather.common.utils.helpers.startCardDisplayManageActivity
import wangdaye.com.geometricweather.common.utils.helpers.startDailyTrendDisplayManageActivity
import wangdaye.com.geometricweather.common.utils.helpers.startDailyWeatherActivity
import wangdaye.com.geometricweather.common.utils.helpers.startHourlyTrendDisplayManageActivityForResult
import wangdaye.com.geometricweather.common.utils.helpers.startMainActivity
import wangdaye.com.geometricweather.common.utils.helpers.startMainActivityForManagement
import wangdaye.com.geometricweather.common.utils.helpers.startPreviewIconActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSearchActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSelectProviderActivity
import wangdaye.com.geometricweather.common.utils.helpers.startSettingsActivity
import wangdaye.com.geometricweather.common.utils.helpers.buildAwakeUpdateActivityIntent
import wangdaye.com.geometricweather.common.utils.helpers.buildMainActivityIntent
import wangdaye.com.geometricweather.common.utils.helpers.buildMainActivityShowAlertsIntent
import wangdaye.com.geometricweather.common.utils.helpers.buildMainActivityShowDailyForecastIntent
import wangdaye.com.geometricweather.main.MainActivity
import wangdaye.com.geometricweather.main.utils.MainThemeColorProvider
import java.text.DateFormat
import java.util.TimeZone

class FirstCardHeaderController(
    private val activity: GeoActivity,
    location: Location
) : View.OnClickListener {

    private val view: View = LayoutInflater.from(activity).inflate(R.layout.container_main_first_card_header, null)
    private val formattedId: String = location.formattedId
    private var container: LinearLayout? = null

    init {
        @SuppressLint("SetTextI18n", "InflateParams")
        val timeIcon = view.findViewById<AppCompatImageView>(R.id.container_main_first_card_header_timeIcon)
        val refreshTime = view.findViewById<TextView>(R.id.container_main_first_card_header_timeText)
        val localTime = view.findViewById<TextClock>(R.id.container_main_first_card_header_localTimeText)
        val alert = view.findViewById<TextView>(R.id.container_main_first_card_header_alert)
        val line = view.findViewById<View>(R.id.container_main_first_card_header_line)

        val weather = location.weather
        if (weather != null) {
            view.setOnClickListener { (activity as MainActivity).setManagementFragmentVisibility(true) }

            if (weather.alertList.isEmpty()) {
                timeIcon.isEnabled = false
                timeIcon.setImageResource(R.drawable.ic_time)
            } else {
                timeIcon.isEnabled = true
                timeIcon.setImageResource(R.drawable.ic_alert)
            }
            timeIcon.contentDescription =
                activity.getString(R.string.content_desc_weather_alert_button)
                    .replace("$", "" + weather.alertList.size)
            ImageViewCompat.setImageTintList(
                timeIcon,
                ColorStateList.valueOf(
                    MainThemeColorProvider.getColor(location, R.attr.colorTitleText)
                )
            )
            timeIcon.setOnClickListener(this)

            refreshTime.text =
                activity.getString(R.string.refresh_at) +
                    " " +
                    Base.getTime(activity, weather.base.updateDate)
            refreshTime.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorTitleText))

            val time = System.currentTimeMillis()
            if (TimeZone.getDefault().getOffset(time) == location.timeZone.getOffset(time)) {
                localTime.visibility = View.GONE
            } else {
                localTime.visibility = View.VISIBLE
                localTime.timeZone = location.timeZone.id
                localTime.format12Hour =
                    activity.getString(R.string.date_format_widget_long) + ", h:mm aa"
                localTime.format24Hour =
                    activity.getString(R.string.date_format_widget_long) + ", HH:mm"
                localTime.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorCaptionText))
            }

            if (weather.alertList.isEmpty()) {
                alert.visibility = View.GONE
                line.visibility = View.GONE
            } else {
                alert.visibility = View.VISIBLE
                val builder = StringBuilder()
                for (i in weather.alertList.indices) {
                    builder.append(weather.alertList[i].description)
                        .append(", ")
                        .append(
                            DateFormat.getDateTimeInstance(
                                DateFormat.LONG,
                                DateFormat.DEFAULT
                            ).format(weather.alertList[i].date)
                        )
                    if (i != weather.alertList.size - 1) {
                        builder.append("\n")
                    }
                }
                alert.text = builder.toString()
                alert.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorBodyText))

                line.visibility = View.VISIBLE
                line.setBackgroundColor(MainThemeColorProvider.getColor(location, R.attr.colorSurface))
            }
            alert.setOnClickListener(this)
        }
    }

    fun bind(firstCardContainer: LinearLayout) {
        container = firstCardContainer
        container!!.addView(view, 0)
    }

    fun unbind() {
        container?.removeViewAt(0)
        container = null
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.container_main_first_card_header_timeIcon,
            R.id.container_main_first_card_header_alert ->
                IntentHelper.startAlertActivity(activity, formattedId)
        }
    }
}
