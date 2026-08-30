package wangdaye.com.geometricweather.main.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import wangdaye.com.geometricweather.common.basic.models.Location
import wangdaye.com.geometricweather.common.utils.DisplayUtils
import wangdaye.com.geometricweather.settings.SettingsManager

object MainModuleUtils {

    private const val BASE_ENTER_DURATION = 500L

    @JvmStatic
    fun needUpdate(context: Context, location: Location): Boolean {
        val pollingIntervalInHour = SettingsManager.getInstance(context)
            .updateInterval
            .intervalInHour
        return !location.isUsable
            || location.weather == null
            || !location.weather!!.isValid(pollingIntervalInHour)
    }

    @JvmStatic
    fun getEnterAnimator(view: View, pendingCount: Int): Animator {
        val animators = DisplayUtils.getFloatingOvershotEnterAnimators(
            view,
            0.4f + 0.2f * pendingCount,
            DisplayUtils.dpToPx(view.context, 120f),
            1.025f,
            1.025f
        )

        val set = AnimatorSet()
        set.playTogether(
            ObjectAnimator.ofFloat(view, "alpha", 0f, 1f),
            animators[0],
            animators[1],
            animators[2]
        )
        set.duration = (BASE_ENTER_DURATION - pendingCount * 50L).coerceAtLeast(BASE_ENTER_DURATION / 2)
        set.startDelay = pendingCount * 200L
        return set
    }
}
