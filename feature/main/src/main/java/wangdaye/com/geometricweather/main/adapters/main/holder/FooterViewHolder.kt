package wangdaye.com.geometricweather.main.adapters.main.holder

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import wangdaye.com.geometricweather.core.R
import wangdaye.com.geometricweather.common.basic.models.Location
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
import wangdaye.com.geometricweather.theme.ThemeManager
import wangdaye.com.geometricweather.theme.weatherThemeDelegate
import wangdaye.com.geometricweather.theme.resource.providers.ResourceProvider

class FooterViewHolder(
    parent: ViewGroup
) : AbstractMainViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.container_main_footer, parent, false)
) {
    private val title: TextView = itemView.findViewById(R.id.container_main_footer_title)
    private val editButton: Button = itemView.findViewById(R.id.container_main_footer_editButton)

    @SuppressLint("SetTextI18n")
    override fun onBindView(
        context: Context,
        location: Location,
        provider: ResourceProvider,
        listAnimationEnabled: Boolean,
        itemAnimationEnabled: Boolean
    ) {
        super.onBindView(context, location, provider, listAnimationEnabled, itemAnimationEnabled)

        val cardMarginsVertical = ThemeManager
            .getInstance(context)
            .weatherThemeDelegate
            .getHomeCardMargins(context)
        val params = itemView.layoutParams as ViewGroup.MarginLayoutParams
        if (cardMarginsVertical != 0) {
            params.setMargins(0, -cardMarginsVertical, 0, 0)
        }
        itemView.layoutParams = params

        title.setTextColor(
            ThemeManager
                .getInstance(context)
                .weatherThemeDelegate
                .getHeaderTextColor(title.context)
        )
        title.text = "* Powered by " + location.weatherSource.sourceUrl

        editButton.setTextColor(
            ThemeManager
                .getInstance(context)
                .weatherThemeDelegate
                .getHeaderTextColor(title.context)
        )
        editButton.setOnClickListener {
            IntentHelper.startCardDisplayManageActivity(context as Activity)
        }
    }

    override fun getEnterAnimator(pendingAnimatorList: List<Animator>): Animator {
        val a: Animator = ObjectAnimator.ofFloat(itemView, "alpha", 0f, 1f)
        a.duration = 450
        a.interpolator = FastOutSlowInInterpolator()
        a.startDelay = pendingAnimatorList.size * 150L
        return a
    }
}
